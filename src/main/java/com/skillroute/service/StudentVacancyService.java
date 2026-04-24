package com.skillroute.service;

import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.id.StudentVacancyId;
import com.skillroute.repository.StudentVacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentVacancyService {
    private final StudentVacancyRepository studentVacancyRepository;

    @Transactional
    public void applyToVacancy(Long studentId, Long vacancyId) {
        StudentVacancyId id = new StudentVacancyId(studentId, vacancyId);

        if (studentVacancyRepository.existsById(id)) {
            throw new DuplicateEntityException("Студент уже отслеживает эту вакансию");
        }

        StudentVacancy application = StudentVacancy.builder()
                .id(id)
                .status(StudentVacancyStatus.SUBMITTED)
                .build();

        studentVacancyRepository.save(application);
    }
}
