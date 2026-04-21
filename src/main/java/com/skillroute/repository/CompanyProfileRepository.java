package com.skillroute.repository;

import com.skillroute.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    List<CompanyProfile> findAll();
}
