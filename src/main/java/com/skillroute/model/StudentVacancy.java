package com.skillroute.model;

import com.skillroute.model.id.StudentVacancyId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_vacancy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentVacancy {
    @EmbeddedId
    private StudentVacancyId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentVacancyStatus status;
}
