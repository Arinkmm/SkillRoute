package com.skillroute.mapper;

import com.skillroute.dto.response.ResourceResponse;
import com.skillroute.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public ResourceResponse toResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .resource(resource.getResource())
                .build();
    }
}
