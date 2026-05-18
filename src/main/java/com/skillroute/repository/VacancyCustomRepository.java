package com.skillroute.repository;

import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.model.Vacancy;
import java.util.List;

public interface VacancyCustomRepository {
    List<Vacancy> findFilteredActiveExcludingFollowed(Long studentId, VacancyFilter filter);

    List<Vacancy> findFollowedActiveByStudentId(Long studentId);

    List<Vacancy> findAllActiveExcludingFollowed(Long studentId);

    List<Vacancy> findHighDemandVacanciesExcludingFollowed(Long studentId, int minSkillsCount);
}
