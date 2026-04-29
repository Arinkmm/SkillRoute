package com.skillroute.dto.request;

import java.util.List;

import com.skillroute.model.VacancyStatus;
import com.skillroute.model.WorkSchedule;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVacancyRequest {
    private String name;

    @Positive(message = "Зарплата должна быть больше нуля")
    @Max(value = 100000000, message = "Слишком большая сумма, проверьте корректность")
    private Long salary;

    private WorkSchedule workSchedule;

    private VacancyStatus status;

    private List<AddSkillRequest> skills;
}
