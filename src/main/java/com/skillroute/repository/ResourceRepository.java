package com.skillroute.repository;

import com.skillroute.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllBySkillIdIn(List<Long> skillIds);

    Optional<Resource> findByIdAndSkillId(Long id, Long skillId);
}
