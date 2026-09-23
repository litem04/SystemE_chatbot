package com.da.da.service;

import com.da.da.repository.ProductRepository;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    private final ProductRepository productRepository;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public IngestionService(ProductRepository productRepository,
                            EmbeddingStore<TextSegment> embeddingStore,
                            EmbeddingModel embeddingModel) {
        this.productRepository = productRepository;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    public void syncProductsToVectorDb() {
        productRepository.findAll().forEach(p -> {
            String content = String.format("Sản phẩm: %s. Loại: %s. Mô tả: %s",
                    p.getName(),
                    p.getProductCategory(),
                    p.getDescription());

            TextSegment segment = TextSegment.from(content);
            embeddingStore.add(embeddingModel.embed(segment).content(), segment);
        });
    }

    public void ingestStorePolicy(String policyContent) {
        TextSegment segment = TextSegment.from("Thông tin chính sách cửa hàng: " + policyContent);
        embeddingStore.add(embeddingModel.embed(segment).content(), segment);
    }
}