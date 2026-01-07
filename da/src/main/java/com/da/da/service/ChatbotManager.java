package com.da.da.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatbotManager {

    @Autowired
    @Qualifier("geminiAssistant")
    private DigitalStoreAssistant geminiAssistant;

    @Autowired
    @Qualifier("ollamaAssistant")
    private DigitalStoreAssistant ollamaAssistant;

//    public String processChat(String message) {
//
//
//        try {
//            // 1. Thử dùng Gemini trước
//            return geminiAssistant.chat(message);
//        } catch (Exception e) {
//            // 2. Nếu Gemini lỗi (429, 500, hoặc lỗi mạng...)
//            System.err.println("Gemini gặp sự cố: " + e.getMessage());
//            
//            // Kiểm tra xem có phải lỗi "Hết hạn mức" (429) không để log cho rõ
//            if (e.getMessage().contains("429")) {
//                System.out.println("Lý do: Hết quota Gemini. Đang chuyển sang Ollama...");
//            }
//
//            try {
//                // 3. GỌI OLLAMA Ở ĐÂY (Bỏ comment và thực thi)
//                String response = ollamaAssistant.chat(message);
//                return response + "\n\n*(Phản hồi từ Ollama dự phòng)*";
//            } catch (Exception ollamaEx) {
//                // 4. Nếu đen đủi là cả Ollama cũng chưa bật hoặc lỗi
//                return "Cả Gemini và hệ thống dự phòng đều không khả dụng. Lỗi: " + ollamaEx.getMessage();
//            }
//        }
//    }
    
    public String processChat(String memoryId, String message) {
        try {
            // Truyền memoryId vào hàm chat
            return geminiAssistant.chat(memoryId, message);
        } catch (Exception e) {
            // Nếu Gemini lỗi, dùng Ollama nhưng vẫn dùng chung memoryId để nhớ mạch cũ
            try {
                return ollamaAssistant.chat(memoryId, message) + "\n*(Dự phòng từ Ollama)*";
            } catch (Exception ex) {
                return "Lỗi hệ thống: " + ex.getMessage();
            }
        }
    }
		
}
