package com.skillroute.dto;

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
public class CreateVacancyRequest {
    private String name;
    private Long specializationId;
    private Long salary;
    private WorkSchedule workSchedule;
    private List<AddSkillRequest> skills;
}