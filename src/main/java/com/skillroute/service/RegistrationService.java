package com.skillroute.service;

import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.FieldValidationException;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;
    private final MailService mailService;
    private final MessageProperties messages;

    @Transactional
    public void register(RegistrationRequest form) {
        validateRegistrationBusinessRules(form);

        Account account = Account.builder()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(form.getRole())
                .isVerified(false)
                .build();

        Account savedAccount = accountRepository.save(account);
        String token = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                redisProperties.getPrefix() + token,
                savedAccount.getEmail(),
                redisProperties.getTtlMinutes(),
                TimeUnit.MINUTES
        );

        mailService.sendVerificationMail(form.getEmail(), token);
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
