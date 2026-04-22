package com.skillroute.model;

import com.skillroute.model.id.StudentSkillId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkill {
    @EmbeddedId
    private StudentSkillId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "is_confirmed_by_github")
    private boolean isConfirmedByGitHub;

    @Column(nullable = false)
    private int level;
}