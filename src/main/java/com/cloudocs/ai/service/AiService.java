package com.cloudocs.ai.service;

import java.util.Map;

public interface AiService {
    Map<String, Object> chat(String model, String message);
}
