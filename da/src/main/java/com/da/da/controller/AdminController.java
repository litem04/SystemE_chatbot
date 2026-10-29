package com.da.da.controller;

import com.da.da.dto.DailyRevenueProjection;
import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.Product;
import com.da.da.service.AdminCustomerService;
import com.da.da.service.OrderService;
import com.da.da.service.ProductService;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.da.da.dto.ProductRequestDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ProductService productService;
    private final AdminCustomerService adminCustomerService;
    private final OrderService orderService;

    public AdminController(ProductService productService,
                           AdminCustomerService adminCustomerService,
                           OrderService orderService) {
        this.productService = productService;
        this.adminCustomerService = adminCustomerService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);

        model.addAttribute("productCount", productService.countProducts());
        model.addAttribute("userCount", adminCustomerService.countCustomers());
        model.addAttribute("orderCount", orderService.countOrders());

        // Tính doanh thu trực tiếp từ Database
        BigDecimal totalRevenue = orderService.calculateTotalRevenue();
        model.addAttribute("totalRevenueStr", totalRevenue != null ? df.format(totalRevenue) : "0");
        BigDecimal shippingRevenue = orderService.calculateShippingRevenue();
        model.addAttribute("shippingRevenueStr", shippingRevenue != null ? df.format(shippingRevenue) : "0");

        // Biểu đồ: Tổng hợp trực tiếp từ Database (PostgreSQL TO_CHAR & GROUP BY) thay vì kéo bảng lên RAM
        List<DailyRevenueProjection> stats = orderService.getDailyRevenueStatistics();
        List<String> labels = new java.util.ArrayList<>();
        List<BigDecimal> data = new java.util.ArrayList<>();
        for (DailyRevenueProjection stat : stats) {
            BigDecimal amt = stat.getTotalAmount() != null ? stat.getTotalAmount() : BigDecimal.ZERO;
            labels.add(stat.getOrderDay());
            data.add(amt);
        }

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);

        // Báo cáo & Cảnh báo quản trị ERP (ERP-style Analytics & Warnings)
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(5));
        model.addAttribute("orderStatusSummary", orderService.getOrderStatusSummary());
        model.addAttribute("topSellingProducts", orderService.getTopSellingProducts(5));

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "category", required = false) String category,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        Page<Product> productPage = productService.searchAdminProducts(keyword, category, page, size);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        ProductRequestDTO product = new ProductRequestDTO();
        product.setDiscountSold(0);
        model.addAttribute("product", product);
        return "admin/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@jakarta.validation.Valid @ModelAttribute ProductRequestDTO request,
                             org.springframework.validation.BindingResult bindingResult,
                             @RequestParam("imageFiles") MultipartFile[] multipartFiles,
                             Model model,
                             RedirectAttributes ra) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Dữ liệu chưa hợp lệ. Vui lòng kiểm tra các trường được đánh dấu.");
            model.addAttribute("product", request);
            return "admin/add-product";
        }
        try {
            productService.addProduct(request, multipartFiles);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("product", request);
            return "admin/add-product";
        }
        ra.addFlashAttribute("successMessage", "Đã thêm sản phẩm thành công.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            log.error("Không thể xóa sản phẩm ID {}", id, e);
            ra.addFlashAttribute("errorMessage", "Không thể xóa sản phẩm đang được tham chiếu bởi dữ liệu khác.");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product != null) {
            ProductRequestDTO request = new ProductRequestDTO();
            request.setId(product.getId());
            request.setName(product.getName());
            request.setDescription(product.getDescription());
            request.setPrice(product.getPrice());
            request.setMrpPrice(product.getMrpPrice());
            request.setProductCategory(product.getProductCategory());
            request.setDiscountPrice(product.getDiscountPrice());
            request.setDiscountLimit(product.getDiscountLimit());
            request.setDiscountSold(product.getDiscountSold());
            request.setStock(product.getStock());
            request.setImage(product.getImage());
            model.addAttribute("product", request);
            return "admin/add-product";
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/update")
    public String updateProduct(@jakarta.validation.Valid @ModelAttribute ProductRequestDTO request,
                                org.springframework.validation.BindingResult bindingResult,
                                @RequestParam(value = "imageFiles", required = false) MultipartFile[] multipartFiles,
                                Model model,
                                RedirectAttributes ra) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Dữ liệu chưa hợp lệ. Vui lòng kiểm tra các trường được đánh dấu.");
            model.addAttribute("product", request);
            return "admin/add-product";
        }
        try {
            MultipartFile multipartFile = multipartFiles == null ? null
                    : java.util.Arrays.stream(multipartFiles).filter(file -> file != null && !file.isEmpty()).findFirst().orElse(null);
            productService.updateProduct(request, multipartFile);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("product", request);
            return "admin/add-product";
        }
        ra.addFlashAttribute("successMessage", "Đã cập nhật sản phẩm thành công.");
        return "redirect:/admin/products";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size) {
        org.springframework.data.domain.Page<Customer> customerPage = adminCustomerService.searchCustomers(keyword, page, size);
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("page", customerPage);
        model.addAttribute("keyword", keyword);
        return "admin/customer";
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            adminCustomerService.deleteCustomerAndExpireSessions(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa! Khách hàng này có thể đã có đơn hàng ràng buộc: " + e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/toggle-lock/{id}")
    public String toggleLockCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean locked = adminCustomerService.toggleLockCustomer(id);
            if (locked) {
                redirectAttributes.addFlashAttribute("successMessage", "Đã KHÓA tài khoản khách hàng ID #" + id + " thành công!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Đã MỞ KHÓA tài khoản khách hàng ID #" + id + " thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}