package com.skillroute.service;

import com.skillroute.dto.PasswordChangeDto;
import com.skillroute.dto.RegistrationDto;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.InvalidPasswordException;
import com.skillroute.exception.UserAlreadyExistsException;
import com.skillroute.model.Account;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Role;
import com.skillroute.model.StudentProfile;
import com.skillroute.properties.MailProperties;
import com.skillroute.repository.AccountRepository;
import com.skillroute.repository.CompanyProfileRepository;
import com.skillroute.repository.StudentProfileRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    @Transactional
    public void register(RegistrationDto form) {
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

        if (form.getRole() == Role.STUDENT) {
            StudentProfile student = new StudentProfile();
            student.setAccount(savedAccount);
            studentProfileRepository.save(student);
        } else if (form.getRole() == Role.COMPANY) {
            CompanyProfile company = new CompanyProfile();
            company.setAccount(savedAccount);
            companyProfileRepository.save(company);
        }
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

    @Transactional(readOnly = true)
    public Account getAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Аккаунт с email " + email + " не найден"));
    }

    @Transactional
    public void changePassword(Long id, PasswordChangeDto form) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account with id " + id + " not found"));
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

    private void sendVerificationMail(RegistrationDto form, String verificationToken) {
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
