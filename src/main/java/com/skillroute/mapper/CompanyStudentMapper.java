package com.skillroute.mapper;

import com.skillroute.dto.response.CompanyStudentDetailsResponse;
import com.skillroute.dto.response.CompanyStudentResponse;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Specialization;
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
                .specializationName(getSpecializationName(student))
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
                .specializationName(getSpecializationName(student))
                .skillCount(student.getStudentSkills().size())
                .confirmedSkillCount(countConfirmedSkills(student));
    }

    private int countConfirmedSkills(StudentProfile student) {
        return Math.toIntExact(student.getStudentSkills().stream()
                .filter(StudentSkill::isConfirmedByGitHub)
                .count());
    }

    private String getSpecializationName(StudentProfile student) {
        Specialization specialization = student.getSpecialization();
        if (specialization == null) {
            return null;
        }

        String direction = formatDirection(specialization.getDirection());
        String language = formatLanguage(specialization.getLanguage());

        if (direction == null) {
            return language;
        }
        if (language == null) {
            return direction;
        }

        return direction + " / " + language;
    }

    private String formatDirection(Direction direction) {
        if (direction == null) {
            return null;
        }

        return switch (direction) {
            case BACKEND -> "Backend";
            case FRONTEND -> "Frontend";
            case FULLSTACK -> "Fullstack";
            case MOBILE -> "Mobile";
            case DATA -> "Data";
            case DEVOPS -> "DevOps";
            case QA -> "QA";
        };
    }

    private String formatLanguage(Language language) {
        if (language == null) {
            return null;
        }

        return switch (language) {
            case JAVA -> "Java";
            case JAVASCRIPT -> "JavaScript";
            case TYPESCRIPT -> "TypeScript";
            case PYTHON -> "Python";
            case CSHARP -> "C#";
            case KOTLIN -> "Kotlin";
            case SWIFT -> "Swift";
            case GO -> "Go";
            case CPP -> "C++";
            case PHP -> "PHP";
            case RUBY -> "Ruby";
            case RUST -> "Rust";
            case SQL -> "SQL";
            case CLOUD -> "Cloud";
        };
    }
}
