package com.skillroute.model;

import com.skillroute.model.id.VacancySkillId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "vacancy_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancySkill {
    @EmbeddedId
    private VacancySkillId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vacancyId")
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(nullable = false)
    private Integer level;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancySkill that = (VacancySkill) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
