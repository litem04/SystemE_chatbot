package com.da.da.config;

import com.da.da.service.DigitalStoreAssistant;
import com.da.da.service.tool.CartTools;
import com.da.da.service.tool.ProductTools;
import com.da.da.service.tool.UserOrderTools;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LangChainConfig {
	
	@Bean
	EmbeddingModel embeddingModel() {
	    // Model này siêu nhẹ, chạy ngay trong RAM của bồ, không cần API Key
	    return new AllMiniLmL6V2EmbeddingModel();
	}  
	
	@Bean
	ContentRetriever contentRetriever(EmbeddingStore<TextSegment> store, EmbeddingModel model) {
	    return EmbeddingStoreContentRetriever.builder()
	            .embeddingStore(store)
	            .embeddingModel(model) // Nó sẽ tự tìm cái Bean EmbeddingModel bồ vừa tạo ở trên
	            .maxResults(3)
	            .build();
	}
	
	
	// Thêm Bean này vào file LangChainConfig.java
	@Bean
	public ChatMemoryProvider chatMemoryProvider() {
	    // Mỗi memoryId (mỗi người dùng) sẽ có một bộ nhớ riêng, nhớ tối đa 10 tin nhắn
	    return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
	}


	// 1. Não bộ Gemini
    @Bean
    ChatLanguageModel geminiModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey("AIzaSyCvh9rCYQVWbHH9pARKXrBcG8KinDtpeys") // Key của bồ
                .modelName("gemini-flash-latest")
                .build();
    }

    // 2. Não bộ Ollama
    @Bean
    ChatLanguageModel ollamaModel() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen2.5:3b")
                .build();
    }
    

    // 3. Trợ lý dùng não Gemini
    @Bean("geminiAssistant")
    DigitalStoreAssistant geminiAssistant(@Qualifier("geminiModel") ChatLanguageModel model, ProductTools tools, UserOrderTools userOrderTools,CartTools cartTools, // 1. Inject nó vào đây
            ContentRetriever retriever,  ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(DigitalStoreAssistant.class)
                .chatLanguageModel(model)
                .tools(tools, userOrderTools, cartTools)
                .contentRetriever(retriever)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    // 4. Trợ lý dùng não Ollama
    @Bean("ollamaAssistant")
    DigitalStoreAssistant ollamaAssistant(@Qualifier("ollamaModel") ChatLanguageModel model, ProductTools tools,UserOrderTools userOrderTools,CartTools cartTools, // 1. Inject nó vào đây
            ContentRetriever retriever,  ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(DigitalStoreAssistant.class)
                .chatLanguageModel(model)
                .tools(tools, userOrderTools, cartTools)
                .contentRetriever(retriever)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}