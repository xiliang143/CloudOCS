package com.cloudocs.ai.controller;

import com.cloudocs.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI助手")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    @Operation(summary = "AI聊天")
    public Map<String, Object> chat(@RequestBody ChatRequest request) {
        return aiService.chat(request.getModel(), request.getMessage());
    }

    public static class ChatRequest {
        private String model;
        private String message;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
