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

    @ManyToOne
    @MapsId
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @Column(nullable = false)
    private Integer level;
}
