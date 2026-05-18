package com.skillroute.service;

import com.skillroute.dto.response.TrackedStudentResponse;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.StudentVacancyMapper;
import com.skillroute.model.*;
import com.skillroute.model.id.StudentVacancyId;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import com.skillroute.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentVacancyService {
    private final StudentVacancyRepository studentVacancyRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final VacancyRepository vacancyRepository;
    private final MessageProperties messages;
    private final StudentVacancyMapper studentVacancyMapper;

    @Transactional(readOnly = true)
    public List<TrackedStudentResponse> getTrackedStudentsForCompany(Long companyId) {
        return studentVacancyRepository.findAllByCompanyIdAndStatusIn(
                        companyId,
                        List.of(StudentVacancyStatus.REVIEWING, StudentVacancyStatus.INTERVIEW))
                .stream()
                .map(studentVacancyMapper::toTrackedStudentResponse)
                .toList();
    }

    @Transactional
    public void applyToVacancy(Long studentId, Long vacancyId) {
        StudentVacancyId id = new StudentVacancyId(studentId, vacancyId);

        if (studentVacancyRepository.existsById(id)) {
            throw new DuplicateEntityException(messages.getVacancy().getDuplicateTracking());
        }

        StudentProfile student = studentProfileRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getVacancyNotFound()));

        StudentVacancy application = StudentVacancy.builder()
                .id(id)
                .student(student)
                .vacancy(vacancy)
                .build();

        studentVacancyRepository.save(application);
    }

    @Transactional(readOnly = true)
    public boolean isTracked(Long studentId, Long vacancyId) {
        return studentVacancyRepository.existsById(new StudentVacancyId(studentId, vacancyId));
    }
}
