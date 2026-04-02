package com.da.da.controller;

import com.da.da.entity.Customer;
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
        // 1. Lấy thông tin User đang đăng nhập (Tương tự như bên CartController)
        Customer user = (Customer) session.getAttribute("user");
        
        // 2. Nếu đã đăng nhập thì dùng User ID, nếu chưa thì dùng Session ID tạm thời
        String memoryId = (user != null) ? String.valueOf(user.getId()) : session.getId(); 
        
        // 3. Truyền xuống cho Manager
        return chatbotManager.processChat(memoryId, message);
    }
    
//    @PostMapping("/chat")
//    public String chat(@RequestBody String message, HttpSession session) {
//        // Lấy ID duy nhất của phiên làm việc hiện tại
//        String memoryId = session.getId(); 
//        
//        // Truyền ID này xuống cho Manager xử lý
//        return chatbotManager.processChat(memoryId, message);
//    }
   
}