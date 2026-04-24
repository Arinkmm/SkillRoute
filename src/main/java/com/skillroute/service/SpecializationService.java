package com.skillroute.service;

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
    public List<Specialization> getSpecializations() {
        return specializationRepository.findAll();
    }
}
