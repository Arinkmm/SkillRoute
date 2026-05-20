package com.skillroute.mapper;

import com.skillroute.model.Account;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Resource;
import com.skillroute.model.Role;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.CompanyProfile;
import com.skillroute.openapi.model.GitHubSyncResponseApi;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileAndSkillMapperTest {
    private final ResourceMapper resourceMapper = new ResourceMapper();
    private final SpecializationMapper specializationMapper = new SpecializationMapper();

    @Test
    void mapsStudentProfileToResponseAndUpdateRequest() {
        StudentProfileMapper mapper = new StudentProfileMapper(specializationMapper);
        Specialization specialization = Specialization.builder()
                .id(7L)
                .direction(Direction.BACKEND)
                .language(Language.JAVA)
                .build();
        StudentProfile profile = StudentProfile.builder()
                .id(1L)
                .firstName("Maria")
                .lastName("Ivanova")
                .githubUrl("https://github.com/maria")
                .bio("Backend")
                .specialization(specialization)
                .build();

        var response = mapper.toResponse(profile);
        var form = mapper.toUpdateRequest(profile);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSpecialization().getId()).isEqualTo(7L);
        assertThat(response.getGithubUrl()).isEqualTo("https://github.com/maria");
        assertThat(form.getFirstName()).isEqualTo("Maria");
        assertThat(form.getSpecializationId()).isEqualTo(7L);
    }

    @Test
    void mapsCompanyProfileToResponseAndUpdateRequest() {
        CompanyProfileMapper mapper = new CompanyProfileMapper();
        Account account = Account.builder()
                .id(2L)
                .email("company@example.com")
                .role(Role.COMPANY)
                .isVerified(true)
                .build();
        CompanyProfile profile = CompanyProfile.builder()
                .id(2L)
                .account(account)
                .companyName("Skill Corp")
                .description("Hiring")
                .websiteUrl("https://example.com")
                .isConfirmed(true)
                .build();

        var response = mapper.toResponse(profile);
        var form = mapper.toUpdateRequest(profile);

        assertThat(response.getEmail()).isEqualTo("company@example.com");
        assertThat(response.isConfirmed()).isTrue();
        assertThat(response.isAccountVerified()).isTrue();
        assertThat(form.getCompanyName()).isEqualTo("Skill Corp");
        assertThat(form.getWebsiteUrl()).isEqualTo("https://example.com");
    }

    @Test
    void mapsSkillResourcesInStableOrderForSkillAndRouteResponses() {
        SkillMapper mapper = new SkillMapper(resourceMapper);
        Skill skill = Skill.builder()
                .id(10L)
                .name("Java")
                .resources(Set.of(
                        Resource.builder().id(2L).resource("Second").build(),
                        Resource.builder().id(1L).resource("First").build()))
                .build();

        var response = mapper.toResponse(skill);
        var routeResponse = mapper.toRouteResponse(skill);

        assertThat(response.getResources())
                .extracting("id")
                .containsExactly(1L, 2L);
        assertThat(routeResponse.getSkillId()).isEqualTo(10L);
        assertThat(routeResponse.getResources())
                .extracting("resource")
                .containsExactly("First", "Second");
    }

    @Test
    void mapsGithubSyncRunningFlagFromStatus() {
        GitHubSyncMapper mapper = new GitHubSyncMapper();

        GitHubSyncResponseApi running = mapper.toResponse("Started", 2, "RUNNING");
        GitHubSyncResponseApi success = mapper.toResponse("Done", 5, "SUCCESS");

        assertThat(running.getRunning()).isTrue();
        assertThat(success.getRunning()).isFalse();
        assertThat(success.getConfirmedCount()).isEqualTo(5);
    }
}
