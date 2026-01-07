package com.da.da.controller;


import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository; // Nhớ autowire thêm mấy cái này
    @Autowired 
    private OrderRepository orderRepository;
    // productRepository đã có rồi



    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Định dạng dấu chấm cho hàng nghìn theo chuẩn VN
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);

        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("userCount", customerRepository.count());
        model.addAttribute("orderCount", orderRepository.count());

        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;     // Doanh thu thực tế (Đã giao)
        BigDecimal shippingRevenue = BigDecimal.ZERO;  // Đang giao/Chờ xử lý

        Map<String, Double> chartDataMap = new TreeMap<>();

        for (Order order : allOrders) {
            String priceStr = order.getProductTotalPrice();
            String status = order.getOrderStatus();
            Date date = order.getOrderDate();

            if (priceStr == null || priceStr.trim().isEmpty()) continue;

            try {
                // FIX LỖI NHÂN 10: Chuyển chuỗi sang Double trước để xử lý dấu thập phân .0
                // Sau đó mới đưa vào BigDecimal để tính toán chính xác
                double rawPrice = Double.parseDouble(priceStr);
                BigDecimal price = BigDecimal.valueOf(rawPrice);

                String st = (status != null) ? status.trim().toLowerCase() : "";

                // 1. NHÓM THÀNH CÔNG (Đã giao)
                if (st.contains("đã giao") || st.contains("thành công") || st.contains("delivered")) {
                    totalRevenue = totalRevenue.add(price);

                    if (date != null) {
                        String dKey = new java.text.SimpleDateFormat("dd/MM").format(date);
                        chartDataMap.put(dKey, chartDataMap.getOrDefault(dKey, 0.0) + rawPrice);
                    }
                } 
                // 2. NHÓM ĐANG XỬ LÝ / ĐANG GIAO (Tính vào mục Đang giao)
                else if (st.contains("chờ") || st.contains("đang") || st.contains("shipped") || st.contains("pending")) {
                    shippingRevenue = shippingRevenue.add(price);
                }
                // 3. NHÓM HỦY (Bỏ qua hoàn toàn)
                else {
                    // Các trạng thái hủy, trả hàng... không cộng vào đâu cả
                }

            } catch (Exception e) {
                System.err.println("Lỗi parse tiền đơn ID " + order.getId() + ": " + priceStr);
            }
        }

        // XỬ LÝ DỮ LIỆU CHART
        StringBuilder labelsBuild = new StringBuilder("[");
        StringBuilder dataBuild = new StringBuilder("[");
        int i = 0;
        for (Map.Entry<String, Double> entry : chartDataMap.entrySet()) {
            if (i > 0) { labelsBuild.append(","); dataBuild.append(","); }
            labelsBuild.append("'").append(entry.getKey()).append("'");
            dataBuild.append(entry.getValue().longValue());
            i++;
        }
        labelsBuild.append("]");
        dataBuild.append("]");

        model.addAttribute("chartLabelsJSON", labelsBuild.toString());
        model.addAttribute("chartDataJSON", dataBuild.toString());

        // Gửi tiền đã định dạng ra View
        model.addAttribute("totalRevenueStr", df.format(totalRevenue) + " đ");
        model.addAttribute("shippingRevenueStr", df.format(shippingRevenue) + " đ");

        return "admin/dashboard";
    }
    // 2. Xem danh sách sản phẩm
    @GetMapping("/products")
    public String viewProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products"; // Trả về file products.html
    }

    // 3. Hiện form thêm sản phẩm mới
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product"; // Trả về file add-product.html
    }

    // 4. Xử lý lưu sản phẩm mới
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, 
                             @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        // 1. Kiểm tra xem người dùng có upload file không
        String fileName = multipartFile.getOriginalFilename();
        boolean isFileUploaded = fileName != null && !fileName.isEmpty();

        if (isFileUploaded) {
            // === TRƯỜNG HỢP 1: CÓ CHỌN FILE ===
            // Lưu tên file vào DB
            product.setImage(fileName);
            
            // Lưu file vào thư mục máy tính
            String uploadDir = "product-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            // === TRƯỜNG HỢP 2: KHÔNG CHỌN FILE ===
            // Kiểm tra xem có nhập Link URL không?
            // (Biến product.getImage() đã hứng giá trị từ ô input text 'Link ảnh' rồi)
            
            if (product.getImage() == null || product.getImage().trim().isEmpty()) {
                // Nếu cả Link cũng trống nốt -> Set ảnh mặc định
                product.setImage("https://via.placeholder.com/300"); 
            }
            // Nếu có Link thì giữ nguyên Link đó
        }

        if (product.getActive() == null) product.setActive("Active");
        productRepository.save(product);
        
        return "redirect:/admin/products";
    }
    
    // 5. Xóa sản phẩm
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }
    
  
    
    @GetMapping("/products/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // Tạo formatter để khử số E, chỉ lấy số nguyên
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(0);

            // Format lại giá bán và giá gốc nếu chúng không null
            if (product.getPrice() != null) {
                try {
                    double p = Double.parseDouble(product.getPrice().toString());
                    model.addAttribute("formattedPrice", df.format(p));
                } catch (Exception e) {
                    model.addAttribute("formattedPrice", product.getPrice());
                }
            }

            if (product.getMrpPrice() != null) {
                try {
                    double mrp = Double.parseDouble(product.getMrpPrice().toString());
                    model.addAttribute("formattedMrpPrice", df.format(mrp));
                } catch (Exception e) {
                    model.addAttribute("formattedMrpPrice", product.getMrpPrice());
                }
            }

            model.addAttribute("product", product);
            return "admin/add-product"; 
        }
        return "redirect:/admin/products";
    }

    // 2. Xử lý lưu sau khi sửa (POST)
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product, 
                                @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        // Logic y hệt hàm addProduct, nhưng cần chú ý ID
        // Nếu upload ảnh mới -> Thay ảnh cũ
        // Nếu KHÔNG upload ảnh mới -> Giữ nguyên ảnh cũ trong DB
        
        Product oldProduct = productRepository.findById(product.getId()).orElse(null);
        
        String fileName = multipartFile.getOriginalFilename();
        if (fileName != null && !fileName.isEmpty()) {
            // Có chọn ảnh mới -> Lưu ảnh mới
            product.setImage(fileName);
            // (Đoạn code lưu file copy từ hàm addProduct xuống đây)
            String uploadDir = "product-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            // Không chọn ảnh mới -> Lấy lại tên ảnh cũ
            if (oldProduct != null) {
                product.setImage(oldProduct.getImage());
            }
        }
        
        // Giữ lại ngày tạo cũ (nếu cần)
        if (oldProduct != null) product.setCreateDate(oldProduct.getCreateDate());
        
        productRepository.save(product);
        return "redirect:/admin/products";
    }
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Tạo một trình định dạng số hiểu dấu phẩy
        DecimalFormat df = new DecimalFormat("#,###");
        // Đăng ký custom editor cho kiểu Double (hoặc kiểu dữ liệu bạn đang dùng)
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, df, true));
    }
    
    
    
    @GetMapping("/customers")
    public String listCustomers(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Customer> list;
        
        if (keyword != null && !keyword.isEmpty()) {
            // Nếu có từ khóa -> Tìm kiếm
            list = customerRepository.searchCustomer(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            // Không có từ khóa -> Lấy hết
            list = customerRepository.findAll();
        }
        
        model.addAttribute("customers", list);
        return "admin/customer";
    }

    // 2. XÓA KHÁCH HÀNG
    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa! Khách hàng này đã có đơn hàng trong hệ thống.");
        }
        return "redirect:/admin/customers";
    }
    


    @GetMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("id") Integer orderId, 
                                    @RequestParam("status") String newStatus,
                                    RedirectAttributes redirectAttributes) {
        
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order != null) {
            String oldStatus = order.getOrderStatus();

            // Check điều kiện Hủy
            if (("Hủy".equalsIgnoreCase(newStatus) || "Cancelled".equalsIgnoreCase(newStatus)) 
                 && !oldStatus.equalsIgnoreCase(newStatus)) {
                
                // Lấy danh sách sản phẩm từ Repository
                List<OrderDetail> details = orderDetailRepository.findByOrder(order); 
                
                if (details != null) {
                    for (OrderDetail item : details) {
                        Product product = item.getProduct();
                        
                        // --- SỬA Ở ĐÂY ---
                        // Dùng getStock() thay vì getQuantity()
                        
                        // Lấy số tồn kho hiện tại (Nếu null thì coi như bằng 0)
                        int currentStock = (product.getStock() != null) ? product.getStock() : 0;
                        
                        // Cộng lại số lượng (Lưu ý: item.getQuantity() là số lượng khách mua trong đơn, cái này giữ nguyên)
                        int newStock = currentStock + item.getQuantity();
                        
                        // Cập nhật lại kho dùng setStock
                        product.setStock(newStock); 
                        // -----------------

                        productRepository.save(product); 
                    }
                }
            }

            order.setOrderStatus(newStatus);
            orderRepository.save(order);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
        }
        
        return "redirect:/admin/orders/view/" + orderId; 
    }
}