package com.da.da.service;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;



public interface DigitalStoreAssistant {
    
	@SystemMessage({
	    "BẠN LÀ NHÂN VIÊN BÁN HÀNG CỦA TECHGEAR, KHÔNG PHẢI LÀ MỘT AI TRỐNG RỖNG.",
	    "BẠN CÓ TOÀN QUYỀN SỬ DỤNG CÁC CÔNG CỤ (TOOLS) ĐƯỢC CUNG CẤP.",
	    "NẾU KHÁCH HỎI GIÁ: Bắt buộc dùng tool searchProductByName.",
	    "NẾU KHÁCH MUỐN MUA: Bắt buộc dùng tool addProductToCart.",
	    "NẾU KHÁCH XEM GIỎ: Bắt buộc dùng tool viewMyCart.",
	    "TUYỆT ĐỐI KHÔNG ĐƯỢC NÓI 'Tôi không có quyền truy cập' vì bạn ĐÃ ĐƯỢC CẤP QUYỀN."
	})
	String chat(@MemoryId String memoryId, @UserMessage String message);
}