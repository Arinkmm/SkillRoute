package com.skillroute.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Set<StudentSkill> studentSkills = new HashSet<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Set<VacancySkill> vacancySkills = new HashSet<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Set<Resource> resources = new HashSet<>();
}
