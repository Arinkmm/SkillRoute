package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.mapper.StudentVacancyMapper;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.id.StudentVacancyId;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import com.skillroute.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentVacancyServiceTest {
    @Mock
    private StudentVacancyRepository studentVacancyRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private VacancyRepository vacancyRepository;

    private StudentVacancyService service;

    @BeforeEach
    void setUp() {
        service = new StudentVacancyService(
                studentVacancyRepository,
                studentProfileRepository,
                vacancyRepository,
                TestMessageProperties.create(),
                new StudentVacancyMapper());
    }

    @Test
    void applyToVacancyCreatesSubmittedApplicationWhenNoPreviousApplicationExists() {
        StudentProfile student = StudentProfile.builder().id(1L).build();
        Vacancy vacancy = Vacancy.builder().id(10L).name("Java").build();

        when(studentVacancyRepository.findById(new StudentVacancyId(1L, 10L))).thenReturn(Optional.empty());
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));

        service.applyToVacancy(1L, 10L);

        ArgumentCaptor<StudentVacancy> captor = ArgumentCaptor.forClass(StudentVacancy.class);
        verify(studentVacancyRepository).save(captor.capture());

        StudentVacancy saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(new StudentVacancyId(1L, 10L));
        assertThat(saved.getStatus()).isEqualTo(StudentVacancyStatus.SUBMITTED);
        assertThat(saved.getStudent()).isSameAs(student);
        assertThat(saved.getVacancy()).isSameAs(vacancy);
    }

    @Test
    void applyToVacancyReopensTerminalApplicationInsteadOfCreatingDuplicate() {
        StudentVacancy existing = StudentVacancy.builder()
                .id(new StudentVacancyId(1L, 10L))
                .status(StudentVacancyStatus.REJECTED)
                .build();

        when(studentVacancyRepository.findById(new StudentVacancyId(1L, 10L))).thenReturn(Optional.of(existing));

        service.applyToVacancy(1L, 10L);

        assertThat(existing.getStatus()).isEqualTo(StudentVacancyStatus.SUBMITTED);
        verify(studentVacancyRepository, never()).save(any(StudentVacancy.class));
        verifyNoInteractions(studentProfileRepository, vacancyRepository);
    }

    @Test
    void applyToVacancyRejectsAlreadyActiveApplication() {
        StudentVacancy existing = StudentVacancy.builder()
                .id(new StudentVacancyId(1L, 10L))
                .status(StudentVacancyStatus.INTERVIEW)
                .build();

        when(studentVacancyRepository.findById(new StudentVacancyId(1L, 10L))).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.applyToVacancy(1L, 10L))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage("Вакансия уже отслеживается");
    }

    @Test
    void isTrackedReturnsTrueOnlyForActiveStatuses() {
        when(studentVacancyRepository.findById(new StudentVacancyId(1L, 10L)))
                .thenReturn(Optional.of(application(StudentVacancyStatus.SUBMITTED)));
        when(studentVacancyRepository.findById(new StudentVacancyId(1L, 11L)))
                .thenReturn(Optional.of(application(StudentVacancyStatus.ACCEPTED)));
        when(studentVacancyRepository.findById(new StudentVacancyId(1L, 12L)))
                .thenReturn(Optional.empty());

        assertThat(service.isTracked(1L, 10L)).isTrue();
        assertThat(service.isTracked(1L, 11L)).isFalse();
        assertThat(service.isTracked(1L, 12L)).isFalse();
    }

    private StudentVacancy application(StudentVacancyStatus status) {
        return StudentVacancy.builder().status(status).build();
    }
}
