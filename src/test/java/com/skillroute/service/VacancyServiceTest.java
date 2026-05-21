package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.dto.request.CreateVacancyRequest;
import com.skillroute.dto.request.UpdateVacancyRequest;
import com.skillroute.exception.ResourceOwnershipException;
import com.skillroute.mapper.SpecializationMapper;
import com.skillroute.mapper.VacancyMapper;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.Vacancy;
import com.skillroute.model.VacancyProfile;
import com.skillroute.model.VacancySkill;
import com.skillroute.model.VacancyStatus;
import com.skillroute.model.WorkSchedule;
import com.skillroute.model.id.VacancySkillId;
import com.skillroute.repository.CompanyProfileRepository;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.SpecializationRepository;
import com.skillroute.repository.StudentVacancyRepository;
import com.skillroute.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CompanyProfileRepository companyProfileRepository;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private StudentVacancyRepository studentVacancyRepository;

    private VacancyService service;

    @BeforeEach
    void setUp() {
        service = new VacancyService(
                vacancyRepository,
                companyProfileRepository,
                specializationRepository,
                skillRepository,
                studentVacancyRepository,
                TestMessageProperties.create(),
                new VacancyMapper(new SpecializationMapper()));
    }

    @Test
    void getVacanciesByCompanyLoadsOpenAndInProgressVacancies() {
        CompanyProfile company = company(1L);
        Vacancy vacancy = vacancy(10L, company, VacancyStatus.OPEN);

        when(vacancyRepository.findAllByCompanyIdAndProfileStatusIn(
                1L,
                List.of(VacancyStatus.OPEN, VacancyStatus.IN_PROGRESS)))
                .thenReturn(List.of(vacancy));

        assertThat(service.getVacanciesByCompany(1L))
                .extracting("id")
                .containsExactly(10L);
    }

    @Test
    void createVacancyBuildsProfileAndRequiredSkills() {
        CompanyProfile company = company(1L);
        Specialization specialization = specialization(5L);
        Skill java = Skill.builder().id(100L).name("Java").build();
        CreateVacancyRequest request = CreateVacancyRequest.builder()
                .name("Java Developer")
                .specializationId(5L)
                .salary(150000L)
                .workSchedule(WorkSchedule.REMOTE)
                .skills(List.of(AddSkillRequest.builder().skillId(100L).level(4).build()))
                .build();

        when(companyProfileRepository.findById(1L)).thenReturn(Optional.of(company));
        when(specializationRepository.findById(5L)).thenReturn(Optional.of(specialization));
        when(skillRepository.findById(100L)).thenReturn(Optional.of(java));
        when(vacancyRepository.save(org.mockito.ArgumentMatchers.any(Vacancy.class))).thenAnswer(invocation -> {
            Vacancy saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        service.createVacancy(request, 1L);

        ArgumentCaptor<Vacancy> captor = ArgumentCaptor.forClass(Vacancy.class);
        verify(vacancyRepository).save(captor.capture());
        Vacancy saved = captor.getValue();
        assertThat(saved.getCompany()).isSameAs(company);
        assertThat(saved.getProfile().getSpecialization()).isSameAs(specialization);
        assertThat(saved.getProfile().getStatus()).isEqualTo(VacancyStatus.OPEN);
        assertThat(saved.getVacancySkills()).hasSize(1);
    }

    @Test
    void updateVacancyChecksOwnershipAndReplacesSkills() {
        CompanyProfile company = company(1L);
        Vacancy vacancy = vacancy(10L, company, VacancyStatus.OPEN);
        Specialization specialization = specialization(6L);
        Skill spring = Skill.builder().id(101L).name("Spring").build();
        UpdateVacancyRequest request = UpdateVacancyRequest.builder()
                .name("Spring Developer")
                .specializationId(6L)
                .salary(200000L)
                .workSchedule(WorkSchedule.HYBRID)
                .status(VacancyStatus.IN_PROGRESS)
                .skills(List.of(AddSkillRequest.builder().skillId(101L).level(5).build()))
                .build();

        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));
        when(specializationRepository.findById(6L)).thenReturn(Optional.of(specialization));
        when(skillRepository.findById(101L)).thenReturn(Optional.of(spring));

        service.updateVacancy(10L, request, 1L);

        assertThat(vacancy.getName()).isEqualTo("Spring Developer");
        assertThat(vacancy.getProfile().getStatus()).isEqualTo(VacancyStatus.IN_PROGRESS);
        assertThat(vacancy.getVacancySkills()).hasSize(1);
    }

    @Test
    void updateVacancyUpdatesExistingSkillLevelWithoutRecreatingDuplicateEntity() {
        CompanyProfile company = company(1L);
        Vacancy vacancy = vacancy(10L, company, VacancyStatus.OPEN);
        Skill java = Skill.builder().id(100L).name("Java").build();
        vacancy.getVacancySkills().add(VacancySkill.builder()
                .id(new VacancySkillId(10L, 100L))
                .vacancy(vacancy)
                .skill(java)
                .level(2)
                .build());
        Specialization specialization = specialization(6L);
        UpdateVacancyRequest request = UpdateVacancyRequest.builder()
                .name("Java Developer")
                .specializationId(6L)
                .salary(140000L)
                .workSchedule(WorkSchedule.REMOTE)
                .status(VacancyStatus.OPEN)
                .skills(List.of(AddSkillRequest.builder().skillId(100L).level(5).build()))
                .build();

        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));
        when(specializationRepository.findById(6L)).thenReturn(Optional.of(specialization));

        service.updateVacancy(10L, request, 1L);

        assertThat(vacancy.getVacancySkills()).hasSize(1);
        assertThat(vacancy.getVacancySkills().iterator().next().getLevel()).isEqualTo(5);
    }

    @Test
    void closeVacancyRejectsOnlyOpenApplications() {
        CompanyProfile company = company(1L);
        Vacancy vacancy = vacancy(10L, company, VacancyStatus.IN_PROGRESS);
        StudentVacancy submitted = application(1L, vacancy, StudentVacancyStatus.SUBMITTED);
        StudentVacancy accepted = application(2L, vacancy, StudentVacancyStatus.ACCEPTED);

        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));
        when(studentVacancyRepository.findAllByVacancyId(10L)).thenReturn(List.of(submitted, accepted));

        service.closeVacancy(10L, 1L);

        assertThat(vacancy.getProfile().getStatus()).isEqualTo(VacancyStatus.CLOSE);
        assertThat(submitted.getStatus()).isEqualTo(StudentVacancyStatus.REJECTED);
        assertThat(accepted.getStatus()).isEqualTo(StudentVacancyStatus.ACCEPTED);
    }

    @Test
    void deleteVacancyRejectsForeignCompany() {
        Vacancy vacancy = vacancy(10L, company(1L), VacancyStatus.OPEN);
        when(vacancyRepository.findById(10L)).thenReturn(Optional.of(vacancy));

        assertThatThrownBy(() -> service.deleteVacancy(10L, 2L))
                .isInstanceOf(ResourceOwnershipException.class)
                .hasMessage("У вас нет прав на удаление этой вакансии");
    }

    private CompanyProfile company(Long id) {
        return CompanyProfile.builder().id(id).companyName("Company").build();
    }

    private Specialization specialization(Long id) {
        return Specialization.builder()
                .id(id)
                .direction(Direction.BACKEND)
                .language(Language.JAVA)
                .build();
    }

    private Vacancy vacancy(Long id, CompanyProfile company, VacancyStatus status) {
        Vacancy vacancy = Vacancy.builder()
                .id(id)
                .company(company)
                .name("Java Developer")
                .build();
        vacancy.setProfile(VacancyProfile.builder()
                .vacancy(vacancy)
                .specialization(specialization(5L))
                .salary(150000L)
                .workSchedule(WorkSchedule.REMOTE)
                .status(status)
                .build());
        return vacancy;
    }

    private StudentVacancy application(Long studentId, Vacancy vacancy, StudentVacancyStatus status) {
        return StudentVacancy.builder()
                .student(StudentProfile.builder().id(studentId).build())
                .vacancy(vacancy)
                .status(status)
                .build();
    }
}
