package com.skillroute.service;

import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.exception.TooManyRequestsException;
import com.skillroute.model.Account;
import com.skillroute.model.Role;
import com.skillroute.properties.MessageProperties;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;
    private final VerificationService verificationService;
    private final MessageProperties messages;

    @Transactional
    public void register(RegistrationRequest form) {
        validateRegistrationBusinessRules(form);

        String limitKey = redisProperties.getRegistrationLimitPrefix() + form.getEmail();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new TooManyRequestsException(messages.getRegistration().getTooManyRequests()
                    .formatted(redisProperties.getRegistrationIntervalSeconds()));
        }

        redisTemplate.opsForValue().set(
                limitKey,
                "lock",
                redisProperties.getRegistrationIntervalSeconds(),
                TimeUnit.SECONDS
        );

        Account account = Account.builder()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(form.getRole())
                .isVerified(false)
                .build();

        Account savedAccount = accountRepository.save(account);

        verificationService.sendVerificationEmail(savedAccount.getEmail());
        eventPublisher.publishEvent(new AccountRegisteredEvent(savedAccount));
    }

    @Transactional(readOnly = true)
    public void validateRegistrationBusinessRules(RegistrationRequest form) {
        Map<String, String> errors = new LinkedHashMap<>();

        MessageProperties.Registration registrationMessages = messages.getRegistration();

        if (form.getRole() != Role.STUDENT && form.getRole() != Role.COMPANY) {
            errors.put("role", registrationMessages.getRoleNotAllowed());
        }

        if (accountRepository.existsByEmail(form.getEmail())) {
            errors.put("email", registrationMessages.getEmailExists());
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.put("confirmPassword", registrationMessages.getPasswordMismatch());
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(registrationMessages.getErrorTitle(), errors);
        }
    }
}
