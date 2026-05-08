package com.skillroute.repository;

import com.skillroute.model.SkillDictionary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillDictionaryRepository extends JpaRepository<SkillDictionary, Long> {
}