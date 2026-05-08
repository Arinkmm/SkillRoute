package com.skillroute.service;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.*;
import com.skillroute.model.Account;
import com.skillroute.properties.MailProperties;
import com.skillroute.properties.RedisProperties;
import com.skillroute.repository.AccountRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

    @Transactional
    public void register(RegistrationRequest form) {
        if (accountRepository.existsByEmail(form.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new InvalidPasswordException("Пароли не совпадают");
        }

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

        sendVerificationMail(form.getEmail(), token);

        eventPublisher.publishEvent(new AccountRegisteredEvent(savedAccount));
    }

    @Transactional
    public boolean verifyUser(String token) {
        String redisKey = redisProperties.getPrefix() + token;

        String email = redisTemplate.opsForValue().get(redisKey);

        if (email == null) {
            return false;
        }

        return accountRepository.findByEmail(email).map(account -> {
            account.setVerified(true);
            accountRepository.save(account);
            redisTemplate.delete(redisKey);
            return true;
        }).orElse(false);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        String limitKey = redisProperties.getResendLimitPrefix() + email;
        if (redisTemplate.hasKey(limitKey)) {
            throw new TooManyRequestsException("Повторное письмо можно отправить только через " + redisProperties.getResendIntervalMinutes() + " мин.");
        }

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Аккаунт не найден"));

        if (account.isVerified()) {
            throw new AccountAlreadyVerifiedException("Аккаунт уже подтвержден");
        }

        String newToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                redisProperties.getPrefix() + newToken,
                email,
                redisProperties.getTtlMinutes(),
                TimeUnit.MINUTES
        );

        redisTemplate.opsForValue().set(limitKey, "lock", redisProperties.getResendIntervalMinutes(), TimeUnit.MINUTES);

        sendVerificationMail(email, newToken);
    }

    @Transactional
    public void editPassword(Long id, EditPasswordRequest form) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Аккаунт не найден"));
        if (!passwordEncoder.matches(form.getOldPassword(), account.getPassword())) {
            throw new InvalidPasswordException("Текущий пароль введен неверно");
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            throw new InvalidPasswordException("Новый пароль и подтверждение не совпадают");
        }

        if (passwordEncoder.matches(form.getNewPassword(), account.getPassword())) {
            throw new InvalidPasswordException("Новый пароль не может быть таким же, как старый");
        }

        String encodedPassword = passwordEncoder.encode(form.getNewPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);
    }

    private void sendVerificationMail(String email, String verificationToken) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String content = mailProperties.getContent();

            mimeMessageHelper.setFrom(mailProperties.getFrom(), mailProperties.getSender());
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(mailProperties.getSubject());

            content = content.replace("$url", mailProperties.getBaseUrl() + "/verification?token=" + verificationToken);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new ServiceUnavailableException("Ошибка при отправке письма. Пожалуйста, попробуйте позже");        }
    }
}