package com.da.da.controller;

import com.da.da.dto.AdminAiChatRequest;
import com.da.da.dto.AdminAiChatResponse;
import com.da.da.dto.AdminAiIngestResponse;
import com.da.da.service.AdminAiService;
import com.da.da.service.IngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AdminAiChatResponse> chat(@Valid @RequestBody AdminAiChatRequest request) {
        String reply = adminAiService.processAdminChat(request.message());
        return ResponseEntity.ok(new AdminAiChatResponse(reply));
    }

    @PostMapping("/ingest")
    public ResponseEntity<AdminAiIngestResponse> ingest() {
        try {
            ingestionService.syncProductsToVectorDb();
            return ResponseEntity.ok(new AdminAiIngestResponse("success", "Đã nạp dữ liệu sản phẩm vào Vector Database thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AdminAiIngestResponse("error", "Lỗi nạp dữ liệu: " + e.getMessage()));
        }
    }
}
