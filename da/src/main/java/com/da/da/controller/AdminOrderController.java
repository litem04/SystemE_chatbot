package com.da.da.controller;


import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.service.EmailService;
import com.da.da.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private EmailService emailService;
    // 1. XEM DANH SÁCH TẤT CẢ ĐƠN HÀNG
    @GetMapping("")
    public String listOrders(Model model) {
        // Lấy list order, sắp xếp cái mới nhất lên đầu (giảm dần theo ID)
        List<Order> orders = orderRepository.findAll();
        // Bạn có thể sort orders bằng Java Stream hoặc sửa Repository sau
        model.addAttribute("orders", orders);
        return "admin/orders"; // Trả về file admin/orders.html
    }

 // 2. XEM CHI TIẾT 1 ĐƠN HÀNG (Code hoàn chỉnh)
    @GetMapping("/view/{id}")
    public String viewOrderDetails(@PathVariable Integer id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return "redirect:/admin/orders";

        // Lấy danh sách sản phẩm trong đơn đó
        List<OrderDetail> details = orderDetailRepository.findByOrder(order);
        
        model.addAttribute("order", order);
        model.addAttribute("details", details);
        
        return "admin/order-details"; // Trả về file admin/order-details.html
    }
    
    // 3. CẬP NHẬT TRẠNG THÁI ĐƠN (VD: Đã giao hàng)
//    @PostMapping("/update-status")
//    public String updateStatus(@RequestParam Integer id, @RequestParam String status, RedirectAttributes redirectAttributes) {
//        Order order = orderRepository.findById(id).orElse(null);
//        if (order != null) {
//            String oldStatus = order.getOrderStatus();
//            
//            // Kiểm tra nếu đơn hàng được hủy và trước đó chưa bị hủy
//            if (("CANCELLED".equalsIgnoreCase(status) || "Hủy".equalsIgnoreCase(status)) 
//                && !("CANCELLED".equalsIgnoreCase(oldStatus) || "Hủy".equalsIgnoreCase(oldStatus))) {
//                
//                // Lấy danh sách sản phẩm trong đơn hàng
//                List<OrderDetail> details = orderDetailRepository.findByOrder(order);
//                
//                if (details != null && !details.isEmpty()) {
//                    // Hoàn trả stock cho từng sản phẩm
//                    for (OrderDetail item : details) {
//                        Product product = item.getProduct();
//                        if (product != null) {
//                            // Lấy số tồn kho hiện tại (nếu null thì coi như bằng 0)
//                            int currentStock = (product.getStock() != null) ? product.getStock() : 0;
//                            
//                            // Cộng lại số lượng đã bán
//                            int newStock = currentStock + item.getQuantity();
//                            
//                            // Cập nhật lại kho
//                            product.setStock(newStock);
//                            productRepository.save(product);
//                        }
//                    }
//                }
//            }
//            
//            // Cập nhật trạng thái đơn hàng
//            order.setOrderStatus(status);
//            orderRepository.save(order);
//            
//            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
//        }
//        return "redirect:/admin/orders/view/" + id;
//    }
//    
    
	    @PostMapping("/update-status")
	    public String updateStatus(@RequestParam("id") Integer id, 
	                               @RequestParam("status") String status, 
	                               RedirectAttributes ra) {
	        Order order = orderRepository.findById(id).orElse(null);
	        if (order == null) {
	            ra.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
	            return "redirect:/admin/orders";
	        }

	        // Logic chặn quay lại trạng thái cũ (ví dụ: đã Hủy/Đã giao thì không được sửa)
	        String currentStatus = order.getOrderStatus();
	        if ("CANCELLED".equals(currentStatus) || "DELIVERED".equals(currentStatus)) {
	            ra.addFlashAttribute("error", "Đơn hàng đã kết thúc, không thể thay đổi trạng thái!");
	            return "redirect:/admin/orders/view/" + id;
	        }

	        // Cập nhật trạng thái mới
	        order.setOrderStatus(status);
	        orderRepository.save(order);
	        
	        ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
	        
	        
	     // 1. Cập nhật trạng thái vào DB
	        order.setOrderStatus(status);
	        orderRepository.save(order);

	        // 2. Gửi mail cho khách hàng (Dùng Try-Catch để tránh lỗi gửi mail làm dừng chương trình)
	        try {
	            if (order.getEmailId() != null && !order.getEmailId().isEmpty()) {
	                emailService.sendOrderStatusEmail(order.getEmailId(), order.getId(), status);
	            }
	        } catch (Exception e) {
	            System.err.println("Lỗi gửi mail: " + e.getMessage());
	            // Bạn vẫn báo thành công vì DB đã cập nhật, chỉ là khách chưa nhận được mail thôi
	        }

	        ra.addFlashAttribute("success", "Cập nhật thành công và đã gửi mail thông báo cho khách!");
	    
	        return "redirect:/admin/orders/view/" + id;
	    }
			
	 // MỚI: Hàm xử lý khi Admin nhấn nút "Xác nhận đã nhận tiền" thủ công
	    @PostMapping("/confirm-payment")
	    public String confirmPaymentManually(@RequestParam("id") Integer id, RedirectAttributes ra) {
	        Order order = orderRepository.findById(id).orElse(null);
	        if (order == null) {
	            ra.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
	            return "redirect:/admin/orders";
	        }

	        // Cập nhật trạng thái tiền
	        order.setPaymentStatus("Paid");
	        // Nếu đơn đang là chờ thanh toán thì đẩy luôn sang Chờ xử lý để Admin đi hàng
	        if ("WAITING_FOR_PAYMENT".equals(order.getOrderStatus())) {
	            order.setOrderStatus("PENDING");
	        }
	        orderRepository.save(order);

	        // Gửi mail báo khách đã nhận được tiền thành công
	        try {
	            if (order.getEmailId() != null && !order.getEmailId().isEmpty()) {
	                emailService.sendOrderStatusEmail(order.getEmailId(), order.getId(), "Đã thanh toán thành công");
	            }
	        } catch (Exception e) {
	            System.err.println("Lỗi gửi mail: " + e.getMessage());
	        }

	        ra.addFlashAttribute("success", "Đã xác nhận thanh toán thành công cho đơn hàng!");
	        return "redirect:/admin/orders/view/" + id;
	    }
}	    