package com.da.da.controller;

import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.repository.CartRepository;
import com.da.da.repository.ProductRepository;
import com.da.da.service.CartService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;
    
    // --- HÀM HỖ TRỢ TÍNH TOÁN GIÁ (Tránh viết lặp lại code) ---
    
    private double calculateMixedTotal(Product product, int quantityToBuy) {
        // 1. Lấy số liệu an toàn
        int limit = (product.getDiscountLimit() != null) ? product.getDiscountLimit() : 0;
        int sold = (product.getDiscountSold() != null) ? product.getDiscountSold() : 0;
        int availableSlots = limit - sold;
        if (availableSlots < 0) availableSlots = 0;

        double originalPrice = product.getPrice();
        double salePrice = originalPrice;

        // 2. Check xem có Sale không
        boolean isSaleActive = false;
        if (product.getDiscountPrice() != null 
            && product.getDiscountPrice() > 0 
            && availableSlots > 0) {
            isSaleActive = true;
            salePrice = product.getDiscountPrice();
        }

        // 3. Tính toán tiền
        double finalTotal = 0;
        if (!isSaleActive) {
            // Không Sale: Tính hết theo giá gốc
            finalTotal = quantityToBuy * originalPrice;
        } else {
            // Có Sale: Phân chia số lượng
            if (quantityToBuy <= availableSlots) {
                // Mua ít hơn suất còn lại -> Hưởng trọn giá Sale
                finalTotal = quantityToBuy * salePrice;
            } else {
                // Mua lố suất Sale -> Trộn giá
                int quantitySale = availableSlots;
                int quantityNormal = quantityToBuy - availableSlots;
                finalTotal = (quantitySale * salePrice) + (quantityNormal * originalPrice);
            }
        }
        return finalTotal;
    }

 // Link gọi: /cart/add?productId=1
    @GetMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            // --- LOGIC CHẶN KHO (QUAN TRỌNG) ---
            int currentStock = (product.getStock() != null) ? product.getStock() : 0; // Lưu ý: check tên hàm getStock() hay getQuantity() trong Entity của bạn
            
            if (currentStock <= 0) {
                // Hết hàng thì không cho thêm
                return "redirect:/product/" + productId + "?error=out_of_stock"; 
            }
            // -----------------------------------

            Long customerId = Long.valueOf(user.getId());
            Cart existingItem = cartRepository.findByCustomerIdAndProductId(customerId, productId);

            if (existingItem != null) {
                // === CẬP NHẬT ===
                int newQuantity = existingItem.getQuantity() + 1;
                
                // --- CHẶN: Nếu cộng thêm 1 mà vượt quá kho -> Giữ nguyên mức tối đa ---
                if (newQuantity > currentStock) {
                    newQuantity = currentStock;
                }
                
                existingItem.setQuantity(newQuantity);
                double total = calculateMixedTotal(product, newQuantity);
                existingItem.setTotalPrice(String.valueOf(total));
                cartRepository.save(existingItem);
                
            } else {
                // === TẠO MỚI ===
                Cart newItem = new Cart();
                newItem.setCustomerId(customerId);
                newItem.setProductId(productId);
                newItem.setQuantity(1); // Mặc định là 1, chắc chắn <= stock vì đã check stock <= 0 ở trên

                double total = calculateMixedTotal(product, 1);
                
                // Logic hiển thị giá (như cũ)
                int limit = (product.getDiscountLimit() != null) ? product.getDiscountLimit() : 0;
                int sold = (product.getDiscountSold() != null) ? product.getDiscountSold() : 0;
                boolean hasSlot = (limit - sold) > 0;
                double displayPrice = (product.getDiscountPrice() != null && hasSlot) ? product.getDiscountPrice() : product.getPrice();

                newItem.setDiscountPrice(String.valueOf(displayPrice)); 
                newItem.setTotalPrice(String.valueOf(total));
                if (product.getMrpPrice() != null) newItem.setMrpPrice(String.valueOf(product.getMrpPrice()));

                cartRepository.save(newItem);
            }
        }
        return "redirect:/cart";
    } 

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Cart> cartItems = cartRepository.findByCustomerIdOrderByIdAsc(Long.valueOf(user.getId()));

        double grandTotal = 0;
        for (Cart item : cartItems) {
            if (item.getProduct() != null) {
                // SỬA Ở ĐÂY: Dùng chính hàm calculateMixedTotal bạn đã viết để tính tiền chuẩn 100%
                double correctTotal = calculateMixedTotal(item.getProduct(), item.getQuantity());
                
                // Cộng dồn vào tổng hóa đơn
                grandTotal += correctTotal;
                
                // (Tùy chọn) Cập nhật lại vào entity để lát đẩy ra HTML cho an toàn
                item.setTotalPrice(String.valueOf(correctTotal)); 
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        return "client/cart";
    }
//    @GetMapping("/cart")
//    public String viewCart(HttpSession session, Model model) {
//        Customer user = (Customer) session.getAttribute("user");
//        if (user == null) return "redirect:/login";
//
//        List<Cart> cartItems = cartRepository.findByCustomerIdOrderByIdAsc(Long.valueOf(user.getId()));
//
//        double grandTotal = 0;
//        for (Cart item : cartItems) {
//            // Chỉ tính tiền nếu sản phẩm tồn tại và có giá
//            if (item.getProduct() != null) {
//                double price = (item.getProduct().getDiscountPrice() != null && item.getProduct().getDiscountPrice() > 0) 
//                               ? item.getProduct().getDiscountPrice() 
//                               : item.getProduct().getPrice();
//                
//                // Cộng dồn: Giá thực tế * Số lượng
//                grandTotal += price * item.getQuantity();
//            }
//        }
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("grandTotal", grandTotal);
//        return "client/cart";
//    }

    @GetMapping("/cart/remove")
    public String removeItem(@RequestParam Long id) {
        cartRepository.deleteById(id);
        return "redirect:/cart";
    }
    
    @GetMapping("/cart/remove/{productId}") // Dùng dấu {productId} để khớp với link HTML
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        if (customer != null) {
            // Gọi repo để xóa đúng món hàng của User đó và Product đó
            cartRepository.deleteByCustomerIdAndProductId(Long.valueOf(customer.getId()), productId);
        }
        return "redirect:/cart";
    }

    // BƯỚC 11: CẬP NHẬT SỐ LƯỢNG (Đã sửa lại Logic chuẩn)
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long id, @RequestParam int quantity) {
        Cart cartItem = cartRepository.findById(id).orElse(null);

        if (cartItem != null) {
            Product product = cartItem.getProduct();
            int currentStock = (product.getStock() != null) ? product.getStock() : 0;

            if (quantity > 0) {
                // --- CHẶN KHO: Nếu khách nhập số > kho, ép về bằng kho ---
                if (quantity > currentStock) {
                    quantity = currentStock; 
                }
                // ---------------------------------------------------------
                
                cartItem.setQuantity(quantity);
                
                if (product != null) {
                    double newTotal = calculateMixedTotal(product, quantity);
                    cartItem.setTotalPrice(String.valueOf(newTotal));
                }
                cartRepository.save(cartItem);
            } else {
                cartRepository.delete(cartItem);
            }
        }
        return "redirect:/cart";
        
        
    }
    
    
    @GetMapping("/cart/update/{id}")
    public String updateCart(@PathVariable("id") Long productId, @RequestParam("qty") int qty, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        // Gọi hàm tính lại tiền và lưu vào DB
        cartService.updateCartQuantity(customer.getEmail(), productId, qty);
        return "redirect:/cart"; // Load lại trang để tiền nhảy chuẩn 100%
    }

 
}