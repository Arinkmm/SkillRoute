package com.skillroute.dto;

import com.skillroute.model.WorkSchedule;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVacancyRequest {
    private String name;

    private Long specializationId;

    @Positive(message = "Зарплата должна быть больше нуля")
    @Max(value = 100000000, message = "Слишком большая сумма, проверьте корректность")
    private Long salary;

    private WorkSchedule workSchedule;

    private List<AddSkillRequest> skills;
}