-- Flyway Migration V7: Bổ sung danh mục sản phẩm công nghệ thực tế, phong phú và chuyên nghiệp cho TechGear Store

-- 1. Laptop Gaming ASUS ROG Strix G16 (2024)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (1, 'TG-LAP-001', 'Laptop Gaming ASUS ROG Strix G16 (2024)', 'Laptop', 28000000, 31000000, 25900000, 10, 3, 15, 'Active', CURRENT_TIMESTAMP, 
'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80',
'Laptop Gaming ASUS ROG Strix G16 (2024) là cỗ máy chiến game đỉnh cao thuộc phân khúc cao cấp, được thiết kế tối ưu cho game thủ chuyên nghiệp và nhà sáng tạo nội dung.

🔥 HIỆU NĂNG BỨT PHÁ VỚI INTEL GEN 13 & RTX 40 SERIES
• CPU: Intel Core i7-13650HX thế hệ 13 (14 nhân, 20 luồng, xung nhịp tối đa lên tới 4.9GHz) xử lý đa nhiệm cực đỉnh.
• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 (TGP tối đa 140W) tích hợp công nghệ MUX Switch + NVIDIA Advanced Optimus, cân mượt mà mọi tựa game AAA ở thiết lập đồ họa cao nhất và render video 4K nhanh chóng.
• RAM: 16GB DDR5 4800MHz (hỗ trợ nâng cấp 2 khe lên đến 64GB).
• Ổ cứng: 512GB PCIe 4.0 NVMe M.2 SSD tốc độ đọc ghi vượt trội, hỗ trợ thêm 1 khe SSD mở rộng.

🖥️ MÀN HÌNH ROG NEBULA 16 INCH 165Hz CHUẨN ĐỒ HỌA
• Kích thước: 16.0 inch tỷ lệ vàng 16:10, độ phân giải FHD+ (1920 x 1200) siêu sắc nét.
• Tần số quét: 165Hz tốc độ phản hồi 7ms, hỗ trợ công nghệ G-Sync chống xé hình hoàn hảo.
• Màu sắc: Độ phủ màu 100% sRGB, độ sáng 500 nits, chuẩn Dolby Vision HDR đáp ứng hoàn hảo nhu cầu đồ họa và dựng phim.

❄️ CÔNG NGHỆ TẢN NHIỆT ROG INTELLIGENT COOLING
• Hệ thống 3 quạt làm mát Tri-Fan kết hợp keo tản nhiệt kim loại lỏng Conductonaut Extreme trên CPU.
• Khe thoát nhiệt bao quanh toàn bộ thân máy giữ cho nhiệt độ luôn mát mẻ và êm ái khi thi đấu cường độ cao liên tục.

⚡ BÀN PHÍM GAMING RGB & KẾT NỐI TOÀN DIỆN
• Bàn phím Chiclet hỗ trợ LED RGB từng phím (Per-Key RGB), hành trình phím sâu 2.0mm nhận lệnh cực nhạy.
• Pin 90Whr hỗ trợ sạc nhanh qua cổng Type-C 100W PD (50% pin chỉ trong 30 phút).
• Kết nối: Thunderbolt 4, HDMI 2.1, USB 3.2 Gen 2, RJ45 2.5G LAN và Wi-Fi 6E siêu tốc.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 2. Apple MacBook Pro 14 M3 Pro
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (2, 'TG-LAP-002', 'Apple MacBook Pro 14 M3 Pro (18GB/512GB)', 'Laptop', 45000000, 49990000, 43500000, 8, 2, 12, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80',
'Apple MacBook Pro 14 M3 Pro là sự kết hợp hoàn hảo giữa thiết kế kim loại nguyên khối unibody đẳng cấp cùng sức mạnh tính toán vượt trội trên tiến trình 3nm tiên tiến nhất thế giới.

🚀 CHIP APPLE M3 PRO THẾ HỆ MỚI ĐẦY UY LỰC
• CPU 11 nhân (5 nhân hiệu năng cao + 6 nhân tiết kiệm điện) xử lý mượt mà các luồng công việc nặng từ lập trình, biên dịch mã nguồn đến dựng phim đa luồng.
• GPU 14 nhân với kiến trúc Dynamic Caching đột phá cùng Ray Tracing tăng tốc phần cứng, mang lại hiệu năng render 3D và xử lý đồ họa gấp nhiều lần thế hệ trước.
• Bộ nhớ Unified Memory 18GB băng thông cực lớn 150GB/s cho khả năng đa nhiệm hàng chục ứng dụng chuyên nghiệp cùng lúc.
• Ổ cứng SSD 512GB siêu tốc với tốc độ đọc ghi lên tới hơn 5.000 MB/s, mở dự án và tệp tin nặng chỉ trong tích tắc.

✨ MÀN HÌNH LIQUID RETINA XDR ĐẸP NHẤT TỪNG CÓ TRÊN LAPTOP
• Kích thước: 14.2 inch độ phân giải cực cao (3024 x 1964) mật độ điểm ảnh 254 ppi.
• Tần số quét tự thích ứng ProMotion 120Hz mượt mà trong từng chuyển động cuộn và thao tác đồ họa.
• Độ sáng duy trì 1000 nits, đạt đỉnh 1600 nits khi phát nội dung HDR, tỷ lệ tương phản 1.000.000:1 cho sắc đen sâu thẳm.

🔋 THỜI LƯỢNG PIN KỶ LỤC LÊN ĐẾN 18 GIỜ
• Sử dụng liên tục cả ngày làm việc mà không cần mang theo củ sạc. Hiệu năng được giữ nguyên 100% dù cắm sạc hay dùng pin.

🎧 ÂM THANH PHÒNG THU & KẾT NỐI CHUYÊN NGHIỆP
• Dàn 6 loa âm thanh vòm Spatial Audio với loa trầm khử lực mang lại trải nghiệm âm nhạc chuẩn rạp chiếu phim.
• Cổng kết nối Pro: 3x Thunderbolt 4, cổng HDMI xuất màn hình 8K, khe đọc thẻ SDXC tốc độ cao, jack 3.5mm cho tai nghe trở kháng cao và cổng sạc từ tính MagSafe 3 tiện lợi.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 3. Bàn phím cơ không dây AKKO 3087 v2 RGB
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (3, 'TG-ACC-001', 'Bàn phím cơ không dây AKKO 3087 v2 RGB', 'Accessories', 1290000, 1590000, 1090000, 30, 12, 50, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80',
'AKKO 3087 v2 RGB là mẫu bàn phím cơ TKL (87 phím) kinh điển được đông đảo game thủ và dân văn phòng săn đón nhờ build chắc chắn, cảm giác gõ êm ái và mức giá cực kỳ dễ tiếp cận.

⌨️ THIẾT KẾ TENKEYLESS NHỎ GỌN & TIỆN LỢI
• Bố cục TKL 87 phím gọn gàng, tiết kiệm không gian bàn làm việc và tạo khoảng trống thoải mái cho thao tác di chuột khi chơi game.
• Khung vỏ nhựa ABS dày dặn, cứng cáp với độ hoàn thiện tinh xảo, chống rung lắc tối đa khi gõ tốc độ cao.

🎯 SWITCH AKKO CS V3 ĐỘC QUYỀN ÊM ÁI
• Sử dụng dòng switch Akko CS V3 thế hệ mới nhất với lò xo kéo dài cho lực nảy đều tay, đầm và cực kỳ kích thích.
• Switch đã được lube sẵn tại nhà máy, mang lại hành trình bấm mượt mà, hạn chế tối đa cảm giác sạn và rung lắc.
• Độ bền cực cao lên tới 60 triệu lần nhấn phím.

🎨 KEYCAP PBT DOUBLE-SHOT CHỐNG MÒN BÓNG
• Chất liệu nhựa PBT cao cấp với công nghệ đúc 2 lớp (Double-Shot) bền bỉ vĩnh viễn, ký tự không bao giờ bị phai mờ.
• Bề mặt hơi nhám nhẹ cho cảm giác bám tay chắc chắn, không bị bóng dầu sau nhiều năm sử dụng.

🌈 HỆ THỐNG LED RGB 16.8 TRIỆU MÀU ĐA DẠNG
• Hơn 20 chế độ hiệu ứng ánh sáng RGB rực rỡ, hỗ trợ tùy chỉnh tốc độ, độ sáng và hiệu ứng nháy theo nhạc.
• Trang bị sẵn foam tiêu âm PCB EVA cao cấp giúp hấp thụ âm thanh rỗng, mang lại âm gõ trầm ấm và không gây phiền toái cho người xung quanh.
• Cáp kết nối Type-C rời bọc dù chắc chắn, dễ dàng tháo lắp khi di chuyển.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 4. Chuột không dây Logitech G Pro X Superlight 2
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (4, 'TG-ACC-003', 'Chuột không dây Logitech G Pro X Superlight 2', 'Accessories', 2990000, 3490000, 2690000, 25, 14, 30, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80',
'Logitech G Pro X Superlight 2 là đỉnh cao của dòng chuột thi đấu eSports chuyên nghiệp, được nghiên cứu và thiết kế cùng các vận động viên thể thao điện tử hàng đầu thế giới để đạt đến sự chuẩn xác và phản xạ tuyệt đối.

🪶 TRỌNG LƯỢNG SIÊU NHẸ CHỈ 60 GRAM
• Thiết kế tối giản trọng lượng chỉ còn 60g mà không cần khoét lỗ tổ ong trên thân chuột, bảo vệ tối đa linh kiện bên trong khỏi bụi bẩn và mồ hôi tay.
• Trọng lượng lông vũ giúp game thủ vẩy chuột nhanh hơn, phản xạ tức thì trong các pha ngắm bắn căng thẳng và hoàn toàn không mỏi cổ tay sau nhiều giờ thi đấu.

🎯 CẢM BIẾN HERO 2 THẾ HỆ MỚI CHÍNH XÁC TỪNG PIXEL
• Cảm biến quang học tân tiến nhất với độ phân giải lên đến 32.000 DPI, tốc độ quét trên 500 IPS và gia tốc tối đa 40G.
• Không làm mịn, không lọc và không gia tốc ảo, đảm bảo từng milimet di chuột đều được tái hiện chính xác 1:1 trên màn hình.

⚡ SWITCH CƠ LAI QUANG HỌC LIGHTFORCE ĐỘT PHÁ
• Công nghệ switch LIGHTFORCE độc quyền kết hợp tốc độ kích hoạt bằng ánh sáng siêu tốc độ (loại bỏ hoàn toàn độ trễ debounce) cùng cảm giác bấm cơ học giòn tan, đanh và rõ ràng.
• Khắc phục hoàn toàn tình trạng double-click vốn thường gặp trên các dòng chuột cơ truyền thống.

📡 KẾT NỐI KHÔNG DÂY LIGHTSPEED 2000Hz (0.5ms)
• Tần số gửi tín hiệu (Polling Rate) lên tới 2000Hz, nhanh gấp 2 lần thế hệ tiền nhiệm, cho độ trễ kết nối không dây gần như bằng 0.
• Thời lượng pin đột phá lên tới 95 giờ chơi game liên tục. Nâng cấp cổng sạc USB Type-C chuẩn hiện đại và tương thích hoàn hảo với bàn di chuột sạc không dây POWERPLAY.
• Đế chuột 100% PTFE nguyên chất không pha tạp chất cho cảm giác lướt chuột êm ái mượt mà trên mọi bề mặt pad.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 5. Tai nghe Bluetooth Sony WH-1000XM5
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (5, 'TG-ACC-005', 'Tai nghe Bluetooth Sony WH-1000XM5', 'Accessories', 6990000, 8490000, 6290000, 15, 6, 18, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80',
'Sony WH-1000XM5 là mẫu tai nghe chống ồn over-ear cao cấp số 1 thế giới hiện nay, thiết lập chuẩn mực mới về khả năng cách ly tạp âm, chất âm Hi-Res Audio tinh tế cùng thiết kế không khớp nối sang trọng.

🔇 CÔNG NGHỆ CHỐNG ỒN CHỦ ĐỘNG AUTO NC OPTIMIZER HÀNG ĐẦU
• Trang bị bộ đôi vi xử lý chuyên dụng V1 và HD Noise Cancelling QN1 điều khiển hệ thống 8 micro thu âm đa hướng.
• Tự động tối ưu hóa khả năng chống ồn theo môi trường xung quanh, hình dạng khuôn mặt và áp suất khí quyển theo thời gian thực, loại bỏ triệt để tiếng ồn động cơ máy bay, tiếng xe cộ và tiếng trò chuyện ồn ào.

🎵 ÂM THANH HI-RES AUDIO ĐỈNH CAO VỚI CỦ LOA SỢI CARBON 30mm
• Củ loa 30mm vòm sợi carbon tổng hợp siêu nhẹ mang lại âm trầm sâu chắc, âm trung dày dặn và âm cao trong trẻo mở rộng lên tới 40.000 Hz.
• Hỗ trợ codec âm thanh cao cấp LDAC truyền tải dữ liệu gấp 3 lần chuẩn Bluetooth thông thường, tái tạo âm thanh nguyên bản như tại phòng thu.
• Công nghệ DSEE Extreme sử dụng trí tuệ nhân tạo (Edge-AI) nâng cấp các file nhạc nén theo thời gian thực.

🎙️ ĐÀM THOẠI TRONG TRẺO VỚI 4 MICRO AI BEAMFORMING
• Hệ thống 4 micro định hướng chùm sóng kết hợp thuật toán AI khử ồn sâu từ hơn 500 triệu mẫu giọng nói, lọc sạch hoàn toàn tiếng gió và tiếng ồn xung quanh để giọng đàm thoại luôn rõ nét.

🔋 PIN 30 GIỜ BỀN BỈ & SẠC SIÊU TỐC USB-PD
• Thời lượng pin lên đến 30 giờ khi bật chống ồn (lên đến 40 giờ khi tắt chống ồn).
• Sạc nhanh thần tốc qua cổng Type-C: Chỉ 3 phút sạc cho ngay 3 giờ nghe nhạc liên tục.
• Tính năng thông minh Speak-to-Chat tự động dừng nhạc khi bạn cất tiếng nói, và kết nối đa điểm đồng thời 2 thiết bị (Multipoint Connection).')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 6. Apple MacBook Air 13 M2 (8GB/256GB Midnight)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (6, 'TG-LAP-003', 'Apple MacBook Air 13 M2 (8GB/256GB Midnight)', 'Laptop', 22490000, 24990000, 20990000, 15, 5, 25, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800&auto=format&fit=crop&q=80',
'MacBook Air M2 mang ngôn ngữ thiết kế vuông vắn siêu mỏng nhẹ chỉ 11.3mm và nặng 1.24kg, khoác lên mình màu Midnight huyền bí cùng sức mạnh ấn tượng của vi xử lý Apple Silicon M2.

⚡ HIỆU NĂNG VƯỢT TRỘI TỪ CHIP APPLE M2
• CPU 8 nhân thế hệ mới nhanh hơn 18% so với thế hệ M1, xử lý trơn tru mọi tác vụ văn phòng, học tập và đồ họa 2D.
• GPU 8 nhân mang lại năng lực xử lý hình ảnh nhanh hơn 35%, render video và chỉnh sửa ảnh Lightroom cực kỳ mượt mà.
• Thiết kế không quạt tản nhiệt (fanless) hoạt động hoàn toàn tĩnh lặng trong mọi tác vụ làm việc.

🖥️ MÀN HÌNH LIQUID RETINA 13.6 INCH RỰC RỠ
• Màn hình Liquid Retina viền siêu mỏng hiển thị 1 tỷ màu, độ sáng 500 nits cao hơn 25% so với đời trước.
• Công nghệ True Tone tự động cân bằng nhiệt độ màu theo môi trường, bảo vệ mắt khi làm việc kéo dài.

🔋 THỜI LƯỢNG PIN CẢ NGÀY LÊN ĐẾN 18 TIẾNG
• Thoải mái làm việc và giải trí trọn vẹn cả ngày mà không cần cắm sạc.
• Cổng sạc MagSafe 3 từ tính an toàn cùng 2 cổng Thunderbolt / USB 4 đa năng.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 7. Laptop Gaming Acer Nitro 5 Tiger AN515-58
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (7, 'TG-LAP-004', 'Laptop Gaming Acer Nitro 5 Tiger AN515-58', 'Laptop', 19500000, 22500000, 17890000, 12, 4, 18, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80',
'Acer Nitro 5 Tiger AN515-58 là chiến binh gaming quốc dân được ưa chuộng hàng đầu nhờ cấu hình cực mạnh trong phân khúc dưới 20 triệu đồng.

🔥 CẤU HÌNH GAMING TỐI ƯU CHI PHÍ
• CPU: Intel Core i5-12500H (12 nhân 16 luồng, turbo 4.5GHz) đáp ứng hoàn hảo nhu cầu vừa chơi game vừa stream hoặc làm việc kỹ thuật.
• VGA: NVIDIA GeForce RTX 3050 4GB GDDR6 tối ưu hóa với công nghệ DLSS tăng khung hình vượt trội.
• RAM: 16GB DDR4 3200MHz chạy kênh đôi (Dual-Channel) mượt mà không giật lag.
• Màn hình: 15.6 inch IPS FHD (1920x1080) tần số quét 144Hz cho chuyển động bắn súng FPS siêu mượt.
• Tản nhiệt: 2 quạt công suất lớn cùng phần mềm NitroSense kiểm soát nhiệt độ linh hoạt.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 8. Laptop Lenovo ThinkPad E14 Gen 5
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (8, 'TG-LAP-005', 'Laptop Lenovo ThinkPad E14 Gen 5', 'Laptop', 18200000, 20500000, 16900000, 10, 2, 20, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&auto=format&fit=crop&q=80',
'Lenovo ThinkPad E14 Gen 5 là mẫu laptop doanh nhân và lập trình viên chuẩn mực với độ bền chuẩn quân đội Mỹ MIL-STD-810H và bàn phím gõ êm nhất thế giới.

💼 TRỢ THỦ ĐẮC LỰC CHO DÂN CÔNG NGHỆ VÀ DOANH NHÂN
• CPU: Intel Core i5-1335U thế hệ 13 (10 nhân, 12 luồng) tiết kiệm điện năng tối đa và vận hành êm ái.
• RAM: 16GB DDR4 cùng SSD 512GB NVMe mở file code và tài liệu nặng trong chớp mắt.
• Bàn phím ThinkPad trứ danh với hành trình phím 1.5mm ôm cong đầu ngón tay gõ không bị mỏi khi làm việc 8-10 tiếng.
• Núm đỏ TrackPoint đặc trưng điều khiển chuột nhanh chóng mà không cần rời tay khỏi phím.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 9. Laptop Dell XPS 13 Plus 9320 OLED Touch
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (9, 'TG-LAP-006', 'Laptop Dell XPS 13 Plus 9320 OLED Touch', 'Laptop', 39900000, 44500000, 36500000, 5, 1, 8, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80',
'Dell XPS 13 Plus 9320 là kiệt tác công nghệ đến từ tương lai với thiết kế bàn di chuột kính liền mạch tàng hình (Seamless Glass Touchpad) và hàng phím chức năng cảm ứng điện dung.

✨ TUYỆT PHẨM ULTRABOOK CAO CẤP
• CPU: Intel Core i7-1360P (12 nhân 16 luồng, 5.0GHz Turbo).
• Màn hình cảm ứng 13.4 inch 3.5K OLED (3456 x 2160) màu sắc DisplayHDR 500 nits tuyệt mỹ.
• RAM 16GB LPDDR5 tốc độ cao 6000MHz và SSD 512GB PCIe 4.0.
• Khung nhôm CNC nguyên khối gia công tinh xảo, trọng lượng nhẹ chỉ 1.26kg.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 10. iPhone 16 Pro Max 256GB Titan Sa Mạc VN/A
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (10, 'TG-PHN-001', 'iPhone 16 Pro Max 256GB Titan Sa Mạc VN/A', 'Mobile', 34990000, 36990000, 33490000, 20, 9, 35, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&auto=format&fit=crop&q=80',
'iPhone 16 Pro Max là đỉnh cao smartphone công nghệ của Apple với màn hình 6.9 inch lớn nhất từ trước đến nay cùng phím điều khiển Camera Control đột phá.

🌟 NHỮNG NÂNG CẤP ĐÁNG GIÁ NHẤT:
• Khung viền Titan cấp 5 mạ màu Desert Titanium sang trọng, viền màn hình siêu mỏng nhất thế giới smartphone.
• Chip Apple A18 Pro trên tiến trình 3nm thế hệ thứ 2 mang lại hiệu năng đồ họa Ray Tracing đỉnh cao.
• Cụm 3 camera 48MP Fusion với cảm biến quad-pixel nhanh hơn gấp 2 lần, quay video 4K 120fps Dolby Vision chuẩn Hollywood.
• Nút Camera Control cảm ứng lực giúp điều chỉnh khẩu độ, zoom và chụp ảnh nhanh chóng như máy ảnh chuyên nghiệp.
• Thời lượng pin lên tới 33 giờ xem video liên tục, hỗ trợ sạc nhanh không dây MagSafe 25W.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 11. Samsung Galaxy S24 Ultra 5G (12GB/256GB)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (11, 'TG-PHN-002', 'Samsung Galaxy S24 Ultra 5G (12GB/256GB)', 'Mobile', 29990000, 33990000, 26990000, 15, 6, 22, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80',
'Samsung Galaxy S24 Ultra 5G mở ra kỷ nguyên quyền năng Galaxy AI trên thiết bị di động kết hợp bút S-Pen thông minh và camera 200MP zoom mắt thần bóng đêm.

🤖 KỶ NGUYÊN GALAXY AI THÔNG MINH
• Tính năng Khoanh tròn để tìm kiếm trên Google (Circle to Search) tiện lợi ngay lập tức.
• Phiên dịch cuộc gọi trực tiếp hai chiều (Live Translate) hỗ trợ tiếng Việt mượt mà.
• Trợ lý chỉnh sửa ảnh AI tạo sinh xóa vật thể thừa và điền khuyết phông nền thông minh.
• Màn hình phẳng Dynamic AMOLED 2X 6.8 inch 120Hz phủ kính chống phản chiếu Corning Gorilla Armor.
• Vi xử lý Snapdragon 8 Gen 3 for Galaxy cân mượt mà mọi tác vụ đồ họa và chiến game liên tục.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 12. iPhone 15 128GB Xanh Pastel Chính Hãng VN/A
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (12, 'TG-PHN-003', 'iPhone 15 128GB Xanh Pastel Chính Hãng VN/A', 'Mobile', 19490000, 21990000, 17990000, 25, 11, 40, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80',
'iPhone 15 sở hữu thiết kế mặt lưng kính pha màu mờ mịn cao cấp, nâng cấp cụm Dynamic Island thông minh và chuyển đổi hoàn toàn sang cổng sạc USB-C tiện dụng.

✨ TRẢI NGHIỆM FLAGSHIP TIÊU CHUẨN
• Dynamic Island tương tác linh hoạt thông báo chuyến đi Grab, tỷ số bóng đá và trình phát nhạc.
• Camera chính 48MP thế hệ mới với zoom quang học 2x cho ảnh chụp độ phân giải siêu cao sắc nét.
• Tính năng tự động nhận diện chế độ Chân Dung Portrait Mode không cần chuyển đổi thủ công.
• Chip A16 Bionic vận hành êm mượt và tối ưu hóa thời lượng pin dùng suốt cả ngày dài.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 13. Xiaomi 14 Ultra 5G (16GB/512GB) Leica Camera
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (13, 'TG-PHN-004', 'Xiaomi 14 Ultra 5G (16GB/512GB) Leica Camera', 'Mobile', 26990000, 29990000, 24490000, 10, 3, 14, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&auto=format&fit=crop&q=80',
'Xiaomi 14 Ultra được coi là "máy ảnh chuyên nghiệp có thể gọi điện thoại" nhờ sự kết hợp chặt chẽ cùng huyền thoại nhiếp ảnh Leica.

📸 ĐỈNH CAO NHIẾP ẢNH DI ĐỘNG LEICA
• Cảm biến 1-inch Sony LYT-900 thế hệ mới nhất với dải tương phản động 14EV.
• Ống kính quang học Leica Summilux với khẩu độ biến thiên vô cấp từ f/1.63 đến f/4.0.
• Hệ thống 4 camera tele kép (3.2x và 5x) zoom xa siêu rõ nét.
• Màn hình cong C8 AMOLED 6.73 inch độ phân giải WQHD+ tần số 120Hz độ sáng đỉnh 3000 nits.
• Pin 5000mAh hỗ trợ sạc nhanh có dây HyperCharge 90W và sạc nhanh không dây 80W.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 14. OPPO Reno11 Pro 5G (12GB/512GB) Pearl White
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (14, 'TG-PHN-005', 'OPPO Reno11 Pro 5G (12GB/512GB) Pearl White', 'Mobile', 12990000, 14990000, 11490000, 12, 4, 16, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&auto=format&fit=crop&q=80',
'OPPO Reno11 Pro 5G sở hữu thiết kế mặt lưng ngọc trai óng ánh sang trọng, chuyên gia chân dung với camera tele 32MP cảm biến Sony IMX709.

💎 CHUYÊN GIA CHÂN DUNG & SẠC SIÊU NHANH
• Camera tele chân dung 32MP zoom 2x với tiêu cự tương đương 47mm chuẩn mực studio.
• Màn hình cong 3D OLED 6.7 inch hiển thị 1.07 tỷ màu, tần số quét 120Hz mượt mà.
• Công nghệ sạc siêu tốc SUPERVOOC 80W sạc đầy 100% pin chỉ trong 28 phút.
• Vi xử lý MediaTek Dimensity 8200 4nm mạnh mẽ, đa nhiệm mượt mà với 12GB RAM.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 15. Bàn phím cơ Custom FL-Esports CMK75 Gasket Wireless
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (15, 'TG-ACC-002', 'Bàn phím cơ FL-Esports CMK75 Gasket Wireless', 'Accessories', 2450000, 2890000, 2190000, 15, 7, 25, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=800&auto=format&fit=crop&q=80',
'FL-Esports CMK75 là bàn phím cơ Custom cao cấp trang bị màn hình TFT hiển thị GIF động cá tính cùng cấu trúc đệm Gasket Mount 5 lớp êm ái hàng đầu.

⌨️ CẤU HÌNH CUSTOM ĐỈNH CAO:
• Cấu trúc Gasket Mount với đệm silicon Poron kép triệt tiêu hoàn toàn tạp âm rỗng.
• Switch TTC Flame Red Linear được bôi trơn tại nhà máy cho hành trình nhấn mượt như lướt trên bơ.
• Màn hình màu TFT hiển thị thời gian, mức pin, hiệu ứng GIF động tùy biến.
• Hỗ trợ 3 chế độ kết nối: Type-C, Bluetooth 5.0 và Wireless 2.4GHz không độ trễ.
• Núm xoay đa phương tiện bằng kim loại cao cấp điều chỉnh âm lượng tức thì.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 16. Chuột công thái học không dây Logitech MX Master 3S
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (16, 'TG-ACC-004', 'Chuột công thái học không dây Logitech MX Master 3S', 'Accessories', 2490000, 2890000, 2190000, 20, 8, 35, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&auto=format&fit=crop&q=80',
'Logitech MX Master 3S là chuẩn mực chuột văn phòng và thiết kế đồ họa cao cấp nhất, đem lại sự thoải mái tối đa cho bàn tay suốt ngày dài làm việc.

🖱️ TỐI ƯU HÓA HIỆU SUẤT LÀM VIỆC CHUYÊN NGHIỆP:
• Cảm biến quang học Darkfield 8.000 DPI có thể di chuyển mượt mà trên mọi bề mặt, kể cả mặt kính trong suốt.
• Công nghệ Quiet Clicks giảm 90% tiếng ồn click chuột, giữ không gian làm việc luôn yên tĩnh.
• Con lăn điện từ MagSpeed cuộn 1.000 dòng trong 1 giây với độ chính xác đến từng pixel.
• Tính năng Logitech Flow di chuyển con trỏ và copy paste dữ liệu giữa 3 máy tính (Windows & macOS) cực kỳ tiện lợi.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 17. Tai nghe Apple AirPods Pro 2 MagSafe USB-C
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (17, 'TG-ACC-006', 'Tai nghe Apple AirPods Pro 2 MagSafe USB-C', 'Accessories', 5990000, 6790000, 5290000, 25, 13, 45, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=800&auto=format&fit=crop&q=80',
'AirPods Pro 2 trang bị chip Apple H2 đem lại khả năng chống ồn chủ động mạnh gấp 2 lần, âm thanh không gian cá nhân hóa và hộp sạc chuẩn USB-C có loa tìm kiếm.

🎧 TRẢI NGHIỆM ÂM THANH ĐỈNH CAO:
• Chip Apple H2 với các thuật toán âm học thông minh khử ồn chủ động vượt bậc.
• Chế độ Âm thanh thích ứng (Adaptive Audio) tự động pha trộn chống ồn và xuyên âm theo môi trường.
• Nhận biết cuộc trò chuyện (Conversation Awareness) tự động giảm âm lượng khi bạn bắt đầu cất lời nói.
• Hộp sạc tích hợp chip U1 tìm kiếm chính xác Precision Finding và chống bụi nước chuẩn IP54.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 18. Loa Bluetooth Marshall Emberton II Chính Hãng
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (18, 'TG-ACC-007', 'Loa Bluetooth Marshall Emberton II Chính Hãng', 'Accessories', 3990000, 4590000, 3490000, 12, 5, 20, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800&auto=format&fit=crop&q=80',
'Marshall Emberton II là biểu tượng âm nhạc cổ điển đậm chất Rock & Roll với kích thước bỏ túi nhỏ gọn cùng âm trường rộng mở 360 độ True Stereophonic.

🎸 ÂM HƯỞNG ROCK N ROLL BẤT TẬN:
• Thiết kế lưới tản nhiệt kim loại cùng logo Marshall mạ vàng đồng cổ điển quý phái.
• Thời lượng pin đột phá hơn 30 giờ chơi nhạc chỉ sau 1 lần sạc đầy.
• Khả năng kháng bụi và nước chuẩn IP67 thách thức mọi thời tiết dã ngoại, tiệc hồ bơi.
• Chế độ Stack Mode cho phép ghép đôi nhiều loa Emberton II cùng lúc để khuếch đại không gian âm nhạc sôi động.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 19. Màn hình Gaming ASUS ROG Swift PG27AQDM OLED 240Hz
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (19, 'TG-ACC-008', 'Màn hình Gaming ASUS ROG Swift PG27AQDM OLED 240Hz', 'Accessories', 21990000, 24990000, 19990000, 6, 2, 10, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80',
'ROG Swift OLED PG27AQDM là màn hình gaming thể thao điện tử hoàn hảo nhất thế giới với tấm nền OLED 2K 240Hz thời gian đáp ứng siêu tốc 0.03ms.

🖥️ THIẾT LẬP MÀN HÌNH ĐỈNH CAO CHO GAME THỦ:
• Kích thước: 27 inch tấm nền OLED độ phân giải 2K QHD (2560 x 1440) độ tương phản 1.500.000:1.
• Tần số quét 240Hz cùng tốc độ phản hồi 0.03ms loại bỏ bóng mờ tuyệt đối trong game bắn súng.
• Tản nhiệt tùy chỉnh độc quyền với buồng hơi thông minh giúp tấm nền OLED mát hơn 5% và chống lưu ảnh (burn-in).
• Độ phủ màu 99% DCI-P3 chuẩn điện ảnh, độ sáng đỉnh 1000 nits hỗ trợ HDR10 rực rỡ.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- 20. Củ sạc nhanh Anker 735 GaNPrime 65W 3 Cổng
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (20, 'TG-ACC-009', 'Củ sạc nhanh Anker 735 GaNPrime 65W 3 Cổng', 'Accessories', 1150000, 1390000, 890000, 40, 22, 60, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80',
'Củ sạc Anker 735 Charger (GaNPrime 65W) với công nghệ sạc GaN thế hệ mới nhỏ gọn hơn 53% so với củ sạc MacBook thông thường nhưng có thể sạc cùng lúc 3 thiết bị.

⚡ CÔNG NGHỆ NĂNG LƯỢNG GANPRIME ĐỘT PHÁ:
• 3 Cổng sạc thông minh: 2 cổng USB-C và 1 cổng USB-A cho phép sạc cùng lúc Laptop, Điện thoại và Tai nghe.
• Công suất tối đa 65W khi cắm đơn cổng, nạp đầy MacBook Pro 13 inch hoặc iPhone trong thời gian ngắn nhất.
• Công nghệ phân bổ dòng điện thông minh PowerIQ 4.0 tự động nhận biết nhu cầu năng lượng của từng thiết bị.
• Hệ thống bảo vệ ActiveShield 2.0 kiểm soát nhiệt độ hơn 3 triệu lần mỗi ngày chống cháy nổ và bảo vệ tuổi thọ pin.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    product_category = EXCLUDED.product_category,
    price = EXCLUDED.price,
    mrp_price = EXCLUDED.mrp_price,
    discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit,
    discount_sold = EXCLUDED.discount_sold,
    stock = EXCLUDED.stock,
    active = EXCLUDED.active,
    image = EXCLUDED.image,
    description = EXCLUDED.description;

-- Cập nhật sequence cho PostgreSQL để tránh lỗi conflict id khi admin thêm sản phẩm mới
SELECT setval('tblproduct_id_seq', (SELECT MAX(id) FROM tblproduct));
