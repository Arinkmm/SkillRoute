package com.skillroute.dto.request;

import com.skillroute.model.WorkSchedule;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyFilter {
    @PositiveOrZero(message = "Минимальная зарплата не может быть отрицательной")
    private Integer minSalary;

    @PositiveOrZero(message = "Максимальная зарплата не может быть отрицательной")
    private Integer maxSalary;

    private Long specializationId;

    private WorkSchedule schedule;
}