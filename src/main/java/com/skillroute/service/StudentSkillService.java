package com.skillroute.service;

import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentSkillService {
    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MessageProperties messages;

    @Transactional(readOnly = true)
    public List<StudentSkillResponse> getStudentSkills(Long studentId) {
        return studentSkillRepository.findAllByStudentId(studentId).stream()
                .map(ss -> StudentSkillResponse.builder()
                        .skillId(ss.getSkill().getId())
                        .name(ss.getSkill().getName())
                        .level(ss.getLevel())
                        .isConfirmedByGitHub(ss.isConfirmedByGitHub())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentSkillResponse> getStudentsSkillsByName(Long studentId, String query) {
        String normalizedQuery = query == null ? "" : query.trim();
        return studentSkillRepository.findAllByStudentIdAndSkillNameContainingIgnoreCase(studentId, normalizedQuery).stream().map(ss -> new StudentSkillResponse(ss.getSkill().getId(), ss.getSkill().getName(), ss.getLevel(), ss.isConfirmedByGitHub())).toList();
    }

    @Transactional(readOnly = true)
    public boolean hasSkill(Long studentId, Long skillId) {
        return studentSkillRepository.existsByStudentIdAndSkillId(studentId, skillId);
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
}
