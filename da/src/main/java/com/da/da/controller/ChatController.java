package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.service.ChatbotManager;
import com.da.da.service.IngestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ai")
public class ChatController {
    
    private final java.util.Map<String, Long> ipRateLimit = new ConcurrentHashMap<>();

    private final IngestionService ingestionService;
    private final ChatbotManager chatbotManager;

    public ChatController(IngestionService ingestionService, ChatbotManager chatbotManager) {
        this.ingestionService = ingestionService;
        this.chatbotManager = chatbotManager;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message, HttpSession session, jakarta.servlet.http.HttpServletRequest request) {
        if (message != null && message.length() > 500) {
            return "Hệ thống: Tin nhắn quá dài. Vui lòng nhập tối đa 500 ký tự.";
        }

        String clientIp = request.getRemoteAddr();
        Long lastRequest = ipRateLimit.get(clientIp);
        long now = System.currentTimeMillis();
        if (lastRequest != null && (now - lastRequest) < 3000) {
            return "Hệ thống: Bạn đang gửi yêu cầu quá nhanh. Vui lòng thử lại sau 3 giây.";
        }
        ipRateLimit.put(clientIp, now);

        Customer user = (Customer) session.getAttribute("user");
        String memoryId = (user != null) ? String.valueOf(user.getId()) : session.getId();
        return chatbotManager.processChat(memoryId, message);
    }
}