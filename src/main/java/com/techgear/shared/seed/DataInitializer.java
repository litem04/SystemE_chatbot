package com.techgear.shared.seed;

import com.techgear.identity.entity.Admin;
import com.techgear.identity.entity.Customer;
import com.techgear.catalog.entity.Product;
import com.techgear.identity.repository.AdminRepository;
import com.techgear.identity.repository.CustomerRepository;
import com.techgear.catalog.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository,
                           CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdmin();
        initCustomer();
        initProducts();
    }

    private void initAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = Admin.builder()
                    .name("Quản Trị Viên")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .addedDate(new Date())
                    .build();
            adminRepository.save(admin);
            log.info(">>> [SEED DATA] Khởi tạo tài khoản Admin mặc định: admin@gmail.com");
        }
    }

    private void initCustomer() {
        if (customerRepository.count() == 0) {
            Customer customer = Customer.builder()
                    .name("Nguyễn Văn A")
                    .email("user@gmail.com")
                    .password(passwordEncoder.encode("user123"))
                    .phone("0901234567")
                    .address("123 Phố Huế, Hoàn Kiếm, Hà Nội")
                    .pinCode("100000")
                    .gender("Nam")
                    .addedDate(new Date())
                    .build();
            customerRepository.save(customer);
            log.info(">>> [SEED DATA] Khởi tạo tài khoản Khách hàng thử nghiệm: user@gmail.com");
        }
    }

    public void initProducts() {
        if (productRepository.count() < 50) {
            List<Product> sampleProducts = List.of(
                    // 1. ASUS ROG Strix G16
                    Product.builder()
                            .code("TG-LAP-001")
                            .name("Laptop Gaming ASUS ROG Strix G16 (2024)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(28000000))
                            .mrpPrice(BigDecimal.valueOf(31000000))
                            .discountPrice(BigDecimal.valueOf(25900000))
                            .discountLimit(10)
                            .discountSold(3)
                            .stock(15)
                            .active("Active")
                            .description("Laptop Gaming ASUS ROG Strix G16 (2024) là cỗ máy chiến game đỉnh cao thuộc phân khúc cao cấp.\n\n" +
                                    "• CPU: Intel Core i7-13650HX thế hệ 13 (14 nhân, 20 luồng, xung nhịp lên tới 4.9GHz).\n" +
                                    "• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 (TGP tối đa 140W, MUX Switch).\n" +
                                    "• RAM: 16GB DDR5 4800MHz (nâng cấp tối đa 64GB).\n" +
                                    "• Ổ cứng: 512GB PCIe 4.0 NVMe SSD.\n" +
                                    "• Màn hình: 16.0 inch FHD+ 165Hz 100% sRGB Dolby Vision.")
                            .image("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 2. MacBook Pro 14 M3 Pro
                    Product.builder()
                            .code("TG-LAP-002")
                            .name("Apple MacBook Pro 14 M3 Pro (18GB/512GB)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(45000000))
                            .mrpPrice(BigDecimal.valueOf(49990000))
                            .discountPrice(BigDecimal.valueOf(43500000))
                            .discountLimit(8)
                            .discountSold(2)
                            .stock(12)
                            .active("Active")
                            .description("Apple MacBook Pro 14 M3 Pro sở hữu chip 3nm đỉnh cao cùng màn hình Liquid Retina XDR 120Hz.\n\n" +
                                    "• CPU 11 nhân, GPU 14 nhân Ray Tracing tăng tốc phần cứng.\n" +
                                    "• RAM Unified Memory 18GB băng thông cực lớn 150GB/s.\n" +
                                    "• SSD 512GB siêu tốc độ đọc ghi trên 5.000 MB/s.\n" +
                                    "• Màn hình Liquid Retina XDR 14.2 inch độ sáng 1000 nits, ProMotion 120Hz.\n" +
                                    "• Thời lượng pin lên đến 18 giờ làm việc liên tục.")
                            .image("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 3. AKKO 3087 v2 RGB
                    Product.builder()
                            .code("TG-ACC-001")
                            .name("Bàn phím cơ không dây AKKO 3087 v2 RGB")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(1290000))
                            .mrpPrice(BigDecimal.valueOf(1590000))
                            .discountPrice(BigDecimal.valueOf(1090000))
                            .discountLimit(30)
                            .discountSold(12)
                            .stock(50)
                            .active("Active")
                            .description("Bàn phím cơ AKKO 3087 v2 RGB layout TKL 87 phím gọn gàng, keycap PBT Double-shot bền bỉ.\n\n" +
                                    "• Bố cục TKL 87 phím nhỏ gọn, tối ưu diện tích di chuột.\n" +
                                    "• Switch AKKO CS V3 thế hệ mới được lube sẵn từ xưởng, gõ êm mượt.\n" +
                                    "• Keycap PBT Double-Shot dày dặn không bóng dầu.\n" +
                                    "• LED RGB 16.8 triệu màu với hơn 20 chế độ ánh sáng.\n" +
                                    "• Cáp kết nối USB-C bọc dù có thể tháo rời tiện lợi.")
                            .image("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 4. Logitech G Pro X Superlight 2
                    Product.builder()
                            .code("TG-ACC-003")
                            .name("Chuột không dây Logitech G Pro X Superlight 2")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2990000))
                            .mrpPrice(BigDecimal.valueOf(3490000))
                            .discountPrice(BigDecimal.valueOf(2690000))
                            .discountLimit(25)
                            .discountSold(14)
                            .stock(30)
                            .active("Active")
                            .description("Chuột thi đấu eSports đỉnh cao với trọng lượng siêu nhẹ chỉ 60 gram.\n\n" +
                                    "• Trọng lượng lông vũ 60g thiết kế công thái học đối xứng.\n" +
                                    "• Cảm biến HERO 2 độ phân giải 32.000 DPI, gia tốc 40G.\n" +
                                    "• Switch cơ lai quang học LIGHTFORCE tốc độ ánh sáng, chống double-click.\n" +
                                    "• Kết nối LIGHTSPEED 2000Hz (0.5ms) siêu mượt.\n" +
                                    "• Thời lượng pin đột phá 95 giờ liên tục, cổng sạc Type-C.")
                            .image("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 5. Sony WH-1000XM5
                    Product.builder()
                            .code("TG-ACC-005")
                            .name("Tai nghe Bluetooth Sony WH-1000XM5")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(6990000))
                            .mrpPrice(BigDecimal.valueOf(8490000))
                            .discountPrice(BigDecimal.valueOf(6290000))
                            .discountLimit(15)
                            .discountSold(6)
                            .stock(18)
                            .active("Active")
                            .description("Tai nghe chống ồn hàng đầu thế giới với vi xử lý kép V1 và QN1.\n\n" +
                                    "• Chống ồn thích ứng Auto NC Optimizer thông minh.\n" +
                                    "• Củ loa 30mm vòm sợi carbon tái tạo âm thanh Hi-Res Audio tinh khiết.\n" +
                                    "• Hỗ trợ chuẩn codec LDAC truyền dữ liệu âm thanh gấp 3 lần Bluetooth thông thường.\n" +
                                    "• Hệ thống 4 micro AI Beamforming đàm thoại rõ như pha lê.\n" +
                                    "• Pin 30 giờ, sạc nhanh 3 phút nghe được 3 giờ.")
                            .image("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 6. MacBook Air 13 M2
                    Product.builder()
                            .code("TG-LAP-003")
                            .name("Apple MacBook Air 13 M2 (8GB/256GB Midnight)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(22490000))
                            .mrpPrice(BigDecimal.valueOf(24990000))
                            .discountPrice(BigDecimal.valueOf(20990000))
                            .discountLimit(15)
                            .discountSold(5)
                            .stock(25)
                            .active("Active")
                            .description("MacBook Air M2 mỏng nhẹ chỉ 1.24kg mang sắc màu Midnight thanh lịch.\n\n" +
                                    "• Chip Apple M2 8-core CPU và 8-core GPU mạnh mẽ.\n" +
                                    "• Màn hình Liquid Retina 13.6 inch độ sáng 500 nits sống động.\n" +
                                    "• Thiết kế fanless không quạt tản nhiệt, vận hành tuyệt đối êm ái.\n" +
                                    "• Cổng sạc MagSafe 3 từ tính an toàn cùng 2 cổng Thunderbolt.\n" +
                                    "• Thời lượng pin lên đến 18 tiếng cả ngày làm việc.")
                            .image("https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 7. Acer Nitro 5 Tiger
                    Product.builder()
                            .code("TG-LAP-004")
                            .name("Laptop Gaming Acer Nitro 5 Tiger AN515-58")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(19500000))
                            .mrpPrice(BigDecimal.valueOf(22500000))
                            .discountPrice(BigDecimal.valueOf(17890000))
                            .discountLimit(12)
                            .discountSold(4)
                            .stock(18)
                            .active("Active")
                            .description("Chiến binh gaming quốc dân hiệu năng đỉnh cao trong tầm giá.\n\n" +
                                    "• CPU: Intel Core i5-12500H 12 nhân 16 luồng turbo 4.5GHz.\n" +
                                    "• VGA: NVIDIA GeForce RTX 3050 4GB GDDR6 tối ưu DLSS.\n" +
                                    "• RAM: 16GB DDR4 3200MHz chạy kênh đôi.\n" +
                                    "• Màn hình: 15.6 inch IPS FHD tần số quét 144Hz.\n" +
                                    "• Tản nhiệt kép 2 quạt công suất lớn với phần mềm NitroSense.")
                            .image("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 8. Lenovo ThinkPad E14 Gen 5
                    Product.builder()
                            .code("TG-LAP-005")
                            .name("Laptop Lenovo ThinkPad E14 Gen 5")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(18200000))
                            .mrpPrice(BigDecimal.valueOf(20500000))
                            .discountPrice(BigDecimal.valueOf(16900000))
                            .discountLimit(10)
                            .discountSold(2)
                            .stock(20)
                            .active("Active")
                            .description("Laptop doanh nhân và coder bền bỉ chuẩn quân đội MIL-STD-810H.\n\n" +
                                    "• CPU: Intel Core i5-1335U thế hệ 13 tiết kiệm điện năng.\n" +
                                    "• RAM: 16GB DDR4 cùng SSD 512GB NVMe tốc độ cao.\n" +
                                    "• Bàn phím ThinkPad trứ danh gõ êm số 1 thế giới.\n" +
                                    "• Núm đỏ TrackPoint đặc trưng thao tác chuẩn xác.\n" +
                                    "• Màn hình 14 inch IPS WUXGA chống chói bảo vệ mắt.")
                            .image("https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 9. Dell XPS 13 Plus 9320
                    Product.builder()
                            .code("TG-LAP-006")
                            .name("Laptop Dell XPS 13 Plus 9320 OLED Touch")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(39900000))
                            .mrpPrice(BigDecimal.valueOf(44500000))
                            .discountPrice(BigDecimal.valueOf(36500000))
                            .discountLimit(5)
                            .discountSold(1)
                            .stock(8)
                            .active("Active")
                            .description("Kiệt tác Ultrabook tương lai với Touchpad kính liền mạch tàng hình.\n\n" +
                                    "• CPU: Intel Core i7-1360P 12 nhân 16 luồng mạnh mẽ.\n" +
                                    "• Màn hình cảm ứng 13.4 inch 3.5K OLED rực rỡ DisplayHDR 500.\n" +
                                    "• RAM: 16GB LPDDR5 6000MHz và SSD 512GB PCIe 4.0.\n" +
                                    "• Thân máy nhôm CNC cao cấp siêu mỏng, trọng lượng 1.26kg.\n" +
                                    "• Hàng phím chức năng cảm ứng điện dung hiện đại.")
                            .image("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 10. iPhone 16 Pro Max
                    Product.builder()
                            .code("TG-PHN-001")
                            .name("iPhone 16 Pro Max 256GB Titan Sa Mạc VN/A")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(34990000))
                            .mrpPrice(BigDecimal.valueOf(36990000))
                            .discountPrice(BigDecimal.valueOf(33490000))
                            .discountLimit(20)
                            .discountSold(9)
                            .stock(35)
                            .active("Active")
                            .description("Đỉnh cao smartphone Apple với khung Titan Sa Mạc và nút Camera Control.\n\n" +
                                    "• Màn hình Super Retina XDR 6.9 inch ProMotion 120Hz viền siêu mỏng.\n" +
                                    "• Chip Apple A18 Pro tiến trình 3nm xử lý đồ họa mượt mà.\n" +
                                    "• Camera 48MP Fusion zoom quang 5x, quay video 4K 120fps Dolby Vision.\n" +
                                    "• Nút Camera Control cảm ứng điều chỉnh nhanh khẩu độ và chụp ảnh.\n" +
                                    "• Pin cực trâu lên tới 33 giờ xem video liên tục.")
                            .image("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 11. Samsung Galaxy S24 Ultra
                    Product.builder()
                            .code("TG-PHN-002")
                            .name("Samsung Galaxy S24 Ultra 5G (12GB/256GB)")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(29990000))
                            .mrpPrice(BigDecimal.valueOf(33990000))
                            .discountPrice(BigDecimal.valueOf(26990000))
                            .discountLimit(15)
                            .discountSold(6)
                            .stock(22)
                            .active("Active")
                            .description("Flagship Android toàn năng tích hợp bút S-Pen và bộ công cụ Galaxy AI.\n\n" +
                                    "• Khoanh tròn để tìm kiếm Circle to Search thông minh.\n" +
                                    "• Phiên dịch cuộc gọi hai chiều Live Translate tiếng Việt trực tiếp.\n" +
                                    "• Camera mắt thần bóng đêm 200MP zoom quang 5x và 10x sắc nét.\n" +
                                    "• Màn hình phẳng 6.8 inch 120Hz chống phản chiếu Corning Gorilla Armor.\n" +
                                    "• Chip Snapdragon 8 Gen 3 for Galaxy hiệu năng vô đối.")
                            .image("https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 12. iPhone 15 128GB
                    Product.builder()
                            .code("TG-PHN-003")
                            .name("iPhone 15 128GB Xanh Pastel Chính Hãng VN/A")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(19490000))
                            .mrpPrice(BigDecimal.valueOf(21990000))
                            .discountPrice(BigDecimal.valueOf(17990000))
                            .discountLimit(25)
                            .discountSold(11)
                            .stock(40)
                            .active("Active")
                            .description("Thiết kế mặt lưng nhám mịn, nâng cấp cụm Dynamic Island và cổng USB-C.\n\n" +
                                    "• Cụm đảo tương tác Dynamic Island linh hoạt.\n" +
                                    "• Camera chính 48MP zoom 2x quang học chụp ảnh chi tiết cao.\n" +
                                    "• Tính năng chụp chân dung tự động xóa phông thông minh.\n" +
                                    "• Cổng kết nối chuẩn USB-C đồng bộ tiện lợi.\n" +
                                    "• Chip A16 Bionic vận hành ổn định bền bỉ.")
                            .image("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 13. Xiaomi 14 Ultra
                    Product.builder()
                            .code("TG-PHN-004")
                            .name("Xiaomi 14 Ultra 5G (16GB/512GB) Leica Camera")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(26990000))
                            .mrpPrice(BigDecimal.valueOf(29990000))
                            .discountPrice(BigDecimal.valueOf(24490000))
                            .discountLimit(10)
                            .discountSold(3)
                            .stock(14)
                            .active("Active")
                            .description("Máy ảnh chuyên nghiệp mang hình hài smartphone hợp tác cùng Leica.\n\n" +
                                    "• Cảm biến 1 inch Sony LYT-900 dải tương phản 14EV.\n" +
                                    "• Ống kính Leica Summilux khẩu độ thay đổi f/1.63 - f/4.0.\n" +
                                    "• Cụm 4 camera tele kép zoom siêu việt.\n" +
                                    "• Màn hình 6.73 inch WQHD+ 120Hz độ sáng đỉnh 3000 nits.\n" +
                                    "• Pin 5000mAh hỗ trợ sạc nhanh có dây 90W và không dây 80W.")
                            .image("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 14. OPPO Reno11 Pro
                    Product.builder()
                            .code("TG-PHN-005")
                            .name("OPPO Reno11 Pro 5G (12GB/512GB) Pearl White")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(12990000))
                            .mrpPrice(BigDecimal.valueOf(14990000))
                            .discountPrice(BigDecimal.valueOf(11490000))
                            .discountLimit(12)
                            .discountSold(4)
                            .stock(16)
                            .active("Active")
                            .description("Chuyên gia chụp ảnh chân dung với mặt lưng ngọc trai satin quý phái.\n\n" +
                                    "• Camera chân dung 32MP cảm biến Sony IMX709 zoom 2x chuẩn studio.\n" +
                                    "• Màn hình cong 3D 120Hz 1.07 tỷ màu rực rỡ.\n" +
                                    "• Sạc nhanh SUPERVOOC 80W sạc đầy 100% trong 28 phút.\n" +
                                    "• Chip Dimensity 8200 4nm mượt mà, RAM 12GB đa nhiệm thả ga.")
                            .image("https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 15. FL-Esports CMK75
                    Product.builder()
                            .code("TG-ACC-002")
                            .name("Bàn phím cơ FL-Esports CMK75 Gasket Wireless")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2450000))
                            .mrpPrice(BigDecimal.valueOf(2890000))
                            .discountPrice(BigDecimal.valueOf(2190000))
                            .discountLimit(15)
                            .discountSold(7)
                            .stock(25)
                            .active("Active")
                            .description("Bàn phím cơ Custom cao cấp có màn hình TFT và đệm Gasket Mount.\n\n" +
                                    "• Cấu trúc Gasket Mount 5 lớp đệm âm Poron cách âm hoàn hảo.\n" +
                                    "• Switch TTC Flame Red Linear bôi trơn sẵn gõ mượt mà.\n" +
                                    "• Màn hình TFT hiển thị GIF động cá tính.\n" +
                                    "• 3 chế độ kết nối: Type-C, Bluetooth 5.0 và 2.4GHz không độ trễ.\n" +
                                    "• Núm kim loại xoay chỉnh âm lượng trực tiếp.")
                            .image("https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 16. Logitech MX Master 3S
                    Product.builder()
                            .code("TG-ACC-004")
                            .name("Chuột công thái học không dây Logitech MX Master 3S")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2490000))
                            .mrpPrice(BigDecimal.valueOf(2890000))
                            .discountPrice(BigDecimal.valueOf(2190000))
                            .discountLimit(20)
                            .discountSold(8)
                            .stock(35)
                            .active("Active")
                            .description("Chuột văn phòng và đồ họa số 1 thế giới với phím bấm yên tĩnh Quiet Clicks.\n\n" +
                                    "• Cảm biến 8.000 DPI di chuột mượt mà trên mặt kính trong suốt.\n" +
                                    "• Cuộn điện từ MagSpeed 1.000 dòng/giây siêu tốc.\n" +
                                    "• Phím bấm Quiet Clicks giảm 90% tiếng ồn.\n" +
                                    "• Kết nối và luân chuyển dữ liệu giữa 3 máy tính qua Logitech Flow.\n" +
                                    "• Thiết kế công thái học ôm trọn bàn tay, chống mỏi cổ tay.")
                            .image("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 17. AirPods Pro 2
                    Product.builder()
                            .code("TG-ACC-006")
                            .name("Tai nghe Apple AirPods Pro 2 MagSafe USB-C")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(5990000))
                            .mrpPrice(BigDecimal.valueOf(6790000))
                            .discountPrice(BigDecimal.valueOf(5290000))
                            .discountLimit(25)
                            .discountSold(13)
                            .stock(45)
                            .active("Active")
                            .description("Tai nghe true wireless hàng đầu của Apple với chip H2 và chống ồn vượt trội.\n\n" +
                                    "• Chống ồn chủ động ANC thế hệ mới mạnh gấp 2 lần.\n" +
                                    "• Âm thanh thích ứng Adaptive Audio điều chỉnh tự động theo môi trường.\n" +
                                    "• Nhận biết cuộc trò chuyện tự hạ nhỏ âm lượng khi bạn nói chuyện.\n" +
                                    "• Hộp sạc MagSafe cổng USB-C có loa định vị Precision Finding.\n" +
                                    "• Thời lượng pin lên đến 30 giờ khi dùng kèm hộp sạc.")
                            .image("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 18. Marshall Emberton II
                    Product.builder()
                            .code("TG-ACC-007")
                            .name("Loa Bluetooth Marshall Emberton II Chính Hãng")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(3990000))
                            .mrpPrice(BigDecimal.valueOf(4590000))
                            .discountPrice(BigDecimal.valueOf(3490000))
                            .discountLimit(12)
                            .discountSold(5)
                            .stock(20)
                            .active("Active")
                            .description("Biểu tượng âm nhạc Marshall cổ điển với âm thanh vòm 360 độ True Stereophonic.\n\n" +
                                    "• Âm thanh đa hướng mạnh mẽ công suất 20W Class D.\n" +
                                    "• Pin hơn 30 giờ chơi nhạc chỉ sau 1 lần sạc.\n" +
                                    "• Chống bụi nước hoàn hảo chuẩn IP67 mang đi du lịch dã ngoại.\n" +
                                    "• Chế độ Stack Mode ghép nối nhiều loa cùng lúc.\n" +
                                    "• Thiết kế bọc da cổ điển sang trọng đậm chất Rock n Roll.")
                            .image("https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 19. ASUS ROG Swift PG27AQDM
                    Product.builder()
                            .code("TG-ACC-008")
                            .name("Màn hình Gaming ASUS ROG Swift PG27AQDM OLED 240Hz")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(21990000))
                            .mrpPrice(BigDecimal.valueOf(24990000))
                            .discountPrice(BigDecimal.valueOf(19990000))
                            .discountLimit(6)
                            .discountSold(2)
                            .stock(10)
                            .active("Active")
                            .description("Màn hình gaming OLED đỉnh chóp thế giới với tần số quét 240Hz và độ trễ 0.03ms.\n\n" +
                                    "• Tấm nền 27 inch 2K QHD (2560x1440) OLED độ tương phản 1.500.000:1.\n" +
                                    "• Tần số quét 240Hz siêu mượt, loại bỏ triệt để bóng mờ.\n" +
                                    "• Tản nhiệt buồng hơi tùy chỉnh chống lưu ảnh burn-in hiệu quả.\n" +
                                    "• Độ phủ màu 99% DCI-P3 chuẩn điện ảnh, độ sáng đỉnh 1000 nits HDR10.\n" +
                                    "• Chân đế công thái học nâng hạ, xoay dọc 90 độ linh hoạt.")
                            .image("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 20. Anker 735 GaNPrime 65W
                    Product.builder()
                            .code("TG-ACC-009")
                            .name("Củ sạc nhanh Anker 735 GaNPrime 65W 3 Cổng")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(1150000))
                            .mrpPrice(BigDecimal.valueOf(1390000))
                            .discountPrice(BigDecimal.valueOf(890000))
                            .discountLimit(40)
                            .discountSold(22)
                            .stock(60)
                            .active("Active")
                            .description("Củ sạc GaN thế hệ mới nhỏ gọn hơn 53%, sạc đồng thời 3 thiết bị cùng lúc.\n\n" +
                                    "• 3 cổng sạc: 2 USB-C và 1 USB-A tương thích iPhone, iPad, MacBook, Android.\n" +
                                    "• Công suất tối đa 65W sạc nhanh laptop mỏng nhẹ.\n" +
                                    "• Công nghệ phân bổ dòng điện thông minh PowerIQ 4.0.\n" +
                                    "• Công nghệ bảo vệ ActiveShield 2.0 kiểm soát nhiệt an toàn.\n" +
                                    "• Thiết kế nhỏ gọn thuận tiện bỏ túi mang đi làm, du lịch.")
                            .image("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 21. iPhone 16 128GB Chính Hãng VN/A
                    Product.builder()
                            .code("TG-PHN-006")
                            .name("iPhone 16 128GB Chính Hãng VN/A")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(21990000))
                            .mrpPrice(BigDecimal.valueOf(23990000))
                            .discountPrice(BigDecimal.valueOf(20490000))
                            .discountLimit(25)
                            .discountSold(8)
                            .stock(30)
                            .active("Active")
                            .description("iPhone 16 sở hữu cụm camera dọc hỗ trợ quay Spatial Video, nút Action Button và nút Camera Control cảm ứng mới.\n\n" +
                                    "• Chip Apple A18 3nm thế hệ mới nhanh hơn 30% và hỗ trợ Apple Intelligence.\n" +
                                    "• Camera Fusion 48MP zoom quang 2x chất lượng cao, camera Ultra Wide tự động lấy nét macro.\n" +
                                    "• Nút Camera Control cho phép trượt để zoom, chỉnh khẩu độ và chụp tức thì.\n" +
                                    "• Màn hình Super Retina XDR 6.1 inch độ sáng ngoài trời 2000 nits.\n" +
                                    "• Mặt kính pha màu Ceramic Shield bền gấp 2 lần mọi smartphone.")
                            .image("https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 22. iPhone 16 Pro 128GB Titan Sa Mạc VN/A
                    Product.builder()
                            .code("TG-PHN-007")
                            .name("iPhone 16 Pro 128GB Titan Tự Nhiên VN/A")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(27990000))
                            .mrpPrice(BigDecimal.valueOf(29990000))
                            .discountPrice(BigDecimal.valueOf(26490000))
                            .discountLimit(18)
                            .discountSold(5)
                            .stock(22)
                            .active("Active")
                            .description("iPhone 16 Pro vỏ Titan Cấp 5 viền mỏng kỷ lục thế giới và vi xử lý Apple A18 Pro tối tân.\n\n" +
                                    "• CPU 6 nhân và GPU 6 nhân hỗ trợ Ray Tracing phần cứng tăng 20% hiệu năng.\n" +
                                    "• Camera Fusion 48MP và camera góc siêu rộng 48MP bắt trọn chi tiết sắc nét.\n" +
                                    "• Ống kính tiềm vọng zoom quang 5x quang học ở tiêu cự 120mm.\n" +
                                    "• Quay video 4K 120fps Dolby Vision chuẩn điện ảnh chuyên nghiệp.\n" +
                                    "• 4 micro chuẩn phòng thu thu âm không gian Spatial Audio.")
                            .image("https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 23. Samsung Galaxy Z Fold6 5G (12GB/256GB)
                    Product.builder()
                            .code("TG-PHN-008")
                            .name("Samsung Galaxy Z Fold6 5G (12GB/256GB)")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(41990000))
                            .mrpPrice(BigDecimal.valueOf(43990000))
                            .discountPrice(BigDecimal.valueOf(38990000))
                            .discountLimit(10)
                            .discountSold(2)
                            .stock(14)
                            .active("Active")
                            .description("Đỉnh cao điện thoại màn hình gập mỏng nhẹ nhất dòng Z Fold với bộ công cụ quyền năng Galaxy AI.\n\n" +
                                    "• Màn hình chính cực đại 7.6 inch Dynamic AMOLED 2X 120Hz độ sáng 2.600 nits.\n" +
                                    "• Màn hình phụ 6.3 inch viền mỏng tỷ lệ hiển thị tự nhiên như điện thoại thanh.\n" +
                                    "• Tính năng Phác họa thông minh Sketch to Image biến nét vẽ tay thành tranh 3D.\n" +
                                    "• Phiên dịch hai chiều trực tiếp trên cả hai màn hình cho người đối diện.\n" +
                                    "• Chip Snapdragon 8 Gen 3 for Galaxy kết hợp buồng tản nhiệt hơi lớn hơn 1.6 lần.")
                            .image("https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 24. Samsung Galaxy Z Flip6 5G (12GB/256GB)
                    Product.builder()
                            .code("TG-PHN-009")
                            .name("Samsung Galaxy Z Flip6 5G (12GB/256GB)")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(26990000))
                            .mrpPrice(BigDecimal.valueOf(28990000))
                            .discountPrice(BigDecimal.valueOf(23990000))
                            .discountLimit(15)
                            .discountSold(4)
                            .stock(20)
                            .active("Active")
                            .description("Biểu tượng thời trang công nghệ gập hộp phấn, camera 50MP ProVisual và tản nhiệt buồng hơi đầu tiên trên Flip.\n\n" +
                                    "• Màn hình phụ FlexWindow 3.4 inch trả lời nhanh và tương tác widget thông minh.\n" +
                                    "• Camera chính 50MP chụp chân dung đêm rạng rỡ và zoom chất lượng quang học 2x.\n" +
                                    "• Tản nhiệt buồng hơi Vapor Chamber giúp máy luôn mát mẻ khi chơi game.\n" +
                                    "• Pin 4.000mAh bền bỉ và khung nhôm Armor Aluminum gia cường chống xước.\n" +
                                    "• Thiết kế gập mở FlexMode chụp ảnh rảnh tay không cần chân tripod.")
                            .image("https://images.unsplash.com/photo-1567581935884-3349723552ca?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 25. Samsung Galaxy A55 5G
                    Product.builder()
                            .code("TG-PHN-010")
                            .name("Samsung Galaxy A55 5G (8GB/128GB) Awesome Iceblue")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(9490000))
                            .mrpPrice(BigDecimal.valueOf(10490000))
                            .discountPrice(BigDecimal.valueOf(8690000))
                            .discountLimit(35)
                            .discountSold(14)
                            .stock(50)
                            .active("Active")
                            .description("Smartphone tầm trung quốc dân với khung kim loại cao cấp, kính Gorilla Glass Victus+ và kháng nước IP67.\n\n" +
                                    "• Màn hình Super AMOLED 6.6 inch 120Hz mượt mà công nghệ Vision Booster.\n" +
                                    "• Vi xử lý Exynos 1480 4nm với GPU AMD Xclipse 530 tối ưu đồ họa mượt.\n" +
                                    "• Camera chính 50MP OIS chống rung quang học, camera góc siêu rộng 12MP.\n" +
                                    "• Pin 5.000mAh dùng 2 ngày, hỗ trợ sạc nhanh 25W an toàn.\n" +
                                    "• Cam kết 4 năm cập nhật hệ điều hành Android và 5 năm bảo mật.")
                            .image("https://images.unsplash.com/photo-1546054454-aa26e2b734c7?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 26. Xiaomi 13T Pro 5G Leica
                    Product.builder()
                            .code("TG-PHN-011")
                            .name("Xiaomi 13T Pro 5G (12GB/512GB) Leica Camera")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(14990000))
                            .mrpPrice(BigDecimal.valueOf(16990000))
                            .discountPrice(BigDecimal.valueOf(13490000))
                            .discountLimit(20)
                            .discountSold(6)
                            .stock(25)
                            .active("Active")
                            .description("Cỗ máy nhiếp ảnh hợp tác cùng Leica với ống kính chuyên nghiệp Summicron và sạc siêu tốc 120W.\n\n" +
                                    "• Cụm camera Leica 50MP Sony IMX707 với 2 phong cách màu sắc Leica độc quyền.\n" +
                                    "• Sạc HyperCharge 120W sạc đầy 100% pin chỉ trong 19 phút.\n" +
                                    "• Màn hình CrystalRes AMOLED 144Hz 1.5K sắc nét, độ sáng 2.600 nits.\n" +
                                    "• Chip Dimensity 9200+ 4nm cực mạnh điểm AnTuTu trên 1.5 triệu điểm.\n" +
                                    "• Kháng nước kháng bụi chuẩn IP68 tuyệt đối an tâm khi trời mưa.")
                            .image("https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 27. OPPO Reno12 5G
                    Product.builder()
                            .code("TG-PHN-012")
                            .name("OPPO Reno12 5G (12GB/256GB) AI Phone Bạc Vũ Trụ")
                            .productCategory("Mobile")
                            .price(BigDecimal.valueOf(11990000))
                            .mrpPrice(BigDecimal.valueOf(12990000))
                            .discountPrice(BigDecimal.valueOf(10790000))
                            .discountLimit(25)
                            .discountSold(7)
                            .stock(30)
                            .active("Active")
                            .description("Chuyên gia chân dung AI thế hệ mới với thiết kế dòng chảy bạc tương lai và khung chống va đập 360 độ.\n\n" +
                                    "• Tính năng AI Eraser 2.0 xóa người và vật thể thông minh một chạm.\n" +
                                    "• Màn hình cong 3D 120Hz hỗ trợ công nghệ chạm khi tay ướt Splash Touch.\n" +
                                    "• Camera chính Sony LYT-600 50MP OIS bắt nét chuyển động siêu nhanh.\n" +
                                    "• Sạc siêu nhanh SUPERVOOC 80W cùng pin 5.000mAh tuổi thọ 4 năm.\n" +
                                    "• Kết nối mạng thông minh AI LinkBoost giữ sóng ổn định cả trong hầm gửi xe.")
                            .image("https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 28. Lenovo Legion Pro 5
                    Product.builder()
                            .code("TG-LAP-007")
                            .name("Laptop Gaming Lenovo Legion Pro 5 16IRX9 (2024)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(37990000))
                            .mrpPrice(BigDecimal.valueOf(42000000))
                            .discountPrice(BigDecimal.valueOf(35490000))
                            .discountLimit(12)
                            .discountSold(3)
                            .stock(16)
                            .active("Active")
                            .description("Quái thú gaming eSports trang bị chip Intel Core i7-14700HX thế hệ 14 và card đồ họa RTX 4060 140W.\n\n" +
                                    "• CPU: Intel Core i7-14700HX (20 nhân, 28 luồng, xung nhịp Turbo tối đa 5.5GHz).\n" +
                                    "• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 (Full TGP 140W, MUX Switch).\n" +
                                    "• Màn hình: 16 inch WQXGA (2560x1600) 240Hz, 100% sRGB, 500 nits DisplayHDR 400.\n" +
                                    "• RAM: 16GB DDR5 5600MHz và SSD 1TB PCIe Gen 4 siêu nhanh.\n" +
                                    "• Tản nhiệt Legion Coldfront 5.0 ống dẫn nhiệt kép vận hành cực êm ái.")
                            .image("https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 29. Dell Alienware m16 R2
                    Product.builder()
                            .code("TG-LAP-008")
                            .name("Laptop Gaming Dell Alienware m16 R2 QHD+ 240Hz")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(48990000))
                            .mrpPrice(BigDecimal.valueOf(54000000))
                            .discountPrice(BigDecimal.valueOf(45990000))
                            .discountLimit(8)
                            .discountSold(2)
                            .stock(10)
                            .active("Active")
                            .description("Chiến hạm ngoài hành tinh mỏng hơn 15%, trang bị vi xử lý Core Ultra 7 tích hợp AI Boost và card RTX 4070.\n\n" +
                                    "• CPU: Intel Core Ultra 7 155H 16 nhân 22 luồng tích hợp NPU AI.\n" +
                                    "• VGA: NVIDIA GeForce RTX 4070 8GB GDDR6 chiến mượt mọi game 2K Ultra Settings.\n" +
                                    "• Màn hình: 16 inch QHD+ 240Hz 3ms 100% sRGB ComfortView Plus.\n" +
                                    "• RAM: 16GB DDR5 5600MHz và SSD 1TB NVMe PCIe 4.0.\n" +
                                    "• Bàn phím cơ AlienFX per-key RGB hành trình phím 1.8mm cực đầm tay.")
                            .image("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 30. ASUS Zenbook 14 OLED
                    Product.builder()
                            .code("TG-LAP-009")
                            .name("Laptop ASUS Zenbook 14 OLED UX3405MA Intel Core Ultra")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(26990000))
                            .mrpPrice(BigDecimal.valueOf(29500000))
                            .discountPrice(BigDecimal.valueOf(24890000))
                            .discountLimit(15)
                            .discountSold(5)
                            .stock(20)
                            .active("Active")
                            .description("Ultrabook doanh nhân mỏng nhẹ 1.2kg, độ mỏng 14.9mm cùng màn hình 3K ASUS Lumina OLED 120Hz.\n\n" +
                                    "• CPU: Intel Core Ultra 5 125H tích hợp NPU AI xử lý tác vụ thông minh.\n" +
                                    "• Màn hình: 14.0 inch 3K OLED (2880x1800) 120Hz 100% DCI-P3 DisplayHDR 600.\n" +
                                    "• RAM: 16GB LPDDR5X 7467MHz băng thông cực lớn, SSD 512GB PCIe 4.0.\n" +
                                    "• Pin cực trâu 75Wh cho thời lượng làm việc liên tục 15 giờ.\n" +
                                    "• Âm thanh Dolby Atmos siêu thực chứng nhận bởi Harman Kardon.")
                            .image("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 31. MacBook Air 15 inch M3
                    Product.builder()
                            .code("TG-LAP-010")
                            .name("Apple MacBook Air 15 inch M3 (16GB/512GB Midnight)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(36990000))
                            .mrpPrice(BigDecimal.valueOf(39990000))
                            .discountPrice(BigDecimal.valueOf(34990000))
                            .discountLimit(14)
                            .discountSold(4)
                            .stock(18)
                            .active("Active")
                            .description("MacBook Air 15 inch M3 mang không gian làm việc rộng rãi trên tấm nền Liquid Retina lớn và vận hành fanless tuyệt đối êm ái.\n\n" +
                                    "• Chip Apple M3 tiến trình 3nm hỗ trợ dò tia Ray Tracing bằng phần cứng.\n" +
                                    "• RAM Unified Memory 16GB, SSD 512GB siêu tốc mở dự án trong tích tắc.\n" +
                                    "• Màn hình Liquid Retina 15.3 inch 2880x1864 hiển thị 1 tỷ màu rực rỡ.\n" +
                                    "• Dàn âm thanh 6 loa với Spatial Audio âm trầm uy lực.\n" +
                                    "• Thời lượng pin lên đến 18 giờ làm việc cả ngày dài.")
                            .image("https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 32. Lenovo ThinkPad X1 Carbon Gen 11
                    Product.builder()
                            .code("TG-LAP-011")
                            .name("Laptop Lenovo ThinkPad X1 Carbon Gen 11 Carbon Fiber")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(39990000))
                            .mrpPrice(BigDecimal.valueOf(44500000))
                            .discountPrice(BigDecimal.valueOf(36500000))
                            .discountLimit(10)
                            .discountSold(2)
                            .stock(12)
                            .active("Active")
                            .description("Laptop doanh nhân đỉnh cao với khung sợi carbon chuẩn quân đội MIL-STD 810H, phím gõ êm số 1 thế giới và trọng lượng chỉ 1.12kg.\n\n" +
                                    "• CPU: Intel Core i7-1355U thế hệ 13 vPro bảo mật doanh nghiệp.\n" +
                                    "• RAM: 16GB LPDDR5 6400MHz, SSD 512GB NVMe PCIe Gen 4.\n" +
                                    "• Màn hình: 14.0 inch 2.8K OLED (2880x1800) DisplayHDR 500 True Black.\n" +
                                    "• Bàn phím ThinkPad trứ danh có đèn nền và núm đỏ TrackPoint điều khiển chuẩn xác.\n" +
                                    "• Cổng kết nối đầy đủ: 2x Thunderbolt 4, HDMI 2.1, USB-A.")
                            .image("https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 33. Acer Predator Helios Neo 16
                    Product.builder()
                            .code("TG-LAP-012")
                            .name("Laptop Gaming Acer Predator Helios Neo 16 PHN16-71")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(31990000))
                            .mrpPrice(BigDecimal.valueOf(35000000))
                            .discountPrice(BigDecimal.valueOf(29490000))
                            .discountLimit(16)
                            .discountSold(5)
                            .stock(20)
                            .active("Active")
                            .description("Vũ khí chiến binh Predator với quạt kim loại AeroBlade 3D thế hệ 5 và keo tản nhiệt kim loại lỏng Liquid Metal.\n\n" +
                                    "• CPU: Intel Core i7-13700HX 16 nhân 24 luồng xung nhịp tối đa 5.0GHz.\n" +
                                    "• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 140W MUX Switch.\n" +
                                    "• Màn hình: 16 inch WQXGA (2560x1600) IPS 165Hz 100% sRGB 500 nits G-Sync.\n" +
                                    "• RAM: 16GB DDR5 4800MHz (2 khe), SSD 512GB PCIe NVMe SED.\n" +
                                    "• Bàn phím LED RGB 4 vùng rực rỡ kèm phím nóng PredatorSense.")
                            .image("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 34. Dell Inspiron 14 5440
                    Product.builder()
                            .code("TG-LAP-013")
                            .name("Laptop Dell Inspiron 14 5440 Core i5-1334U Mỏng Nhẹ")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(16490000))
                            .mrpPrice(BigDecimal.valueOf(18500000))
                            .discountPrice(BigDecimal.valueOf(15190000))
                            .discountLimit(20)
                            .discountSold(6)
                            .stock(25)
                            .active("Active")
                            .description("Laptop học tập và làm việc văn phòng thanh lịch, vỏ nhôm mát mẻ cùng bản lề ErgoLift nâng máy tự nhiên.\n\n" +
                                    "• CPU: Intel Core i5-1334U (10 nhân, 12 luồng, xung nhịp lên tới 4.6GHz).\n" +
                                    "• RAM: 16GB DDR5 5200MHz đa nhiệm mượt mà, SSD 512GB NVMe PCIe.\n" +
                                    "• Màn hình: 14.0 inch FHD+ (1920x1200) tỷ lệ 16:10 chống chói WVA.\n" +
                                    "• Trọng lượng gọn nhẹ 1.5kg thuận tiện mang đến trường, cơ quan.\n" +
                                    "• Cảm biến vân tay tích hợp nút nguồn một chạm đăng nhập an toàn.")
                            .image("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 35. Keychron K2 Pro
                    Product.builder()
                            .code("TG-ACC-010")
                            .name("Bàn phím cơ không dây Keychron K2 Pro QMK/VIA Hot-swap")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2190000))
                            .mrpPrice(BigDecimal.valueOf(2590000))
                            .discountPrice(BigDecimal.valueOf(1890000))
                            .discountLimit(35)
                            .discountSold(12)
                            .stock(45)
                            .active("Active")
                            .description("Bàn phím cơ không dây layout 75% tùy biến vô hạn qua QMK/VIA, hỗ trợ Hot-swap và tương thích macOS/Windows.\n\n" +
                                    "• Switch Gateron Jupiter Brown bôi trơn sẵn từ nhà máy êm đầm.\n" +
                                    "• Mạch xuôi South-Facing RGB không cấn keycap profile Cherry.\n" +
                                    "• Foam tiêu âm PCB và đệm đáy silicone cho âm gõ chắc nịch.\n" +
                                    "• Kết nối Bluetooth 5.1 cùng lúc 3 thiết bị và cáp Type-C.\n" +
                                    "• Dung lượng pin khủng 4000mAh sử dụng lên đến 300 giờ.")
                            .image("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 36. Razer DeathAdder V3 Pro
                    Product.builder()
                            .code("TG-ACC-011")
                            .name("Chuột gaming không dây Razer DeathAdder V3 Pro 30.000 DPI")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(3290000))
                            .mrpPrice(BigDecimal.valueOf(3790000))
                            .discountPrice(BigDecimal.valueOf(2890000))
                            .discountLimit(25)
                            .discountSold(9)
                            .stock(30)
                            .active("Active")
                            .description("Vũ khí của các tay súng FPS thế giới với thiết kế công thái học hoàn hảo và trọng lượng siêu nhẹ 63g.\n\n" +
                                    "• Cảm biến quang Focus Pro 30K DPI chính xác 99.8%, di được cả trên kính.\n" +
                                    "• Switch quang học Razer Gen-3 độ bền 90 triệu lần bấm không double-click.\n" +
                                    "• Kết nối không dây Razer HyperSpeed Wireless ổn định không độ trễ.\n" +
                                    "• Pin liên tục lên tới 90 giờ, sạc nhanh qua cổng USB-C.\n" +
                                    "• Feet chuột 100% PTFE nguyên chất lướt siêu trơn mịn.")
                            .image("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 37. Dell UltraSharp U2724D
                    Product.builder()
                            .code("TG-ACC-012")
                            .name("Màn hình Đồ họa Dell UltraSharp U2724D 2K 120Hz IPS Black")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(10990000))
                            .mrpPrice(BigDecimal.valueOf(12500000))
                            .discountPrice(BigDecimal.valueOf(9890000))
                            .discountLimit(15)
                            .discountSold(4)
                            .stock(18)
                            .active("Active")
                            .description("Màn hình chuẩn đồ họa hàng đầu của Dell với tấm nền IPS Black độ tương phản 2.000:1 và tần số quét 120Hz mượt mà.\n\n" +
                                    "• Tấm nền 27 inch 2K QHD (2560x1440) IPS Black màu đen sâu hơn 35%.\n" +
                                    "• Tần số quét 120Hz cuộn trang và làm đồ họa mượt mà không mỏi mắt.\n" +
                                    "• Độ phủ màu 100% sRGB, 98% Display P3, chứng nhận bảo vệ mắt 4 sao TUV.\n" +
                                    "• Cảm biến ánh sáng môi trường tự động điều chỉnh độ sáng và nhiệt màu.\n" +
                                    "• Chân đế công thái học nâng hạ, xoay dọc 90 độ.")
                            .image("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 38. Samsung Odyssey G7 32 inch 240Hz
                    Product.builder()
                            .code("TG-ACC-013")
                            .name("Màn hình Gaming Cong Samsung Odyssey G7 32 inch 240Hz 1ms")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(12990000))
                            .mrpPrice(BigDecimal.valueOf(15000000))
                            .discountPrice(BigDecimal.valueOf(11490000))
                            .discountLimit(12)
                            .discountSold(3)
                            .stock(15)
                            .active("Active")
                            .description("Trải nghiệm đắm chìm vào thế giới game với độ cong 1000R lý tưởng theo tầm mắt người và tần số quét 240Hz.\n\n" +
                                    "• Kích thước 32 inch độ phân giải WQHD (2560x1440) độ cong 1000R ôm trọn tầm nhìn.\n" +
                                    "• Tần số quét 240Hz tốc độ phản hồi 1ms GtG triệt tiêu bóng mờ.\n" +
                                    "• Công nghệ QLED tái tạo màu sắc DCI-P3 95% chân thực, chuẩn HDR600.\n" +
                                    "• Đèn LED lõi vô cực Infinity Core Lighting mặt lưng cực ngầu.\n" +
                                    "• Hỗ trợ G-Sync Compatible và AMD FreeSync Premium Pro.")
                            .image("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 39. HyperX Cloud III Wireless
                    Product.builder()
                            .code("TG-ACC-014")
                            .name("Tai nghe Gaming HyperX Cloud III Wireless DTS Spatial Audio")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(3590000))
                            .mrpPrice(BigDecimal.valueOf(4190000))
                            .discountPrice(BigDecimal.valueOf(3190000))
                            .discountLimit(20)
                            .discountSold(6)
                            .stock(25)
                            .active("Active")
                            .description("Huyền thoại tai nghe gaming với thời lượng pin không tưởng lên tới 120 giờ và công nghệ âm thanh vòm DTS Spatial Audio.\n\n" +
                                    "• Đệm mút hoạt tính bọc da cao cấp êm ái suốt cả ngày dài chơi game.\n" +
                                    "• Củ loa nghiêng 53mm tinh chỉnh tối ưu cho nhận diện tiếng bước chân trong FPS.\n" +
                                    "• Micro 10mm khử ồn đàm thoại rõ ràng có đèn LED báo tắt tiếng.\n" +
                                    "• Kết nối không dây 2.4GHz không độ trễ tương thích PC, PS5, Switch.\n" +
                                    "• Khung kim loại nhôm nguyên khối bền bỉ theo năm tháng.")
                            .image("https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 40. JBL Charge 5
                    Product.builder()
                            .code("TG-ACC-015")
                            .name("Loa Bluetooth di động JBL Charge 5 Chống Nước IP67 Pin 20h")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(3490000))
                            .mrpPrice(BigDecimal.valueOf(3990000))
                            .discountPrice(BigDecimal.valueOf(2990000))
                            .discountLimit(30)
                            .discountSold(11)
                            .stock(40)
                            .active("Active")
                            .description("Chất âm JBL Pro Sound mạnh mẽ với củ loa trầm độc lập, loa tweeter riêng biệt và bộ tản âm bass kép uy lực.\n\n" +
                                    "• Kháng nước kháng bụi chuẩn IP67 thoải mái mang đi biển dã ngoại.\n" +
                                    "• Thời lượng pin lên đến 20 giờ chơi nhạc thả ga không lo hết pin.\n" +
                                    "• Tích hợp pin sạc dự phòng sạc ngược cho điện thoại qua cổng USB.\n" +
                                    "• Tính năng PartyBoost ghép nối nhiều loa JBL tạo không gian âm thanh vòm.\n" +
                                    "• Vỏ bọc cao su chống va đập bền bỉ trong mọi chuyến đi.")
                            .image("https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 41. Anker 737 GaNPrime 120W
                    Product.builder()
                            .code("TG-ACC-016")
                            .name("Trạm sạc Anker 737 GaNPrime 120W 4 Cổng Thông Minh")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(1890000))
                            .mrpPrice(BigDecimal.valueOf(2290000))
                            .discountPrice(BigDecimal.valueOf(1590000))
                            .discountLimit(40)
                            .discountSold(15)
                            .stock(50)
                            .active("Active")
                            .description("Trạm sạc bàn GaN siêu mạnh mẽ 120W sạc cùng lúc cả MacBook Pro, iPad và iPhone ở tốc độ cao nhất.\n\n" +
                                    "• 3 cổng USB-C và 1 cổng USB-A công suất đơn lên tới 100W.\n" +
                                    "• Công nghệ PowerIQ 4.0 phân bổ công suất thông minh theo thiết bị.\n" +
                                    "• Cảm biến nhiệt ActiveShield 2.0 kiểm soát nhiệt độ an toàn 24/7.\n" +
                                    "• Tiết kiệm năng lượng hơn và nhỏ hơn 39% so với củ sạc truyền thống.\n" +
                                    "• Đi kèm chân đế silicon chống trượt đặt trên bàn làm việc gọn gàng.")
                            .image("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 42. Baseus Blade HD 100W
                    Product.builder()
                            .code("TG-ACC-017")
                            .name("Pin sạc dự phòng Baseus Blade HD 100W Màn hình LED Siêu Mỏng")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(1290000))
                            .mrpPrice(BigDecimal.valueOf(1590000))
                            .discountPrice(BigDecimal.valueOf(990000))
                            .discountLimit(50)
                            .discountSold(19)
                            .stock(60)
                            .active("Active")
                            .description("Pin sạc dự phòng công suất khủng 100W mỏng nhất thế giới chỉ 18mm, dễ dàng nhét vừa túi đựng laptop.\n\n" +
                                    "• Dung lượng chuẩn 20.000mAh hỗ trợ PD 3.0, QC 4.0+, PPS sạc nhanh laptop.\n" +
                                    "• Màn hình LED hiển thị % pin, thời gian sạc đầy và công suất W thực tế.\n" +
                                    "• 2 cổng USB-C và 2 cổng USB-A sạc đồng thời 4 thiết bị cùng lúc.\n" +
                                    "• Sạc đầy lại viên pin 20.000mAh chỉ trong 90 phút với củ sạc 65W.\n" +
                                    "• Đạt tiêu chuẩn an toàn hàng không quốc tế mang lên máy bay.")
                            .image("https://images.unsplash.com/photo-1609592807664-4a572a1ec737?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 43. ASUS TUF Gaming F15
                    Product.builder()
                            .code("TG-LAP-014")
                            .name("Laptop ASUS TUF Gaming F15 FX507ZC4 Core i5 RTX 3050")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(18490000))
                            .mrpPrice(BigDecimal.valueOf(20990000))
                            .discountPrice(BigDecimal.valueOf(16990000))
                            .discountLimit(25)
                            .discountSold(8)
                            .stock(30)
                            .active("Active")
                            .description("Laptop Gaming độ bền chuẩn quân đội MIL-STD-810H với chip Intel Core i5-12500H 12 nhân 16 luồng và card rời RTX 3050 4GB.\n\n" +
                                    "• CPU: Intel Core i5-12500H (12 nhân, 16 luồng, xung nhịp Turbo 4.5GHz).\n" +
                                    "• VGA: NVIDIA GeForce RTX 3050 4GB GDDR6 (có MUX Switch).\n" +
                                    "• RAM: 16GB DDR4 3200MHz, SSD 512GB NVMe PCIe 4.0.\n" +
                                    "• Màn hình: 15.6 inch FHD 144Hz IPS chống chói Adaptive-Sync.\n" +
                                    "• Pin 56Wh hỗ trợ sạc nhanh qua cổng Type-C.")
                            .image("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 44. MSI Katana 15
                    Product.builder()
                            .code("TG-LAP-015")
                            .name("Laptop Gaming MSI Katana 15 B13VFK Core i7 RTX 4060")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(27990000))
                            .mrpPrice(BigDecimal.valueOf(31500000))
                            .discountPrice(BigDecimal.valueOf(25990000))
                            .discountLimit(15)
                            .discountSold(5)
                            .stock(18)
                            .active("Active")
                            .description("Thanh kiếm Katana gaming sắc bén với Intel Core i7-13620H thế hệ 13 và card đồ họa RTX 4060 8GB GDDR6 cực mạnh.\n\n" +
                                    "• CPU: Intel Core i7-13620H (10 nhân, 16 luồng, xung nhịp Turbo 4.9GHz).\n" +
                                    "• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 Full TGP 105W DLSS 3.\n" +
                                    "• RAM: 16GB DDR5 5200MHz, SSD 1TB PCIe NVMe Gen 4.\n" +
                                    "• Màn hình: 15.6 inch FHD 144Hz IPS viền siêu mỏng.\n" +
                                    "• Bàn phím 4 vùng LED RGB với cụm phím WASD nổi bật.")
                            .image("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 45. LG Gram 16 2024
                    Product.builder()
                            .code("TG-LAP-016")
                            .name("Laptop LG Gram 16 2024 Siêu Nhẹ 1.19kg Intel Core Ultra 7")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(34990000))
                            .mrpPrice(BigDecimal.valueOf(38990000))
                            .discountPrice(BigDecimal.valueOf(32490000))
                            .discountLimit(10)
                            .discountSold(2)
                            .stock(12)
                            .active("Active")
                            .description("Đỉnh cao laptop 16 inch siêu nhẹ chỉ 1.19kg, khung hợp kim Magie Nano chuẩn độ bền quân đội Mỹ và chip Intel Core Ultra 7.\n\n" +
                                    "• CPU: Intel Core Ultra 7 155H thế hệ mới tích hợp Intel AI Boost NPU.\n" +
                                    "• RAM: 16GB LPDDR5X 7467MHz băng thông cực lớn, SSD 512GB NVMe.\n" +
                                    "• Màn hình: 16.0 inch 2.5K WQXGA (2560x1600) IPS chống chói 99% DCI-P3.\n" +
                                    "• Dung lượng pin khủng 77Wh cho thời lượng làm việc văn phòng tới 24 giờ liên tục.")
                            .image("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 46. LG UltraGear 27GR95QE-B OLED 240Hz
                    Product.builder()
                            .code("TG-ACC-018")
                            .name("Màn hình Gaming LG UltraGear 27GR95QE-B OLED 240Hz 0.03ms")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(18990000))
                            .mrpPrice(BigDecimal.valueOf(22000000))
                            .discountPrice(BigDecimal.valueOf(16990000))
                            .discountLimit(8)
                            .discountSold(3)
                            .stock(10)
                            .active("Active")
                            .description("Màn hình OLED chơi game đầu tiên trên thế giới với tần số quét 240Hz và thời gian phản hồi 0.03ms cực nhanh.\n\n" +
                                    "• Tấm nền OLED 27 inch 2K QHD (2560x1440) tỷ lệ tương phản 1.500.000:1.\n" +
                                    "• Tần số quét 240Hz, phản hồi 0.03ms GtG triệt tiêu hoàn toàn bóng mờ.\n" +
                                    "• Độ phủ màu 98.5% DCI-P3 chuẩn điện ảnh HDR10.\n" +
                                    "• Tương thích NVIDIA G-Sync và AMD FreeSync Premium.")
                            .image("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 47. DareU EK87 V2
                    Product.builder()
                            .code("TG-ACC-019")
                            .name("Bàn phím cơ DareU EK87 V2 Multi-LED D-Switch")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(590000))
                            .mrpPrice(BigDecimal.valueOf(690000))
                            .discountPrice(BigDecimal.valueOf(490000))
                            .discountLimit(50)
                            .discountSold(24)
                            .stock(80)
                            .active("Active")
                            .description("Bàn phím cơ quốc dân dành cho học sinh, sinh viên với layout TKL 87 phím và switch D-Switch độc quyền bền bỉ 50 triệu lần bấm.\n\n" +
                                    "• Layout TKL 87 phím gọn gàng, khung vỏ cứng cáp đầm tay.\n" +
                                    "• Switch D-Switch (Blue / Brown / Red) cảm giác gõ tốt trong tầm giá.\n" +
                                    "• Đèn nền Multi-LED Rainbow rực rỡ nhiều hiệu ứng.\n" +
                                    "• Keycap ABS Double-Shot siêu bền không mờ chữ.")
                            .image("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 48. Pulsar X2V2 Wireless
                    Product.builder()
                            .code("TG-ACC-020")
                            .name("Chuột gaming không dây Pulsar X2V2 Wireless Siêu Nhẹ 52g")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2290000))
                            .mrpPrice(BigDecimal.valueOf(2590000))
                            .discountPrice(BigDecimal.valueOf(1990000))
                            .discountLimit(20)
                            .discountSold(7)
                            .stock(25)
                            .active("Active")
                            .description("Chuột eSports bắn súng đỉnh cao với trọng lượng siêu nhẹ chỉ 52g và cảm biến PixArt PAW3395 26.000 DPI.\n\n" +
                                    "• Trọng lượng lông vũ 52 gram không đục lỗ, cầm đối xứng hoàn hảo.\n" +
                                    "• Cảm biến flagship PixArt PAW3395 26K DPI, 650 IPS, 50G.\n" +
                                    "• Switch quang học chống nảy đúp (double-click) tuyệt đối.\n" +
                                    "• Hỗ trợ polling rate lên tới 4000Hz (với dongle 4K rời).\n" +
                                    "• Thời lượng pin lên tới 100 giờ liên tục.")
                            .image("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 49. Sennheiser Momentum 4
                    Product.builder()
                            .code("TG-ACC-021")
                            .name("Tai nghe Chống Ồn Sennheiser Momentum 4 Wireless Pin 60h")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(7990000))
                            .mrpPrice(BigDecimal.valueOf(9490000))
                            .discountPrice(BigDecimal.valueOf(6990000))
                            .discountLimit(12)
                            .discountSold(4)
                            .stock(15)
                            .active("Active")
                            .description("Tuyệt tác âm thanh Audiophile từ Đức với thời lượng pin kỷ lục 60 giờ và công nghệ chống ồn thích ứng Adaptive ANC.\n\n" +
                                    "• Màng loa 42mm audiophile mang lại chất âm tinh khiết giàu chi tiết.\n" +
                                    "• Chống ồn thích ứng thế hệ mới và chế độ xuyên âm tự nhiên Transparency Mode.\n" +
                                    "• Pin 60 giờ phát nhạc liên tục (bật cả chống ồn ANC).\n" +
                                    "• Hỗ trợ codec chất lượng cao aptX Adaptive, AAC, SBC.\n" +
                                    "• Thiết kế bọc vải cao cấp sang trọng, đệm tai mút hoạt tính cực êm.")
                            .image("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build(),

                    // 50. Belkin BoostCharge Pro MagSafe 3-in-1
                    Product.builder()
                            .code("TG-ACC-022")
                            .name("Đế sạc không dây 3-in-1 Belkin BoostCharge Pro MagSafe 15W")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2990000))
                            .mrpPrice(BigDecimal.valueOf(3590000))
                            .discountPrice(BigDecimal.valueOf(2590000))
                            .discountLimit(30)
                            .discountSold(11)
                            .stock(35)
                            .active("Active")
                            .description("Đế sạc không dây cao cấp đạt chứng nhận Apple Made for MagSafe (MFM), sạc đồng thời iPhone, Apple Watch và AirPods.\n\n" +
                                    "• Sạc nhanh không dây chuẩn MagSafe 15W cho iPhone 12/13/14/15/16.\n" +
                                    "• Sạc nhanh Apple Watch từ Series 7/8/9/Ultra nhanh hơn 33%.\n" +
                                    "• Đế sạc AirPods chuẩn Qi 5W riêng biệt.\n" +
                                    "• Thiết kế kim loại mạ crom sang trọng tô điểm cho bàn làm việc.")
                            .image("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80")
                            .createDate(new Date())
                            .build()
            );

            int addedCount = 0;
            for (Product p : sampleProducts) {
                if (!productRepository.existsByCode(p.getCode())) {
                    productRepository.save(p);
                    addedCount++;
                }
            }
            if (addedCount > 0) {
                log.info(">>> [SEED DATA] Khởi tạo/Bổ sung thành công {} sản phẩm mẫu công nghệ thực tế cho TechGear Store!", addedCount);
            }
        }
    }

    public void ensureProductsSeeded() {
        initProducts();
    }
}
