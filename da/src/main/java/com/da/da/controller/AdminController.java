package com.da.da.controller;


import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.entity.ProductImage;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductImageRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.repository.ProductReviewRepository;

import jakarta.transaction.Transactional;

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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired
    private ProductReviewRepository reviewRepository;
    @Autowired private ProductImageRepository productImageRepository;
    @Autowired
    private SessionRegistry sessionRegistry;

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
        BigDecimal totalRevenue = BigDecimal.ZERO;    
        BigDecimal shippingRevenue = BigDecimal.ZERO;  
        Map<String, Double> chartDataMap = new TreeMap<>();

        for (Order order : allOrders) {
            String priceStr = order.getProductTotalPrice();
            String status = order.getOrderStatus();
            Date date = order.getOrderDate();

            if (priceStr == null || priceStr.trim().isEmpty()) continue;

            try {
               
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
    // 2. Xem danh sách sản phẩm\
    @GetMapping("/products")
    public String viewProducts(@RequestParam(value = "category", required = false) String category, Model model) {
       // List<Product> listProducts;
        List<Product> list = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        
        // Kiểm tra nếu người dùng có chọn danh mục (Mobile, Laptop, Phụ kiện)
        if (category != null && !category.isEmpty()) {
            // Gọi hàm tìm kiếm theo danh mục từ Repository
        	list = productRepository.findByProductCategory(category);
            model.addAttribute("selectedCategory", category); // Gửi lại để biết đang lọc mục nào
        } else {
            // Nếu không chọn gì thì lấy tất cả như cũ
        	list = productRepository.findAll();
        }

     //   model.addAttribute("products", listProducts);
        model.addAttribute("products", list);
        return "admin/products"; 
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
                             @RequestParam("imageFiles") MultipartFile[] multipartFiles) throws IOException {
        
        // 1. Thiết lập các giá trị mặc định cho Product
        if (product.getActive() == null) product.setActive("Active");
        
        // 2. LƯU VÀ ÉP GHI (Save and Flush) để chắc chắn Product có ID ngay lập tức
        Product savedProduct = productRepository.saveAndFlush(product);

        String uploadDir = "product-images/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        boolean hasUpload = false;
        String firstFileName = null;

        for (MultipartFile file : multipartFiles) {
            if (file != null && !file.getOriginalFilename().isEmpty()) {
                hasUpload = true;
                String fileName = file.getOriginalFilename();

                // Lưu file vào thư mục
                try (InputStream inputStream = file.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // 3. Lưu vào bảng ProductImage (Album)
                ProductImage pi = new ProductImage();
                pi.setImageName(fileName);
                pi.setProduct(savedProduct); // Bây giờ savedProduct chắc chắn đã có ID
                productImageRepository.save(pi);

                // Lưu lại tên ảnh đầu tiên để làm ảnh đại diện
                if (firstFileName == null) {
                    firstFileName = fileName;
                }
            }
        }

        // 4. Cập nhật ảnh đại diện cho Product
        if (hasUpload) {
            savedProduct.setImage(firstFileName);
        } else if (savedProduct.getImage() == null || savedProduct.getImage().trim().isEmpty()) {
            savedProduct.setImage("https://via.placeholder.com/300"); 
        }

        // Lưu lại Product lần cuối cùng với ảnh đại diện
        productRepository.save(savedProduct);
        
        return "redirect:/admin/products";
    }
  
    @GetMapping("/products/delete/{id}")
    @Transactional // Đảm bảo nếu xóa review xong mà xóa product lỗi thì nó sẽ Rollback lại
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            // 1. Xóa các đánh giá liên quan đến sản phẩm này trước
            reviewRepository.deleteByProductId(id);
            
            // 2. Bây giờ mới xóa sản phẩm
            productRepository.deleteById(id);
            
            ra.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
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
                                @RequestParam(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException {
        
        // 1. Tìm sản phẩm cũ trong DB để lấy lại các thông tin không thay đổi
        Product oldProduct = productRepository.findById(product.getId()).orElse(null);
        
        if (oldProduct == null) {
            return "redirect:/admin/products?error=notfound";
        }

        // 2. Xử lý hình ảnh
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // CÓ CHỌN ẢNH MỚI: Lưu file mới
            String fileName = multipartFile.getOriginalFilename();
            product.setImage(fileName);
            
            String uploadDir = "product-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            // KHÔNG CHỌN ẢNH MỚI: Giữ nguyên tên ảnh cũ từ DB
            product.setImage(oldProduct.getImage());
        }
        
        // 3. Giữ lại các thông tin cũ không có trong form (ví dụ: ngày tạo)
        product.setCreateDate(oldProduct.getCreateDate());
        
        // 4. Lưu cập nhật
        productRepository.save(product);
        return "redirect:/admin/products";
    }
   /*  @PostMapping("/products/update")
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
    } */
    
    
    
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
            // 1. Lấy thông tin khách hàng trước khi xóa để biết Email
            Customer customer = customerRepository.findById(id).orElse(null);
            
            if (customer != null) {
                String email = customer.getEmail();

                // 2. Tìm và kick Session của người dùng này
                List<Object> principals = sessionRegistry.getAllPrincipals();
                for (Object principal : principals) {
                    if (principal instanceof UserDetails) {
                        UserDetails user = (UserDetails) principal;
                        if (user.getUsername().equals(email)) {
                            // Tìm thấy User, lấy tất cả session đang chạy
                            sessionRegistry.getAllSessions(principal, false).forEach(sessionInfo -> {
                                sessionInfo.expireNow(); // Đuổi ra ngay lập tức!
                            });
                        }
                    }
                }

                // 3. Xóa trong DB
                customerRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khách hàng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa! Khách hàng này đã có đơn hàng.");
        }
        return "redirect:/admin/customers";
    }
   /* @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa! Khách hàng này đã có đơn hàng trong hệ thống.");
        }
        return "redirect:/admin/customers";
    }*/
    


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