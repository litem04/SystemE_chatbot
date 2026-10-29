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
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Value("${langchain4j.google-ai-gemini.chat-model.api-key:}")
    private String geminiApiKey;

    @Value("${langchain4j.google-ai-gemini.chat-model.model-name:gemini-1.5-flash}")
    private String geminiModelName;

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model-name:qwen2.5:3b}")
    private String ollamaModelName;

    @Value("${vector.db.host:localhost}")
    private String vectorDbHost;

    @Value("${vector.db.port:5433}")
    private Integer vectorDbPort;

    @Value("${vector.db.name:chatbot_rag}")
    private String vectorDbName;

    @Value("${vector.db.user:postgres}")
    private String vectorDbUser;

    @Value("${vector.db.password:postgres}")
    private String vectorDbPassword;

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Value("${app.embedding.in-memory:false}")
    private boolean useInMemoryStore;

    @Bean
    @ConditionalOnMissingBean
    EmbeddingStore<TextSegment> embeddingStore() {
        if (useInMemoryStore) {
            return new dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore<>();
        }
        return PgVectorEmbeddingStore.builder()
                .host(vectorDbHost)
                .port(vectorDbPort)
                .database(vectorDbName)
                .user(vectorDbUser)
                .password(vectorDbPassword)
                .table("product_embeddings")
                .dimension(384)
                .build();
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> store, EmbeddingModel model) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(model)
                .maxResults(3)
                .build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }

    @Bean("geminiModel")
    ChatLanguageModel geminiModel() {
        String key = (geminiApiKey != null && !geminiApiKey.isBlank())
                ? geminiApiKey
                : "AIzaSy_DEV_DUMMY_KEY_FOR_LOCAL_STARTUP";
        return GoogleAiGeminiChatModel.builder()
                .apiKey(key)
                .modelName(geminiModelName)
                .build();
    }

    @Bean("ollamaModel")
    ChatLanguageModel ollamaModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .build();
    }

    @Bean("geminiAssistant")
    DigitalStoreAssistant geminiAssistant(@Qualifier("geminiModel") ChatLanguageModel model,
                                         ProductTools tools,
                                         UserOrderTools userOrderTools,
                                         CartTools cartTools,
                                         ContentRetriever retriever,
                                         ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(DigitalStoreAssistant.class)
                .chatLanguageModel(model)
                .tools(tools, userOrderTools, cartTools)
                .contentRetriever(retriever)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    @Bean("ollamaAssistant")
    DigitalStoreAssistant ollamaAssistant(@Qualifier("ollamaModel") ChatLanguageModel model,
                                         ProductTools tools,
                                         UserOrderTools userOrderTools,
                                         CartTools cartTools,
                                         ContentRetriever retriever,
                                         ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(DigitalStoreAssistant.class)
                .chatLanguageModel(model)
                .tools(tools, userOrderTools, cartTools)
                .contentRetriever(retriever)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}