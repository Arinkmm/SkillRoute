package com.skillroute.repository;

import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>, VacancyCustomRepository {
    List<Vacancy> findAllByProfileStatus(VacancyStatus vacancyStatus);

    List<Vacancy> findAllByCompanyId(Long companyId);

    List<Vacancy> findAllByCompanyIdAndProfileStatus(Long companyId, VacancyStatus vacancyStatus);
}
