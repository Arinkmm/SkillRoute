package com.skillroute.service;

import com.skillroute.dto.request.ForgotPasswordRequest;
import com.skillroute.dto.request.ResetPasswordRequest;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.exception.PasswordResetTokenException;
import com.skillroute.exception.TooManyRequestsException;
import com.skillroute.model.Account;
import com.skillroute.openapi.model.ResetPasswordRequestApi;
import com.skillroute.properties.MessageProperties;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
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
public class PasswordResetService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;
    private final MailService mailService;
    private final MessageProperties messages;

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest form) {
        String email = form.getEmail();
        String limitKey = redisProperties.getPasswordResetLimitPrefix() + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new TooManyRequestsException(messages.getPasswordReset().getTooManyRequests().formatted(redisProperties.getPasswordResetIntervalMinutes()));
        }

        redisTemplate.opsForValue().set(
                limitKey,
                "lock",
                redisProperties.getPasswordResetIntervalMinutes(),
                TimeUnit.MINUTES
        );

        accountRepository.findByEmail(email).ifPresent(account -> {
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(
                    redisProperties.getPasswordResetPrefix() + token,
                    account.getEmail(),
                    redisProperties.getPasswordResetTtlMinutes(),
                    TimeUnit.MINUTES
            );

            mailService.sendPasswordResetMail(account.getEmail(), token);
        });
    }

    @Transactional(readOnly = true)
    public void validatePasswordResetToken(String token) {
        getEmailByToken(token);
    }

    @Transactional(readOnly = true)
    public void validateResetPasswordBusinessRules(ResetPasswordRequest form) {
        validateResetPasswordBusinessRules(form.getToken(), form.getNewPassword(), form.getConfirmNewPassword());
    }

    @Transactional(readOnly = true)
    public void validateResetPasswordBusinessRules(ResetPasswordRequestApi form) {
        validateResetPasswordBusinessRules(form.getToken(), form.getNewPassword(), form.getConfirmNewPassword());
    }

    private void validateResetPasswordBusinessRules(String token, String newPassword, String confirmNewPassword) {
        Map<String, String> errors = new LinkedHashMap<>();
        MessageProperties.PasswordReset passwordResetMessages = messages.getPasswordReset();

        if (!isPasswordResetTokenValid(token)) {
            errors.putIfAbsent("token", passwordResetMessages.getTokenInvalid());
        }

        if (newPassword != null && confirmNewPassword != null && !newPassword.equals(confirmNewPassword)) {
            errors.putIfAbsent("confirmNewPassword", passwordResetMessages.getPasswordMismatch());
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(passwordResetMessages.getErrorTitle(), errors);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest form) {
        validateResetPasswordBusinessRules(form);

        String redisKey = redisProperties.getPasswordResetPrefix() + form.getToken();
        String email = getEmailByToken(form.getToken());
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new PasswordResetTokenException(messages.getPasswordReset().getTokenInvalid()));

        account.setPassword(passwordEncoder.encode(form.getNewPassword()));
        accountRepository.save(account);
        redisTemplate.delete(redisKey);
    }

    private String getEmailByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new PasswordResetTokenException(messages.getPasswordReset().getTokenInvalid());
        }

        String email = redisTemplate.opsForValue().get(redisProperties.getPasswordResetPrefix() + token);
        if (email == null) {
            throw new PasswordResetTokenException(messages.getPasswordReset().getTokenInvalid());
        }

        return email;
    }

    private boolean isPasswordResetTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        return Boolean.TRUE.equals(redisTemplate.hasKey(redisProperties.getPasswordResetPrefix() + token));
    }
}
