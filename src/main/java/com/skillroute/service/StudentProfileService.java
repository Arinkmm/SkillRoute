package com.skillroute.service;

import com.skillroute.dto.request.UpdateStudentRequest;
import com.skillroute.dto.response.StudentProfileResponse;
import com.skillroute.event.AccountRegisteredEvent;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.FieldValidationException;
import com.skillroute.mapper.StudentProfileMapper;
import com.skillroute.model.Role;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.SpecializationRepository;
import com.skillroute.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentProfileService {
    private final StudentProfileRepository studentProfileRepository;
    private final SpecializationRepository specializationRepository;
    private final MessageProperties messages;
    private final StudentProfileMapper studentProfileMapper;

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
        return studentProfileRepository.findById(id)
                .map(studentProfileMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));
    }

    @Transactional(readOnly = true)
    public UpdateStudentRequest getUpdateForm(Long id) {
        StudentProfile profile = studentProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        return studentProfileMapper.toUpdateRequest(profile);
    }

    @Transactional
    public void updateProfile(Long id, UpdateStudentRequest form) {
        StudentProfile studentProfile = studentProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        validateNameFields(form);

        studentProfile.setFirstName(normalizeBlank(form.getFirstName()));
        studentProfile.setLastName(normalizeBlank(form.getLastName()));
        studentProfile.setGithubUrl(normalizeBlank(form.getGitHubUrl()));
        studentProfile.setBio(normalizeBlank(form.getBio()));

        if (form.getSpecializationId() != null) {
            Specialization specialization = specializationRepository.getReferenceById(form.getSpecializationId());
            studentProfile.setSpecialization(specialization);
        }
    }

    @Transactional(readOnly = true)
    public boolean isProfileComplete(Long id) {
        return studentProfileRepository.findById(id)
                .map(profile -> hasText(profile.getFirstName()) && hasText(profile.getLastName()))
                .orElse(false);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeBlank(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private void validateNameFields(UpdateStudentRequest form) {
        boolean hasFirstName = hasText(form.getFirstName());
        boolean hasLastName = hasText(form.getLastName());

        if (hasFirstName == hasLastName) {
            return;
        }

        Map<String, String> errors = new LinkedHashMap<>();
        String message = messages.getAccount().getProfileNamePairRequired();

        if (!hasFirstName) {
            errors.put("firstName", message);
        }

        if (!hasLastName) {
            errors.put("lastName", message);
        }

        throw new FieldValidationException(messages.getValidationError(), errors);
    }
}
