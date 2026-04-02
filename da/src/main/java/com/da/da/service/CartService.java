package com.da.da.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.da.da.entity.Cart;
import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.repository.CartRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 1. Hàm thêm sản phẩm vào giỏ hàng
     * Dùng @Transactional để đảm bảo dữ liệu được lưu xuống DB an toàn
     */
//    @Transactional
//    public String addToCart(String email, Long productId, int quantity) {
//        // BƯỚC 1: Tìm Customer theo email
//        // Vì CustomerRepository trả về Customer (không phải Optional) nên ta dùng IF để check null
//        Customer customer = customerRepository.findByEmail(email);
//        if (customer == null) {
//            return "Lỗi: Không tìm thấy thông tin khách hàng. bồ vui lòng đăng nhập lại nhé.";
//        }
//        
//        // Chuyển đổi ID từ Integer (Customer) sang Long (Cart) để khớp kiểu dữ liệu
//        Long customerId = Long.valueOf(customer.getId());
//
//        // BƯỚC 2: Kiểm tra xem sản phẩm có tồn tại không
//        // findById mặc định của JPA trả về Optional nên dùng orElseThrow ở đây là CHUẨN
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Lỗi: Sản phẩm không tồn tại trên hệ thống."));
//
//        // BƯỚC 3: Kiểm tra xem món này đã có trong giỏ hàng chưa
//        Cart existingCart = cartRepository.findByCustomerIdAndProductId(customerId, productId);
//
//        if (existingCart != null) {
//            // Nếu đã có, tăng thêm số lượng
//            existingCart.setQuantity(existingCart.getQuantity() + quantity);
//            cartRepository.save(existingCart);
//            return "Mình đã cập nhật thêm số lượng cho sản phẩm " + product.getName() + " vào giỏ hàng của bạn rồi nhé!";
//        } else {
//            // Nếu chưa có, tạo mới bản ghi Cart
//            Cart newCart = new Cart();
//            newCart.setCustomerId(customerId);
//            newCart.setProductId(productId);
//            newCart.setQuantity(quantity);
//            
//            cartRepository.save(newCart);
//            return "Đã thêm sản phẩm " + product.getName() + " vào giỏ hàng thành công ạ!";
//        }
//    }
    
    @Transactional
    public String addToCart(String email, Long productId, int quantity) {
        // 1. Tìm khách hàng
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            return "Lỗi: Không tìm thấy thông tin khách hàng.";
        }
        Long customerId = Long.valueOf(customer.getId());

        // 2. Tìm sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Sản phẩm không tồn tại."));

        // 3. Chuẩn bị dữ liệu giá (Chuyển từ Double sang String vì Entity của bồ là String)
        double mrp = product.getPrice();
        double discount = (product.getDiscountPrice() != null) ? product.getDiscountPrice() : 0;
        double finalPrice = (discount > 0) ? discount : mrp;

        // 4. Kiểm tra giỏ hàng cũ
        Cart existingCart = cartRepository.findByCustomerIdAndProductId(customerId, productId);

        if (existingCart != null) {
            // Cập nhật số lượng
            int newQuantity = existingCart.getQuantity() + quantity;
            existingCart.setQuantity(newQuantity);
            
            // Tính lại tổng tiền (Giá x Số lượng) và chuyển về String
            double newTotal = finalPrice * newQuantity;
            existingCart.setTotalPrice(String.valueOf(newTotal));
            
            cartRepository.save(existingCart);
            return "Đã cập nhật số lượng " + product.getName() + " trong giỏ hàng!";
        } else {
            // Tạo mới hoàn toàn
            Cart newCart = new Cart();
            newCart.setCustomerId(customerId);
            newCart.setProductId(productId);
            newCart.setQuantity(quantity);
            
            // Gán các biến theo đúng hình bồ gửi (Kiểu String)
            newCart.setMrpPrice(String.valueOf(mrp));
            newCart.setDiscountPrice(String.valueOf(discount));
            
            // Tính tổng tiền cho món này
            double total = finalPrice * quantity;
            newCart.setTotalPrice(String.valueOf(total));
            
            // Nếu bồ có quan hệ @ManyToOne, hãy gán luôn object product
            newCart.setProduct(product); 

            cartRepository.save(newCart);
            return "Đã thêm " + product.getName() + " vào giỏ hàng thành công!";
        }
    }
    
    
    public void updateCartQuantity(String email, Long productId, int newQuantity) {
        Customer customer = customerRepository.findByEmail(email);
        Cart cart = cartRepository.findByCustomerIdAndProductId(Long.valueOf(customer.getId()), productId);
        if (cart != null) {
            double price = (cart.getProduct().getDiscountPrice() != null && cart.getProduct().getDiscountPrice() > 0) 
                           ? cart.getProduct().getDiscountPrice() : cart.getProduct().getPrice();
            cart.setQuantity(newQuantity);
            // Cập nhật lại thành tiền của món này
            cart.setTotalPrice(String.valueOf(price * newQuantity)); 
            cartRepository.save(cart); // LƯU VÀO DB
        }
    }

    /**
     * 2. Hàm tổng hợp giỏ hàng thành văn bản để AI trả lời khách
     */
    public String getCartSummaryForAi(String email) {
        // 1. Tìm thông tin khách hàng
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            return "Rất tiếc, mình không tìm thấy tài khoản của bạn. bồ đăng nhập giúp mình nhé!";
        }

        // 2. Lấy danh sách sản phẩm trong giỏ
        Long customerId = Long.valueOf(customer.getId());
        List<Cart> cartItems = cartRepository.findByCustomerId(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            return "Giỏ hàng của bạn hiện đang trống. bồ có muốn mình tư vấn món nào đang hot không?";
        }

        // 3. Duyệt danh sách và xây dựng câu trả lời
        StringBuilder sb = new StringBuilder();
        sb.append("Trong giỏ hàng của bạn hiện có các sản phẩm sau:\n");
        
        double totalCartPrice = 0;

        for (int i = 0; i < cartItems.size(); i++) {
            Cart item = cartItems.get(i);
            
            // Tìm chi tiết sản phẩm để lấy tên và giá
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                
                // Logic lấy giá: Ưu tiên giá Sale (discountPrice)
                double currentPrice = (product.getDiscountPrice() != null && product.getDiscountPrice() > 0) 
                                      ? product.getDiscountPrice() : product.getPrice();
                
                double itemTotal = currentPrice * item.getQuantity();
                totalCartPrice += itemTotal;

                // Đánh số thứ tự 1, 2, 3...
                sb.append(String.format("%d. %s (SL: %d) - Đơn giá: %,.0fđ\n", 
                    (i + 1), product.getName(), item.getQuantity(), currentPrice));
            }
        }

        // 4. Tổng kết
        sb.append(String.format("\nTổng giá trị giỏ hàng của bạn là: %,.0fđ.", totalCartPrice));
        sb.append("\nBạn có muốn mình hỗ trợ thanh toán luôn không ạ?");

        return sb.toString();
    }
}