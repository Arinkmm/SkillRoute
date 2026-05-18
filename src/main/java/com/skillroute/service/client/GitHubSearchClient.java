package com.skillroute.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillroute.exception.GitHubRateLimitException;
import com.skillroute.exception.ServiceUnavailableException;
import com.skillroute.properties.GithubProperties;
import com.skillroute.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
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
            String body = readBody(response);
            checkRateLimit(response, body);

            if (!response.isSuccessful()) {
                throw new ServiceUnavailableException(messages.getGithub().getApiError().formatted(response.code()));
            }
            JsonNode repos = objectMapper.readTree(body);
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
            String body = readBody(response);
            checkRateLimit(response, body);

            if (!response.isSuccessful()) {
                throw new ServiceUnavailableException(messages.getGithub().getApiError().formatted(response.code()));
            }

            JsonNode root = objectMapper.readTree(body);
            int total = root.path("total_count").asInt();
            return total > 0;
        } catch (IOException e) {
            throw new ServiceUnavailableException(messages.getGithub().getNetworkError());
        }
    }

    private Request createRequest(HttpUrl url) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .addHeader("User-Agent", "SkillRoute-App");

        if (githubProperties.getToken() != null && !githubProperties.getToken().isBlank()) {
            builder.addHeader("Authorization", "Bearer " + githubProperties.getToken());
        }

        return builder.build();
    }

    private String readBody(Response response) throws IOException {
        return response.body() == null ? "" : response.body().string();
    }

    private void checkRateLimit(Response response, String body) {
        if (!isRateLimited(response, body)) {
            return;
        }

        throw new GitHubRateLimitException(messages.getGithub().getRateLimitExceeded(), resolveRetryAfter(response));
    }

    private boolean isRateLimited(Response response, String body) {
        if (response.code() != 403 && response.code() != 429) {
            return false;
        }

        String normalizedBody = body == null ? "" : body.toLowerCase(Locale.ROOT);
        return response.header("Retry-After") != null
                || "0".equals(response.header("X-RateLimit-Remaining"))
                || "0".equals(response.header("x-ratelimit-remaining"))
                || normalizedBody.contains("rate limit");
    }

    private LocalDateTime resolveRetryAfter(Response response) {
        String retryAfter = response.header("Retry-After");
        if (retryAfter != null && retryAfter.matches("\\d+")) {
            return LocalDateTime.now().plusSeconds(Long.parseLong(retryAfter));
        }

        String reset = response.header("X-RateLimit-Reset");
        if (reset == null) {
            reset = response.header("x-ratelimit-reset");
        }

        if (reset != null && reset.matches("\\d+")) {
            return Instant.ofEpochSecond(Long.parseLong(reset))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        return LocalDateTime.now().plusMinutes(githubProperties.getSync().getDefaultRateLimitWaitMinutes());
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
