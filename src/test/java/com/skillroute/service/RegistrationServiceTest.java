package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.exception.TooManyRequestsException;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private VerificationService verificationService;

    private RegistrationService service;
    private RedisProperties redisProperties;

    @BeforeEach
    void setUp() {
        redisProperties = new RedisProperties();
        redisProperties.setRegistrationLimitPrefix("registration:lock:");
        redisProperties.setRegistrationIntervalSeconds(30);

        service = new RegistrationService(
                accountRepository,
                passwordEncoder,
                eventPublisher,
                redisTemplate,
                redisProperties,
                verificationService,
                TestMessageProperties.create());
    }

    @Test
    void validateRegistrationBusinessRulesCollectsBusinessErrors() {
        RegistrationRequest request = RegistrationRequest.builder()
                .email("taken@example.com")
                .password("password")
                .confirmPassword("other")
                .role(Role.ADMIN)
                .build();

        when(accountRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.validateRegistrationBusinessRules(request))
                .isInstanceOfSatisfying(FieldValidationException.class, exception -> {
                    assertThat(exception.getFields())
                            .containsEntry("role", "Недопустимая роль")
                            .containsEntry("email", "Пользователь с такой почтой уже существует")
                            .containsEntry("confirmPassword", "Пароли не совпадают");
                });
    }

    @Test
    void registerCreatesLockAccountVerificationMailAndEvent() {
        RegistrationRequest request = RegistrationRequest.builder()
                .email("student@example.com")
                .password("password")
                .confirmPassword("password")
                .role(Role.STUDENT)
                .build();

        when(accountRepository.existsByEmail("student@example.com")).thenReturn(false);
        when(redisTemplate.hasKey("registration:lock:student@example.com")).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        service.register(request);

        verify(valueOperations).set("registration:lock:student@example.com", "lock", 30, TimeUnit.SECONDS);
        verify(verificationService).sendVerificationEmail("student@example.com");

        ArgumentCaptor<AccountRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(AccountRegisteredEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getAccount().getEmail()).isEqualTo("student@example.com");
        assertThat(eventCaptor.getValue().getAccount().isVerified()).isFalse();
    }

    @Test
    void registerUsesRedisLockToPreventDoubleSubmit() {
        RegistrationRequest request = RegistrationRequest.builder()
                .email("student@example.com")
                .password("password")
                .confirmPassword("password")
                .role(Role.STUDENT)
                .build();

        when(accountRepository.existsByEmail("student@example.com")).thenReturn(false);
        when(redisTemplate.hasKey("registration:lock:student@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessage("Регистрация временно заблокирована на 30 секунд");
    }
}
