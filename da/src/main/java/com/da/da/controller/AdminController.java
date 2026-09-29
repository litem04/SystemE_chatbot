package com.da.da.controller;

import com.da.da.dto.DailyRevenueProjection;
import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.Product;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.service.AdminCustomerService;
import com.da.da.service.OrderService;
import com.da.da.service.ProductService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final AdminCustomerService adminCustomerService;
    private final OrderService orderService;

    public AdminController(ProductRepository productRepository,
                           CustomerRepository customerRepository,
                           OrderRepository orderRepository,
                           ProductService productService,
                           AdminCustomerService adminCustomerService,
                           OrderService orderService) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
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
        model.addAttribute("totalRevenue", totalRevenue != null ? df.format(totalRevenue) : "0");

        // Biểu đồ: Tổng hợp trực tiếp từ Database (PostgreSQL TO_CHAR & GROUP BY) thay vì kéo bảng lên RAM
        List<DailyRevenueProjection> stats = orderService.getDailyRevenueStatistics();
        Map<String, BigDecimal> chartDataMap = new LinkedHashMap<>();
        List<String> labels = new java.util.ArrayList<>();
        List<BigDecimal> data = new java.util.ArrayList<>();
        for (DailyRevenueProjection stat : stats) {
            BigDecimal amt = stat.getTotalAmount() != null ? stat.getTotalAmount() : BigDecimal.ZERO;
            chartDataMap.put(stat.getOrderDay(), amt);
            labels.add("'" + stat.getOrderDay() + "'");
            data.add(amt);
        }

        model.addAttribute("chartDataMap", chartDataMap);
        model.addAttribute("chartLabelsJSON", labels.toString());
        model.addAttribute("chartDataJSON", data.toString());
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "category", required = false) String category) {
        List<Product> list;
        if (category != null && !category.trim().isEmpty()) {
            list = productService.findByCategory(category.trim());
            model.addAttribute("selectedCategory", category.trim());
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            list = productService.searchByName(keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            list = productService.findAll();
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
    public String addProduct(@jakarta.validation.Valid @ModelAttribute ProductRequestDTO request,
                             org.springframework.validation.BindingResult bindingResult,
                             @RequestParam("imageFiles") MultipartFile[] multipartFiles,
                             RedirectAttributes ra) throws IOException {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("errorMessage", "Dữ liệu thêm mới không hợp lệ. Vui lòng kiểm tra lại giá, số lượng, v.v.");
            return "redirect:/admin/products/add";
        }
        productService.addProduct(request, multipartFiles);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "admin/add-product";
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/update")
    public String updateProduct(@jakarta.validation.Valid @ModelAttribute ProductRequestDTO request,
                                org.springframework.validation.BindingResult bindingResult,
                                @RequestParam(value = "imageFile", required = false) MultipartFile multipartFile,
                                RedirectAttributes ra) throws IOException {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("errorMessage", "Dữ liệu cập nhật không hợp lệ. Vui lòng kiểm tra lại giá, số lượng, v.v.");
            return "redirect:/admin/products/update/" + request.getId();
        }
        try {
            productService.updateProduct(request, multipartFile);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
            return "redirect:/admin/products";
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Customer> list = adminCustomerService.searchCustomers(keyword);
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("keyword", keyword);
        }
        model.addAttribute("customers", list);
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


}