package com.skillroute.repository;

import com.skillroute.dto.request.ApplicantFilter;
import com.skillroute.model.StudentProfile;
import java.util.List;

public interface StudentProfileCustomRepository {
    List<StudentProfile> findApplicantsByVacancyAndFilter(Long vacancyId, ApplicantFilter filter);
}