package com.skillroute.service;

import com.skillroute.dto.request.UpdateStudentRequest;
import com.skillroute.dto.response.StudentProfileResponse;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Role;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.repository.SpecializationRepository;
import com.skillroute.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {
    private final StudentProfileRepository studentProfileRepository;
    private final SpecializationRepository specializationRepository;

    @EventListener
    public void handleAccountRegistration(AccountRegisteredEvent event) {
        if (event.getAccount().getRole() == Role.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setAccount(event.getAccount());
            studentProfileRepository.save(profile);
        }
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentById(Long id) {
        return studentProfileRepository.findById(id).map(this::mapToResponseDto).orElseThrow(() -> new EntityNotFoundException("Студент не найден"));
    }

    @Transactional
    public void updateProfile(Long id, UpdateStudentRequest form) {
        StudentProfile studentProfile = studentProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Студент не найден"));

        studentProfile.setFirstName(form.getFirstName());
        studentProfile.setLastName(form.getLastName());
        studentProfile.setGithubUrl(form.getGitHubUrl());
        studentProfile.setBio(form.getBio());

        if (form.getSpecializationId() != null) {
            Specialization specialization = specializationRepository.getReferenceById(form.getSpecializationId());
            studentProfile.setSpecialization(specialization);
        }
    }

    private StudentProfileResponse mapToResponseDto(StudentProfile profile) {
        return StudentProfileResponse.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .githubUrl(profile.getBio())
                .specializationId(profile.getSpecialization().getId())
                .bio(profile.getBio())
                .build();
    }
}
