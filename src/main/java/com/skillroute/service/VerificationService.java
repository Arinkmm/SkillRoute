package com.skillroute.service;

import com.skillroute.exception.AccountAlreadyVerifiedException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.TooManyRequestsException;
import com.skillroute.exception.VerificationTokenException;
import com.skillroute.model.Account;
import com.skillroute.properties.MessageProperties;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String email = getEmailByToken(token);
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

        String newToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                redisProperties.getPrefix() + newToken,
                email,
                redisProperties.getTtlMinutes(),
                TimeUnit.MINUTES
        );

        redisTemplate.opsForValue().set(limitKey, "lock", redisProperties.getResendIntervalMinutes(), TimeUnit.MINUTES);
        mailService.sendVerificationMail(email, newToken);
    }

    private String getEmailByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new VerificationTokenException(messages.getVerification().getTokenInvalid());
        }

        String email = redisTemplate.opsForValue().get(redisProperties.getPrefix() + token);
        if (email == null) {
            throw new VerificationTokenException(messages.getVerification().getTokenInvalid());
        }

        return email;
    }
}
