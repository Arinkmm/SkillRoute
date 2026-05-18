package com.skillroute.service;

import com.skillroute.dto.response.SpecializationResponse;
import com.skillroute.mapper.SpecializationMapper;
import com.skillroute.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;

    @Transactional(readOnly = true)
    public List<SpecializationResponse> getSpecializations() {
        return specializationRepository.findAll()
                .stream()
                .map(specializationMapper::toResponse)
                .toList();
    }
}
