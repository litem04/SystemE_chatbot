package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.service.ChatbotManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private static final int MAX_RATE_LIMIT_ENTRIES = 5000;
    private static final long RATE_LIMIT_WINDOW_MS = 2000;
    private final Map<String, Long> ipRateLimit = new ConcurrentHashMap<>();

    private final ChatbotManager chatbotManager;

    public ChatController(ChatbotManager chatbotManager) {
        this.chatbotManager = chatbotManager;
    }

    @PostMapping(value = "/chat")
    public ResponseEntity<String> chat(@RequestBody(required = false) String message,
                                       HttpSession session,
                                       HttpServletRequest request) {
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Hệ thống: Tin nhắn không được để trống.");
        }

        String trimmedMessage = message.trim();
        if (trimmedMessage.length() > 500) {
            return ResponseEntity.badRequest().body("Hệ thống: Tin nhắn quá dài. Vui lòng nhập tối đa 500 ký tự.");
        }

        String clientIp = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        // Dọn dẹp cache nếu danh sách quá lớn để tránh rò rỉ RAM (Memory Leak Protection)
        if (ipRateLimit.size() > MAX_RATE_LIMIT_ENTRIES) {
            ipRateLimit.entrySet().removeIf(entry -> (now - entry.getValue()) > 60000);
        }

        Long lastRequest = ipRateLimit.get(clientIp);
        if (lastRequest != null && (now - lastRequest) < RATE_LIMIT_WINDOW_MS) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Hệ thống: Bạn đang gửi yêu cầu quá nhanh. Vui lòng thử lại sau 2 giây.");
        }
        ipRateLimit.put(clientIp, now);

        Customer user = (Customer) session.getAttribute("user");
        String memoryId = (user != null) ? String.valueOf(user.getId()) : session.getId();

        String reply = chatbotManager.processChat(memoryId, trimmedMessage);
        return ResponseEntity.ok(reply);
    }
}