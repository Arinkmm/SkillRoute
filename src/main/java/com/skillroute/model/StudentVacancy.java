package com.skillroute.model;

import com.skillroute.model.id.StudentVacancyId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vacancyId")
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentVacancyStatus status = StudentVacancyStatus.SUBMITTED;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentVacancy that = (StudentVacancy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
