package com.skillroute.mapper;

import com.skillroute.dto.response.TrackedStudentResponse;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.Vacancy;
import org.springframework.stereotype.Component;

@Component
public class StudentVacancyMapper {

    public TrackedStudentResponse toTrackedStudentResponse(StudentVacancy studentVacancy) {
        StudentProfile student = studentVacancy.getStudent();
        Vacancy vacancy = studentVacancy.getVacancy();

        return TrackedStudentResponse.builder()
                .studentId(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .vacancyId(vacancy.getId())
                .vacancyName(vacancy.getName())
                .status(studentVacancy.getStatus())
                .build();
    }
}
