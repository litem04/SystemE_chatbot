package com.da.da.controller;

import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public String listOrders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "15") int size,
                             Model model) {
        Page<Order> ordersPage = orderService.getAllOrdersPaged(page, size);
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("page", ordersPage);
        return "admin/orders";
    }

    @GetMapping("/view/{id}")
    public String viewOrderDetails(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) return "redirect:/admin/orders";

        List<OrderDetail> details = orderService.getOrderDetails(order);
        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "admin/order-details";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("id") Integer id,
                               @RequestParam("status") String status,
                               RedirectAttributes ra) {
        try {
            orderService.updateOrderStatus(id, status);
            ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi cập nhật trạng thái đơn hàng #{}: ", id, e);
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/orders/view/" + id;
    }

    @PostMapping("/confirm-payment")
    public String confirmPaymentManually(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            orderService.confirmPaymentManually(id);
            ra.addFlashAttribute("success", "Đã xác nhận thanh toán thành công cho đơn hàng #" + id);
        } catch (Exception e) {
            log.error("Lỗi xác nhận thanh toán cho đơn #{}: ", id, e);
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/orders/view/" + id;
    }
}