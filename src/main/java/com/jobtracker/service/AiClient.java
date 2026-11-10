package com.jobtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.exception.AiException;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

/** Sirf LLM (Groq) ko call karta hai. Prompts AiService mein hain. */
@Component
public class AiClient {
    private final RestClient rest;
    private final ObjectMapper mapper;
    @Value("${groq.api-key:}") private String apiKey;
    @Value("${groq.model}") private String model;

    public AiClient(ObjectMapper mapper) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);   // 10 sec
        factory.setReadTimeout(45000);      // 45 sec
        this.rest = RestClient.builder().baseUrl("https://api.groq.com/openai/v1").requestFactory(factory).build();
        this.mapper = mapper;
    }

    public String chat(String system, String user, boolean json) {
        if (apiKey == null || apiKey.isBlank()) throw new AiException("AI is not configured. Set GROQ_API_KEY and restart.", 503);
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", 0.3);
        body.put("messages", List.of(Map.of("role", "system", "content", system), Map.of("role", "user", "content", user)));
        if (json) body.put("response_format", Map.of("type", "json_object"));
        try {
            JsonNode res = rest.post().uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON).body(body)
                    .retrieve().body(JsonNode.class);
            return res.path("choices").path(0).path("message").path("content").asText();
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new AiException("AI rate limit reached. Please try again in a minute.", 429);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new AiException("Invalid GROQ_API_KEY.", 502);
        } catch (RestClientResponseException e) {
            throw new AiException("AI request failed (" + e.getStatusCode().value() + "). Check the model name.", 502);
        } catch (ResourceAccessException e) {
            throw new AiException("AI service timed out. Please try again.", 504);
        }
    }

    /** JSON maango, galat aaye to ek baar retry */
    public JsonNode chatJson(String system, String user) {
        for (int i = 0; i < 2; i++) {
            String raw = chat(system, user, true);
            try {
                return mapper.readTree(raw.replaceAll("(?s)```json|```", "").trim());
            } catch (Exception ignored) { /* retry */ }
        }
        throw new AiException("AI returned an invalid response. Please try again.", 502);
    }
}
