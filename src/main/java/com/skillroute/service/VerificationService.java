package com.skillroute.service;

import com.skillroute.exception.*;
import com.skillroute.model.Account;
import com.skillroute.properties.MessageProperties;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final AccountRepository accountRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;
    private final MailService mailService;
    private final MessageProperties messages;

    @Transactional
    public void verifyUser(String token) {
        String redisKey = redisProperties.getPrefix() + token;
        TokenData tokenData = getTokenData(token);
        if (Instant.now().isAfter(tokenData.expiresAt())) {
            throw new VerificationTokenException(messages.getVerification().getTokenInvalid());
        }

        String email = tokenData.email();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new VerificationTokenException(messages.getVerification().getTokenInvalid()));

        account.setVerified(true);
        accountRepository.save(account);
        redisTemplate.delete(redisKey);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        String limitKey = redisProperties.getResendLimitPrefix() + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new TooManyRequestsException(messages.getVerification().getTooManyRequests().formatted(redisProperties.getResendIntervalMinutes()));
        }

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(messages.getVerification().getAccountNotFound()));

        if (account.isVerified()) {
            throw new AccountAlreadyVerifiedException(messages.getVerification().getAlreadyVerified());
        }

        sendVerificationEmail(account.getEmail());
        redisTemplate.opsForValue().set(limitKey, "lock", redisProperties.getResendIntervalMinutes(), TimeUnit.MINUTES);
    }

    @Transactional
    public void resendVerificationEmailByToken(String token) {
        TokenData tokenData = getTokenData(token);
        resendVerificationEmail(tokenData.email());
        redisTemplate.delete(redisProperties.getPrefix() + token);
    }

    public void sendVerificationEmail(String email) {
        String newToken = UUID.randomUUID().toString();
        String redisKey = redisProperties.getPrefix() + newToken;
        Instant expiresAt = Instant.now().plus(redisProperties.getTtlMinutes(), ChronoUnit.MINUTES);

        redisTemplate.opsForHash().putAll(redisKey, Map.of(
                "email", email,
                "expiresAt", expiresAt.toString()
        ));
        redisTemplate.expire(redisKey, redisProperties.getRetentionTtlMinutes(), TimeUnit.MINUTES);

        mailService.sendVerificationMail(email, newToken);
    }

    private TokenData getTokenData(String token) {
        if (token == null || token.isBlank()) {
            throw new VerificationTokenException(messages.getVerification().getTokenInvalid());
        }

        Map<Object, Object> tokenData = redisTemplate.opsForHash().entries(redisProperties.getPrefix() + token);
        Object email = tokenData.get("email");
        Object expiresAt = tokenData.get("expiresAt");

        if (!(email instanceof String emailValue) || emailValue.isBlank() || !(expiresAt instanceof String expiresAtValue)) {
            throw new VerificationTokenException(messages.getVerification().getTokenInvalid());
        }

        try {
            return new TokenData(emailValue, Instant.parse(expiresAtValue));
        } catch (DateTimeParseException e) {
            throw new VerificationTokenException(messages.getVerification().getTokenInvalid());
        }
    }

    private record TokenData(String email, Instant expiresAt) {}
}
