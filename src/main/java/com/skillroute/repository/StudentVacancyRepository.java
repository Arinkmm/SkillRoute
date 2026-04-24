package com.skillroute.repository;

import com.skillroute.model.StudentVacancy;
import com.skillroute.model.id.StudentVacancyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentVacancyRepository extends JpaRepository<StudentVacancy, StudentVacancyId> {
}
