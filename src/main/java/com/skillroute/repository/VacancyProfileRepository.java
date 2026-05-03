package com.skillroute.repository;

import com.skillroute.model.VacancyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyProfileRepository extends JpaRepository<VacancyProfile, Long> {
}
