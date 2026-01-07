package com.da.da.controller;


import com.da.da.entity.*;
import com.da.da.repository.*;
import com.da.da.service.EmailService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.da.da.service.PdfService;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.entity.OrderDetail;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayInputStream;

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
    // 1. HIỂN THỊ TRANG CHECKOUT (Điền địa chỉ)
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Lấy lại giỏ hàng để tính tổng tiền lần cuối
        List<Cart> cartItems = cartRepository.findByCustomerId(Long.valueOf(user.getId()));
        if (cartItems.isEmpty()) return "redirect:/"; // Giỏ trống thì ko cho vào

        double grandTotal = 0;
        for (Cart item : cartItems) {
            try {
                grandTotal += Double.parseDouble(item.getTotalPrice());
            } catch (Exception e) {}
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("user", user); // Để điền sẵn tên, sđt vào form

        return "client/checkout";
    }

    // 2. XỬ LÝ ĐẶT HÀNG (Lưu vào DB)
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String address,
                             @RequestParam String phone,
                             HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // A. Lấy giỏ hàng hiện tại
        List<Cart> cartItems = cartRepository.findByCustomerId(Long.valueOf(user.getId()));
        if (cartItems.isEmpty()) return "redirect:/";

        // B. Tạo Đơn Hàng Tổng (Order)
        Order order = new Order();
        order.setCustomerName(user.getName()); // Lưu tên người mua
        order.setEmailId(user.getEmail());
        order.setMobileNumber(phone); // SĐT giao hàng
        order.setAddress(address);    // Địa chỉ giao hàng
        order.setOrderDate(new Date());
        order.setOrderStatus("PENDING"); // Trạng thái chờ xử lý
        order.setPaymentMode("COD");     // Thanh toán khi nhận hàng

        // Tính tổng tiền
        double total = 0;
        for (Cart c : cartItems) {
            try { total += Double.parseDouble(c.getTotalPrice()); } catch (Exception e) {}
        }
        order.setProductTotalPrice(String.valueOf(total));
        
   
        // LƯU ĐƠN HÀNG VÀO DB
        Order savedOrder = orderRepository.save(order);
        
        if (savedOrder.getEmailId() != null) {
            emailService.sendOrderConfirmation(
                savedOrder.getEmailId(),               // <--- SỬA CHỖ NÀY
                String.valueOf(savedOrder.getId()), 
                savedOrder.getProductTotalPrice()
            );
          
        }

     

     // C. Lưu Chi Tiết, Trừ Kho & Cập Nhật Số Lượng Đã Bán (LOGIC MỚI)
        for (Cart item : cartItems) {
            Product p = item.getProduct();
            int quantityBuy = item.getQuantity();

            // 1. Kiểm tra tồn kho (An toàn)
            if (p.getStock() < quantityBuy) {
                // Nếu hết hàng giữa chừng -> Có thể xóa đơn hàng vừa tạo để tránh đơn rác (Optional)
                orderRepository.delete(savedOrder); 
                return "redirect:/cart?error=out_of_stock"; 
            }

            // ============================================================
            // 2. TÍNH TOÁN GIÁ & SỐ LƯỢNG SALE (LOGIC TRỘN GIÁ)
            // ============================================================
            
            // Lấy các chỉ số an toàn (tránh NullPointerException)
            int limit = (p.getDiscountLimit() != null) ? p.getDiscountLimit() : 0;
            int currentSold = (p.getDiscountSold() != null) ? p.getDiscountSold() : 0;
            Double originalPrice = p.getPrice();
            Double salePrice = (p.getDiscountPrice() != null) ? p.getDiscountPrice() : originalPrice;

            // Tính số suất sale còn lại
            int availableSlots = limit - currentSold;
            if (availableSlots < 0) availableSlots = 0;

            boolean isSaleActive = (p.getDiscountPrice() != null && p.getDiscountPrice() > 0);
            
            Double finalPriceForDetail = originalPrice; // Mặc định là giá gốc
            int soldCountToAdd = 0; // Biến để cộng thêm vào discountSold

            if (isSaleActive && availableSlots > 0) {
                if (quantityBuy <= availableSlots) {
                    // TRƯỜNG HỢP 1: Mua ít hơn hoặc bằng suất còn lại -> Hưởng trọn giá Sale
                    finalPriceForDetail = salePrice;
                    soldCountToAdd = quantityBuy;
                } else {
                    // TRƯỜNG HỢP 2: Mua lố suất Sale -> TRỘN GIÁ (Cực quan trọng)
                    // Ví dụ: Còn 2 suất, mua 5 cái. -> 2 cái giá Sale, 3 cái giá Gốc
                    int qtyCheap = availableSlots;
                    int qtyExpensive = quantityBuy - availableSlots;
                    
                    double totalMoney = (qtyCheap * salePrice) + (qtyExpensive * originalPrice);
                    
                    // Tính ra đơn giá trung bình để lưu vào OrderDetail
                    finalPriceForDetail = totalMoney / quantityBuy; 
                    
                    soldCountToAdd = qtyCheap; // Chỉ cộng thêm số lượng thực tế được sale
                }
            } else {
                // TRƯỜNG HỢP 3: Không có sale hoặc đã hết suất -> Giá Gốc
                finalPriceForDetail = originalPrice;
            }

            // ============================================================
            // 3. CẬP NHẬT DATABASE (SỬA LỖI KHÔNG TĂNG SỐ ĐÃ BÁN)
            // ============================================================
            
            // A. Trừ kho tổng (Stock)
            p.setStock(p.getStock() - quantityBuy);
            
            // B. Cộng số lượng đã bán Flash Sale (DiscountSold)
            if (soldCountToAdd > 0) {
                p.setDiscountSold(currentSold + soldCountToAdd);
            }
            
            productRepository.save(p); // LƯU LẠI VÀO DB

            // ============================================================
            // 4. LƯU CHI TIẾT ĐƠN HÀNG
            // ============================================================
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(p);
            detail.setProductName(p.getName());
            detail.setQuantity(quantityBuy);
            
            // QUAN TRỌNG: Lưu giá đã tính toán (finalPriceForDetail) thay vì p.getDiscountPrice()
            detail.setPrice(finalPriceForDetail); 
            
            // Nếu bạn có dùng BigDecimal cho totalPrice thì tính ở đây, còn không thì bỏ qua
            // detail.setTotalPrice(BigDecimal.valueOf(finalPriceForDetail * quantityBuy)); 
            
            orderDetailRepository.save(detail);
        }

        // D. Xóa sạch giỏ hàng của khách
        cartRepository.deleteAll(cartItems);

        // E. Chuyển hướng sang trang thông báo thành công
        return "redirect:/order-success";
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
