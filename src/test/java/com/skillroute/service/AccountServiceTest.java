package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.EditPasswordRequest;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.model.Account;
import com.skillroute.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService(accountRepository, passwordEncoder, TestMessageProperties.create());
    }

    @Test
    void validateEditPasswordBusinessRulesCollectsFieldErrors() {
        Account account = Account.builder().id(1L).password("encoded-old").build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrong-old", "encoded-old")).thenReturn(false);
        when(passwordEncoder.matches("new-password", "encoded-old")).thenReturn(false);

        EditPasswordRequest request = EditPasswordRequest.builder()
                .oldPassword("wrong-old")
                .newPassword("new-password")
                .confirmNewPassword("other-password")
                .build();

        assertThatThrownBy(() -> service.validateEditPasswordBusinessRules(1L, request))
                .isInstanceOfSatisfying(FieldValidationException.class, exception -> {
                    assertThat(exception).hasMessage("Не удалось обновить пароль");
                    assertThat(exception.getFields())
                            .containsEntry("oldPassword", "Текущий пароль указан неверно")
                            .containsEntry("confirmNewPassword", "Пароли не совпадают");
                });
    }

    @Test
    void editPasswordEncodesAndSavesNewPassword() {
        Account account = Account.builder().id(1L).password("encoded-old").build();
        EditPasswordRequest request = EditPasswordRequest.builder()
                .oldPassword("old-password")
                .newPassword("new-password")
                .confirmNewPassword("new-password")
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "encoded-old")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        service.editPassword(1L, request);

        assertThat(account.getPassword()).isEqualTo("encoded-new");
        verify(accountRepository).save(account);
    }
}
