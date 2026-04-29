package com.skillroute.service;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.dto.request.RegistrationRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.InvalidPasswordException;
import com.skillroute.exception.UserAlreadyExistsException;
import com.skillroute.model.Account;
import com.skillroute.properties.MailProperties;
import com.skillroute.repository.AccountRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final ApplicationEventPublisher eventPublisher;

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
                .verificationToken(UUID.randomUUID().toString())
                .build();

        Account savedAccount = accountRepository.save(account);
        sendVerificationMail(form, account.getVerificationToken());

        eventPublisher.publishEvent(new AccountRegisteredEvent(savedAccount));
    }

    @Transactional
    public boolean verifyUser(String token) {
        return accountRepository.findByVerificationToken(token).map(account-> {
            account.setVerified(true);
            account.setVerificationToken(null);
            accountRepository.save(account);
            return true;
        }).orElse(false);
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

    private void sendVerificationMail(RegistrationRequest form, String verificationToken) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String content = mailProperties.getContent();
            mimeMessageHelper.setFrom(mailProperties.getFrom(), mailProperties.getSender());
            mimeMessageHelper.setTo(form.getEmail());
            mimeMessageHelper.setSubject(mailProperties.getSubject());

            content = content.replace("$url", mailProperties.getBaseUrl() + "/verification?token=" + verificationToken);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
