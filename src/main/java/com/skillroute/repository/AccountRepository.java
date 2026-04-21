package com.skillroute.repository;

import com.skillroute.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByVerificationToken(String token);

    Optional<Account> findByEmail(String email);
}
