// src/main/java/com/da/da/service/OrderService.java

package com.da.da.service;

import com.da.da.entity.Cart;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;
    
    
 // Hàm 1: Lấy danh sách đơn hàng để AI trả lời khách
    public String getOrdersForAi(String emailId) {
        // Gọi đúng hàm trong Repo của bồ
        List<Order> orders = orderRepository.findByEmailIdOrderByIdDesc(emailId);

        if (orders.isEmpty()) {
            return "Khách hàng " + emailId + " chưa có đơn hàng nào.";
        }

        StringBuilder sb = new StringBuilder("Đây là danh sách đơn hàng của bồ:\n");
        for (Order o : orders) {
            // Lưu ý: Thay getId(), getStatus(), getTotalPrice() bằng đúng tên getter trong class Order của bồ
            sb.append(String.format("- Đơn hàng #%s | Trạng thái: %s | Tổng tiền: %s\n", 
                o.getId(), o.getOrderStatus(), o.getProductPrice()));
        }
        return sb.toString();
    }

    // Hàm 2: Thống kê (nếu bồ muốn AI làm báo cáo)
    public String countByStatusForAi(String status) {
        List<Order> orders = orderRepository.findByOrderStatus(status);
        return "Hiện có " + orders.size() + " đơn hàng đang ở trạng thái " + status;
    }

    // Annotation này cực kỳ quan trọng: Nếu có lỗi xảy ra ở bất cứ dòng nào, 
    // DB sẽ rollback (không lưu gì cả) để tránh sai lệch dữ liệu.
    @Transactional
    public void updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return; // Hoặc throw Exception
        }

        String oldStatus = order.getOrderStatus();
        
        // Chuẩn hóa chuỗi để so sánh chính xác hơn (trim khoảng trắng, uppercase)
        String statusToCheck = newStatus.trim(); 

        // LOGIC HOÀN KHO
        // Chỉ hoàn kho khi trạng thái MỚI là Hủy VÀ trạng thái CŨ chưa phải là Hủy
        boolean isNewStatusCancelled = "CANCELLED".equalsIgnoreCase(statusToCheck) || "Hủy".equalsIgnoreCase(statusToCheck);
        boolean isOldStatusCancelled = "CANCELLED".equalsIgnoreCase(oldStatus) || "Hủy".equalsIgnoreCase(oldStatus);

        if (isNewStatusCancelled && !isOldStatusCancelled) {
            List<OrderDetail> details = orderDetailRepository.findByOrder(order);
            
            for (OrderDetail item : details) {
                Product product = item.getProduct();
                if (product != null) {
                    int currentStock = (product.getStock() != null) ? product.getStock() : 0;
                    int quantityToReturn = item.getQuantity();
                    
                    product.setStock(currentStock + quantityToReturn);
                    
                    // Lưu sản phẩm
                    productRepository.save(product);
                    System.out.println("Đã hoàn kho cho SP: " + product.getId() + " - SL thêm: " + quantityToReturn);
                }
            }
        }

        // Cập nhật trạng thái đơn hàng
        order.setOrderStatus(newStatus);
        orderRepository.save(order);
    }
//    @Transactional
//    public void placeOrder(Long productId, int quantityToBuy) throws Exception {
//        // 1. Dùng hàm có Lock để lấy sản phẩm và KHÓA nó lại
//        Product product = productRepository.findByIdWithLock(productId)
//                .orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));
//
//        // 2. Kiểm tra tồn kho (Lúc này yên tâm là không ai khác đang sửa đổi nó)
//        if (product.getStock() < quantityToBuy) {
//            throw new Exception("Hết hàng! Kho chỉ còn: " + product.getStock());
//        }
//        // code mới về flash sale
//        Double originalPrice = product.getPrice();
//        
//        // Lấy thông tin giảm giá (xử lý null để tránh lỗi NullPointerException)
//        int limit = (product.getDiscountLimit() != null) ? product.getDiscountLimit() : 0;
//        int sold = (product.getDiscountSold() != null) ? product.getDiscountSold() : 0;
//        Double discountPrice = (product.getDiscountPrice() != null) ? product.getDiscountPrice() : originalPrice;
//        
//        // Tính số suất giảm giá còn lại (Available)
//        int availableSlots = limit - sold;
//        if (availableSlots < 0) availableSlots = 0; // Đề phòng âm
//
//        // Biến lưu số lượng chia tách
//        int qtyDiscount = 0; // Số lượng được mua giá giảm
//        int qtyNormal = 0;   // Số lượng phải mua giá gốc
//
//        if (availableSlots > 0) {
//            if (quantityToBuy <= availableSlots) {
//                // Trường hợp 1: Còn đủ suất cho cả đơn hàng
//                qtyDiscount = quantityToBuy;
//            } else {
//                // Trường hợp 2: Thiếu suất -> Tách làm đôi (QUAN TRỌNG)
//                qtyDiscount = availableSlots;       // Vét hết suất còn lại
//                qtyNormal = quantityToBuy - availableSlots; // Phần dư ra tính giá gốc
//            }
//        } else {
//            // Trường hợp 3: Đã hết sạch suất giảm giá
//            qtyNormal = quantityToBuy;
//        }
//
//        // 3. CẬP NHẬT DATABASE
//        // Cộng thêm số lượng đã bán vào discount_sold
//        if (qtyDiscount > 0) {
//            product.setDiscountSold(sold + qtyDiscount);
//        }
//        
//        // Trừ tồn kho tổng
//        product.setStock(product.getStock() - quantityToBuy);
//        
//        // Lưu Product
//        productRepository.save(product);
//
//        // 4. LƯU ORDER DETAIL (Phần này bạn tích hợp vào logic tạo đơn của bạn)
//        // Thay vì lưu 1 dòng, bây giờ có thể sẽ phải lưu 2 dòng
//        
//        System.out.println("--- CHI TIẾT ĐƠN HÀNG ---");
//        if (qtyDiscount > 0) {
//            // TODO: Lưu OrderDetail dòng 1
//            // detail1.setQuantity(qtyDiscount);
//            // detail1.setPrice(discountPrice);
//            System.out.println("-> Mua " + qtyDiscount + " sản phẩm với giá GIẢM: " + discountPrice);
//        }
//        
//        if (qtyNormal > 0) {
//            // TODO: Lưu OrderDetail dòng 2
//            // detail2.setQuantity(qtyNormal);
//            // detail2.setPrice(originalPrice);
//            System.out.println("-> Mua " + qtyNormal + " sản phẩm với giá GỐC: " + originalPrice);
//        }
//        System.out.println("-------------------------");
//        
//     
//        
//        
//        // 3. Trừ kho
//        product.setStock(product.getStock() - quantityToBuy);
//        productRepository.save(product);
//
//       
//        
//        System.out.println("Mua thành công! Kho còn lại: " + product.getStock());
//    }
 // Trong OrderService.java

    @Transactional(rollbackFor = Exception.class) // Rollback nếu có lỗi
    public void placeOrder(Order order, List<Cart> cartItems) throws Exception {
        
        // 1. Lưu Header đơn hàng trước để lấy ID
        orderRepository.save(order);
        
        double finalGrandTotal = 0;

        // 2. Duyệt qua từng món trong giỏ để xử lý kho và giá
        for (Cart item : cartItems) {
            Product product = item.getProduct();
            
            // --- A. KIỂM TRA KHO LẦN CUỐI (Quan trọng) ---
            // Dùng findByIdWithLock nếu bạn đã cấu hình Lock, hoặc findById thường
            // Ở đây mình dùng logic Java thuần để bạn dễ hiểu
            int currentStock = (product.getStock() != null) ? product.getStock() : 0;
            int quantityToBuy = item.getQuantity();

            if (currentStock < quantityToBuy) {
                throw new Exception("Sản phẩm " + product.getName() + " không đủ hàng! (Còn: " + currentStock + ")");
            }

            // --- B. TÍNH TOÁN SUẤT SALE (LOGIC TRỘN GIÁ) ---
            int limit = (product.getDiscountLimit() != null) ? product.getDiscountLimit() : 0;
            int sold = (product.getDiscountSold() != null) ? product.getDiscountSold() : 0;
            int availableSlots = limit - sold; 
            if (availableSlots < 0) availableSlots = 0;

            double originalPrice = product.getPrice();
            double salePrice = (product.getDiscountPrice() != null) ? product.getDiscountPrice() : originalPrice;

            int qtyCheap = 0;  // Số lượng mua giá rẻ
            int qtyNormal = 0; // Số lượng mua giá gốc

            // Check xem sản phẩm có đang chạy sale không
            boolean isSaleActive = (product.getDiscountPrice() != null && product.getDiscountPrice() > 0);

            if (isSaleActive && availableSlots > 0) {
                if (quantityToBuy <= availableSlots) {
                    // Mua ít hơn suất còn -> Tất cả giá rẻ
                    qtyCheap = quantityToBuy;
                } else {
                    // Mua lố -> Chia ra
                    qtyCheap = availableSlots;
                    qtyNormal = quantityToBuy - availableSlots;
                }
            } else {
                // Không sale hoặc hết suất -> Tất cả giá gốc
                qtyNormal = quantityToBuy;
            }

            // --- C. TÍNH TỔNG TIỀN CHO DÒNG NÀY ---
            double lineTotal = (qtyCheap * salePrice) + (qtyNormal * originalPrice);
            finalGrandTotal += lineTotal;

            // --- D. CẬP NHẬT KHO VÀ SỐ ĐÃ BÁN (CỰC KỲ QUAN TRỌNG) ---
            
            // 1. Trừ kho tổng
            product.setStock(currentStock - quantityToBuy);
            
            // 2. Tăng số lượng đã bán Flash Sale (để lần sau web hiện đúng)
            if (qtyCheap > 0) {
                product.setDiscountSold(sold + qtyCheap);
            }
            
            productRepository.save(product); // Lưu lại vào DB ngay

            // --- E. TẠO ORDER DETAIL ---
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order); // Liên kết với đơn hàng
            detail.setProduct(product);
            detail.setQuantity(quantityToBuy);
            
            // Lưu tổng tiền của dòng này (bao gồm cả giá trộn)
            // Bạn có thể lưu đơn giá trung bình hoặc lưu thành tiền. 
            // Ở đây mình lưu thành tiền để chính xác nhất.
            detail.setPrice(lineTotal / quantityToBuy); // Đơn giá trung bình (để tham khảo)
            detail.setTotalPrice(BigDecimal.valueOf(lineTotal)); // Tổng tiền chuẩn
            
            orderDetailRepository.save(detail);
        }

        // 3. Cập nhật lại tổng tiền cuối cùng cho đơn hàng (nếu cần)
        // order.setTotalAmount(finalGrandTotal);
        // orderRepository.save(order);
    }
}