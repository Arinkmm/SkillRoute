package com.skillroute.service;

import com.skillroute.dto.SkillAddDto;
import com.skillroute.dto.StudentSkillDto;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
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

    @Transactional(readOnly = true)
    public List<StudentSkillDto> getStudentSkills(Long studentId) {
        return studentSkillRepository.findAllByStudentId(studentId).stream()
                .map(ss -> StudentSkillDto.builder()
                        .name(ss.getSkill().getName())
                        .level(ss.getLevel())
                        .isConfirmedByGitHub(ss.isConfirmedByGitHub())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentSkillDto> searchSkillsByName(String query) {
        return studentSkillRepository.findByNameContainingIgnoreCase(query).stream()
                .map(ss -> new StudentSkillDto(ss.getSkill().getName(), ss.getLevel(), ss.isConfirmedByGitHub()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasSkill(Long studentId, Long skillId) {
        return studentSkillRepository.existsByStudentIdAndSkillId(studentId, skillId);
    }

    @Transactional
    public void addSkillToStudent(Long id, SkillAddDto form) {
        StudentProfile studentProfile = studentProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Профиль студента с таким id " + id + " не найден"));

        Skill skill = skillRepository.findById(form.getId()).orElseThrow(() -> new EntityNotFoundException("Скилл отсутствует"));

        StudentSkillId compositeKey = new StudentSkillId(studentProfile.getId(), skill.getId());
        if (studentSkillRepository.existsById(compositeKey)) {
            throw new DuplicateEntityException("Этот навык уже добавлен в ваш профиль");
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
