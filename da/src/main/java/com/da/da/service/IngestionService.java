package com.da.da.service;

import com.da.da.repository.ProductRepository;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    @Autowired
    private ProductRepository productRepository; // Kết nối 5432
    
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore; // Kết nối 5433
    
    @Autowired
    private EmbeddingModel embeddingModel;

    public void syncProductsToVectorDb() {
        productRepository.findAll().forEach(p -> {
            // Dùng các trường thực tế: name, description, productCategory
            String content = String.format("Sản phẩm: %s. Loại: %s. Mô tả: %s", 
                p.getName(), 
                p.getProductCategory(), 
                p.getDescription());

            TextSegment segment = TextSegment.from(content);
            // Tạo vector và lưu vào pgvector
            embeddingStore.add(embeddingModel.embed(segment).content(), segment);
        });
    }

    public void ingestStorePolicy(String policyContent) {
        // Biến các quy định bảo hành, ship hàng thành Vector
        TextSegment segment = TextSegment.from("Thông tin chính sách cửa hàng: " + policyContent);
        embeddingStore.add(embeddingModel.embed(segment).content(), segment);
    }
}