package com.skillroute.mapper;

import com.skillroute.dto.response.CompanyStudentDetailsResponse;
import com.skillroute.dto.response.CompanyStudentResponse;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.StudentVacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyStudentMapper {
    private final StudentSkillMapper studentSkillMapper;
    private final SpecializationMapper specializationMapper;

    public CompanyStudentResponse toTrackedResponse(StudentVacancy studentVacancy) {
        StudentProfile student = studentVacancy.getStudent();

        return baseResponse(student)
                .vacancyId(studentVacancy.getVacancy().getId())
                .vacancyName(studentVacancy.getVacancy().getName())
                .status(studentVacancy.getStatus())
                .build();
    }

    public CompanyStudentResponse toCatalogResponse(StudentProfile student) {
        return baseResponse(student).build();
    }

    public CompanyStudentDetailsResponse toDetailsResponse(StudentProfile student, List<StudentSkillResponse> skills) {
        return CompanyStudentDetailsResponse.builder()
                .studentId(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .specialization(specializationMapper.toResponse(student.getSpecialization()))
                .githubUrl(student.getGithubUrl())
                .bio(student.getBio())
                .skills(skills)
                .build();
    }

    public StudentSkillResponse toSkillResponse(StudentSkill studentSkill) {
        return studentSkillMapper.toResponse(studentSkill);
    }

    private CompanyStudentResponse.CompanyStudentResponseBuilder baseResponse(StudentProfile student) {
        return CompanyStudentResponse.builder()
                .studentId(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .specialization(specializationMapper.toResponse(student.getSpecialization()))
                .skillCount(student.getStudentSkills().size())
                .confirmedSkillCount(countConfirmedSkills(student));
    }

    private int countConfirmedSkills(StudentProfile student) {
        return Math.toIntExact(student.getStudentSkills().stream()
                .filter(StudentSkill::isConfirmedByGitHub)
                .count());
    }
}
