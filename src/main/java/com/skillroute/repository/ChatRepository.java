package com.skillroute.repository;

import com.skillroute.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByStudentAccountIdAndCompanyAccountId(Long studentId, Long companyId);

    @Query("SELECT c FROM Chat c WHERE c.student.id = :id OR c.company.id = :id")
    List<Chat> findAllById(@Param("id") Long accountId);
}