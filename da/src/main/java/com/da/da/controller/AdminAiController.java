package com.da.da.controller;

import com.da.da.service.AdminAiService;
import com.da.da.service.IngestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/ai")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAiController {

    private final AdminAiService adminAiService;
    private final IngestionService ingestionService;

    public AdminAiController(AdminAiService adminAiService, IngestionService ingestionService) {
        this.adminAiService = adminAiService;
        this.ingestionService = ingestionService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("message", "");
        String reply = adminAiService.processAdminChat(query);
        return Map.of("reply", reply);
    }

    @PostMapping("/ingest")
    public Map<String, String> ingest() {
        try {
            ingestionService.syncProductsToVectorDb();
            return Map.of("status", "success", "message", "Đã nạp dữ liệu sản phẩm vào Vector Database thành công!");
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Lỗi nạp dữ liệu: " + e.getMessage());
        }
    }
}
