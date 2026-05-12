package com.skillroute.service;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.model.Account;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageProperties messages;

    @Transactional(readOnly = true)
    public void validateEditPasswordBusinessRules(Long id, EditPasswordRequest form) {
        MessageProperties.Account accountMessages = messages.getAccount();
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(accountMessages.getNotFound()));

        Map<String, String> errors = new LinkedHashMap<>();

        if (!passwordEncoder.matches(form.getOldPassword(), account.getPassword())) {
            errors.put("oldPassword", accountMessages.getCurrentPasswordInvalid());
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            errors.put("confirmNewPassword", accountMessages.getPasswordMismatch());
        }

        if (passwordEncoder.matches(form.getNewPassword(), account.getPassword())) {
            errors.put("newPassword", accountMessages.getSamePassword());
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(accountMessages.getPasswordUpdateError(), errors);
        }
    }

    @Transactional
    public void editPassword(Long id, EditPasswordRequest form) {
        validateEditPasswordBusinessRules(id, form);

        MessageProperties.Account accountMessages = messages.getAccount();
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(accountMessages.getNotFound()));

        account.setPassword(passwordEncoder.encode(form.getNewPassword()));
        accountRepository.save(account);
    }
}
