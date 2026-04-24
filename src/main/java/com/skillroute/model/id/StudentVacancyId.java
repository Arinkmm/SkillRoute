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
public class StudentVacancyId implements Serializable {
    private Long studentId;
    private Long vacancyId;
}
