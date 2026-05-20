package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.ForgotPasswordRequest;
import com.skillroute.dto.request.ResetPasswordRequest;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.exception.TooManyRequestsException;
import com.skillroute.model.Account;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private MailService mailService;

    private PasswordResetService service;
    private RedisProperties redisProperties;

    @BeforeEach
    void setUp() {
        redisProperties = new RedisProperties();
        redisProperties.setPasswordResetPrefix("password:token:");
        redisProperties.setPasswordResetTtlMinutes(20);
        redisProperties.setPasswordResetLimitPrefix("password:lock:");
        redisProperties.setPasswordResetIntervalMinutes(5);

        service = new PasswordResetService(
                accountRepository,
                passwordEncoder,
                redisTemplate,
                redisProperties,
                mailService,
                TestMessageProperties.create());
    }

    @Test
    void requestPasswordResetCreatesLimitAndTokenWhenAccountExists() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("student@example.com");

        when(redisTemplate.hasKey("password:lock:student@example.com")).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(accountRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(Account.builder().email("student@example.com").build()));

        service.requestPasswordReset(request);

        verify(valueOperations).set("password:lock:student@example.com", "lock", 5, TimeUnit.MINUTES);
        verify(valueOperations).set(org.mockito.ArgumentMatchers.startsWith("password:token:"),
                eq("student@example.com"),
                eq(20L),
                eq(TimeUnit.MINUTES));
        verify(mailService).sendPasswordResetMail(eq("student@example.com"), anyString());
    }

    @Test
    void requestPasswordResetUsesLimitKey() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("student@example.com");

        when(redisTemplate.hasKey("password:lock:student@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.requestPasswordReset(request))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessage("Повторный запрос восстановления пароля доступен через 5 минут");
    }

    @Test
    void validateResetPasswordBusinessRulesReportsInvalidTokenAndMismatch() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("expired")
                .newPassword("new-password")
                .confirmNewPassword("other-password")
                .build();

        when(redisTemplate.hasKey("password:token:expired")).thenReturn(false);

        assertThatThrownBy(() -> service.validateResetPasswordBusinessRules(request))
                .isInstanceOfSatisfying(FieldValidationException.class, exception -> {
                    assertThat(exception.getFields())
                            .containsEntry("token", "Ссылка для восстановления пароля недействительна или срок ее действия истек")
                            .containsEntry("confirmNewPassword", "Пароли не совпадают");
                });
    }

    @Test
    void resetPasswordUpdatesPasswordAndDeletesToken() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("token")
                .newPassword("new-password")
                .confirmNewPassword("new-password")
                .build();
        Account account = Account.builder().email("student@example.com").password("old").build();

        when(redisTemplate.hasKey("password:token:token")).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("password:token:token")).thenReturn("student@example.com");
        when(accountRepository.findByEmail("student@example.com")).thenReturn(Optional.of(account));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        service.resetPassword(request);

        assertThat(account.getPassword()).isEqualTo("encoded-new");
        verify(accountRepository).save(account);
        verify(redisTemplate).delete("password:token:token");
    }
}
