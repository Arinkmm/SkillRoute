package com.skillroute.repository;

import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>, VacancyCustomRepository {
    List<Vacancy> findAllByProfileStatus(VacancyStatus vacancyStatus);

    List<Vacancy> findAllByCompanyId(Long companyId);
}
