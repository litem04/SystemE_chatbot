package com.da.da.service;

import com.da.da.entity.Customer;
import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.da.da.entity.Product;
import com.da.da.entity.enums.OrderStatus;
import com.da.da.entity.enums.PaymentMode;
import com.da.da.entity.enums.PaymentStatus;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.OrderDetailRepository;
import com.da.da.repository.OrderRepository;
import com.da.da.repository.ProductRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminAiService {

    private static final Logger log = LoggerFactory.getLogger(AdminAiService.class);

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ChatLanguageModel geminiModel;
    private final ChatLanguageModel ollamaModel;

    public AdminAiService(OrderRepository orderRepository,
                          OrderDetailRepository orderDetailRepository,
                          ProductRepository productRepository,
                          CustomerRepository customerRepository,
                          @Qualifier("geminiModel") ChatLanguageModel geminiModel,
                          @Qualifier("ollamaModel") ChatLanguageModel ollamaModel) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.geminiModel = geminiModel;
        this.ollamaModel = ollamaModel;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,### đ", symbols);
        return df.format(amount);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }

    public String processAdminChat(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Xin chào Quản trị viên! Tôi là **TechGear Admin Copilot**. Bạn cần tổng hợp thông tin gì hôm nay?";
        }

        String lower = query.trim().toLowerCase();

        // 1. Tra cứu đơn hàng theo ID (Ví dụ: "kiểm tra đơn #2", "đơn 1", "chi tiết đơn hàng 3")
        Pattern orderIdPattern = Pattern.compile("(?:đơn(?:\\s*hàng)?|mã(?:\\s*đơn)?|#|order)\\s*#?(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = orderIdPattern.matcher(lower);
        if (matcher.find()) {
            try {
                int orderId = Integer.parseInt(matcher.group(1));
                return getOrderDetailSummary(orderId);
            } catch (Exception ignored) {
            }
        }

        // 2. Báo cáo Doanh thu & Dòng tiền
        if (lower.contains("doanh thu") || lower.contains("doanh số") || lower.contains("tiền") || lower.contains("tài chính")) {
            return getRevenueReport();
        }

        // 3. Tình trạng Đơn hàng & Đơn chờ xử lý
        if (lower.contains("đơn hàng") || lower.contains("chờ xử lý") || lower.contains("chờ duyệt") || lower.contains("vận chuyển") || lower.contains("đơn mới")) {
            return getOrderStatusSummary();
        }

        // 4. Cảnh báo Tồn kho & Sản phẩm sắp hết hàng
        if (lower.contains("tồn kho") || lower.contains("kho") || lower.contains("hết hàng") || lower.contains("sắp hết") || lower.contains("cảnh báo")) {
            return getInventoryAlert();
        }

        // 5. Thống kê Khách hàng
        if (lower.contains("khách hàng") || lower.contains("thành viên") || lower.contains("user") || lower.contains("người dùng")) {
            return getCustomerSummary();
        }

        // 6. Chào hỏi / Hướng dẫn
        if (lower.matches(".*\\b(chào|hi|hello|alo|giúp|hướng dẫn|menu|làm được gì)\\b.*") && lower.length() < 30) {
            return getGreetingMenu();
        }

        // 7. Câu hỏi phân tích phức tạp hơn -> Thử AI LLM với System Context
        try {
            return callAiWithContext(query);
        } catch (Exception e) {
            log.warn("Admin AI LLM generation failed ({}), returning intelligent summary...", e.getMessage());
            return getGeneralSystemOverview();
        }
    }

    private String getRevenueReport() {
        BigDecimal deliveredRevenue = orderRepository.calculateTotalRevenue();
        if (deliveredRevenue == null) deliveredRevenue = BigDecimal.ZERO;

        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal pendingRevenue = BigDecimal.ZERO;
        int paidCount = 0;
        int unpaidCount = 0;
        int vietQrCount = 0;
        int codCount = 0;

        for (Order o : allOrders) {
            BigDecimal amt = o.getTotalAmount();
            if (o.getPaymentStatus() == PaymentStatus.PAID) {
                totalPaid = totalPaid.add(amt);
                paidCount++;
            } else {
                unpaidCount++;
            }

            if (o.getOrderStatus() == OrderStatus.SHIPPED || o.getOrderStatus() == OrderStatus.PROCESSING || o.getOrderStatus() == OrderStatus.PENDING) {
                pendingRevenue = pendingRevenue.add(amt);
            }

            if (o.getPaymentMode() == PaymentMode.VIETQR) {
                vietQrCount++;
            } else if (o.getPaymentMode() == PaymentMode.COD) {
                codCount++;
            }
        }

        return """
                ### 📊 Báo cáo Doanh thu & Dòng tiền TechGear
                
                - **Doanh thu thực thu (Giao thành công):** `%s`
                - **Tổng tiền các đơn đã thanh toán (PAID):** `%s` (%d đơn)
                - **Doanh thu đang trên đường giao / chờ xử lý:** `%s`
                
                **💳 Phương thức thanh toán:**
                - Chuyển khoản VietQR: **%d đơn**
                - Thanh toán COD: **%d đơn**
                - Đơn đã thanh toán: **%d** | Đơn chưa thanh toán: **%d**
                
                👉 [Xem danh sách toàn bộ đơn hàng](/admin/orders)
                """.formatted(
                formatMoney(deliveredRevenue),
                formatMoney(totalPaid), paidCount,
                formatMoney(pendingRevenue),
                vietQrCount, codCount,
                paidCount, unpaidCount
        );
    }

    private String getOrderStatusSummary() {
        long waitingPayment = orderRepository.countByOrderStatus(OrderStatus.WAITING_FOR_PAYMENT);
        long pending = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        long processing = orderRepository.countByOrderStatus(OrderStatus.PROCESSING);
        long shipped = orderRepository.countByOrderStatus(OrderStatus.SHIPPED);
        long delivered = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
        long cancelled = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

        StringBuilder sb = new StringBuilder();
        sb.append("### 📦 Tình trạng Đơn hàng Hệ thống\n\n");
        sb.append(String.format("- ⏳ **Chờ thanh toán (VietQR):** %d đơn\n", waitingPayment));
        sb.append(String.format("- 🟡 **Chờ duyệt & đóng gói (PENDING/PROCESSING):** %d đơn\n", (pending + processing)));
        sb.append(String.format("- 🚚 **Đang vận chuyển (SHIPPED):** %d đơn\n", shipped));
        sb.append(String.format("- ✅ **Giao thành công (DELIVERED):** %d đơn\n", delivered));
        sb.append(String.format("- ❌ **Đã hủy (CANCELLED):** %d đơn\n\n", cancelled));

        // Liệt kê tối đa 5 đơn cần xử lý ngay
        List<Order> pendingOrders = orderRepository.findByOrderStatus(OrderStatus.PENDING);
        if (pendingOrders.isEmpty()) {
            pendingOrders = orderRepository.findByOrderStatus(OrderStatus.PROCESSING);
        }

        if (!pendingOrders.isEmpty()) {
            sb.append("**⚡ Các đơn hàng cần xử lý giao ngay:**\n");
            int count = 0;
            for (Order o : pendingOrders) {
                if (count++ >= 5) break;
                String payBadge = (o.getPaymentStatus() == PaymentStatus.PAID) ? "🟢 Đã thanh toán" : "🔴 Chưa thanh toán";
                sb.append(String.format("- **Đơn #%d** - %s (%s) - %s: `%s` | [Xử lý đơn](/admin/orders/view/%d)\n",
                        o.getId(),
                        o.getCustomerName() != null ? o.getCustomerName() : "Khách",
                        o.getMobileNumber() != null ? o.getMobileNumber() : "SĐT N/A",
                        payBadge,
                        formatMoney(o.getTotalAmount()),
                        o.getId()
                ));
            }
        } else {
            sb.append("🎉 *Hiện không có đơn hàng nào tồn đọng cần xử lý!*\n");
        }

        return sb.toString();
    }

    private String getInventoryAlert() {
        List<Product> allProducts = productRepository.findAll();
        List<Product> outOfStock = new ArrayList<>();
        List<Product> lowStock = new ArrayList<>();

        for (Product p : allProducts) {
            int stock = (p.getStock() != null) ? p.getStock() : 0;
            if (stock <= 0) {
                outOfStock.add(p);
            } else if (stock <= 15) {
                lowStock.add(p);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("### ⚠️ Báo cáo Tồn kho & Cảnh báo Hàng hóa\n\n");
        sb.append(String.format("- Tổng số mặt hàng đang quản lý: **%d sản phẩm**\n", allProducts.size()));
        sb.append(String.format("- Sản phẩm hết hàng (Stock = 0): **%d mặt hàng**\n", outOfStock.size()));
        sb.append(String.format("- Sản phẩm sắp hết (Stock ≤ 15): **%d mặt hàng**\n\n", lowStock.size()));

        if (!outOfStock.isEmpty()) {
            sb.append("**🚨 Đã hết hàng (Cần nhập kho ngay):**\n");
            for (Product p : outOfStock) {
                sb.append(String.format("- ❌ **%s** (Danh mục: %s) | [Nhập hàng](/admin/products/update/%d)\n",
                        p.getName(), p.getProductCategory(), p.getId()));
            }
            sb.append("\n");
        }

        if (!lowStock.isEmpty()) {
            sb.append("**⚠️ Sắp hết hàng (Tồn kho thấp):**\n");
            for (Product p : lowStock) {
                sb.append(String.format("- ⚠️ **%s** - Còn lại: **%d cái** | [Cập nhật kho](/admin/products/update/%d)\n",
                        p.getName(), p.getStock(), p.getId()));
            }
        }

        if (outOfStock.isEmpty() && lowStock.isEmpty()) {
            sb.append("✅ *Tất cả sản phẩm đều có số lượng tồn kho dồi dào trên 15 sản phẩm!*");
        }

        return sb.toString();
    }

    private String getCustomerSummary() {
        long totalCustomers = customerRepository.count();
        List<Customer> customers = customerRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("### 👥 Thống kê Khách hàng TechGear\n\n");
        sb.append(String.format("- Tổng số tài khoản khách hàng: **%d người**\n\n", totalCustomers));

        sb.append("**Danh sách khách hàng gần nhất:**\n");
        int count = 0;
        for (int i = customers.size() - 1; i >= 0 && count < 5; i--, count++) {
            Customer c = customers.get(i);
            sb.append(String.format("- **%s** - Email: `%s` | SĐT: `%s` | Đ/c: %s\n",
                    c.getName(),
                    c.getEmail(),
                    c.getPhone() != null ? c.getPhone() : "Chưa cập nhật",
                    c.getAddress() != null ? c.getAddress() : "Chưa cập nhật"
            ));
        }

        sb.append("\n👉 [Quản lý toàn bộ khách hàng](/admin/customers)");
        return sb.toString();
    }

    private String getOrderDetailSummary(int orderId) {
        Optional<Order> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) {
            return String.format("❌ Không tìm thấy đơn hàng **#%d** trong hệ thống! Vui lòng kiểm tra lại mã đơn.", orderId);
        }

        Order o = opt.get();
        List<OrderDetail> details = orderDetailRepository.findByOrder(o);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("### 📄 Chi tiết Đơn hàng #%d\n\n", o.getId()));
        sb.append(String.format("- **Khách hàng:** %s (%s)\n", o.getCustomerName(), o.getMobileNumber() != null ? o.getMobileNumber() : ""));
        sb.append(String.format("- **Email:** `%s`\n", o.getEmailId()));
        sb.append(String.format("- **Địa chỉ:** %s\n", o.getAddress()));
        sb.append(String.format("- **Ngày đặt:** %s\n", formatDate(o.getOrderDate())));
        sb.append(String.format("- **Hình thức TT:** %s\n", o.getPaymentMode() != null ? o.getPaymentMode().getDisplayName() : "N/A"));
        
        String payStatusText = (o.getPaymentStatus() == PaymentStatus.PAID) ? "🟢 ĐÃ THANH TOÁN" : "🔴 CHƯA THANH TOÁN";
        sb.append(String.format("- **Trạng thái TT:** **%s**\n", payStatusText));

        String orderStatusText = switch (o.getOrderStatus()) {
            case WAITING_FOR_PAYMENT -> "⏳ Chờ thanh toán VietQR";
            case PENDING -> "🟡 Chờ duyệt (Chưa đóng gói)";
            case PROCESSING -> "📦 Đang đóng gói";
            case SHIPPED -> "🚚 Đang vận chuyển";
            case DELIVERED -> "✅ Giao thành công";
            case CANCELLED -> "❌ Đã hủy";
        };
        sb.append(String.format("- **Vận chuyển:** **%s**\n", orderStatusText));
        sb.append(String.format("- **Tổng giá trị:** `%s`\n\n", formatMoney(o.getTotalAmount())));

        if (details != null && !details.isEmpty()) {
            sb.append("**Danh sách sản phẩm trong đơn:**\n");
            for (OrderDetail d : details) {
                sb.append(String.format("- %s x %d = %s\n",
                        d.getProductName(),
                        d.getQuantity(),
                        formatMoney(d.getTotalPrice())
                ));
            }
            sb.append("\n");
        }

        sb.append(String.format("👉 [Mở trang quản trị đơn hàng #%d](/admin/orders/view/%d)", o.getId(), o.getId()));
        return sb.toString();
    }

    private String getGreetingMenu() {
        return """
                👋 Xin chào Quản trị viên! Tôi là **TechGear Admin Copilot**, trợ lý ảo vận hành cửa hàng.
                
                Tôi có thể giúp bạn tổng hợp và thao tác nhanh chóng:
                1. 📊 **Báo cáo doanh thu & dòng tiền** (Hỏi: *"Doanh thu hôm nay"*, *"Tài chính"*)
                2. 📦 **Tổng hợp tình trạng đơn hàng** (Hỏi: *"Đơn chờ xử lý"*, *"Đơn hàng mới"*)
                3. ⚠️ **Cảnh báo tồn kho & hết hàng** (Hỏi: *"Tồn kho"*, *"Sản phẩm sắp hết"*)
                4. 👥 **Thống kê người dùng** (Hỏi: *"Khách hàng"*, *"Số lượng user"*)
                5. 🔍 **Tra cứu chi tiết đơn hàng** (Hỏi: *"Kiểm tra đơn #2"*, *"Đơn 1"*)
                
                Bạn muốn kiểm tra thông tin nào trước?
                """;
    }

    private String getGeneralSystemOverview() {
        BigDecimal deliveredRevenue = orderRepository.calculateTotalRevenue();
        long pendingCount = orderRepository.countByOrderStatus(OrderStatus.PENDING) + orderRepository.countByOrderStatus(OrderStatus.PROCESSING);
        long waitingPayment = orderRepository.countByOrderStatus(OrderStatus.WAITING_FOR_PAYMENT);
        long customerCount = customerRepository.count();
        long productCount = productRepository.count();

        return """
                ### 📌 Tổng quan Vận hành Nhanh Hệ thống
                
                - **Doanh thu thực thu:** `%s`
                - **Đơn hàng chờ xử lý:** `%d đơn` (Cần duyệt đóng gói)
                - **Đơn chờ khách quét VietQR:** `%d đơn`
                - **Tổng mặt hàng:** `%d sản phẩm`
                - **Tổng khách hàng:** `%d thành viên`
                
                💡 *Mẹo:* Bạn có thể bấm vào các nút gợi ý nhanh bên dưới hoặc gõ trực tiếp câu hỏi như `Kiểm tra đơn #2`, `Tồn kho`, `Doanh thu` để tra cứu tức thì!
                """.formatted(
                formatMoney(deliveredRevenue != null ? deliveredRevenue : BigDecimal.ZERO),
                pendingCount,
                waitingPayment,
                productCount,
                customerCount
        );
    }

    private String callAiWithContext(String query) {
        BigDecimal deliveredRevenue = orderRepository.calculateTotalRevenue();
        long orderCount = orderRepository.count();
        long customerCount = customerRepository.count();
        long productCount = productRepository.count();

        String systemPrompt = """
                Bạn là Admin AI Copilot của sàn thương mại điện tử công nghệ TechGear.
                Dưới đây là số liệu tóm tắt vận hành thực tế tại thời điểm hiện tại:
                - Tổng doanh thu thực thu: %s
                - Tổng số đơn hàng: %d
                - Tổng số sản phẩm kinh doanh: %d
                - Tổng số khách hàng: %d
                
                Hãy trả lời câu hỏi của Quản trị viên một cách súc tích, chuyên nghiệp, đưa ra các nhận định chiến lược kinh doanh nếu được hỏi.
                Nếu quản trị viên hỏi về số liệu, hãy dựa trên số liệu thực tế được cung cấp.
                Format câu trả lời rõ ràng bằng Markdown.
                
                Câu hỏi của Admin: "%s"
                """.formatted(
                formatMoney(deliveredRevenue != null ? deliveredRevenue : BigDecimal.ZERO),
                orderCount,
                productCount,
                customerCount,
                query
        );

        try {
            return geminiModel.generate(systemPrompt);
        } catch (Exception e) {
            try {
                return ollamaModel.generate(systemPrompt);
            } catch (Exception ex) {
                return getGeneralSystemOverview();
            }
        }
    }
}
