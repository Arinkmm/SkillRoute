package com.skillroute.dto;

import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.VacancyStatus;
import com.skillroute.model.WorkSchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyResponseDto {
    private Long id;
    private String name;
    private Long companyId;
    private Long salary;
    private WorkSchedule workSchedule;
    private VacancyStatus status;
    private Language language;
    private Direction direction;
    private String fullSpecialization;
    private List<SkillResponseDto> skills;
}