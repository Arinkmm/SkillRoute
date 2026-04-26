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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    private Long salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_schedule")
    private WorkSchedule workSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status;
}
