package com.skillroute.service;

import com.skillroute.exception.DataMappingException;
import com.skillroute.exception.EntityNotFoundException;
import com.skillroute.exception.GithubUrlNotFoundException;
import com.skillroute.model.SkillDictionary;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentSkill;
import com.skillroute.model.id.StudentSkillId;
import com.skillroute.properties.MessageProperties;
import com.skillroute.repository.SkillDictionaryRepository;
import com.skillroute.repository.SkillRepository;
import com.skillroute.repository.StudentProfileRepository;
import com.skillroute.repository.StudentSkillRepository;
import com.skillroute.service.client.GitHubSearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern GITHUB_PATTERN = Pattern.compile("^(?:https?://)?(?:www\\.)?github\\.com/([a-zA-Z0-9-]+)(?:/.*|\\?.*|#.*)?$");

    @Transactional
    public void syncSkills(Long accountId) {
        StudentProfile student = profileRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getStudentNotFound()));

        String username = extractUsernameFromUrl(student.getGithubUrl());

        List<SkillDictionary> dictionary = dictionaryRepository.findAll();

        for (SkillDictionary dictEntry : dictionary) {
            int occurrences = gitHubClient.countImportOccurrences(username, dictEntry.getImportPattern());

            if (occurrences > 0) {
                upsertStudentSkill(accountId, dictEntry.getSkillId(), occurrences);
            }

            sleepSafely(2500);
        }
    }

    private void upsertStudentSkill(Long studentId, Long skillId, int occurrences) {
        StudentSkillId id = new StudentSkillId(studentId, skillId);

        StudentSkill studentSkill = studentSkillRepository.findById(id)
                .orElseGet(() -> {
                    StudentSkill newSkill = new StudentSkill();
                    newSkill.setId(id);
                    newSkill.setSkill(skillRepository.findById(skillId).orElseThrow(() -> new EntityNotFoundException(messages.getEntity().getSkillNotFound())));
                    return newSkill;
                });

        studentSkill.setConfirmedByGitHub(true);
        int newLevel = calculateLevel(occurrences);

        if (newLevel > studentSkill.getLevel()) {
            studentSkill.setLevel(newLevel);
        }

        studentSkillRepository.save(studentSkill);
    }

    private String extractUsernameFromUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new GithubUrlNotFoundException(messages.getGithub().getUrlRequired());
        }

        Matcher matcher = GITHUB_PATTERN.matcher(url.trim());

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new DataMappingException(messages.getGithub().getLoginExtractFailed().formatted(url));
    }

    private int calculateLevel(int occurrences) {
        if (occurrences >= 50) return 5;
        if (occurrences >= 20) return 4;
        if (occurrences >= 10) return 3;
        if (occurrences >= 3)  return 2;
        return 1;
    }

    private void sleepSafely(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
