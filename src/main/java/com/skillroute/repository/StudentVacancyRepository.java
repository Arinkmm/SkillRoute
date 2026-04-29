package com.skillroute.repository;

import com.skillroute.model.StudentVacancy;
import com.skillroute.model.id.StudentVacancyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentVacancyRepository extends JpaRepository<StudentVacancy, StudentVacancyId> {
    Optional<StudentVacancy> findByStudentIdAndVacancyId(Long studentId, Long vacancyId);
}
