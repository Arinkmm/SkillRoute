package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.mapper.CompanyStudentMapper;
import com.skillroute.mapper.SpecializationMapper;
import com.skillroute.mapper.StudentSkillMapper;
import com.skillroute.mapper.VacancyMapper;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyProfile;
import com.skillroute.model.VacancyStatus;
import com.skillroute.model.WorkSchedule;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.properties.VacancyCatalogProperties;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import com.skillroute.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogAndCompanyStudentServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private StudentVacancyRepository studentVacancyRepository;

    private StudentVacancyCatalogService catalogService;
    private CompanyStudentService companyStudentService;

    @BeforeEach
    void setUp() {
        VacancyCatalogProperties catalogProperties = new VacancyCatalogProperties();
        catalogProperties.setHighDemandLimit(3);
        catalogService = new StudentVacancyCatalogService(
                vacancyRepository,
                studentProfileRepository,
                catalogProperties,
                TestMessageProperties.create(),
                new VacancyMapper(new SpecializationMapper()));
        companyStudentService = new CompanyStudentService(
                studentProfileRepository,
                studentVacancyRepository,
                new CompanyStudentMapper(new StudentSkillMapper()),
                TestMessageProperties.create());
    }

    @Test
    void recommendedVacanciesUseStudentSpecializationWhenFilterHasNone() {
        StudentProfile student = StudentProfile.builder()
                .id(1L)
                .specialization(specialization(9L))
                .build();
        Vacancy vacancy = vacancy(10L, "Frontend", specialization(9L));
        VacancyFilter filter = VacancyFilter.builder().minSalary(100000).build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(vacancyRepository.findFilteredActiveExcludingFollowed(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.argThat(argument -> argument.getSpecializationId().equals(9L)
                        && argument.getMinSalary().equals(100000))))
                .thenReturn(List.of(vacancy));

        assertThat(catalogService.getRecommendedVacanciesForStudent(1L, filter))
                .extracting("id")
                .containsExactly(10L);
    }

    @Test
    void catalogMethodsDelegateToRepositoryWithConfiguredLimit() {
        Vacancy vacancy = vacancy(10L, "Backend", specialization(5L));
        VacancyFilter filter = VacancyFilter.builder().schedule(WorkSchedule.REMOTE).build();

        when(vacancyRepository.findFollowedActiveByStudentId(1L)).thenReturn(List.of(vacancy));
        when(vacancyRepository.findFilteredActiveExcludingFollowed(1L, filter)).thenReturn(List.of(vacancy));
        when(vacancyRepository.findHighDemandVacanciesExcludingFollowed(1L, 3)).thenReturn(List.of(vacancy));
        when(vacancyRepository.findAllActiveExcludingFollowed(1L)).thenReturn(List.of(vacancy));

        assertThat(catalogService.getFollowedVacancies(1L)).hasSize(1);
        assertThat(catalogService.getFilteredVacanciesForStudent(1L, filter)).hasSize(1);
        assertThat(catalogService.getHighDemandVacanciesForStudent(1L)).hasSize(1);
        assertThat(catalogService.getAllActiveForStudent(1L)).hasSize(1);
        assertThat(catalogService.isFilterApplied(filter)).isTrue();
        assertThat(catalogService.isFilterApplied(new VacancyFilter())).isFalse();
    }

    @Test
    void trackedStudentsAreDeduplicatedByBestApplicationAndAvailableStudentsExcludeTracked() {
        StudentProfile maria = student(1L, "Maria");
        StudentProfile anna = student(2L, "Anna");
        Vacancy firstVacancy = vacancy(10L, "Junior", specialization(5L));
        Vacancy secondVacancy = vacancy(11L, "Middle", specialization(5L));
        StudentVacancy lowerPriority = application(maria, firstVacancy, StudentVacancyStatus.REVIEWING);
        StudentVacancy higherPriority = application(maria, secondVacancy, StudentVacancyStatus.INTERVIEW);

        when(studentVacancyRepository.findAllByCompanyIdAndStatusIn(20L, List.of(
                StudentVacancyStatus.REVIEWING,
                StudentVacancyStatus.INTERVIEW)))
                .thenReturn(List.of(lowerPriority, higherPriority));
        when(studentProfileRepository.findAllByFirstNameIsNotNullAndLastNameIsNotNullOrderByFirstNameAscLastNameAsc())
                .thenReturn(List.of(anna, maria));

        var tracked = companyStudentService.getTrackedStudents(20L);
        var available = companyStudentService.getAvailableStudents(20L);

        assertThat(tracked).hasSize(1);
        assertThat(tracked.getFirst().getVacancyId()).isEqualTo(11L);
        assertThat(available)
                .extracting("studentId")
                .containsExactly(2L);
    }

    @Test
    void studentDetailsIncludeSortedSkills() {
        StudentProfile maria = student(1L, "Maria");
        maria.setStudentSkills(Set.of(
                skill(maria, 1L, "Spring", false),
                skill(maria, 2L, "Java", true)));

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(maria));

        var details = companyStudentService.getStudentDetails(1L);

        assertThat(details.getSkills())
                .extracting("name")
                .containsExactly("Java", "Spring");
    }

    private StudentProfile student(Long id, String firstName) {
        return StudentProfile.builder()
                .id(id)
                .firstName(firstName)
                .lastName("Ivanova")
                .specialization(specialization(5L))
                .build();
    }

    private StudentSkill skill(StudentProfile student, Long skillId, String name, boolean confirmed) {
        Skill skill = Skill.builder().id(skillId).name(name).build();
        return StudentSkill.builder()
                .id(new StudentSkillId(student.getId(), skillId))
                .student(student)
                .skill(skill)
                .level(3)
                .isConfirmedByGitHub(confirmed)
                .build();
    }

    private Specialization specialization(Long id) {
        return Specialization.builder()
                .id(id)
                .direction(Direction.FRONTEND)
                .language(Language.TYPESCRIPT)
                .build();
    }

    private Vacancy vacancy(Long id, String name, Specialization specialization) {
        Vacancy vacancy = Vacancy.builder()
                .id(id)
                .name(name)
                .company(CompanyProfile.builder().id(20L).companyName("Company").build())
                .build();
        vacancy.setProfile(VacancyProfile.builder()
                .vacancy(vacancy)
                .specialization(specialization)
                .status(VacancyStatus.OPEN)
                .salary(150000L)
                .workSchedule(WorkSchedule.REMOTE)
                .build());
        return vacancy;
    }

    private StudentVacancy application(StudentProfile student, Vacancy vacancy, StudentVacancyStatus status) {
        return StudentVacancy.builder()
                .student(student)
                .vacancy(vacancy)
                .status(status)
                .build();
    }
}
