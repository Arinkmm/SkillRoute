package com.skillroute.repository;

import com.skillroute.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    List<CompanyProfile> findAll();

    @Query("SELECT company from CompanyProfile company WHERE company.isConfirmed = true")
    List<CompanyProfile> findAllConfirmed();

    Optional<CompanyProfile> findById(Long id);
}
