package com.da.da.controller;

import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.repository.CartRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.service.CartService;
import com.da.da.service.OrderService;
import com.da.da.service.PaymentService;
import com.da.da.service.PdfService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final PdfService pdfService;

    public OrderController(CartRepository cartRepository,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository,
                           OrderService orderService,
                           CartService cartService,
                           PaymentService paymentService,
                           PdfService pdfService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderService = orderService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.pdfService = pdfService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Cart> allItems = cartRepository.findByCustomerIdOrderByIdAsc(Long.valueOf(user.getId()));
        List<Cart> validItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Cart item : allItems) {
            if (item.getProduct() != null) {
                validItems.add(item);
                BigDecimal lineTotal = cartService.calculateMixedTotal(item.getProduct(), item.getQuantity() != null ? item.getQuantity() : 1);
                grandTotal = grandTotal.add(lineTotal);
            }
        }

        if (validItems.isEmpty()) return "redirect:/cart";

        model.addAttribute("cartItems", validItems);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("user", user);

        return "client/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String address,
                             @RequestParam String phone,
                             @RequestParam String paymentMode,
                             HttpSession session,
                             Model model,
                             RedirectAttributes ra) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            Order savedOrder = orderService.placeOrder(user, address, phone, paymentMode);

            if ("VIETQR".equalsIgnoreCase(paymentMode)) {
                String qrUrl = paymentService.getVietQRUrl(savedOrder);
                model.addAttribute("qrUrl", qrUrl);
                model.addAttribute("order", savedOrder);
                return "client/payment_vietqr";
            } else if ("MOMO".equalsIgnoreCase(paymentMode)) {
                return "redirect:/momo/pay/" + savedOrder.getId();
            }

            return "redirect:/order-success";

        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/order-success")
    public String orderSuccess() {
        return "client/order-success";
    }

    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Order> myOrders = orderRepository.findByEmailIdOrderByIdDesc(user.getEmail());
        model.addAttribute("orders", myOrders);
        return "client/my-orders";
    }

    @GetMapping("/my-orders/view/{id}")
    public String viewOrderDetails(@PathVariable Integer id, HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || !user.getEmail().equalsIgnoreCase(order.getEmailId())) {
            return "redirect:/my-orders";
        }

        List<OrderDetail> details = orderDetailRepository.findByOrder(order);
        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "client/order-details";
    }

    @GetMapping("/api/order/status/{id}")
    @ResponseBody
    public ResponseEntity<?> checkStatus(@PathVariable Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            return ResponseEntity.ok(Map.of("paymentStatus", order.getPaymentStatus()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/download-invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Integer id, HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (order.getEmailId() == null || !order.getEmailId().equalsIgnoreCase(user.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        ByteArrayInputStream bis = pdfService.exportInvoicePdf(order, orderDetails);

        if (bis == null) {
            return ResponseEntity.internalServerError().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
