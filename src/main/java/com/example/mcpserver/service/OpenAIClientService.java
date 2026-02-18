package com.example.mcpserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAIClientService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public String getResponseText(String prompt) throws Exception {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("OpenAI API key not configured (openai.api.key)");
        }

        HttpClient client = HttpClient.newHttpClient();

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1");
        body.put("input", prompt);
        body.put("temperature", 0);

        String requestBody = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("OpenAI API returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());

        JsonNode outputNode = root.path("output");
        if (!outputNode.isArray() || outputNode.size() == 0) {
            throw new RuntimeException("Unexpected OpenAI response format: missing output");
        }

        JsonNode contentNode = outputNode.get(0).path("content");
        if (!contentNode.isArray() || contentNode.size() == 0) {
            throw new RuntimeException("Unexpected OpenAI response format: missing content");
        }

        String text = contentNode.get(0).path("text").asText(null);
        if (text == null) {
            throw new RuntimeException("Unexpected OpenAI response format: missing text");
        }

        return text;
    }
}
