package com.skillroute.mapper;

import com.skillroute.dto.request.UpdateStudentRequest;
import com.skillroute.dto.response.StudentProfileResponse;
import com.skillroute.model.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentProfileMapper {
    private final SpecializationMapper specializationMapper;

    public StudentProfileResponse toResponse(StudentProfile profile) {
        return StudentProfileResponse.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .githubUrl(profile.getGithubUrl())
                .bio(profile.getBio())
                .specialization(specializationMapper.toResponse(profile.getSpecialization()))
                .build();
    }

    public UpdateStudentRequest toUpdateRequest(StudentProfile profile) {
        return UpdateStudentRequest.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .gitHubUrl(profile.getGithubUrl())
                .specializationId(profile.getSpecialization() != null ? profile.getSpecialization().getId() : null)
                .bio(profile.getBio())
                .build();
    }
}
