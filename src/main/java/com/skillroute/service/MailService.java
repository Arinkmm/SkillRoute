package com.skillroute.service;

import com.skillroute.exception.ServiceUnavailableException;
import com.skillroute.properties.MailProperties;
import com.skillroute.properties.MessageProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final MessageProperties messages;

    public void sendVerificationMail(String email, String verificationToken) {
        String url = mailProperties.getBaseUrl() + "/verification?token=" + verificationToken;
        sendMail(email, mailProperties.getSubject(), mailProperties.getContent(), url);
    }

    public void sendPasswordResetMail(String email, String resetToken) {
        String url = mailProperties.getBaseUrl() + "/password/reset?token=" + resetToken;
        sendMail(email, mailProperties.getPasswordResetSubject(), mailProperties.getPasswordResetContent(), url);
    }

    private void sendMail(String email, String subject, String contentTemplate, String url) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(mailProperties.getFrom(), mailProperties.getSender());
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);

            String content = contentTemplate.replace("$url", url);
            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessage);
        } catch (MailSendException | MessagingException | UnsupportedEncodingException e) {
            throw new ServiceUnavailableException(messages.getMail().getSendError());
        }
    }
}
