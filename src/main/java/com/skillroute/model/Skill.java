package com.skillroute.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(name, skill.name);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name);
    }
}
