package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.exception.AccountAlreadyVerifiedException;
import com.skillroute.exception.TooManyRequestsException;
import com.skillroute.exception.VerificationTokenException;
import com.skillroute.model.Account;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private MailService mailService;

    private VerificationService service;
    private RedisProperties redisProperties;

    @BeforeEach
    void setUp() {
        redisProperties = new RedisProperties();
        redisProperties.setPrefix("verification:token:");
        redisProperties.setTtlMinutes(30);
        redisProperties.setRetentionTtlMinutes(120);
        redisProperties.setResendLimitPrefix("verification:lock:");
        redisProperties.setResendIntervalMinutes(10);

        service = new VerificationService(
                accountRepository,
                redisTemplate,
                redisProperties,
                mailService,
                TestMessageProperties.create());
    }

    @Test
    void verifyUserMarksAccountVerifiedAndKeepsTokenForRepeatClick() {
        Account account = Account.builder().email("student@example.com").isVerified(false).build();
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries("verification:token:token")).thenReturn(Map.of(
                "email", "student@example.com",
                "expiresAt", Instant.now().plus(10, ChronoUnit.MINUTES).toString()));
        when(accountRepository.findByEmail("student@example.com")).thenReturn(Optional.of(account));

        service.verifyUser("token");

        assertThat(account.isVerified()).isTrue();
        verify(accountRepository).save(account);
        verify(redisTemplate, never()).delete("verification:token:token");
    }

    @Test
    void verifyUserRejectsAlreadyVerifiedAccountWithoutResendState() {
        Account account = Account.builder().email("student@example.com").isVerified(true).build();
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries("verification:token:token")).thenReturn(Map.of(
                "email", "student@example.com",
                "expiresAt", Instant.now().minus(10, ChronoUnit.MINUTES).toString()));
        when(accountRepository.findByEmail("student@example.com")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.verifyUser("token"))
                .isInstanceOf(AccountAlreadyVerifiedException.class)
                .hasMessage("Аккаунт уже подтвержден");
    }

    @Test
    void verifyUserRejectsExpiredOrMalformedTokens() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries("verification:token:expired")).thenReturn(Map.of(
                "email", "student@example.com",
                "expiresAt", Instant.now().minus(1, ChronoUnit.MINUTES).toString()));
        when(accountRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(Account.builder().email("student@example.com").isVerified(false).build()));

        assertThatThrownBy(() -> service.verifyUser("expired"))
                .isInstanceOf(VerificationTokenException.class)
                .hasMessage("Ссылка подтверждения недействительна или срок ее действия истек");
        assertThatThrownBy(() -> service.verifyUser(" "))
                .isInstanceOf(VerificationTokenException.class)
                .hasMessage("Ссылка подтверждения недействительна или срок ее действия истек");
    }

    @Test
    void resendVerificationEmailUsesLimitAndSendsNewToken() {
        Account account = Account.builder().email("student@example.com").isVerified(false).build();
        when(redisTemplate.hasKey("verification:lock:student@example.com")).thenReturn(false);
        when(accountRepository.findByEmail("student@example.com")).thenReturn(Optional.of(account));
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.resendVerificationEmail("student@example.com");

        verify(hashOperations).putAll(startsWith("verification:token:"), anyMap());
        verify(redisTemplate).expire(startsWith("verification:token:"), eq(120L), eq(TimeUnit.MINUTES));
        verify(valueOperations).set("verification:lock:student@example.com", "lock", 10, TimeUnit.MINUTES);
        verify(mailService).sendVerificationMail(eq("student@example.com"), anyString());
    }

    @Test
    void resendVerificationEmailRejectsLimitAndAlreadyVerifiedAccount() {
        when(redisTemplate.hasKey("verification:lock:student@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.resendVerificationEmail("student@example.com"))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessage("Повторная отправка письма доступна через 10 минут");

        Account verified = Account.builder().email("verified@example.com").isVerified(true).build();
        when(redisTemplate.hasKey("verification:lock:verified@example.com")).thenReturn(false);
        when(accountRepository.findByEmail("verified@example.com")).thenReturn(Optional.of(verified));

        assertThatThrownBy(() -> service.resendVerificationEmail("verified@example.com"))
                .isInstanceOf(AccountAlreadyVerifiedException.class)
                .hasMessage("Аккаунт уже подтвержден");
    }

    @Test
    void resendVerificationEmailByTokenReadsEmailAndDeletesOldToken() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries("verification:token:old")).thenReturn(Map.of(
                "email", "student@example.com",
                "expiresAt", Instant.now().plus(10, ChronoUnit.MINUTES).toString()));
        when(redisTemplate.hasKey("verification:lock:student@example.com")).thenReturn(false);
        when(accountRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(Account.builder().email("student@example.com").build()));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.resendVerificationEmailByToken("old");

        verify(mailService).sendVerificationMail(eq("student@example.com"), anyString());
        verify(redisTemplate).delete("verification:token:old");
    }
}
