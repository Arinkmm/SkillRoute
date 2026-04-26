package com.skillroute.repository;

import com.skillroute.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    List<CompanyProfile> findAll();
    Optional<CompanyProfile> findById(Long id);
}
