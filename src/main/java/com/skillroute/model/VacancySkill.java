package com.skillroute.model;

import com.skillroute.model.id.VacancySkillId;
import jakarta.persistence.*;
import lombok.*;

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
}
