package com.skillroute.repository;

import com.skillroute.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllBySkillIdIn(List<Long> skillIds);
}
