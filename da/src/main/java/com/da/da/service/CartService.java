package com.da.da.service;

import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.repository.CartRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Tính toán tổng tiền theo chính sách trộn giá (suất sale và giá gốc)
     */
    public BigDecimal calculateMixedTotal(Product product, int quantityToBuy) {
        if (product == null || quantityToBuy <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal originalPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
        BigDecimal salePrice = (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                ? product.getDiscountPrice()
                : originalPrice;

        int limit = product.getDiscountLimit() != null ? product.getDiscountLimit() : 0;
        int sold = product.getDiscountSold() != null ? product.getDiscountSold() : 0;
        int availableSlots = Math.max(0, limit - sold);

        boolean isSaleActive = product.getDiscountPrice() != null
                && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                && availableSlots > 0;

        if (!isSaleActive) {
            return originalPrice.multiply(BigDecimal.valueOf(quantityToBuy));
        }

        if (quantityToBuy <= availableSlots) {
            return salePrice.multiply(BigDecimal.valueOf(quantityToBuy));
        } else {
            BigDecimal saleTotal = salePrice.multiply(BigDecimal.valueOf(availableSlots));
            BigDecimal regularTotal = originalPrice.multiply(BigDecimal.valueOf(quantityToBuy - availableSlots));
            return saleTotal.add(regularTotal);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String addToCart(String email, Long productId, int quantity) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            return "Lỗi: Không tìm thấy thông tin tài khoản người dùng.";
        }
        Long customerId = Long.valueOf(customer.getId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Lỗi: Sản phẩm không tồn tại."));

        if (product.getStock() == null || product.getStock() <= 0) {
            return "Rất tiếc, sản phẩm " + product.getName() + " hiện đã hết hàng.";
        }

        BigDecimal unitPrice = product.getEffectivePrice();
        Cart existingCart = cartRepository.findByCustomerIdAndProductId(customerId, productId);

        if (existingCart != null) {
            int newQuantity = existingCart.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                newQuantity = product.getStock();
            }
            existingCart.setQuantity(newQuantity);
            existingCart.setTotalPrice(calculateMixedTotal(product, newQuantity));
            cartRepository.save(existingCart);
            return "Đã cập nhật số lượng cho " + product.getName() + " trong giỏ hàng!";
        } else {
            int actualQuantity = Math.min(quantity, product.getStock());
            Cart newCart = Cart.builder()
                    .customerId(customerId)
                    .productId(productId)
                    .product(product)
                    .quantity(actualQuantity)
                    .mrpPrice(product.getMrpPrice())
                    .discountPrice(product.getDiscountPrice())
                    .totalPrice(calculateMixedTotal(product, actualQuantity))
                    .build();

            cartRepository.save(newCart);
            return "Đã thêm " + product.getName() + " vào giỏ hàng thành công!";
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCartQuantity(String email, Long productId, int newQuantity) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) return;

        Cart cart = cartRepository.findByCustomerIdAndProductId(Long.valueOf(customer.getId()), productId);
        if (cart != null && cart.getProduct() != null) {
            if (newQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                int stock = cart.getProduct().getStock() != null ? cart.getProduct().getStock() : 0;
                int validQuantity = Math.min(newQuantity, stock);
                cart.setQuantity(validQuantity);
                cart.setTotalPrice(calculateMixedTotal(cart.getProduct(), validQuantity));
                cartRepository.save(cart);
            }
        }
    }

    @Transactional(readOnly = true)
    public String getCartSummaryForAi(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            return "Rất tiếc, không tìm thấy tài khoản. Bạn vui lòng đăng nhập trước nhé!";
        }

        Long customerId = Long.valueOf(customer.getId());
        List<Cart> cartItems = cartRepository.findByCustomerId(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            return "Giỏ hàng của bạn hiện đang trống.";
        }

        StringBuilder sb = new StringBuilder("Trong giỏ hàng của bạn hiện có các sản phẩm sau:\n");
        BigDecimal totalCartPrice = BigDecimal.ZERO;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (int i = 0; i < cartItems.size(); i++) {
            Cart item = cartItems.get(i);
            Optional<Product> productOpt = productRepository.findById(item.getProductId());

            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal itemTotal = item.getTotalPrice() != null ? item.getTotalPrice() : calculateMixedTotal(product, item.getQuantity());
                totalCartPrice = totalCartPrice.add(itemTotal);

                sb.append(String.format("%d. %s (SL: %d) - Thành tiền: %s\n",
                        (i + 1), product.getName(), item.getQuantity(), currencyFormat.format(itemTotal)));
            }
        }

        sb.append(String.format("\nTổng giá trị giỏ hàng: %s.", currencyFormat.format(totalCartPrice)));
        return sb.toString();
    }
}