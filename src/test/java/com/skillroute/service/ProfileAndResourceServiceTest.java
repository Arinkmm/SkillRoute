package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.AddResourceRequest;
import com.skillroute.dto.request.UpdateCompanyRequest;
import com.skillroute.dto.request.UpdateStudentRequest;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.mapper.CompanyProfileMapper;
import com.skillroute.mapper.SpecializationMapper;
import com.skillroute.mapper.StudentProfileMapper;
import com.skillroute.model.Account;
import com.skillroute.model.CompanyProfile;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Resource;
import com.skillroute.model.Role;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.repository.CompanyProfileRepository;
import com.skillroute.repository.ResourceRepository;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.SpecializationRepository;
import com.skillroute.repository.StudentProfileRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileAndResourceServiceTest {
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private CompanyProfileRepository companyProfileRepository;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ResourceRepository resourceRepository;

    private StudentProfileService studentProfileService;
    private CompanyProfileService companyProfileService;
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        studentProfileService = new StudentProfileService(
                studentProfileRepository,
                specializationRepository,
                TestMessageProperties.create(),
                new StudentProfileMapper(new SpecializationMapper()));
        companyProfileService = new CompanyProfileService(
                companyProfileRepository,
                TestMessageProperties.create(),
                new CompanyProfileMapper());
        resourceService = new ResourceService(skillRepository, resourceRepository, TestMessageProperties.create());
    }

    @Test
    void studentRegistrationCreatesEmptyStudentProfileOnlyForStudents() {
        Account student = Account.builder().id(1L).role(Role.STUDENT).build();
        Account company = Account.builder().id(2L).role(Role.COMPANY).build();

        studentProfileService.handleAccountRegistration(new AccountRegisteredEvent(student));
        studentProfileService.handleAccountRegistration(new AccountRegisteredEvent(company));

        ArgumentCaptor<StudentProfile> captor = ArgumentCaptor.forClass(StudentProfile.class);
        verify(studentProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getAccount()).isSameAs(student);
    }

    @Test
    void updateStudentProfileNormalizesBlankFieldsAndSetsSpecialization() {
        StudentProfile profile = StudentProfile.builder().id(1L).build();
        Specialization specialization = Specialization.builder()
                .id(10L)
                .direction(Direction.FRONTEND)
                .language(Language.TYPESCRIPT)
                .build();
        UpdateStudentRequest form = UpdateStudentRequest.builder()
                .firstName(" Maria ")
                .lastName(" Ivanova ")
                .gitHubUrl(" ")
                .bio(" Bio ")
                .specializationId(10L)
                .build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(specializationRepository.getReferenceById(10L)).thenReturn(specialization);

        studentProfileService.updateProfile(1L, form);

        assertThat(profile.getFirstName()).isEqualTo("Maria");
        assertThat(profile.getLastName()).isEqualTo("Ivanova");
        assertThat(profile.getGithubUrl()).isNull();
        assertThat(profile.getBio()).isEqualTo("Bio");
        assertThat(profile.getSpecialization()).isSameAs(specialization);
    }

    @Test
    void updateStudentProfileRequiresFirstAndLastNameTogether() {
        StudentProfile profile = StudentProfile.builder().id(1L).build();
        UpdateStudentRequest form = UpdateStudentRequest.builder()
                .firstName("Maria")
                .lastName(" ")
                .build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> studentProfileService.updateProfile(1L, form))
                .isInstanceOf(FieldValidationException.class);
        verify(specializationRepository, never()).getReferenceById(any());
    }

    @Test
    void companyProfileQueriesAndUpdatesUseMapperAndNormalizeBlanks() {
        CompanyProfile confirmed = companyProfile(1L, "Acme", true);
        CompanyProfile pending = companyProfile(2L, "Beta", false);
        UpdateCompanyRequest form = UpdateCompanyRequest.builder()
                .companyName(" Acme Updated ")
                .description(" ")
                .websiteUrl(" https://example.com ")
                .build();

        when(companyProfileRepository.findById(1L)).thenReturn(Optional.of(confirmed));
        when(companyProfileRepository.findAllConfirmed()).thenReturn(List.of(confirmed));
        when(companyProfileRepository.findAllPending()).thenReturn(List.of(pending));

        assertThat(companyProfileService.getCompanyById(1L).getCompanyName()).isEqualTo("Acme");
        assertThat(companyProfileService.getUpdateForm(1L).getCompanyName()).isEqualTo("Acme");
        assertThat(companyProfileService.getConfirmedCompanies()).hasSize(1);
        assertThat(companyProfileService.getPendingCompanies()).hasSize(1);
        assertThat(companyProfileService.isConfirmed(1L)).isTrue();
        assertThat(companyProfileService.isProfileComplete(1L)).isTrue();

        companyProfileService.updateProfile(1L, form);
        assertThat(confirmed.getCompanyName()).isEqualTo("Acme Updated");
        assertThat(confirmed.getDescription()).isNull();
        assertThat(confirmed.getWebsiteUrl()).isEqualTo("https://example.com");

        companyProfileService.approveCompany(1L);
        assertThat(confirmed.isConfirmed()).isTrue();
    }

    @Test
    void companyRegistrationCreatesProfileOnlyForCompanies() {
        Account company = Account.builder().id(2L).role(Role.COMPANY).build();
        Account student = Account.builder().id(1L).role(Role.STUDENT).build();

        companyProfileService.handleAccountRegistration(new AccountRegisteredEvent(company));
        companyProfileService.handleAccountRegistration(new AccountRegisteredEvent(student));

        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(companyProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getAccount()).isSameAs(company);
    }

    @Test
    void resourceServiceAddsAndDeletesSkillResources() {
        Skill skill = Skill.builder().id(10L).name("Java").build();
        Resource resource = Resource.builder().id(20L).skill(skill).resource("https://docs").build();
        AddResourceRequest form = AddResourceRequest.builder().resource("https://docs").build();

        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(resourceRepository.findByIdAndSkillId(20L, 10L)).thenReturn(Optional.of(resource));

        resourceService.addResourceToSkill(10L, form);
        resourceService.deleteResourceFromSkill(10L, 20L);

        ArgumentCaptor<Resource> captor = ArgumentCaptor.forClass(Resource.class);
        verify(resourceRepository).save(captor.capture());
        assertThat(captor.getValue().getSkill()).isSameAs(skill);
        assertThat(captor.getValue().getResource()).isEqualTo("https://docs");
        verify(resourceRepository).delete(resource);
    }

    private CompanyProfile companyProfile(Long id, String name, boolean confirmed) {
        Account account = Account.builder()
                .id(id)
                .email(name.toLowerCase() + "@example.com")
                .role(Role.COMPANY)
                .isVerified(true)
                .build();
        return CompanyProfile.builder()
                .id(id)
                .account(account)
                .companyName(name)
                .isConfirmed(confirmed)
                .build();
    }
}
