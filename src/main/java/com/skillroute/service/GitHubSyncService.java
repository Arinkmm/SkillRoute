package com.skillroute.service;

import com.skillroute.model.*;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.*;
import com.skillroute.service.client.GitHubSearchClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubSyncService {
    private final StudentProfileRepository profileRepository;
    private final SkillDictionaryRepository dictionaryRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final GitHubSearchClient gitHubClient;
    private final MessageProperties messages;

    @Transactional
    public int syncSkills(Long accountId) {
        StudentProfile student = profileRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        String username = extractUsername(student.getGithubUrl());

        Map<String, Integer> profileSignals = gitHubClient.collectProfileSignals(username);

        Map<Long, String> skillNames = skillRepository.findAll().stream().collect(Collectors.toMap(Skill::getId, Skill::getName));

        List<SkillDictionary> dictionary = dictionaryRepository.findAll();
        int confirmedCount = 0;

        for (SkillDictionary dict : dictionary) {
            String name = skillNames.get(dict.getSkillId());

            if (processSkillSync(student, username, profileSignals, dict, name)) {
                confirmedCount++;
            }
        }

        return confirmedCount;
    }

    private boolean processSkillSync(StudentProfile student, String username, Map<String, Integer> signals, SkillDictionary dict, String name) {
        String pattern = dict.getImportPattern();

        Set<String> terms = buildTerms(name, pattern, dict.getQuickSignals());
        int weight = signals.entrySet().stream()
                .filter(e -> terms.stream().anyMatch(t -> e.getKey().contains(t)))
                .mapToInt(Map.Entry::getValue).sum();

        if (weight == 0 && pattern != null) {

            if (gitHubClient.hasCodeMatch(username, pattern)) {
                weight = 10;
            }

            sleep(7000);
        }

        if (weight > 0) {
            upsertSkill(student, dict.getSkillId(), weight);
            return true;
        }
        return false;
    }

    private void upsertSkill(StudentProfile student, Long skillId, int weight) {
        StudentSkillId id = new StudentSkillId(student.getId(), skillId);
        StudentSkill ss = studentSkillRepository.findById(id).orElseGet(() -> {
            StudentSkill s = new StudentSkill();
            s.setId(id);
            s.setStudent(student);
            s.setSkill(skillRepository.findById(skillId).orElseThrow());
            s.setLevel(0);
            return s;
        });

        ss.setConfirmedByGitHub(true);
        int level = calculateLevel(weight);
        if (level > ss.getLevel()) ss.setLevel(level);

        studentSkillRepository.save(ss);
    }

    private int calculateLevel(int weight) {
        if (weight >= 50) return 5;
        if (weight >= 30) return 4;
        if (weight >= 15) return 3;
        if (weight >= 5) return 2;
        return 1;
    }

    private Set<String> buildTerms(String name, String pattern, String quickSignals) {
        Set<String> terms = new HashSet<>();
        terms.add(gitHubClient.normalize(name));
        if (pattern != null && !pattern.contains(":")) terms.add(gitHubClient.normalize(pattern));
        if (quickSignals != null) {
            Arrays.stream(quickSignals.split(",")).map(gitHubClient::normalize).forEach(terms::add);
        }
        return terms;
    }

    private String extractUsername(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}