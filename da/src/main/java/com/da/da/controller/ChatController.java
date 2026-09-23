package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.service.ChatbotManager;
import com.da.da.service.IngestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final IngestionService ingestionService;
    private final ChatbotManager chatbotManager;

    public ChatController(IngestionService ingestionService, ChatbotManager chatbotManager) {
        this.ingestionService = ingestionService;
        this.chatbotManager = chatbotManager;
    }

    @GetMapping("/ingest")
    public String ingest() {
        try {
            ingestionService.syncProductsToVectorDb();
            return "Đã nạp dữ liệu sản phẩm vào Vector Database thành công!";
        } catch (Exception e) {
            return "Lỗi nạp dữ liệu: " + e.getMessage();
        }
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message, HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        String memoryId = (user != null) ? String.valueOf(user.getId()) : session.getId();
        return chatbotManager.processChat(memoryId, message);
    }
}