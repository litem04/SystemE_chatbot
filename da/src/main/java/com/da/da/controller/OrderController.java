package com.da.da.controller;


import com.da.da.entity.*;
import com.da.da.repository.*;
import com.da.da.service.EmailService;
import com.da.da.service.PaymentService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.da.da.service.PdfService;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.entity.OrderDetail;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Autowired private CartRepository cartRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private ProductRepository productRepository; // Để trừ tồn kho
    @Autowired
    private EmailService emailService;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private PaymentService paymentService;
    // 1. HIỂN THỊ TRANG CHECKOUT (Điền địa chỉ)
    
    
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Cart> allItems = cartRepository.findByCustomerIdOrderByIdAsc(Long.valueOf(user.getId()));
        
        List<Cart> validItems = new ArrayList<>();
        double grandTotal = 0;

        for (Cart item : allItems) {
            // KIỂM TRA: Nếu không có sản phẩm thì bỏ qua, không cộng tiền, không hiển thị
            if (item.getProduct() != null) {
                validItems.add(item);
                
                // Tính tiền dựa trên giá thực tế
                double price = (item.getProduct().getDiscountPrice() != null && item.getProduct().getDiscountPrice() > 0) 
                               ? item.getProduct().getDiscountPrice() 
                               : item.getProduct().getPrice();
                
                grandTotal += price * item.getQuantity();
            }
        }

        if (validItems.isEmpty()) return "redirect:/cart"; 

        model.addAttribute("cartItems", validItems); // Chỉ gửi danh sách đã lọc sạch rác
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("user", user);

        return "client/checkout";
    }
 
    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute Order order, Model model) {
        // 1. Thiết lập mặc định
        order.setOrderDate(new Date());
        order.setOrderStatus("Pending");
        order.setPaymentStatus("Unpaid");

        // 2. Lưu vào DB
        Order savedOrder = orderRepository.save(order);

        System.out.println("Phương thức thanh toán nhận được: " + order.getPaymentMode());

        // 3. Kiểm tra điều hướng
        if ("VIETQR".equals(order.getPaymentMode())) {
            String qrUrl = paymentService.getVietQRUrl(savedOrder);
            model.addAttribute("qrUrl", qrUrl);
            model.addAttribute("order", savedOrder);
            return "client/payment_vietqr"; // Chuyển đến trang hiển thị mã QR
        } 
        else if ("MOMO".equals(order.getPaymentMode())) {
            return "redirect:/momo/pay/" + savedOrder.getId();
        } 
        else {
            // CHỈ trả về trang success nếu là COD hoặc các trường hợp khác
            return "redirect:/order/success"; 
        }
    }

    // 2. XỬ LÝ ĐẶT HÀNG (Lưu vào DB)
    
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String address,
                             @RequestParam String phone,
                             @RequestParam String paymentMode,
                             HttpSession session,
                             Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // A. Lấy giỏ hàng hiện tại
        List<Cart> cartItems = cartRepository.findByCustomerId(Long.valueOf(user.getId()));
        if (cartItems.isEmpty()) return "redirect:/";

        // B. Tạo Đơn Hàng Tổng (Lưu tạm trước để có ID)
        Order order = new Order();
        order.setCustomerName(user.getName());
        order.setEmailId(user.getEmail());
        order.setMobileNumber(phone);
        order.setAddress(address);
        order.setOrderDate(new Date());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("Unpaid");
        order.setPaymentMode(paymentMode);
        
        if ("VIETQR".equals(paymentMode)) {
            // Nếu là chuyển khoản, trạng thái là Chờ thanh toán
            order.setOrderStatus("WAITING_FOR_PAYMENT"); 
        } else {
            // Nếu là COD, trạng thái là Chờ xử lý/Chờ xác nhận
            order.setOrderStatus("PENDING"); 
        }
        Order savedOrder = orderRepository.save(order);
        

        double grandTotalForOrder = 0; // Biến tính tổng tiền thực tế

        // C. Lưu Chi Tiết & Trừ Kho
        for (Cart item : cartItems) {
            Product p = item.getProduct();
            
            // --- CHỐT CHẶN 1: Kiểm tra Product null (Fix lỗi 500) ---
            if (p == null) {
                cartRepository.delete(item); // Xóa luôn bản ghi rác này trong DB
                continue; // Bỏ qua món này, tiếp tục món tiếp theo
            }

            int quantityBuy = item.getQuantity();

            // 1. Kiểm tra tồn kho
            if (p.getStock() < quantityBuy) {
                orderRepository.delete(savedOrder); 
                return "redirect:/cart?error=out_of_stock"; 
            }

            // 2. Logic tính giá (giữ nguyên logic trộn giá của bạn)
            int limit = (p.getDiscountLimit() != null) ? p.getDiscountLimit() : 0;
            int currentSold = (p.getDiscountSold() != null) ? p.getDiscountSold() : 0;
            Double originalPrice = p.getPrice();
            Double salePrice = (p.getDiscountPrice() != null && p.getDiscountPrice() > 0) ? p.getDiscountPrice() : originalPrice;

            int availableSlots = Math.max(0, limit - currentSold);
            boolean isSaleActive = (p.getDiscountPrice() != null && p.getDiscountPrice() > 0);
            
            Double finalPriceForDetail = originalPrice;
            int soldCountToAdd = 0;

            if (isSaleActive && availableSlots > 0) {
                if (quantityBuy <= availableSlots) {
                    finalPriceForDetail = salePrice;
                    soldCountToAdd = quantityBuy;
                } else {
                    // TRỘN GIÁ: (Suất rẻ * Giá sale + Suất đắt * Giá gốc) / Tổng mua
                    double totalMoneyItem = (availableSlots * salePrice) + ((quantityBuy - availableSlots) * originalPrice);
                    finalPriceForDetail = totalMoneyItem / quantityBuy; 
                    soldCountToAdd = availableSlots;
                }
            }

            // Cộng dồn vào tổng tiền đơn hàng (Sửa lỗi sai tiền 33 triệu)
            grandTotalForOrder += (finalPriceForDetail * quantityBuy);

            // 3. Cập nhật Database Product
            p.setStock(p.getStock() - quantityBuy);
            if (soldCountToAdd > 0) {
                p.setDiscountSold(currentSold + soldCountToAdd);
            }
            productRepository.save(p);

            // 4. Lưu OrderDetail
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(p);
            detail.setProductName(p.getName());
            detail.setQuantity(quantityBuy);
            detail.setPrice(finalPriceForDetail);
            orderDetailRepository.save(detail);
        }

/*        // D. Cập nhật lại tổng tiền ĐƠN HÀNG chuẩn & gửi Email
        savedOrder.setProductTotalPrice(String.valueOf(grandTotalForOrder));
        orderRepository.save(savedOrder); // Lưu lại lần nữa với giá chuẩn

        if (savedOrder.getEmailId() != null) {
            emailService.sendOrderConfirmation(savedOrder.getEmailId(), String.valueOf(savedOrder.getId()), savedOrder.getProductTotalPrice());
        }

        cartRepository.deleteAll(cartItems);
        return "redirect:/order-success";  */
     // D. CẬP NHẬT ĐIỀU HƯỚNG THANH TOÁN
        savedOrder.setProductTotalPrice(String.valueOf(grandTotalForOrder));
        orderRepository.save(savedOrder); 

        // Gửi email xong thì check điều hướng
        if (savedOrder.getEmailId() != null) {
            emailService.sendOrderConfirmation(savedOrder.getEmailId(), String.valueOf(savedOrder.getId()), savedOrder.getProductTotalPrice());
        }
        cartRepository.deleteAll(cartItems);

        // 3. LOGIC RẼ NHÁNH THANH TOÁN Ở ĐÂY
        if ("VIETQR".equals(paymentMode)) {
            String qrUrl = paymentService.getVietQRUrl(savedOrder);
            model.addAttribute("qrUrl", qrUrl);
            model.addAttribute("order", savedOrder);
            return "client/payment_vietqr"; // Phải dùng return trực tiếp, không redirect
        } 
        else if ("MOMO".equals(paymentMode)) {
            return "redirect:/momo/pay/" + savedOrder.getId();
        }

        return "redirect:/order-success"; // Dành cho COD
    }
    
 
    
    // 3. TRANG THÔNG BÁO THÀNH CÔNG
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "client/order-success";
    }
 // 4. XEM LỊCH SỬ ĐƠN HÀNG (MY ORDERS)
    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Lấy danh sách đơn hàng của user này
        List<Order> myOrders = orderRepository.findByEmailIdOrderByIdDesc(user.getEmail());
        
        model.addAttribute("orders", myOrders);
        return "client/my-orders"; // Trả về file giao diện
    }
    @Autowired 
    private OrderDetailRepository OrderDetailRepository; // Đảm bảo đã autowired cái này

    // 5. XEM CHI TIẾT ĐƠN HÀNG (Phía Client)
    @GetMapping("/my-orders/view/{id}")
    public String viewOrderDetails(@PathVariable Integer id, HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // 1. Tìm đơn hàng theo ID
        Order order = orderRepository.findById(id).orElse(null);

        // 2. KIỂM TRA BẢO MẬT (Quan trọng)
        // Nếu không có đơn, hoặc đơn này KHÔNG PHẢI của user đang đăng nhập (check email)
        if (order == null || !order.getEmailId().equals(user.getEmail())) {
            return "redirect:/my-orders"; // Đá về trang danh sách
        }

        // 3. Lấy chi tiết sản phẩm trong đơn
        // (Hàm findByOrder này bạn đã viết trong OrderDetailRepository lúc làm Admin rồi)
        List<OrderDetail> details = orderDetailRepository.findByOrder(order);

        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "client/order-details"; // Trả về file giao diện chi tiết
    }
    @GetMapping("/api/order/status/{id}")
    @ResponseBody
    public ResponseEntity<?> checkStatus(@PathVariable Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            // In ra màn hình console của Eclipse/STS để bạn xem nó có chạy vào đây không
            System.out.println("Đang check đơn: " + id + " - Trạng thái: " + order.getPaymentStatus());
            
            return ResponseEntity.ok(java.util.Map.of(
                "paymentStatus", order.getPaymentStatus()
            ));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/download-invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Integer id, HttpSession session) {
        
        // 1. Kiểm tra đăng nhập
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build(); // Chưa đăng nhập -> Lỗi 401
        }

        // 2. Tìm đơn hàng theo ID
        // .orElse(null) nghĩa là: tìm không thấy thì trả về null
        Order order = orderRepository.findById(id).orElse(null);

        // --- ĐÂY LÀ CHỖ BẠN ĐANG THIẾU ---
        // 3. Kiểm tra xem có tìm thấy đơn hàng không?
        if (order == null) {
            // Nếu order là null (không tìm thấy), lập tức trả về lỗi Not Found
            // Không được chạy tiếp xuống dưới, nếu chạy tiếp sẽ bị lỗi NullPointerException như bạn gặp
            return ResponseEntity.notFound().build(); 
        }
        // ----------------------------------

        // 4. Bảo mật: Kiểm tra đơn hàng này có phải của người đang đăng nhập không?
        // (Tránh trường hợp user A tải hóa đơn của user B)
        // Lưu ý: Đảm bảo user.getEmail() và order.getEmailId() không bị null
        if (order.getEmailId() == null || !order.getEmailId().equals(user.getEmail())) {
             return ResponseEntity.status(403).build(); // Cấm truy cập
        }

        // 5. Lấy danh sách sản phẩm
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);

        // 6. Gọi Service tạo PDF
        ByteArrayInputStream bis = pdfService.exportInvoicePdf(order, orderDetails);

        // Check nếu service bị lỗi trả về null
        if (bis == null) {
             return ResponseEntity.internalServerError().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
  
}
