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
        int page = 0;
        int size = 100;
        org.springframework.data.domain.Page<com.da.da.entity.Product> productPage;
        do {
            productPage = productRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
            productPage.getContent().forEach(p -> {
                String content = String.format("Sản phẩm: %s. Loại: %s. Mô tả: %s",
                        p.getName(),
                        p.getProductCategory(),
                        p.getDescription());

                TextSegment segment = TextSegment.from(content);
                embeddingStore.add(embeddingModel.embed(segment).content(), segment);
            });
            page++;
        } while (productPage.hasNext());
    }

    public void ingestStorePolicy(String policyContent) {
        TextSegment segment = TextSegment.from("Thông tin chính sách cửa hàng: " + policyContent);
        embeddingStore.add(embeddingModel.embed(segment).content(), segment);
    }
}