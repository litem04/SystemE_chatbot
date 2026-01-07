package com.da.da.controller;

import com.da.da.service.ChatbotManager;
import com.da.da.service.DigitalStoreAssistant;
import com.da.da.service.IngestionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {



    @Autowired
    private IngestionService ingestionService;

    @Autowired
    private ChatbotManager chatbotManager;
    
    // 1. Chạy cái này ĐẦU TIÊN để nạp dữ liệu vào Vector DB (5433)
    @GetMapping("/ingest")
    public String ingest() {
        try {
            ingestionService.syncProductsToVectorDb();
            return "Đã nạp dữ liệu sản phẩm vào Vector Database thành công!";
        } catch (Exception e) {
            return "Lỗi nạp dữ liệu: " + e.getMessage();
        }
    }

    // 2. Endpoint để bạn chat với Gemini
 //   @PostMapping("/chat")
//    public String chat(@RequestBody String message) {
//        // AI sẽ tự động: 
//        // - Tìm thông số ở 5433
//        // - Lấy giá/tồn kho ở 5432 (thông qua ProductTools)
//    	 try {
//    	        return assistant.chat(message);
//    	    } catch (RuntimeException e) {
//    	        if (e.getMessage().contains("429")) {
//    	            return "Dạ, hiện tại em đang hơi bận một chút, bồ đợi em khoảng 30 giây rồi nhắn lại nhé!";
//    	        }
//    	        throw e;
//    	    }
//    }
	 
    @PostMapping("/chat")
    public String chat(@RequestBody String message, HttpSession session) {
        // Lấy ID duy nhất của phiên làm việc hiện tại
        String memoryId = session.getId(); 
        
        // Truyền ID này xuống cho Manager xử lý
        return chatbotManager.processChat(memoryId, message);
    }
   
}