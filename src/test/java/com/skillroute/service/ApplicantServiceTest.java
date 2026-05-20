package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.ApplicantFilter;
import com.skillroute.mapper.ApplicantMapper;
import com.skillroute.mapper.StudentSkillMapper;
import com.skillroute.exception.ResourceOwnershipException;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyProfile;
import com.skillroute.model.VacancySkill;
import com.skillroute.model.VacancyStatus;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.model.id.VacancySkillId;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentVacancyRepository;
import com.skillroute.repository.VacancyProfileRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceTest {
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private StudentVacancyRepository studentVacancyRepository;
    @Mock
    private VacancyProfileRepository vacancyProfileRepository;
    @Mock
    private VacancyRepository vacancyRepository;

    private ApplicantService service;

    @BeforeEach
    void setUp() {
        service = new ApplicantService(
                studentProfileRepository,
                studentVacancyRepository,
                vacancyProfileRepository,
                new MatchingService(),
                vacancyRepository,
                TestMessageProperties.create(),
                new ApplicantMapper(),
                new StudentSkillMapper());
    }

    @Test
    void startReviewingMovesApplicationAndVacancyToInProgress() {
        StudentVacancy application = StudentVacancy.builder()
                .status(StudentVacancyStatus.SUBMITTED)
                .build();
        VacancyProfile profile = VacancyProfile.builder()
                .status(VacancyStatus.OPEN)
                .build();

        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 10L)).thenReturn(Optional.of(application));
        when(vacancyProfileRepository.findById(10L)).thenReturn(Optional.of(profile));

        service.startReviewing(1L, 10L);

        assertThat(application.getStatus()).isEqualTo(StudentVacancyStatus.REVIEWING);
        assertThat(profile.getStatus()).isEqualTo(VacancyStatus.IN_PROGRESS);
    }

    @Test
    void startReviewingIgnoresTerminalApplications() {
        StudentVacancy application = StudentVacancy.builder()
                .status(StudentVacancyStatus.REJECTED)
                .build();

        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 10L)).thenReturn(Optional.of(application));

        service.startReviewing(1L, 10L);

        assertThat(application.getStatus()).isEqualTo(StudentVacancyStatus.REJECTED);
        verify(vacancyProfileRepository, never()).findById(10L);
    }

    @Test
    void acceptStudentAcceptsOneRejectsOtherOpenApplicationsAndClosesVacancy() {
        CompanyProfile company = CompanyProfile.builder().id(20L).build();
        Vacancy vacancy = Vacancy.builder().id(10L).company(company).build();
        VacancyProfile profile = VacancyProfile.builder()
                .status(VacancyStatus.IN_PROGRESS)
                .build();
        StudentVacancy accepted = application(1L, vacancy, StudentVacancyStatus.INTERVIEW);
        StudentVacancy otherOpen = application(2L, vacancy, StudentVacancyStatus.REVIEWING);
        StudentVacancy alreadyRejected = application(3L, vacancy, StudentVacancyStatus.REJECTED);

        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 10L)).thenReturn(Optional.of(accepted));
        when(studentVacancyRepository.findAllByVacancyId(10L)).thenReturn(List.of(accepted, otherOpen, alreadyRejected));
        when(vacancyProfileRepository.findById(10L)).thenReturn(Optional.of(profile));

        service.acceptStudent(1L, 10L, 20L);

        assertThat(accepted.getStatus()).isEqualTo(StudentVacancyStatus.ACCEPTED);
        assertThat(otherOpen.getStatus()).isEqualTo(StudentVacancyStatus.REJECTED);
        assertThat(alreadyRejected.getStatus()).isEqualTo(StudentVacancyStatus.REJECTED);
        assertThat(profile.getStatus()).isEqualTo(VacancyStatus.CLOSE);
    }

    @Test
    void getStudentGapBuildsMissingSkillGapsAndStudentSkills() {
        Skill java = Skill.builder().id(10L).name("Java").build();
        Skill spring = Skill.builder().id(11L).name("Spring").build();
        StudentProfile student = StudentProfile.builder().id(1L).firstName("Maria").build();
        student.setStudentSkills(Set.of(StudentSkill.builder()
                .id(new StudentSkillId(1L, 10L))
                .student(student)
                .skill(java)
                .level(2)
                .isConfirmedByGitHub(true)
                .build()));
        Vacancy vacancy = Vacancy.builder().id(100L).build();
        vacancy.setVacancySkills(Set.of(
                vacancySkill(vacancy, java, 4),
                vacancySkill(vacancy, spring, 2)));
        StudentVacancy application = application(1L, vacancy, StudentVacancyStatus.INTERVIEW);

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(vacancy));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 100L)).thenReturn(Optional.of(application));

        var response = service.getStudentGap(1L, 100L);

        assertThat(response.getStatus()).isEqualTo(StudentVacancyStatus.INTERVIEW);
        assertThat(response.getMatchPercentage()).isEqualTo(0.0);
        assertThat(response.getTotalGapLevel()).isEqualTo(4);
        assertThat(response.getGaps()).extracting("skillName").containsExactlyInAnyOrder("Java", "Spring");
        assertThat(response.getSkills()).extracting("name").containsExactly("Java");
    }

    @Test
    void getFilteredApplicantsFiltersByMatchAndSortsDescending() {
        Skill java = Skill.builder().id(10L).name("Java").build();
        Vacancy vacancy = Vacancy.builder().id(100L).build();
        vacancy.setVacancySkills(Set.of(vacancySkill(vacancy, java, 4)));
        StudentProfile strong = studentWithSkill(1L, "Anna", java, 4);
        StudentProfile weak = studentWithSkill(2L, "Maria", java, 1);
        ApplicantFilter filter = ApplicantFilter.builder()
                .minMatch(50)
                .maxGap(0)
                .build();

        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(vacancy));
        when(studentProfileRepository.findApplicantsByVacancyAndFilter(100L, filter)).thenReturn(List.of(weak, strong));
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(strong));
        when(studentProfileRepository.findById(2L)).thenReturn(Optional.of(weak));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 100L)).thenReturn(Optional.empty());
        when(studentVacancyRepository.findByStudentIdAndVacancyId(2L, 100L)).thenReturn(Optional.empty());

        var applicants = service.getFilteredApplicants(100L, filter);

        assertThat(applicants)
                .extracting("studentId")
                .containsExactly(1L);
    }

    @Test
    void startInterviewingUpdatesOnlyNonTerminalApplications() {
        StudentVacancy active = StudentVacancy.builder().status(StudentVacancyStatus.REVIEWING).build();
        StudentVacancy rejected = StudentVacancy.builder().status(StudentVacancyStatus.REJECTED).build();

        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 10L)).thenReturn(Optional.of(active));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(2L, 10L)).thenReturn(Optional.of(rejected));

        service.startInterviewing(1L, 10L);
        service.startInterviewing(2L, 10L);

        assertThat(active.getStatus()).isEqualTo(StudentVacancyStatus.INTERVIEW);
        assertThat(rejected.getStatus()).isEqualTo(StudentVacancyStatus.REJECTED);
    }

    @Test
    void rejectStudentRequiresCompanyOwnership() {
        Vacancy vacancy = Vacancy.builder().id(10L).company(CompanyProfile.builder().id(20L).build()).build();
        StudentVacancy application = application(1L, vacancy, StudentVacancyStatus.REVIEWING);

        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));
        when(studentVacancyRepository.findByStudentIdAndVacancyId(1L, 10L)).thenReturn(Optional.of(application));

        service.rejectStudent(1L, 10L, 20L);

        assertThat(application.getStatus()).isEqualTo(StudentVacancyStatus.REJECTED);
        assertThatThrownBy(() -> service.rejectStudent(1L, 10L, 30L))
                .isInstanceOf(ResourceOwnershipException.class)
                .hasMessage("У вас нет прав на редактирование этой вакансии");
    }

    private StudentVacancy application(Long studentId, Vacancy vacancy, StudentVacancyStatus status) {
        return StudentVacancy.builder()
                .student(StudentProfile.builder().id(studentId).build())
                .vacancy(vacancy)
                .status(status)
                .build();
    }

    private VacancySkill vacancySkill(Vacancy vacancy, Skill skill, int level) {
        return VacancySkill.builder()
                .id(new VacancySkillId(vacancy.getId(), skill.getId()))
                .vacancy(vacancy)
                .skill(skill)
                .level(level)
                .build();
    }

    private StudentProfile studentWithSkill(Long id, String firstName, Skill skill, int level) {
        StudentProfile student = StudentProfile.builder()
                .id(id)
                .firstName(firstName)
                .build();
        student.setStudentSkills(Set.of(StudentSkill.builder()
                .id(new StudentSkillId(id, skill.getId()))
                .student(student)
                .skill(skill)
                .level(level)
                .build()));
        return student;
    }
}
