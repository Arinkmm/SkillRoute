package com.skillroute.dto.response;

import com.skillroute.dto.request.AddResourceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteSkillResponse {
    private String name;
    private List<AddResourceRequest> resources;
}
