package com.skillroute.service;

import com.skillroute.dto.EditStudentDto;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Specialization;
import com.skillroute.model.StudentProfile;
import com.skillroute.repository.SpecificationRepository;
import com.skillroute.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {
    private final StudentProfileRepository studentProfileRepository;
    private final SpecificationRepository specificationRepository;

    @Transactional
    public void updateProfile(Long id, EditStudentDto form) {
        StudentProfile studentProfile = studentProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Профиль студента с таким id " + id + " не найден"));

        studentProfile.setFirstName(form.getFirstName());
        studentProfile.setLastName(form.getLastName());
        studentProfile.setGithubUrl(form.getGitHubUrl());
        studentProfile.setBio(form.getBio());

        if (form.getSpecializationId() != null) {
            Specialization specialization = specificationRepository.getReferenceById(form.getSpecializationId());
            studentProfile.setSpecialization(specialization);
        }
    }
}
