package com.da.da.service;

import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.entity.ProductReview;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.repository.ProductReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ReviewService(ProductReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         CustomerRepository customerRepository,
                         OrderDetailRepository orderDetailRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Transactional
    public ProductReview saveReview(Long productId, String email, String comment, Integer rating) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Vui lòng đăng nhập để đánh giá sản phẩm.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Số sao đánh giá phải từ 1 đến 5 sao.");
        }
        if (comment == null || comment.trim().isBlank()) {
            throw new IllegalArgumentException("Nội dung nhận xét không được để trống.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: ID " + productId));

        Customer customer = customerRepository.findByEmail(email.trim().toLowerCase());
        if (customer == null) {
            throw new IllegalArgumentException("Tài khoản người dùng không tồn tại.");
        }

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setComment(comment.trim());
        review.setRating(rating);
        review.setDate(new Date());

        ProductReview saved = reviewRepository.save(review);
        log.info("Khách hàng {} đã đánh giá {} sao cho sản phẩm ID #{}", email, rating, productId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ProductReview> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByDateDesc(productId);
    }

    @Transactional(readOnly = true)
    public double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRating(productId);
        if (avg == null) {
            return 5.0; // Mặc định 5 sao nếu chưa có đánh giá
        }
        return Math.round(avg * 10.0) / 10.0;
    }

    @Transactional(readOnly = true)
    public long getReviewCount(Long productId) {
        Long count = reviewRepository.countByProductId(productId);
        return count != null ? count : 0L;
    }

    @Transactional(readOnly = true)
    public boolean hasUserPurchased(String email, Long productId) {
        if (email == null || productId == null) return false;
        return orderDetailRepository.hasPurchasedDeliveredProduct(email.trim().toLowerCase(), productId);
    }
}
