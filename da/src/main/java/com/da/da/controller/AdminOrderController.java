package com.da.da.controller;

import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.service.EmailService;
import com.da.da.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderService orderService;
    private final EmailService emailService;

    public AdminOrderController(OrderRepository orderRepository,
                                OrderDetailRepository orderDetailRepository,
                                OrderService orderService,
                                EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderService = orderService;
        this.emailService = emailService;
    }

    @GetMapping("")
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/view/{id}")
    public String viewOrderDetails(@PathVariable Integer id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return "redirect:/admin/orders";

        List<OrderDetail> details = orderDetailRepository.findByOrder(order);
        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "admin/order-details";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("id") Integer id,
                               @RequestParam("status") String status,
                               RedirectAttributes ra) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            ra.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/admin/orders";
        }

        String currentStatus = order.getOrderStatus() != null ? order.getOrderStatus().toUpperCase() : "";
        if ("CANCELLED".equals(currentStatus) || "DELIVERED".equals(currentStatus)) {
            ra.addFlashAttribute("error", "Đơn hàng đã kết thúc, không thể thay đổi trạng thái!");
            return "redirect:/admin/orders/view/" + id;
        }

        try {
            orderService.updateOrderStatus(id, status);
            ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            log.error("Lỗi cập nhật trạng thái đơn hàng #{}: ", id, e);
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/orders/view/" + id;
    }

    @PostMapping("/confirm-payment")
    public String confirmPaymentManually(@RequestParam("id") Integer id, RedirectAttributes ra) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            ra.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/admin/orders";
        }

        order.setPaymentStatus("Paid");
        if ("WAITING_FOR_PAYMENT".equalsIgnoreCase(order.getOrderStatus())) {
            order.setOrderStatus("PENDING");
        }
        orderRepository.save(order);

        try {
            if (order.getEmailId() != null && !order.getEmailId().isEmpty()) {
                emailService.sendOrderStatusEmail(order.getEmailId(), order.getId(), "Đã thanh toán thành công");
            }
        } catch (Exception e) {
            log.warn("Lỗi gửi email xác nhận thanh toán cho đơn hàng #{}: {}", order.getId(), e.getMessage());
        }

        ra.addFlashAttribute("success", "Đã xác nhận thanh toán thành công cho đơn hàng #" + id);
        return "redirect:/admin/orders/view/" + id;
    }
}