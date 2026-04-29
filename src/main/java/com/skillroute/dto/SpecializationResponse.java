package com.skillroute.dto;

import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import lombok.Builder;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationResponse {
    private Direction direction;
    private Language language;
}