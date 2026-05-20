package com.skillroute.service;

import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.StudentSkillMapper;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.openapi.model.StudentSkillResponseApi;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentSkillService {
    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MessageProperties messages;
    private final StudentSkillMapper studentSkillMapper;

    @Transactional(readOnly = true)
    public List<StudentSkillResponse> getStudentSkills(Long studentId) {
        return studentSkillRepository.findAllByStudentId(studentId).stream()
                .map(studentSkillMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentSkillResponseApi> getStudentsSkillsByName(Long studentId, String query) {
        String normalizedQuery = query == null ? "" : query.trim();
        return studentSkillRepository.findAllByStudentIdAndSkillNameContainingIgnoreCase(studentId, normalizedQuery).stream()
                .map(studentSkillMapper::toResponseApi)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentSkillResponse getStudentSkill(Long studentId, Long skillId) {
        return studentSkillRepository.findById(new StudentSkillId(studentId, skillId))
                .map(studentSkillMapper::toResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public int countConfirmedByGitHub(Long studentId) {
        return Math.toIntExact(studentSkillRepository.countConfirmedByGitHub(studentId));
    }

    @Transactional
    public void addSkillToStudent(Long id, AddSkillRequest form) {
        StudentProfile studentProfile = studentProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        Skill skill = skillRepository.findById(form.getSkillId()).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillMissing()));

        StudentSkillId compositeKey = new StudentSkillId(studentProfile.getId(), skill.getId());
        if (studentSkillRepository.existsById(compositeKey)) {
            throw new DuplicateEntityException(messages.getSkill().getDuplicate());
        }

        StudentSkill studentSkill = StudentSkill.builder()
                .id(compositeKey)
                .student(studentProfile)
                .skill(skill)
                .level(form.getLevel())
                .build();

        studentSkillRepository.save(studentSkill);
    }

    @Transactional
    public void addOrUpdateSkillFromRoadmap(Long id, AddSkillRequest form) {
        StudentProfile studentProfile = studentProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));
        Skill skill = skillRepository.findById(form.getSkillId()).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillMissing()));
        StudentSkillId compositeKey = new StudentSkillId(studentProfile.getId(), skill.getId());

        StudentSkill studentSkill = studentSkillRepository.findById(compositeKey)
                .orElseGet(() -> StudentSkill.builder()
                        .id(compositeKey)
                        .student(studentProfile)
                        .skill(skill)
                        .build());

        studentSkill.setLevel(form.getLevel());
        studentSkill.setConfirmedByGitHub(false);
        studentSkillRepository.save(studentSkill);
    }
}
