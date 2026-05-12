package com.skillroute.dto.response;

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
public class VacancyResponse {
    private Long id;
    private String name;
    private Long companyId;
    private Long salary;
    private WorkSchedule workSchedule;
    private VacancyStatus status;
    private SpecializationResponse specialization;
    private List<VacancySkillResponse> skills;
}
