package com.skillroute.service;

import com.skillroute.dto.response.SpecializationResponse;
import com.skillroute.model.Specialization;
import com.skillroute.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    @Transactional(readOnly = true)
    public List<SpecializationResponse> getSpecializations() {
        return specializationRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private SpecializationResponse mapToResponseDto(Specialization specialization) {
        return SpecializationResponse.builder()
                .direction(specialization.getDirection())
                .language(specialization.getLanguage())
                .build();
    }
}
