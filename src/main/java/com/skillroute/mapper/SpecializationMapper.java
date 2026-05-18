package com.skillroute.mapper;

import com.skillroute.dto.response.SpecializationResponse;
import com.skillroute.model.Specialization;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper {

    public SpecializationResponse toResponse(Specialization specialization) {
        if (specialization == null) {
            return null;
        }

        return SpecializationResponse.builder()
                .id(specialization.getId())
                .direction(specialization.getDirection())
                .language(specialization.getLanguage())
                .build();
    }
}
