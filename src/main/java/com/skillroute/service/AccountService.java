package com.skillroute.service;

import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.model.Account;
import com.skillroute.openapi.model.EditPasswordRequestApi;
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
        validateEditPasswordBusinessRules(id, form.getOldPassword(), form.getNewPassword(), form.getConfirmNewPassword());
    }

    @Transactional(readOnly = true)
    public void validateEditPasswordBusinessRules(Long id, EditPasswordRequestApi form) {
        validateEditPasswordBusinessRules(id, form.getOldPassword(), form.getNewPassword(), form.getConfirmNewPassword());
    }

    private void validateEditPasswordBusinessRules(Long id, String oldPassword, String newPassword, String confirmNewPassword) {
        MessageProperties.Account accountMessages = messages.getAccount();
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(accountMessages.getNotFound()));

        Map<String, String> errors = new LinkedHashMap<>();

        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            errors.put("oldPassword", accountMessages.getCurrentPasswordInvalid());
        }

        if (!newPassword.equals(confirmNewPassword)) {
            errors.put("confirmNewPassword", accountMessages.getPasswordMismatch());
        }

        if (passwordEncoder.matches(newPassword, account.getPassword())) {
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
