package com.skillroute.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "vacancy_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyProfile {
    @Id
    @Column(name = "vacancy_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Positive(message = "Зарплата должна быть больше нуля")
    @Max(value = 100000000, message = "Слишком большая сумма, проверьте корректность")
    private Long salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_schedule")
    private WorkSchedule workSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status = VacancyStatus.OPEN;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyProfile that = (VacancyProfile) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
