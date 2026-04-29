package com.skillroute.dto;

import java.util.List;

import com.skillroute.model.VacancyStatus;
import com.skillroute.model.WorkSchedule;
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
    private Long salary;
    private WorkSchedule workSchedule;
    private VacancyStatus status;
    private List<AddSkillRequest> skills;
}
