package com.skillroute.model.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class VacancySkillId implements Serializable {
    private Long vacancyId;
    private Long skillId;
}
