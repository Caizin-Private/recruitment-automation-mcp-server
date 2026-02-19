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
            throw new IllegalStateException("OpenAI API key not configured");
        }

        HttpClient client = HttpClient.newHttpClient();

        Map<String, Object> body = new HashMap<>();

        body.put("model", "gpt-4o-mini");

        body.put("input", prompt);

        body.put("temperature", 0);

        body.put("text", Map.of(
                "format", Map.of(
                        "type", "json_object"
                )
        ));

        String requestBody =
                mapper.writeValueAsString(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/responses"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {

            throw new RuntimeException(
                    "OpenAI API error: " +
                            response.statusCode() +
                            " " +
                            response.body()
            );
        }

        JsonNode root =
                mapper.readTree(response.body());

        JsonNode contentArray =
                root.path("output").get(0).path("content");

        for (JsonNode content : contentArray) {

            if ("output_text".equals(content.path("type").asText())) {

                return content.path("text").asText();
            }
        }

        throw new RuntimeException("No output_text found in OpenAI response");
    }
}
