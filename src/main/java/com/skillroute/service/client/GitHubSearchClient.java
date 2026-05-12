package com.skillroute.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillroute.exception.ServiceUnavailableException;
import com.skillroute.properties.GithubProperties;
import com.skillroute.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GitHubSearchClient {
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GithubProperties githubProperties;
    private final MessageProperties messages;

    public Map<String, Integer> collectProfileSignals(String username) {
        Map<String, Integer> signals = new HashMap<>();
        HttpUrl url = HttpUrl.parse("https://api.github.com/users/" + username + "/repos").newBuilder().addQueryParameter("per_page", "100").build();

        Request request = createRequest(url);

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ServiceUnavailableException(messages.getGithub().getApiError().formatted(response.code()));
            }
            JsonNode repos = objectMapper.readTree(response.body().string());
            for (JsonNode repo : repos) {
                addSignal(signals, repo.path("language").asText(), 10);
                addSignal(signals, repo.path("name").asText(), 5);
                addSignal(signals, repo.path("description").asText(), 3);
                repo.path("topics").forEach(topic -> addSignal(signals, topic.asText(), 7));
            }
        } catch (IOException e) {
            throw new ServiceUnavailableException(messages.getGithub().getNetworkError());
        }
        return signals;
    }

    public boolean hasCodeMatch(String username, String query) {
        if (query == null || query.isBlank()) return false;

        String fullQuery = "user:" + username + " " + query.trim();

        HttpUrl url = HttpUrl.parse("https://api.github.com/search/code").newBuilder().addQueryParameter("q", fullQuery).build();

        Request request = createRequest(url);

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 403) {
                throw new ServiceUnavailableException(messages.getGithub().getRateLimitExceeded());
            }

            if (!response.isSuccessful()) {
                throw new ServiceUnavailableException(messages.getGithub().getApiError().formatted(response.code()));
            }

            JsonNode root = objectMapper.readTree(response.body().string());
            int total = root.path("total_count").asInt();
            return total > 0;
        } catch (IOException e) {
            throw new ServiceUnavailableException(messages.getGithub().getNetworkError());
        }
    }

    private Request createRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Authorization", "Bearer " + githubProperties.getToken())
                .addHeader("User-Agent", "SkillRoute-App")
                .build();
    }

    private void addSignal(Map<String, Integer> signals, String value, int weight) {
        String normalized = normalize(value);
        if (!normalized.isBlank()) {
            String[] parts = normalized.split("[^a-zA-Z0-9#++]+");
            for (String part : parts) {
                if (part.length() > 2) signals.merge(part, weight, Integer::sum);
            }
        }
    }

    public String normalize(String val) {
        if (val == null || val.equalsIgnoreCase("null")) return "";
        return val.toLowerCase().trim();
    }
}