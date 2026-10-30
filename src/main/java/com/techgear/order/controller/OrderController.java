package com.techgear.order.controller;

import com.techgear.identity.entity.Customer;
import com.techgear.order.entity.Order;
import com.techgear.order.entity.OrderDetail;
import com.techgear.order.dto.PlaceOrderRequest;
import com.techgear.cart.service.CartService;
import com.techgear.order.service.OrderService;
import com.techgear.payment.service.PaymentService;
import com.techgear.shared.service.PdfService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final PdfService pdfService;

    public OrderController(OrderService orderService,
                           CartService cartService,
                           PaymentService paymentService,
                           PdfService pdfService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.pdfService = pdfService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        CartService.CartViewDto cartView = cartService.getCartView(Long.valueOf(user.getId()));
        if (cartView.cartItems().isEmpty()) return "redirect:/cart";

        model.addAttribute("cartItems", cartView.cartItems());
        model.addAttribute("grandTotal", cartView.grandTotal());
        model.addAttribute("user", user);

        return "client/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@Valid @ModelAttribute PlaceOrderRequest request,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model,
                             RedirectAttributes ra) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("errorMessage", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/checkout";
        }

        try {
            Order savedOrder = orderService.placeOrder(user, request);

            if ("VIETQR".equalsIgnoreCase(request.getPaymentMode())) {
                String qrUrl = paymentService.getVietQRUrl(savedOrder);
                model.addAttribute("qrUrl", qrUrl);
                model.addAttribute("order", savedOrder);
                return "client/payment_vietqr";
            } else if ("MOMO".equalsIgnoreCase(request.getPaymentMode())) {
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
    public String myOrders(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           HttpSession session,
                           Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Page<Order> myOrders = orderService.getOrdersByCustomer(user.getEmail(), page, size);
        model.addAttribute("orders", myOrders.getContent());
        model.addAttribute("page", myOrders);
        return "client/my-orders";
    }

    @GetMapping("/my-orders/view/{id}")
    public String viewOrderDetails(@PathVariable Integer id, HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Order order = orderService.getOrderForCustomer(id, user.getEmail());
        if (order == null) {
            return "redirect:/my-orders";
        }

        List<OrderDetail> details = orderService.getOrderDetails(order);
        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "client/order-details";
    }

    @GetMapping("/order/payment/vietqr/{id}")
    public String viewVietQrPayment(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Order order = orderService.getOrderForCustomer(id, user.getEmail());
        if (order == null) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/my-orders";
        }

        if (order.isPaid()) {
            ra.addFlashAttribute("successMessage", "Đơn hàng #" + id + " đã được thanh toán thành công!");
            return "redirect:/my-orders/view/" + id;
        }

        if (order.getOrderStatus() == com.techgear.order.entity.enums.OrderStatus.CANCELLED) {
            ra.addFlashAttribute("errorMessage", "Đơn hàng #" + id + " đã bị hủy.");
            return "redirect:/my-orders/view/" + id;
        }

        String qrUrl = paymentService.getVietQRUrl(order);
        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("order", order);
        return "client/payment_vietqr";
    }

    @PostMapping("/my-orders/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            orderService.cancelOrderByCustomer(id, user.getEmail());
            ra.addFlashAttribute("successMessage", "Đã hủy đơn hàng #" + id + " thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/my-orders";
    }

    @GetMapping("/api/order/status/{id}")
    @ResponseBody
    public ResponseEntity<?> checkStatus(@PathVariable Integer id, HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            com.techgear.order.entity.enums.PaymentStatus status = orderService.getPaymentStatusForCustomer(id, user.getEmail());
            if (status != null) {
                return ResponseEntity.ok(Map.of("paymentStatus", status));
            }
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/download-invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Integer id, HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Order order = orderService.getOrderForCustomer(id, user.getEmail());
        if (order == null) {
            return ResponseEntity.status(403).build();
        }

        List<OrderDetail> orderDetails = orderService.getOrderDetails(order);
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
