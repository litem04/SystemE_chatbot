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

    private static final List<String> REVENUE_KEYWORDS = List.of(
            "doanh thu", "doanh so", "tien", "tai chinh", "revenue", "loi nhuan", "tien nong", "thu nhap"
    );

    private static final List<String> ORDER_KEYWORDS = List.of(
            "don hang", "cho xu ly", "cho duyet", "dong goi", "van chuyen", "don moi", "giao hang", "don cho", "xu ly don", "orders"
    );

    private static final List<String> INVENTORY_KEYWORDS = List.of(
            "ton kho", "kho", "het hang", "sap het", "canh bao kho", "canh bao", "stock", "hang ton", "nhap hang"
    );

    private static final List<String> CUSTOMER_KEYWORDS = List.of(
            "khach hang", "khach", "thanh vien", "user", "nguoi dung", "customer", "tai khoan"
    );

    private static final List<String> GREETING_KEYWORDS = List.of(
            "chao", "hi", "hello", "alo", "giup", "huong dan", "menu", "help", "lam duoc gi", "chuc nang"
    );

    public static String removeAccents(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        return withoutDiacritics.replace("đ", "d").replace("Đ", "D").toLowerCase().trim();
    }

    public static int levenshteinDistance(String a, String b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private static boolean matchesAnyKeyword(String normText, List<String> keywords, int maxDistance) {
        for (String kw : keywords) {
            if (normText.contains(kw)) return true;
        }
        String[] words = normText.split("\\s+");
        for (String kw : keywords) {
            int kwWordsCount = kw.split("\\s+").length;
            if (kwWordsCount == 1) {
                for (String w : words) {
                    if (levenshteinDistance(w, kw) <= maxDistance) return true;
                }
            } else if (words.length >= kwWordsCount) {
                for (int i = 0; i <= words.length - kwWordsCount; i++) {
                    StringBuilder window = new StringBuilder();
                    for (int j = 0; j < kwWordsCount; j++) {
                        if (j > 0) window.append(" ");
                        window.append(words[i + j]);
                    }
                    if (levenshteinDistance(window.toString(), kw) <= maxDistance) return true;
                }
            }
        }
        return false;
    }

    public String processAdminChat(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Xin chào Quản trị viên! Tôi là **TechGear Admin Copilot**. Bạn cần tổng hợp thông tin gì hôm nay?";
        }

        String norm = removeAccents(query);

        // 1. Tra cứu đơn hàng theo ID (Ví dụ: "kiểm tra đơn #2", "đơn 1", "chi tiết đơn hàng 3", "don 2", "#1")
        Pattern orderIdPattern = Pattern.compile("(?:đơn(?:\\s*hàng)?|don(?:\\s*hang)?|mã(?:\\s*đơn)?|ma(?:\\s*don)?|#|order|check\\s*don|tra\\s*cuu\\s*don)?\\s*#?(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = orderIdPattern.matcher(norm);
        if (matcher.find()) {
            try {
                int orderId = Integer.parseInt(matcher.group(1));
                if (orderId > 0) {
                    return getOrderDetailSummary(orderId);
                }
            } catch (Exception ignored) {
            }
        }

        // 2. Thống kê Khách hàng (Hỗ trợ bắt lỗi chính tả, vd: "khách hànhg", "khach", "thanh vien")
        if (matchesAnyKeyword(norm, CUSTOMER_KEYWORDS, 2)) {
            return getCustomerSummary();
        }

        // 3. Báo cáo Doanh thu & Dòng tiền (Hỗ trợ lỗi chính tả, vd: "doang thu", "doanhthu", "tien", "doanh so")
        if (matchesAnyKeyword(norm, REVENUE_KEYWORDS, 2)) {
            return getRevenueReport();
        }

        // 4. Tình trạng Đơn hàng & Đơn chờ xử lý
        if (matchesAnyKeyword(norm, ORDER_KEYWORDS, 2)) {
            return getOrderStatusSummary();
        }

        // 5. Cảnh báo Tồn kho & Sản phẩm sắp hết hàng
        if (matchesAnyKeyword(norm, INVENTORY_KEYWORDS, 2)) {
            return getInventoryAlert();
        }

        // 6. Chào hỏi / Hướng dẫn
        if (matchesAnyKeyword(norm, GREETING_KEYWORDS, 1) && norm.length() < 35) {
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
