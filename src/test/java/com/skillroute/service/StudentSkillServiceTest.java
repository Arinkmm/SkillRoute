package com.skillroute.service;

import com.skillroute.TestMessageProperties;
import com.skillroute.dto.request.AddSkillRequest;
import com.skillroute.exception.DuplicateEntityException;
import com.skillroute.mapper.StudentSkillMapper;
import com.skillroute.model.Skill;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentSkillServiceTest {
    @Mock
    private StudentSkillRepository studentSkillRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;

    private StudentSkillService service;

    @BeforeEach
    void setUp() {
        service = new StudentSkillService(
                studentSkillRepository,
                skillRepository,
                studentProfileRepository,
                TestMessageProperties.create(),
                new StudentSkillMapper());
    }

    @Test
    void addSkillToStudentCreatesNewUnconfirmedSkill() {
        StudentProfile student = StudentProfile.builder().id(1L).build();
        Skill skill = Skill.builder().id(10L).name("Java").build();
        AddSkillRequest request = AddSkillRequest.builder().skillId(10L).level(4).build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.existsById(new StudentSkillId(1L, 10L))).thenReturn(false);

        service.addSkillToStudent(1L, request);

        ArgumentCaptor<StudentSkill> captor = ArgumentCaptor.forClass(StudentSkill.class);
        verify(studentSkillRepository).save(captor.capture());

        StudentSkill saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(new StudentSkillId(1L, 10L));
        assertThat(saved.getLevel()).isEqualTo(4);
        assertThat(saved.isConfirmedByGitHub()).isFalse();
    }

    @Test
    void addSkillToStudentRejectsDuplicateSkill() {
        StudentProfile student = StudentProfile.builder().id(1L).build();
        Skill skill = Skill.builder().id(10L).name("Java").build();
        AddSkillRequest request = AddSkillRequest.builder().skillId(10L).level(4).build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.existsById(new StudentSkillId(1L, 10L))).thenReturn(true);

        assertThatThrownBy(() -> service.addSkillToStudent(1L, request))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessage("Навык уже добавлен");
    }

    @Test
    void addOrUpdateSkillFromRoadmapUpdatesExistingSkillAndResetsConfirmation() {
        StudentProfile student = StudentProfile.builder().id(1L).build();
        Skill skill = Skill.builder().id(10L).name("Java").build();
        StudentSkill existing = StudentSkill.builder()
                .id(new StudentSkillId(1L, 10L))
                .student(student)
                .skill(skill)
                .level(2)
                .isConfirmedByGitHub(true)
                .build();
        AddSkillRequest request = AddSkillRequest.builder().skillId(10L).level(5).build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.findById(new StudentSkillId(1L, 10L))).thenReturn(Optional.of(existing));

        service.addOrUpdateSkillFromRoadmap(1L, request);

        assertThat(existing.getLevel()).isEqualTo(5);
        assertThat(existing.isConfirmedByGitHub()).isFalse();
        verify(studentSkillRepository).save(existing);
    }

    @Test
    void readMethodsMapSkillsAndConfirmedCount() {
        StudentSkill java = StudentSkill.builder()
                .id(new StudentSkillId(1L, 10L))
                .skill(Skill.builder().id(10L).name("Java").build())
                .level(4)
                .isConfirmedByGitHub(true)
                .build();

        when(studentSkillRepository.findAllByStudentId(1L)).thenReturn(List.of(java));
        when(studentSkillRepository.findAllByStudentIdAndSkillNameContainingIgnoreCase(1L, "ja"))
                .thenReturn(List.of(java));
        when(studentSkillRepository.findById(new StudentSkillId(1L, 10L))).thenReturn(Optional.of(java));
        when(studentSkillRepository.countConfirmedByGitHub(1L)).thenReturn(1L);

        assertThat(service.getStudentSkills(1L))
                .extracting("name")
                .containsExactly("Java");
        assertThat(service.getStudentsSkillsByName(1L, " ja "))
                .extracting("name")
                .containsExactly("Java");
        assertThat(service.getStudentSkill(1L, 10L).getName()).isEqualTo("Java");
        assertThat(service.countConfirmedByGitHub(1L)).isEqualTo(1);
    }

    @Test
    void addOrUpdateSkillFromRoadmapCreatesMissingSkill() {
        StudentProfile student = StudentProfile.builder().id(1L).build();
        Skill skill = Skill.builder().id(10L).name("Java").build();
        AddSkillRequest request = AddSkillRequest.builder().skillId(10L).level(3).build();

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.findById(new StudentSkillId(1L, 10L))).thenReturn(Optional.empty());

        service.addOrUpdateSkillFromRoadmap(1L, request);

        ArgumentCaptor<StudentSkill> captor = ArgumentCaptor.forClass(StudentSkill.class);
        verify(studentSkillRepository).save(captor.capture());
        assertThat(captor.getValue().getLevel()).isEqualTo(3);
        assertThat(captor.getValue().isConfirmedByGitHub()).isFalse();
    }
}
