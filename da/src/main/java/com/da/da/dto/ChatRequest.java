package com.da.da.dto;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private List<Map<String, Object>> contents;
    public ChatRequest(String text) {
        this.contents = List.of(Map.of("parts", List.of(Map.of("text", text))));
    }
}