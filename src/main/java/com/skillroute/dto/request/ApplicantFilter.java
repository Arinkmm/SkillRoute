package com.skillroute.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantFilter {
    @PositiveOrZero(message = "Максимальный гэп не может быть отрицательным")
    private int maxGap;

    @Min(value = 0, message = "Совпадение не может быть меньше 0%")
    @Max(value = 100, message = "Совпадение не может быть больше 100%")
    private double minMatch;
}