package com.da.da.config;

import com.da.da.entity.Admin;
import com.da.da.entity.Customer;
import com.da.da.entity.Product;
import com.da.da.repository.AdminRepository;
import com.da.da.repository.CustomerRepository;
import com.da.da.repository.ProductRepository;
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
@Profile("dev")
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

    private void initProducts() {
        if (productRepository.count() == 0) {
            List<Product> sampleProducts = List.of(
                    Product.builder()
                            .name("Laptop Gaming ASUS ROG Strix G16 (2024)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(28000000))
                            .mrpPrice(BigDecimal.valueOf(31000000))
                            .discountPrice(BigDecimal.valueOf(25900000))
                            .discountLimit(5)
                            .discountSold(0)
                            .stock(15)
                            .active("Active")
                            .description("Laptop Gaming ASUS ROG Strix G16 (2024) là cỗ máy chiến game đỉnh cao thuộc phân khúc cao cấp, được thiết kế tối ưu cho game thủ chuyên nghiệp và nhà sáng tạo nội dung.\n\n" +
                                    "🔥 HIỆU NĂNG BỨT PHÁ VỚI INTEL GEN 13 & RTX 40 SERIES\n" +
                                    "• CPU: Intel Core i7-13650HX thế hệ 13 (14 nhân, 20 luồng, xung nhịp tối đa lên tới 4.9GHz) xử lý đa nhiệm cực đỉnh.\n" +
                                    "• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 (TGP tối đa 140W) tích hợp công nghệ MUX Switch + NVIDIA Advanced Optimus, cân mượt mà mọi tựa game AAA ở thiết lập đồ họa cao nhất và render video 4K nhanh chóng.\n" +
                                    "• RAM: 16GB DDR5 4800MHz (hỗ trợ nâng cấp 2 khe lên đến 64GB).\n" +
                                    "• Ổ cứng: 512GB PCIe 4.0 NVMe M.2 SSD tốc độ đọc ghi vượt trội, hỗ trợ thêm 1 khe SSD mở rộng.\n\n" +
                                    "🖥️ MÀN HÌNH ROG NEBULA 16 INCH 165Hz CHUẨN ĐỒ HỌA\n" +
                                    "• Kích thước: 16.0 inch tỷ lệ vàng 16:10, độ phân giải FHD+ (1920 x 1200) siêu sắc nét.\n" +
                                    "• Tần số quét: 165Hz tốc độ phản hồi 7ms, hỗ trợ công nghệ G-Sync chống xé hình hoàn hảo.\n" +
                                    "• Màu sắc: Độ phủ màu 100% sRGB, độ sáng 500 nits, chuẩn Dolby Vision HDR đáp ứng hoàn hảo nhu cầu đồ họa và dựng phim.\n\n" +
                                    "❄️ CÔNG NGHỆ TẢN NHIỆT ROG INTELLIGENT COOLING\n" +
                                    "• Hệ thống 3 quạt làm mát Tri-Fan kết hợp keo tản nhiệt kim loại lỏng Conductonaut Extreme trên CPU.\n" +
                                    "• Khe thoát nhiệt bao quanh toàn bộ thân máy giữ cho nhiệt độ luôn mát mẻ và êm ái khi thi đấu cường độ cao liên tục.\n\n" +
                                    "⚡ BÀN PHÍM GAMING RGB & KẾT NỐI TOÀN DIỆN\n" +
                                    "• Bàn phím Chiclet hỗ trợ LED RGB từng phím (Per-Key RGB), hành trình phím sâu 2.0mm nhận lệnh cực nhạy.\n" +
                                    "• Pin 90Whr hỗ trợ sạc nhanh qua cổng Type-C 100W PD (50% pin chỉ trong 30 phút).\n" +
                                    "• Kết nối: Thunderbolt 4, HDMI 2.1, USB 3.2 Gen 2, RJ45 2.5G LAN và Wi-Fi 6E siêu tốc.")
                            .image("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600")
                            .createDate(new Date())
                            .build(),

                    Product.builder()
                            .name("Apple MacBook Pro 14 M3 Pro (18GB/512GB)")
                            .productCategory("Laptop")
                            .price(BigDecimal.valueOf(45000000))
                            .mrpPrice(BigDecimal.valueOf(49990000))
                            .discountPrice(BigDecimal.valueOf(43500000))
                            .discountLimit(3)
                            .discountSold(0)
                            .stock(10)
                            .active("Active")
                            .description("Apple MacBook Pro 14 M3 Pro là sự kết hợp hoàn hảo giữa thiết kế kim loại nguyên khối unibody đẳng cấp cùng sức mạnh tính toán vượt trội trên tiến trình 3nm tiên tiến nhất thế giới.\n\n" +
                                    "🚀 CHIP APPLE M3 PRO THẾ HỆ MỚI ĐẦY UY LỰC\n" +
                                    "• CPU 11 nhân (5 nhân hiệu năng cao + 6 nhân tiết kiệm điện) xử lý mượt mà các luồng công việc nặng từ lập trình, biên dịch mã nguồn đến dựng phim đa luồng.\n" +
                                    "• GPU 14 nhân với kiến trúc Dynamic Caching đột phá cùng Ray Tracing tăng tốc phần cứng, mang lại hiệu năng render 3D và xử lý đồ họa gấp nhiều lần thế hệ trước.\n" +
                                    "• Bộ nhớ Unified Memory 18GB băng thông cực lớn 150GB/s cho khả năng đa nhiệm hàng chục ứng dụng chuyên nghiệp cùng lúc.\n" +
                                    "• Ổ cứng SSD 512GB siêu tốc với tốc độ đọc ghi lên tới hơn 5.000 MB/s, mở dự án và tệp tin nặng chỉ trong tích tắc.\n\n" +
                                    "✨ MÀN HÌNH LIQUID RETINA XDR ĐẸP NHẤT TỪNG CÓ TRÊN LAPTOP\n" +
                                    "• Kích thước: 14.2 inch độ phân giải cực cao (3024 x 1964) mật độ điểm ảnh 254 ppi.\n" +
                                    "• Tần số quét tự thích ứng ProMotion 120Hz mượt mà trong từng chuyển động cuộn và thao tác đồ họa.\n" +
                                    "• Độ sáng duy trì 1000 nits, đạt đỉnh 1600 nits khi phát nội dung HDR, tỷ lệ tương phản 1.000.000:1 cho sắc đen sâu thẳm.\n\n" +
                                    "🔋 THỜI LƯỢNG PIN KỶ LỤC LÊN ĐẾN 18 GIỜ\n" +
                                    "• Sử dụng liên tục cả ngày làm việc mà không cần mang theo củ sạc. Hiệu năng được giữ nguyên 100% dù cắm sạc hay dùng pin.\n\n" +
                                    "🎧 ÂM THANH PHÒNG THU & KẾT NỐI CHUYÊN NGHIỆP\n" +
                                    "• Dàn 6 loa âm thanh vòm Spatial Audio với loa trầm khử lực mang lại trải nghiệm âm nhạc chuẩn rạp chiếu phim.\n" +
                                    "• Cổng kết nối Pro: 3x Thunderbolt 4, cổng HDMI xuất màn hình 8K, khe đọc thẻ SDXC tốc độ cao, jack 3.5mm cho tai nghe trở kháng cao và cổng sạc từ tính MagSafe 3 tiện lợi.")
                            .image("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600")
                            .createDate(new Date())
                            .build(),

                    Product.builder()
                            .name("Bàn phím cơ không dây AKKO 3087 v2 RGB")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(1290000))
                            .mrpPrice(BigDecimal.valueOf(1590000))
                            .discountPrice(BigDecimal.valueOf(1090000))
                            .discountLimit(10)
                            .discountSold(0)
                            .stock(30)
                            .active("Active")
                            .description("AKKO 3087 v2 RGB là mẫu bàn phím cơ TKL (87 phím) kinh điển được đông đảo game thủ và dân văn phòng săn đón nhờ build chắc chắn, cảm giác gõ êm ái và mức giá cực kỳ dễ tiếp cận.\n\n" +
                                    "⌨️ THIẾT KẾ TENKEYLESS NHỎ GỌN & TIỆN LỢI\n" +
                                    "• Bố cục TKL 87 phím gọn gàng, tiết kiệm không gian bàn làm việc và tạo khoảng trống thoải mái cho thao tác di chuột khi chơi game.\n" +
                                    "• Khung vỏ nhựa ABS dày dặn, cứng cáp với độ hoàn thiện tinh xảo, chống rung lắc tối đa khi gõ tốc độ cao.\n\n" +
                                    "🎯 SWITCH AKKO CS V3 ĐỘC QUYỀN ÊM ÁI\n" +
                                    "• Sử dụng dòng switch Akko CS V3 thế hệ mới nhất với lò xo kéo dài cho lực nảy đều tay, đầm và cực kỳ kích thích.\n" +
                                    "• Switch đã được lube sẵn tại nhà máy, mang lại hành trình bấm mượt mà, hạn chế tối đa cảm giác sạn và rung lắc.\n" +
                                    "• Độ bền cực cao lên tới 60 triệu lần nhấn phím.\n\n" +
                                    "🎨 KEYCAP PBT DOUBLE-SHOT CHỐNG MÒN BÓNG\n" +
                                    "• Chất liệu nhựa PBT cao cấp với công nghệ đúc 2 lớp (Double-Shot) bền bỉ vĩnh viễn, ký tự không bao giờ bị phai mờ.\n" +
                                    "• Bề mặt hơi nhám nhẹ cho cảm giác bám tay chắc chắn, không bị bóng dầu sau nhiều năm sử dụng.\n\n" +
                                    "🌈 HỆ THỐNG LED RGB 16.8 TRIỆU MÀU ĐA DẠNG\n" +
                                    "• Hơn 20 chế độ hiệu ứng ánh sáng RGB rực rỡ, hỗ trợ tùy chỉnh tốc độ, độ sáng và hiệu ứng nháy theo nhạc.\n" +
                                    "• Trang bị sẵn foam tiêu âm PCB EVA cao cấp giúp hấp thụ âm thanh rỗng, mang lại âm gõ trầm ấm và không gây phiền toái cho người xung quanh.\n" +
                                    "• Cáp kết nối Type-C rời bọc dù chắc chắn, dễ dàng tháo lắp khi di chuyển.")
                            .image("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600")
                            .createDate(new Date())
                            .build(),

                    Product.builder()
                            .name("Chuột không dây Logitech G Pro X Superlight 2")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(2990000))
                            .mrpPrice(BigDecimal.valueOf(3490000))
                            .discountPrice(BigDecimal.valueOf(2690000))
                            .discountLimit(8)
                            .discountSold(0)
                            .stock(20)
                            .active("Active")
                            .description("Logitech G Pro X Superlight 2 là đỉnh cao của dòng chuột thi đấu eSports chuyên nghiệp, được nghiên cứu và thiết kế cùng các vận động viên thể thao điện tử hàng đầu thế giới để đạt đến sự chuẩn xác và phản xạ tuyệt đối.\n\n" +
                                    "🪶 TRỌNG LƯỢNG SIÊU NHẸ CHỈ 60 GRAM\n" +
                                    "• Thiết kế tối giản trọng lượng chỉ còn 60g mà không cần khoét lỗ tổ ong trên thân chuột, bảo vệ tối đa linh kiện bên trong khỏi bụi bẩn và mồ hôi tay.\n" +
                                    "• Trọng lượng lông vũ giúp game thủ vẩy chuột nhanh hơn, phản xạ tức thì trong các pha ngắm bắn căng thẳng và hoàn toàn không mỏi cổ tay sau nhiều giờ thi đấu.\n\n" +
                                    "🎯 CẢM BIẾN HERO 2 THẾ HỆ MỚI CHÍNH XÁC TỪNG PIXEL\n" +
                                    "• Cảm biến quang học tân tiến nhất với độ phân giải lên đến 32.000 DPI, tốc độ quét trên 500 IPS và gia tốc tối đa 40G.\n" +
                                    "• Không làm mịn, không lọc và không gia tốc ảo, đảm bảo từng milimet di chuột đều được tái hiện chính xác 1:1 trên màn hình.\n\n" +
                                    "⚡ SWITCH CƠ LAI QUANG HỌC LIGHTFORCE ĐỘT PHÁ\n" +
                                    "• Công nghệ switch LIGHTFORCE độc quyền kết hợp tốc độ kích hoạt bằng ánh sáng siêu tốc độ (loại bỏ hoàn toàn độ trễ debounce) cùng cảm giác bấm cơ học giòn tan, đanh và rõ ràng.\n" +
                                    "• Khắc phục hoàn toàn tình trạng double-click vốn thường gặp trên các dòng chuột cơ truyền thống.\n\n" +
                                    "📡 KẾT NỐI KHÔNG DÂY LIGHTSPEED 2000Hz (0.5ms)\n" +
                                    "• Tần số gửi tín hiệu (Polling Rate) lên tới 2000Hz, nhanh gấp 2 lần thế hệ tiền nhiệm, cho độ trễ kết nối không dây gần như bằng 0.\n" +
                                    "• Thời lượng pin đột phá lên tới 95 giờ chơi game liên tục. Nâng cấp cổng sạc USB Type-C chuẩn hiện đại và tương thích hoàn hảo với bàn di chuột sạc không dây POWERPLAY.\n" +
                                    "• Đế chuột 100% PTFE nguyên chất không pha tạp chất cho cảm giác lướt chuột êm ái mượt mà trên mọi bề mặt pad.")
                            .image("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600")
                            .createDate(new Date())
                            .build(),

                    Product.builder()
                            .name("Tai nghe Bluetooth Sony WH-1000XM5")
                            .productCategory("Accessories")
                            .price(BigDecimal.valueOf(6990000))
                            .mrpPrice(BigDecimal.valueOf(8490000))
                            .discountPrice(BigDecimal.valueOf(6290000))
                            .discountLimit(5)
                            .discountSold(0)
                            .stock(12)
                            .active("Active")
                            .description("Sony WH-1000XM5 là mẫu tai nghe chống ồn over-ear cao cấp số 1 thế giới hiện nay, thiết lập chuẩn mực mới về khả năng cách ly tạp âm, chất âm Hi-Res Audio tinh tế cùng thiết kế không khớp nối sang trọng.\n\n" +
                                    "🔇 CÔNG NGHỆ CHỐNG ỒN CHỦ ĐỘNG AUTO NC OPTIMIZER HÀNG ĐẦU\n" +
                                    "• Trang bị bộ đôi vi xử lý chuyên dụng V1 và HD Noise Cancelling QN1 điều khiển hệ thống 8 micro thu âm đa hướng.\n" +
                                    "• Tự động tối ưu hóa khả năng chống ồn theo môi trường xung quanh, hình dạng khuôn mặt và áp suất khí quyển theo thời gian thực, loại bỏ triệt để tiếng ồn động cơ máy bay, tiếng xe cộ và tiếng trò chuyện ồn ào.\n\n" +
                                    "🎵 ÂM THANH HI-RES AUDIO ĐỈNH CAO VỚI CỦ LOA SỢI CARBON 30mm\n" +
                                    "• Củ loa 30mm vòm sợi carbon tổng hợp siêu nhẹ mang lại âm trầm sâu chắc, âm trung dày dặn và âm cao trong trẻo mở rộng lên tới 40.000 Hz.\n" +
                                    "• Hỗ trợ codec âm thanh cao cấp LDAC truyền tải dữ liệu gấp 3 lần chuẩn Bluetooth thông thường, tái tạo âm thanh nguyên bản như tại phòng thu.\n" +
                                    "• Công nghệ DSEE Extreme sử dụng trí tuệ nhân tạo (Edge-AI) nâng cấp các file nhạc nén theo thời gian thực.\n\n" +
                                    "🎙️ ĐÀM THOẠI TRONG TRẺO VỚI 4 MICRO AI BEAMFORMING\n" +
                                    "• Hệ thống 4 micro định hướng chùm sóng kết hợp thuật toán AI khử ồn sâu từ hơn 500 triệu mẫu giọng nói, lọc sạch hoàn toàn tiếng gió và tiếng ồn xung quanh để giọng đàm thoại luôn rõ nét.\n\n" +
                                    "🔋 PIN 30 GIỜ BỀN BỈ & SẠC SIÊU TỐC USB-PD\n" +
                                    "• Thời lượng pin lên đến 30 giờ khi bật chống ồn (lên đến 40 giờ khi tắt chống ồn).\n" +
                                    "• Sạc nhanh thần tốc qua cổng Type-C: Chỉ 3 phút sạc cho ngay 3 giờ nghe nhạc liên tục.\n" +
                                    "• Tính năng thông minh Speak-to-Chat tự động dừng nhạc khi bạn cất tiếng nói, và kết nối đa điểm đồng thời 2 thiết bị (Multipoint Connection).")
                            .image("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600")
                            .createDate(new Date())
                            .build()
            );

            productRepository.saveAll(sampleProducts);
            log.info(">>> [SEED DATA] Khởi tạo thành công {} sản phẩm mẫu cho TechGear Store!", sampleProducts.size());
        }
    }
}
