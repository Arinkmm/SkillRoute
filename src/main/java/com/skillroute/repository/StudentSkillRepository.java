package com.skillroute.repository;

import com.skillroute.model.Skill;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, StudentSkillId> {
    @Query("SELECT ss FROM StudentSkill ss JOIN FETCH ss.skill WHERE ss.id.studentId = :studentId")
    List<StudentSkill> findAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT ss FROM StudentSkill ss JOIN FETCH ss.skill WHERE ss.skill.name = :name")
    Optional<StudentSkill> findByNameContainingIgnoreCase(@Param("name") String name);
}
