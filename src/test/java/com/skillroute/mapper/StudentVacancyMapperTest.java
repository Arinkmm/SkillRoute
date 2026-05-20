package com.skillroute.mapper;

import com.skillroute.dto.response.TrackedStudentResponse;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentVacancyMapperTest {
    private final StudentVacancyMapper mapper = new StudentVacancyMapper();

    @Test
    void mapsTrackedStudentResponse() {
        StudentVacancy application = StudentVacancy.builder()
                .student(StudentProfile.builder()
                        .id(1L)
                        .firstName("Maria")
                        .lastName("Ivanova")
                        .build())
                .vacancy(Vacancy.builder()
                        .id(10L)
                        .name("Java Developer")
                        .build())
                .status(StudentVacancyStatus.REVIEWING)
                .build();

        TrackedStudentResponse response = mapper.toTrackedStudentResponse(application);

        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Maria");
        assertThat(response.getLastName()).isEqualTo("Ivanova");
        assertThat(response.getVacancyId()).isEqualTo(10L);
        assertThat(response.getVacancyName()).isEqualTo("Java Developer");
        assertThat(response.getStatus()).isEqualTo(StudentVacancyStatus.REVIEWING);
    }
}
