package com.skillroute.repository;

import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.model.Vacancy;
import java.util.List;

public interface VacancyCustomRepository {
    List<Vacancy> findRecommendedWithSubquery(Long studentId, VacancyFilter filter);
    
    List<Vacancy> findHighDemandVacancies(int minSkillsCount);
}