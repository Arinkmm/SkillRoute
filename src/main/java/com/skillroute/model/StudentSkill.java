package com.skillroute.model;

import com.skillroute.model.id.StudentSkillId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;

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
    private boolean isConfirmedByGitHub = false;

    @Min(value = 1, message = "Уровень не может быть ниже 1")
    @Max(value = 5, message = "Уровень не может быть выше 5")
    @Column(nullable = false)
    private int level = 1;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentSkill that = (StudentSkill) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}