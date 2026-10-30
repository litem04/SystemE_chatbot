package com.techgear.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminAiChatRequest(
    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    @Size(max = 2000, message = "Câu hỏi tối đa 2000 ký tự")
    String message
) {}
