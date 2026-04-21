package com.cloudocs.ai.service.impl;

import com.cloudocs.ai.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.default-model:MiniMax-Text-01}")
    private String defaultModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> chat(String model, String message) {
        try {
            // 后端始终使用配置的默认模型，忽略前端传来的模型参数
            String actualModel = defaultModel;

            // 构建请求体 - MiniMax 格式
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", actualModel);

            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", message);
            requestBody.put("messages", objectMapper.createArrayNode().add(messageNode));

            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            // 发送HTTP请求
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            String requestJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .timeout(Duration.ofSeconds(120))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> result = new HashMap<>();
            result.put("status", response.statusCode());
            result.put("body", response.body());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                log.info("MiniMax API 响应: {}", responseBody);
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // MiniMax 响应格式: choices[0].message.content
                JsonNode choices = jsonNode.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode firstChoice = choices.get(0);
                    JsonNode msgNode = firstChoice.get("message");
                    if (msgNode != null && msgNode.has("content")) {
                        String content = msgNode.get("content").asText();
                        result.put("content", content);
                    }
                }
            } else {
                result.put("error", response.body());
            }

            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            log.error("AI服务异常: {}", e.getMessage(), e);
            error.put("status", 500);
            error.put("error", e.getMessage());
            return error;
        }
    }
}
