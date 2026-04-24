package com.skillroute.model;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne
    @MapsId
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @Column(name = "specialization_id")
    private Long specializationId;

    private Long salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_schedule")
    private WorkSchedule workSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status;
}
