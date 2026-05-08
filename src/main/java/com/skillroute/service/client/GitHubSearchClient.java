package com.skillroute.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillroute.exception.ServiceUnavailableException;
import com.skillroute.properties.GithubProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class GitHubSearchClient {
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GithubProperties githubProperties;

    public int countImportOccurrences(String username, String importPattern) {
        String query = String.format("import %s user:%s", importPattern, username);

        HttpUrl url = HttpUrl.parse("https://api.github.com/search/code").newBuilder()
                .addQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + githubProperties.getToken())
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 403) {
                throw new ServiceUnavailableException("GitHub API лимит запросов исчерпан. Попробуйте позже");
            }

            if (!response.isSuccessful()) {
                throw new ServiceUnavailableException("Ошибка обращения к GitHub API: " + response.code());
            }

            if (response.body() == null) {
                return 0;
            }

            JsonNode rootNode = objectMapper.readTree(response.body().string());
            return rootNode.path("total_count").asInt(0);

        } catch (IOException e) {
            throw new ServiceUnavailableException("Сервис синхронизации временно недоступен из-за проблем с сетью");
        }
    }
}