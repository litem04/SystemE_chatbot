package com.techgear.admin.controller;

import com.techgear.admin.dto.AdminAiChatRequest;
import com.techgear.admin.service.AdminAiService;
import com.techgear.assistant.service.IngestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminAiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminAiService adminAiService;

    @Mock
    private IngestionService ingestionService;

    @InjectMocks
    private AdminAiController adminAiController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminAiController).build();
    }

    @Test
    @DisplayName("Admin AI Chat - Hợp lệ trả về 200 OK cùng reply")
    void testChat_Valid_Returns200() throws Exception {
        when(adminAiService.processAdminChat(anyString())).thenReturn("Doanh thu hôm nay là 10.000.000 VNĐ");

        AdminAiChatRequest request = new AdminAiChatRequest("Hôm nay doanh thu bao nhiêu?");

        mockMvc.perform(post("/admin/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Doanh thu hôm nay là 10.000.000 VNĐ"));
    }

    @Test
    @DisplayName("Admin AI Ingest - Thành công trả về 200 OK")
    void testIngest_Success_Returns200() throws Exception {
        mockMvc.perform(post("/admin/api/ai/ingest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}
