package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.Product;
import com.da.da.entity.ProductImage;
import com.da.da.repository.*;
import com.da.da.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductImageRepository productImageRepository;
    private final SessionRegistry sessionRegistry;
    private final OrderService orderService;

    public AdminController(ProductRepository productRepository,
                           OrderDetailRepository orderDetailRepository,
                           CustomerRepository customerRepository,
                           OrderRepository orderRepository,
                           ProductReviewRepository reviewRepository,
                           ProductImageRepository productImageRepository,
                           SessionRegistry sessionRegistry,
                           OrderService orderService) {
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.productImageRepository = productImageRepository;
        this.sessionRegistry = sessionRegistry;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);

        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("userCount", customerRepository.count());
        model.addAttribute("orderCount", orderRepository.count());

        // Tính doanh thu trực tiếp từ Database thay vì tải toàn bộ bảng lên RAM
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? df.format(totalRevenue) : "0");

        // Biểu đồ: lấy 50 đơn hàng gần nhất để tổng hợp ngày tháng
        List<Order> recentOrders = orderRepository.findAll();
        Map<String, Double> chartDataMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

        for (Order order : recentOrders) {
            String status = order.getOrderStatus() != null ? order.getOrderStatus().toLowerCase() : "";
            if (status.contains("đã giao") || status.contains("thành công") || status.contains("delivered")) {
                if (order.getOrderDate() != null && order.getProductTotalPrice() != null) {
                    String dKey = sdf.format(order.getOrderDate());
                    chartDataMap.put(dKey, chartDataMap.getOrDefault(dKey, 0.0) + order.getProductTotalPrice().doubleValue());
                }
            }
        }

        model.addAttribute("chartDataMap", chartDataMap);
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Product> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = productRepository.findByNameContainingIgnoreCase(keyword.trim());
            model.addAttribute("keyword", keyword);
        } else {
            list = productRepository.findAll();
        }
        model.addAttribute("products", list);
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFiles") MultipartFile[] multipartFiles) throws IOException {
        if (product.getActive() == null) product.setActive("Active");
        Product savedProduct = productRepository.saveAndFlush(product);

        String uploadDir = "product-images/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        boolean hasUpload = false;
        String firstFileName = null;

        for (MultipartFile file : multipartFiles) {
            if (file != null && !file.isEmpty() && file.getOriginalFilename() != null) {
                String originalName = file.getOriginalFilename();
                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase() : "";

                // Whitelist an toàn cho định dạng ảnh
                if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(ext)) {
                    continue;
                }

                // Sinh tên ngẫu nhiên UUID chống Path Traversal và trùng tên
                String safeFileName = UUID.randomUUID() + ext;
                hasUpload = true;

                try (InputStream inputStream = file.getInputStream()) {
                    Path filePath = uploadPath.resolve(safeFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                ProductImage pi = new ProductImage();
                pi.setImageName(safeFileName);
                pi.setProduct(savedProduct);
                productImageRepository.save(pi);

                if (firstFileName == null) {
                    firstFileName = safeFileName;
                }
            }
        }

        if (hasUpload) {
            savedProduct.setImage(firstFileName);
        } else if (savedProduct.getImage() == null || savedProduct.getImage().trim().isEmpty()) {
            savedProduct.setImage("https://via.placeholder.com/300");
        }

        productRepository.save(savedProduct);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            reviewRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            model.addAttribute("product", product);
            return "admin/add-product";
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException {
        Product oldProduct = productRepository.findById(product.getId()).orElse(null);
        if (oldProduct == null) {
            return "redirect:/admin/products?error=notfound";
        }

        if (multipartFile != null && !multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
            String originalName = multipartFile.getOriginalFilename();
            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase() : "";

            if (List.of(".jpg", ".jpeg", ".png", ".webp").contains(ext)) {
                String safeFileName = UUID.randomUUID() + ext;
                product.setImage(safeFileName);

                String uploadDir = "product-images/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(safeFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } else {
            product.setImage(oldProduct.getImage());
        }

        product.setCreateDate(oldProduct.getCreateDate());
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Customer> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = customerRepository.searchCustomer(keyword.trim());
            model.addAttribute("keyword", keyword);
        } else {
            list = customerRepository.findAll();
        }
        model.addAttribute("customers", list);
        return "admin/customer";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerRepository.findById(id).orElse(null);
            if (customer != null) {
                String email = customer.getEmail();
                List<Object> principals = sessionRegistry.getAllPrincipals();
                for (Object principal : principals) {
                    if (principal instanceof UserDetails user) {
                        if (user.getUsername().equals(email)) {
                            sessionRegistry.getAllSessions(principal, false).forEach(sessionInfo -> sessionInfo.expireNow());
                        }
                    }
                }
                customerRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khách hàng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa! Khách hàng này có thể đã có đơn hàng ràng buộc.");
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("id") Integer orderId,
                                    @RequestParam("status") String newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/admin/orders/view/" + orderId;
    }
}