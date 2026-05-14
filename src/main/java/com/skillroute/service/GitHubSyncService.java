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
        StudentProfile student = profileRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        String username = extractUsername(student.getGithubUrl());
        Map<String, Integer> profileSignals = gitHubClient.collectProfileSignals(username);

        Map<Long, String> skillNames = skillRepository.findAll().stream()
                .collect(Collectors.toMap(Skill::getId, Skill::getName));

        return (int) dictionaryRepository.findAll().stream()
                .filter(dict -> processSkillSync(student, username, profileSignals, dict, skillNames.get(dict.getSkillId())))
                .count();
    }

    private boolean processSkillSync(StudentProfile student, String username, Map<String, Integer> signals, SkillDictionary dict, String name) {
        int weight = calculateMetadataWeight(name, dict, signals);

        if (weight == 0 && dict.getImportPattern() != null) {
            weight = performDeepSearch(username, dict.getImportPattern());
        }

        if (weight > 0) {
            updateStudentSkill(student, dict.getSkillId(), weight);
            return true;
        }
        return false;
    }

    private int calculateMetadataWeight(String name, SkillDictionary dict, Map<String, Integer> signals) {
        Set<String> terms = buildTerms(name, dict.getImportPattern(), dict.getQuickSignals());
        return signals.entrySet().stream()
                .filter(e -> terms.stream().anyMatch(t -> e.getKey().contains(t)))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    private int performDeepSearch(String username, String pattern) {
        boolean isMatch = gitHubClient.hasCodeMatch(username, pattern);
        sleep(7000);
        return isMatch ? 10 : 0;
    }

    private void updateStudentSkill(StudentProfile student, Long skillId, int weight) {
        StudentSkillId id = new StudentSkillId(student.getId(), skillId);
        StudentSkill ss = studentSkillRepository.findById(id).orElseGet(() -> createNewSkill(student, skillId, id));

        ss.setConfirmedByGitHub(true);
        int newLevel = calculateLevel(weight);
        if (newLevel > ss.getLevel()) {
            ss.setLevel(newLevel);
        }
        studentSkillRepository.save(ss);
    }

    private StudentSkill createNewSkill(StudentProfile student, Long skillId, StudentSkillId id) {
        StudentSkill s = new StudentSkill();
        s.setId(id);
        s.setStudent(student);
        s.setSkill(skillRepository.findById(skillId).orElseThrow());
        s.setLevel(1);
        return s;
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
        if (pattern != null && !pattern.contains(":")) {
            terms.add(gitHubClient.normalize(pattern));
        }
        if (quickSignals != null) {
            Arrays.stream(quickSignals.split(","))
                    .map(gitHubClient::normalize)
                    .forEach(terms::add);
        }
        return terms;
    }

    private String extractUsername(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}