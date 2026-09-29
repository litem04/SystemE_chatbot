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
                            .description("Laptop Gaming đỉnh cao trang bị CPU Intel Core i7-13650HX, RTX 4060 8GB, Màn hình 16 inch 165Hz chuẩn màu 100% sRGB.")
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
                            .description("Siêu phẩm đồ họa chuyên nghiệp chip Apple M3 Pro, màn hình Liquid Retina XDR 120Hz siêu nét, thời lượng pin tới 18 tiếng.")
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
                            .description("Bàn phím cơ TKL nhỏ gọn, switch Akko CS V3 êm ái, kết nối Type-C đa năng, đèn LED RGB rực rỡ.")
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
                            .description("Chuột Esports siêu nhẹ chỉ 60g, cảm biến HERO 2 32.000 DPI, Switch cơ học lai quang học LIGHTFORCE.")
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
                            .description("Tai nghe chống ồn số 1 thế giới công nghệ Auto NC Optimizer, âm thanh Hi-Res Audio, thời lượng pin 30 giờ.")
                            .image("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600")
                            .createDate(new Date())
                            .build()
            );

            productRepository.saveAll(sampleProducts);
            log.info(">>> [SEED DATA] Khởi tạo thành công {} sản phẩm mẫu cho TechGear Store!", sampleProducts.size());
        }
    }
}
