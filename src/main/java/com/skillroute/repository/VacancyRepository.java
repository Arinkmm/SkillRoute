package com.skillroute.repository;

import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VacancyRepository extends CrudRepository<Vacancy, Long> {
    List<Vacancy> findAllByProfileStatus(VacancyStatus vacancyStatus);

    List<Vacancy> findAllByProfileSpecializationIdAndProfileStatus(Long specializationId, VacancyStatus profileStatus);

    List<Vacancy> findAllByCompanyId(Long companyId);
}
