package com.skillroute.dto.request;

import com.skillroute.model.WorkSchedule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Название вакансии обязательно")
    private String name;

    @NotNull(message = "Выберите специализацию")
    private Long specializationId;

    @NotNull(message = "Укажите зарплату")
    @Positive(message = "Зарплата должна быть больше нуля")
    @Max(value = 100000000, message = "Слишком большая сумма, проверьте корректность")
    private Long salary;

    @NotNull(message = "Выберите формат работы")
    private WorkSchedule workSchedule;

    @Valid
    private List<AddSkillRequest> skills;
}
