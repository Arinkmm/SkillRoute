package com.skillroute.service;

import com.skillroute.model.Skill;
import com.skillroute.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<Skill> getSkills() {
        return skillRepository.findAll();
    }
}
