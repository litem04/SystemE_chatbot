package com.da.da.service;

import com.da.da.entity.Product;
import com.da.da.entity.ProductImage;
import com.da.da.repository.ProductImageRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.repository.ProductReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.da.da.dto.ProductRequestDTO;
import java.util.Date;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    public ProductService(ProductRepository productRepository,
                          ProductImageRepository productImageRepository,
                          ProductReviewRepository reviewRepository,
                          FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.reviewRepository = reviewRepository;
        this.fileStorageService = fileStorageService;
    }

    private void validateProductPrices(Product product) {
        if (product == null) return;
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá gốc của sản phẩm không được âm.");
        }
        if (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (product.getPrice() == null || product.getDiscountPrice().compareTo(product.getPrice()) > 0) {
                throw new IllegalArgumentException(String.format("Giá khuyến mãi (%s) không được lớn hơn giá gốc (%s).",
                        product.getDiscountPrice(), product.getPrice()));
            }
        }
    }

    @Transactional
    public Product addProduct(ProductRequestDTO request, MultipartFile[] multipartFiles) throws IOException {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrpPrice(request.getMrpPrice());
        product.setProductCategory(request.getProductCategory());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setDiscountLimit(request.getDiscountLimit());
        product.setStock(request.getStock());
        product.setCreateDate(new Date());
        product.setDiscountSold(0);
        product.setActive("Active");

        validateProductPrices(product);
        Product savedProduct = productRepository.saveAndFlush(product);

        String uploadDir = "product-images/";
        boolean hasUpload = false;
        String firstFileName = null;

        if (multipartFiles != null) {
            for (MultipartFile file : multipartFiles) {
                if (file != null && !file.isEmpty()) {
                    String safeFileName = fileStorageService.storeFile(file, uploadDir);
                    if (safeFileName != null) {
                        hasUpload = true;
                        ProductImage pi = new ProductImage();
                        pi.setImageName(safeFileName);
                        pi.setProduct(savedProduct);
                        productImageRepository.save(pi);

                        if (firstFileName == null) {
                            firstFileName = safeFileName;
                        }
                    }
                }
            }
        }

        if (hasUpload) {
            savedProduct.setImage(firstFileName);
        } else if (savedProduct.getImage() == null || savedProduct.getImage().trim().isEmpty()) {
            savedProduct.setImage("https://via.placeholder.com/300");
        }

        return productRepository.save(savedProduct);
    }

    @Transactional
    public Product updateProduct(ProductRequestDTO request, MultipartFile multipartFile) throws IOException {
        Product oldProduct = productRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm ID: " + request.getId()));

        oldProduct.setName(request.getName());
        oldProduct.setDescription(request.getDescription());
        oldProduct.setPrice(request.getPrice());
        oldProduct.setMrpPrice(request.getMrpPrice());
        oldProduct.setProductCategory(request.getProductCategory());
        oldProduct.setDiscountPrice(request.getDiscountPrice());
        oldProduct.setDiscountLimit(request.getDiscountLimit());
        oldProduct.setStock(request.getStock());

        validateProductPrices(oldProduct);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String safeFileName = fileStorageService.storeFile(multipartFile, "product-images/");
            if (safeFileName != null) {
                if (oldProduct.getImage() != null && !oldProduct.getImage().startsWith("http")) {
                    fileStorageService.deleteFile("product-images/" + oldProduct.getImage());
                }
                oldProduct.setImage(safeFileName);
            }
        }
        return productRepository.save(oldProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        reviewRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public long countProducts() {
        return productRepository.count();
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByProductCategory(category);
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByProductCategory(categoryName);
    }

    @Transactional(readOnly = true)
    public String searchProductForAi(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        if (products.isEmpty()) {
            return "Hiện tại cửa hàng không có sản phẩm nào khớp với từ khóa: " + keyword;
        }

        StringBuilder sb = new StringBuilder("Dưới đây là các sản phẩm tìm thấy:\n");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        boolean isLaptopSearch = false;

        for (Product p : products) {
            String priceText = p.getPrice() != null ? currencyFormat.format(p.getPrice()) : "Liên hệ";
            sb.append(String.format("- ID: %d | Tên: %s | Giá: %s | Tồn kho: %d | Mô tả: %s\n",
                    p.getId(), p.getName(), priceText, p.getStock() != null ? p.getStock() : 0, p.getDescription()));

            if (p.getProductCategory() != null && p.getProductCategory().toLowerCase().contains("laptop")) {
                isLaptopSearch = true;
            }
        }

        if (isLaptopSearch) {
            List<Product> accessories = getRecommendedAccessories();
            if (!accessories.isEmpty()) {
                sb.append("\n[GỢI Ý PHỤ KIỆN KÈM THEO]:\n");
                for (Product acc : accessories) {
                    String accPrice = acc.getPrice() != null ? currencyFormat.format(acc.getPrice()) : "";
                    sb.append(String.format(" + Phụ kiện: %s | Giá: %s\n", acc.getName(), accPrice));
                }
            }
        }

        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<Product> getRecommendedAccessories() {
        String categoryName = "Accessories";
        Pageable limitFour = PageRequest.of(0, 4);
        return productRepository.findAccessoriesForLaptop(categoryName, "chuột", "phím", "tai nghe", limitFour);
    }

    @Transactional(readOnly = true)
    public List<Product> getDynamicAccessories(Product product) {
        if (product == null || product.getProductCategory() == null || product.getName() == null) {
            return new ArrayList<>();
        }

        String categoryName = product.getProductCategory().toLowerCase();
        String productName = product.getName().toLowerCase();
        List<String> tempKeywords = new ArrayList<>();

        if (categoryName.contains("laptop") || categoryName.contains("pc") || categoryName.contains("máy tính") || categoryName.contains("macbook")) {
            tempKeywords = List.of("chuột", "bàn phím", "tai nghe", "balo", "đế tản nhiệt");
        } else if (categoryName.contains("điện") || categoryName.contains("phone") || categoryName.contains("dien thoai") || categoryName.contains("smart") || categoryName.contains("mobile") || categoryName.contains("apple") || categoryName.contains("ipad") || categoryName.contains("tablet")) {
            tempKeywords = List.of("ốp", "sạc", "dự phòng", "tai nghe", "cáp");
        } else if (categoryName.contains("camera") || categoryName.contains("máy ảnh") || productName.contains("gopro")) {
            tempKeywords = List.of("light", "thẻ nhớ", "tripod", "pin");
        } else {
            tempKeywords = List.of("chuột", "sạc", "tai nghe");
        }

        final List<String> finalKeywords = tempKeywords;
        List<Product> results = new ArrayList<>();
        int page = 0;
        int size = 50;
        while (results.size() < 4) {
            Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size));
            if (!productPage.hasContent()) break;

            for (Product p : productPage.getContent()) {
                if (p.getId() != null && !p.getId().equals(product.getId()) && p.getName() != null) {
                    String currentProductName = p.getName().toLowerCase();
                    if (finalKeywords.stream().anyMatch(currentProductName::contains)) {
                        results.add(p);
                        if (results.size() == 4) break;
                    }
                }
            }
            page++;
        }
        return results;
    }
}