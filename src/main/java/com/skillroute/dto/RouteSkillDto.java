package com.skillroute.dto;

import com.skillroute.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteSkillDto {
    private String name;
    private Set<Resource> resources;
}
