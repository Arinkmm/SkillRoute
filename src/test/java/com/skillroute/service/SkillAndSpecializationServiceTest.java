package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.mapper.ResourceMapper;
import com.skillroute.mapper.SkillMapper;
import com.skillroute.mapper.SpecializationMapper;
import com.skillroute.model.Direction;
import com.skillroute.model.Language;
import com.skillroute.model.Skill;
import com.skillroute.model.Specialization;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.SpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillAndSpecializationServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SpecializationRepository specializationRepository;

    private SkillService skillService;
    private SpecializationService specializationService;

    @BeforeEach
    void setUp() {
        skillService = new SkillService(
                skillRepository,
                TestMessageProperties.create(),
                new SkillMapper(new ResourceMapper()));
        specializationService = new SpecializationService(specializationRepository, new SpecializationMapper());
    }

    @Test
    void skillServiceMapsListsDetailsAndRouteDetails() {
        Skill skill = Skill.builder().id(10L).name("Java").build();
        when(skillRepository.findAll()).thenReturn(List.of(skill));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));

        assertThat(skillService.getSkills()).extracting("name").containsExactly("Java");
        assertThat(skillService.getSkillById(10L).getName()).isEqualTo("Java");
        assertThat(skillService.getRouteSkillById(10L).getSkillId()).isEqualTo(10L);
    }

    @Test
    void skillServiceThrowsWhenSkillIsMissing() {
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.getSkillById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Навык не найден");
    }

    @Test
    void specializationServiceMapsAllSpecializations() {
        Specialization specialization = Specialization.builder()
                .id(1L)
                .direction(Direction.BACKEND)
                .language(Language.JAVA)
                .build();
        when(specializationRepository.findAll()).thenReturn(List.of(specialization));

        assertThat(specializationService.getSpecializations())
                .extracting("language")
                .containsExactly(Language.JAVA);
    }
}
