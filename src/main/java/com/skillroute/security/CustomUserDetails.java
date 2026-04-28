package com.skillroute.security;

import com.skillroute.model.Account;
import com.skillroute.model.Role;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomUserDetails extends User {
    private final Long id;
    private final Role role;
    private final Account account;

    public CustomUserDetails(Account account) {
        super(account.getEmail(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name())));
        this.id = account.getId();
        this.role = account.getRole();
        this.account = account;
    }
}