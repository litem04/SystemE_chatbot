package com.da.da.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatbotManager {

    private static final Logger log = LoggerFactory.getLogger(ChatbotManager.class);

    private final DigitalStoreAssistant geminiAssistant;
    private final DigitalStoreAssistant ollamaAssistant;

    public ChatbotManager(@Qualifier("geminiAssistant") DigitalStoreAssistant geminiAssistant,
                          @Qualifier("ollamaAssistant") DigitalStoreAssistant ollamaAssistant) {
        this.geminiAssistant = geminiAssistant;
        this.ollamaAssistant = ollamaAssistant;
    }

    public String processChat(String memoryId, String message) {
        try {
            return geminiAssistant.chat(memoryId, message);
        } catch (Exception e) {
            log.warn("Gemini gặp sự cố ({}), tự động chuyển sang mô hình Ollama dự phòng...", e.getMessage());
            try {
                return ollamaAssistant.chat(memoryId, message) + "\n\n*(Phản hồi từ mô hình dự phòng)*";
            } catch (Exception ex) {
                log.error("Cả hai mô hình AI Gemini và Ollama đều không khả dụng: ", ex);
                return "Hệ thống tư vấn AI hiện đang bận. Bạn vui lòng thử lại sau ít phút hoặc để lại tin nhắn.";
            }
        }
    }
}
