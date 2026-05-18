package com.skillroute.repository;

import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, StudentSkillId> {
    @Query("SELECT ss FROM StudentSkill ss JOIN FETCH ss.skill WHERE ss.id.studentId = :studentId")
    List<StudentSkill> findAllByStudentId(@Param("studentId") Long studentId);

    @Query("""
            SELECT ss FROM StudentSkill ss
            JOIN FETCH ss.skill
            WHERE ss.id.studentId = :studentId
            AND LOWER(ss.skill.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<StudentSkill> findAllByStudentIdAndSkillNameContainingIgnoreCase(@Param("studentId") Long studentId, @Param("name") String name);

    boolean existsByStudentIdAndSkillId(Long studentId, Long skillId);

    @Query("SELECT COUNT(ss) FROM StudentSkill ss WHERE ss.id.studentId = :studentId AND ss.isConfirmedByGitHub = true")
    long countConfirmedByGitHub(@Param("studentId") Long studentId);

    @Query("SELECT ss.id.skillId FROM StudentSkill ss WHERE ss.id.studentId = :studentId AND ss.isConfirmedByGitHub = true")
    Set<Long> findConfirmedGitHubSkillIds(@Param("studentId") Long studentId);
}
