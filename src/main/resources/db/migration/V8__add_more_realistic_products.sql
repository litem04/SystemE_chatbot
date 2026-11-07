-- Flyway Migration V8: Mở rộng danh mục sản phẩm công nghệ thực tế (Điện thoại, Laptop, Phụ kiện & Gaming Gear)
-- Nâng cấp trải nghiệm mua sắm chuẩn sàn thương mại điện tử chuyên nghiệp TechGear Store

-- 21. iPhone 16 128GB Chính Hãng VN/A
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (21, 'TG-PHN-006', 'iPhone 16 128GB Chính Hãng VN/A', 'Mobile', 21990000, 23990000, 20490000, 25, 8, 30, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=800&auto=format&fit=crop&q=80',
'iPhone 16 sở hữu thiết kế cụm camera dọc tinh tế hỗ trợ quay video không gian Spatial Video, nút Action Button và nút Camera Control cảm biến lực hoàn toàn mới.

🔥 HIỆU NĂNG MẠNH MẼ VỚI CHIP APPLE A18
• Vi xử lý Apple A18 sản xuất trên tiến trình 3nm thế hệ thứ hai cho tốc độ xử lý CPU nhanh hơn 30% và tiết kiệm năng lượng 30% so với thế hệ trước.
• Hỗ trợ toàn diện công nghệ Apple Intelligence xử lý ngôn ngữ và hình ảnh thông minh trên thiết bị.
• Bộ nhớ trong 128GB tốc độ cao, RAM 8GB đa nhiệm mượt mà không độ trễ.

📸 CỤM CAMERA 48MP FUSION ĐA NĂNG
• Camera chính 48MP Fusion khẩu độ f/1.6 với chống rung quang học cảm biến (Sensor-shift OIS), tích hợp tùy chọn zoom quang 2x chất lượng quang học.
• Camera Ultra Wide 12MP tự động lấy nét hỗ trợ chụp ảnh siêu cận cảnh macro sắc nét đến từng chi tiết.
• Nút Camera Control cho phép trượt ngón tay để zoom, chỉnh khẩu độ và bấm chụp tức thì.

📱 MÀN HÌNH SUPER RETINA XDR & ĐỘ BỀN KÍNH CERAMIC SHIELD THẾ HỆ 2
• Kích thước 6.1 inch độ sáng tối đa ngoài trời lên tới 2000 nits, hiển thị rõ nét ngay dưới ánh nắng gắt.
• Kính Ceramic Shield bền bỉ gấp đôi so với bất kỳ loại kính smartphone nào trên thị trường.
• Thời lượng pin lên đến 22 giờ phát video, cổng sạc USB-C đồng bộ tiện lợi.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 22. iPhone 16 Pro 128GB Titan Tự Nhiên VN/A
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (22, 'TG-PHN-007', 'iPhone 16 Pro 128GB Titan Tự Nhiên VN/A', 'Mobile', 27990000, 29990000, 26490000, 18, 5, 22, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=800&auto=format&fit=crop&q=80',
'iPhone 16 Pro sở hữu bộ khung vỏ Titan Cấp 5 siêu bền nhẹ, màn hình 6.3 inch viền mỏng kỷ lục thế giới và vi xử lý Apple A18 Pro tối tân.

⚡ CHIP APPLE A18 PRO VƯỢT TRỘI
• CPU 6 nhân mạnh nhất giới smartphone, GPU 6 nhân hỗ trợ Ray Tracing tăng tốc phần cứng nhanh hơn 20%.
• Bộ xử lý mạng thần kinh Neural Engine 16 nhân xử lý AI cục bộ với băng thông bộ nhớ tăng 17%.

🎥 CAMERA QUAY 4K 120FPS DOLBY VISION CHUẨN ĐIỆN ẢNH
• Camera Fusion 48MP thế hệ thứ hai với cảm biến quad-pixel siêu nhanh, không độ trễ màn trập ở định dạng RAW.
• Ống kính tiềm vọng Tetraprism zoom quang 5x quang học sắc nét ở tiêu cự 120mm.
• 4 micro chuẩn studio ghi âm không gian Spatial Audio với tính năng Audio Mix lọc tạp âm đỉnh cao.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 23. Samsung Galaxy Z Fold6 5G (12GB/256GB)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (23, 'TG-PHN-008', 'Samsung Galaxy Z Fold6 5G (12GB/256GB)', 'Mobile', 41990000, 43990000, 38990000, 10, 2, 14, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&auto=format&fit=crop&q=80',
'Tuyệt tác điện thoại gập flagship mỏng nhẹ nhất dòng Z Fold với thiết kế vuông vức nam tính và bộ công cụ quyền năng Galaxy AI.

📱 MÀN HÌNH KÉP CỰC ĐẠI DYNAMIC AMOLED 2X 120Hz
• Màn hình chính 7.6 inch QXGA+ sắc nét, độ sáng đỉnh cao 2.600 nits hiển thị rõ ràng ngay cả dưới trời nắng gắt.
• Màn hình phụ 6.3 inch tỷ lệ rộng hơn, thao tác bấm bàn phím gập lại như điện thoại thanh thông thường.
• Bản lề rãnh kép Flex Hinge cải tiến chống va đập và nếp gấp mờ hơn đáng kể.

🤖 QUYỀN NĂNG GALAXY AI & BÚT S-PEN TIỆN ÍCH
• Tính năng Phác họa thông minh Sketch to Image biến nét vẽ tay thành tác phẩm nghệ thuật 3D.
• Trợ lý phiên dịch phiên bản màn hình kép hiển thị bản dịch đồng thời cho hai người đối thoại.
• Chip Snapdragon 8 Gen 3 for Galaxy kết hợp buồng tản nhiệt hơi lớn hơn 1.6 lần.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 24. Samsung Galaxy Z Flip6 5G (12GB/256GB)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (24, 'TG-PHN-009', 'Samsung Galaxy Z Flip6 5G (12GB/256GB)', 'Mobile', 26990000, 28990000, 23990000, 15, 4, 20, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1567581935884-3349723552ca?w=800&auto=format&fit=crop&q=80',
'Biểu tượng thời trang công nghệ dạng hộp phấn gập gọn bỏ túi, nâng cấp camera 50MP sắc nét và tản nhiệt buồng hơi Vapor Chamber lần đầu tiên xuất hiện trên dòng Flip.

✨ THIẾT KẾ GẬP MỞ FLEXMODE SÁNG TẠO
• Màn hình phụ FlexWindow 3.4 inch đa năng tương tác không cần mở máy, tùy biến widget và hình nền AI tương tác 3D.
• Camera chính nâng cấp lên 50MP kết hợp công cụ ProVisual Engine chụp chân dung đêm rạng rỡ.
• Pin nâng cấp lên 4.000mAh bền bỉ cả ngày, khung viền nhôm Armor Aluminum gia cường chống xước.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 25. Samsung Galaxy A55 5G (8GB/128GB)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (25, 'TG-PHN-010', 'Samsung Galaxy A55 5G (8GB/128GB) Awesome Iceblue', 'Mobile', 9490000, 10490000, 8690000, 35, 14, 50, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1546054454-aa26e2b734c7?w=800&auto=format&fit=crop&q=80',
'Smartphone tầm trung quốc dân với khung viền kim loại cao cấp, mặt lưng kính cường lực Gorilla Glass Victus+ và chuẩn kháng nước kháng bụi IP67.

💎 THIẾT KẾ VIỀN KIM LOẠI SANG TRỌNG
• Màn hình Super AMOLED 6.6 inch 120Hz mượt mà, công nghệ Vision Booster hiển thị sống động.
• Vi xử lý Exynos 1480 4nm với GPU AMD Xclipse 530 tối ưu hóa trải nghiệm chiến game mượt mà.
• Bộ 3 camera 50MP OIS chống rung quang học, camera selfie 32MP làm đẹp tự nhiên.
• Pin 5.000mAh hỗ trợ sạc nhanh 25W, hỗ trợ cập nhật 4 năm hệ điều hành Android.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 26. Xiaomi 13T Pro 5G (12GB/512GB) Leica Camera
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (26, 'TG-PHN-011', 'Xiaomi 13T Pro 5G (12GB/512GB) Leica Camera', 'Mobile', 14990000, 16990000, 13490000, 20, 6, 25, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=800&auto=format&fit=crop&q=80',
'Cỗ máy nhiếp ảnh hợp tác cùng Leica với ống kính chuyên nghiệp Summicron và sạc siêu tốc HyperCharge 120W (nạp đầy pin trong 19 phút).

📸 HỆ THỐNG ỐNG KÍNH LEICA ĐỈNH CAO
• Ống kính Leica Summicron tiêu cự chuyên nghiệp với 2 phong cách màu sắc độc quyền: Leica Chân thực và Leica Rực rỡ.
• Camera chính 50MP Sony IMX707 kích thước cảm biến lớn 1/1.28 inch thu sáng vượt trội.
• Màn hình CrystalRes AMOLED 144Hz tần số PWM 2880Hz bảo vệ mắt tối đa.
• Chip Dimensity 9200+ tiến trình 4nm hiệu năng chấm điểm AnTuTu trên 1.5 triệu điểm.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 27. OPPO Reno12 5G (12GB/256GB) AI Phone
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (27, 'TG-PHN-012', 'OPPO Reno12 5G (12GB/256GB) AI Phone Bạc Vũ Trụ', 'Mobile', 11990000, 12990000, 10790000, 25, 7, 30, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&auto=format&fit=crop&q=80',
'Chuyên gia chụp ảnh chân dung AI thế hệ mới với thiết kế mặt lưng dòng chảy bạc tương lai và khung bảo vệ cường lực chống va đập 360 độ.

🎨 CÔNG NGHỆ CHỤP ẢNH CHÂN DUNG OPPO AI
• Tính năng AI Eraser 2.0 xóa vật thể và người qua đường thông minh chỉ bằng một lần khoanh tròn.
• AI Clear Face tối ưu độ sắc nét khuôn mặt khi chụp nhóm đông người.
• Màn hình cong 3D vô cực 6.7 inch AMOLED 120Hz hỗ trợ công nghệ cảm ứng chống nước Splash Touch.
• Sạc nhanh SUPERVOOC 80W sạc đầy 100% chỉ trong 46 phút.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 28. Laptop Gaming Lenovo Legion Pro 5 16IRX9 (2024)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (28, 'TG-LAP-007', 'Laptop Gaming Lenovo Legion Pro 5 16IRX9 (2024)', 'Laptop', 37990000, 42000000, 35490000, 12, 3, 16, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?w=800&auto=format&fit=crop&q=80',
'Quái thú gaming eSports hàng đầu với chip Intel Core i7-14700HX thế hệ 14 mới nhất và card đồ họa rời NVIDIA GeForce RTX 4060 8GB TGP 140W cực đại.

🔥 CẤU HÌNH GAMING ĐỈNH CAO THẾ HỆ 14
• CPU: Intel Core i7-14700HX (20 nhân, 28 luồng, xung nhịp Turbo tối đa lên tới 5.5GHz) cân mượt mà cả livestream và render 3D nặng.
• Card đồ họa: NVIDIA GeForce RTX 4060 8GB GDDR6 (Full TGP 140W, MUX Switch + Advanced Optimus).
• RAM: 16GB DDR5 5600MHz (2 khe cắm nâng cấp tối đa 64GB).
• Ổ cứng: 1TB PCIe Gen 4 SSD tốc độ siêu nhanh.

🖥️ MÀN HÌNH PUREIGHT GAMING 240Hz 2.5K SIÊU NÉT
• Kích thước: 16.0 inch tỷ lệ 16:10, độ phân giải WQXGA (2560 x 1600), tần số quét 240Hz, phản hồi 3ms.
• 100% sRGB, độ sáng 500 nits, chứng nhận VESA DisplayHDR 400 và Dolby Vision.
• Tản nhiệt Legion Coldfront 5.0 với quạt cánh siêu mỏng và ống dẫn nhiệt kép làm mát tức thì.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 29. Laptop Gaming Dell Alienware m16 R2
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (29, 'TG-LAP-008', 'Laptop Gaming Dell Alienware m16 R2 QHD+ 240Hz', 'Laptop', 48990000, 54000000, 45990000, 8, 2, 10, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80',
'Chiến hạm ngoài hành tinh với ngôn ngữ thiết kế Legend 3.0 mỏng nhẹ hơn 15%, trang bị vi xử lý Intel Core Ultra 7 155H tích hợp NPU AI và card đồ họa RTX 4070.

🛸 THIẾT KẾ NGOÀI HÀNH TINH & HIỆU NĂNG VÔ ĐỊCH
• CPU: Intel Core Ultra 7 155H (16 nhân, 22 luồng, xung nhịp lên tới 4.8GHz, tích hợp Intel AI Boost NPU).
• VGA: NVIDIA GeForce RTX 4070 8GB GDDR6 công suất đồ họa tối đa 140W.
• RAM: 16GB DDR5 5600MHz kênh đôi, Ổ cứng: 1TB NVMe M.2 PCIe Gen 4.
• Màn hình: 16.0 inch QHD+ (2560x1600) 240Hz 3ms, 100% sRGB, công nghệ ComfortView Plus giảm ánh sáng xanh.
• Bàn phím cơ AlienFX per-key RGB với hành trình phím 1.8mm đầm tay.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 30. Laptop ASUS Zenbook 14 OLED UX3405MA
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (30, 'TG-LAP-009', 'Laptop ASUS Zenbook 14 OLED UX3405MA Intel Core Ultra', 'Laptop', 26990000, 29500000, 24890000, 15, 5, 20, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&auto=format&fit=crop&q=80',
'Kiệt tác Ultrabook doanh nhân mỏng nhẹ chỉ 1.2kg, độ mỏng 14.9mm cùng màn hình 3K ASUS Lumina OLED 120Hz chuẩn rạp chiếu phim.

✨ MỎNG NHẸ TINH TẾ & MÀN HÌNH 3K OLED 120Hz
• CPU: Intel Core Ultra 5 125H (14 nhân, 18 luồng) trang bị NPU hỗ trợ tăng tốc AI cục bộ mượt mà.
• RAM: 16GB LPDDR5X onboard 7467MHz băng thông cực lớn, SSD 512GB PCIe 4.0.
• Màn hình: 14.0 inch 3K (2880 x 1800) OLED 16:10 120Hz, 100% DCI-P3, độ sáng đỉnh 600 nits DisplayHDR True Black 600.
• Pin cực khủng 75Wh cho thời lượng làm việc văn phòng bền bỉ tới 15 giờ liên tục.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 31. Apple MacBook Air 15 inch M3 (16GB/512GB)
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (31, 'TG-LAP-010', 'Apple MacBook Air 15 inch M3 (16GB/512GB Midnight)', 'Laptop', 36990000, 39990000, 34990000, 14, 4, 18, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&auto=format&fit=crop&q=80',
'MacBook Air 15 inch M3 mang đến không gian làm việc rộng rãi trên tấm nền Liquid Retina lớn cùng sự êm ái hoàn hảo của thiết kế không quạt tản nhiệt.

💻 SỨC MẠNH VƯỢT BẬC TỪ CHIP APPLE M3 3nm
• Chip Apple M3 8-core CPU và 10-core GPU hỗ trợ tính năng dò tia Ray Tracing bằng phần cứng.
• RAM 16GB Unified Memory, SSD 512GB tốc độ cao cho phép mở nhiều file thiết kế nặng và đa nhiệm thoải mái.
• Màn hình Liquid Retina 15.3 inch 2880x1864 hiển thị 1 tỷ màu rực rỡ, độ sáng 500 nits.
• Thời lượng pin lên đến 18 giờ, dàn âm thanh 6 loa với Spatial Audio trầm ấm.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 32. Laptop Lenovo ThinkPad X1 Carbon Gen 11
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (32, 'TG-LAP-011', 'Laptop Lenovo ThinkPad X1 Carbon Gen 11 Carbon Fiber', 'Laptop', 39990000, 44500000, 36500000, 10, 2, 12, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800&auto=format&fit=crop&q=80',
'Huyền thoại laptop doanh nhân tối thượng với khung sợi carbon chuẩn quân đội Mỹ MIL-STD 810H, bàn phím gõ êm số 1 thế giới và trọng lượng siêu nhẹ chỉ 1.12kg.

💼 ĐỈNH CAO DOANH NHÂN & LẬP TRÌNH VIÊN CHUYÊN NGHIỆP
• CPU: Intel Core i7-1355U thế hệ 13 vPro bảo mật phần cứng cấp doanh nghiệp.
• RAM: 16GB LPDDR5 6400MHz, Ổ cứng: 512GB SSD PCIe NVMe Gen 4.
• Màn hình: 14.0 inch 2.8K OLED (2880 x 1800) DisplayHDR 500 True Black, chống chói chống lóa.
• Bàn phím ThinkPad trứ danh có đèn nền và núm đỏ TrackPoint điều khiển chuột chuẩn xác từng milimet.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 33. Laptop Gaming Acer Predator Helios Neo 16
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (33, 'TG-LAP-012', 'Laptop Gaming Acer Predator Helios Neo 16 PHN16-71', 'Laptop', 31990000, 35000000, 29490000, 16, 5, 20, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80',
'Vũ khí tối thượng của chiến binh Predator với hệ thống tản nhiệt quạt kim loại AeroBlade 3D thế hệ 5 và keo tản nhiệt kim loại lỏng Liquid Metal.

⚡ CHIẾN GAME KHÔNG THỎA HIỆP
• CPU: Intel Core i7-13700HX (16 nhân, 24 luồng, xung nhịp tối đa 5.0GHz).
• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 (140W MUX Switch).
• Màn hình: 16 inch WQXGA (2560x1600) IPS 165Hz 100% sRGB, độ sáng 500 nits G-Sync.
• RAM: 16GB DDR5 4800MHz (2 khe), SSD 512GB PCIe NVMe SED.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 34. Laptop Dell Inspiron 14 5440 Core i5-1334U
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (34, 'TG-LAP-013', 'Laptop Dell Inspiron 14 5440 Core i5-1334U Mỏng Nhẹ', 'Laptop', 16490000, 18500000, 15190000, 20, 6, 25, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80',
'Laptop học tập và làm việc văn phòng thanh lịch, vỏ nhôm nguyên khối màu Ice Blue mát mẻ cùng bản lề ErgoLift nâng máy tự nhiên.

📚 HOÀN HẢO CHO HỌC TẬP VÀ VĂN PHÒNG
• CPU: Intel Core i5-1334U (10 nhân, 12 luồng, xung nhịp lên tới 4.6GHz).
• RAM: 16GB DDR5 5200MHz đa nhiệm mượt mà, SSD 512GB NVMe PCIe.
• Màn hình: 14.0 inch FHD+ (1920x1200) tỷ lệ 16:10 góc nhìn rộng WVA chống chói.
• Trọng lượng nhẹ 1.5kg, cảm biến vân tay tích hợp nút nguồn một chạm.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 35. Bàn phím cơ không dây Keychron K2 Pro QMK/VIA
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (35, 'TG-ACC-010', 'Bàn phím cơ không dây Keychron K2 Pro QMK/VIA Hot-swap', 'Accessories', 2190000, 2590000, 1890000, 35, 12, 45, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80',
'Bàn phím cơ không dây layout 75% tùy biến vô hạn qua phần mềm QMK/VIA, hỗ trợ thay nóng switch Hot-swap và tương thích hoàn hảo cả macOS lẫn Windows.

⌨️ TRẢI NGHIỆM GÕ PHÍM CAO CẤP
• Switch Gateron Jupiter Brown bôi trơn sẵn từ nhà máy, lực nhấn vừa phải êm ái.
• Mạch xuôi South-Facing RGB không bị cấn keycap profile Cherry.
• Foam tiêu âm PCB và silicone đệm đáy hấp thụ âm thanh rỗng cho tiếng gõ đầm chắc.
• Kết nối Bluetooth 5.1 cùng lúc 3 thiết bị và cáp rời Type-C, pin khủng 4000mAh dùng 300 giờ.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 36. Chuột gaming không dây Razer DeathAdder V3 Pro
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (36, 'TG-ACC-011', 'Chuột gaming không dây Razer DeathAdder V3 Pro 30.000 DPI', 'Accessories', 3290000, 3790000, 2890000, 25, 9, 30, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80',
'Vũ khí của các nhà vô địch FPS thế giới với thiết kế công thái học hoàn hảo và trọng lượng siêu nhẹ chỉ 63 gram.

🎯 CẢM BIẾN QUANG FOCUS PRO 30K DPI HÀNG ĐẦU THẾ GIỚI
• Cảm biến quang học Focus Pro 30K đạt độ chính xác phân giải 99.8%, di chuột trơn tru trên cả mặt kính.
• Switch quang học Razer Gen-3 độ bền 90 triệu lần nhấn, tốc độ nhận lệnh 0.2ms không độ trễ.
• Kết nối không dây Razer HyperSpeed Wireless ổn định không độ trễ.
• Pin dùng lên đến 90 giờ liên tục, cổng sạc Type-C tiện lợi.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 37. Màn hình chuyên đồ họa Dell UltraSharp U2724D 2K 120Hz
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (37, 'TG-ACC-012', 'Màn hình Đồ họa Dell UltraSharp U2724D 2K 120Hz IPS Black', 'Accessories', 10990000, 12500000, 9890000, 15, 4, 18, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80',
'Màn hình chuẩn đồ họa hàng đầu của Dell với tấm nền IPS Black tỷ lệ tương phản 2.000:1, tần số quét 120Hz siêu mượt và cảm biến ánh sáng xung quanh thông minh.

🎨 CHUẨN MỰC MÀU SẮC CHO DESIGNER & CODER
• Kích thước: 27 inch độ phân giải 2K QHD (2560 x 1440), tần số quét 120Hz.
• Tấm nền IPS Black mang lại sắc đen sâu hơn 35% so với IPS thông thường.
• Độ phủ màu 100% sRGB, 98% Display P3, chứng nhận hiển thị thoải mái mắt 4 sao TUV.
• Cổng kết nối phong phú: DisplayPort 1.4, HDMI 2.1, USB-C 15W cấp nguồn phụ kiện.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 38. Màn hình Gaming cong Samsung Odyssey G7 32 inch 240Hz
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (38, 'TG-ACC-013', 'Màn hình Gaming Cong Samsung Odyssey G7 32 inch 240Hz 1ms', 'Accessories', 12990000, 15000000, 11490000, 12, 3, 15, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80',
'Trải nghiệm đắm chìm vào thế giới game với độ cong 1000R lý tưởng hoàn hảo theo tầm mắt người, công nghệ chấm lượng tử QLED và tần số quét 240Hz.

🌌 ĐỘ CONG 1000R ĐẲNG CẤP VÀ TỐC ĐỘ 240Hz
• Kích thước: 32 inch độ phân giải WQHD (2560 x 1440) độ cong 1000R ôm trọn tầm nhìn.
• Tần số quét 240Hz, thời gian phản hồi siêu tốc 1ms (GtG) loại bỏ hiện tượng giật bóng mờ.
• Công nghệ QLED tái hiện dải màu DCI-P3 95% chân thực, chuẩn HDR600 tái tạo vùng sáng tối ấn tượng.
• Tương thích G-Sync và AMD FreeSync Premium Pro.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 39. Tai nghe Gaming không dây HyperX Cloud III Wireless
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (39, 'TG-ACC-014', 'Tai nghe Gaming HyperX Cloud III Wireless DTS Spatial Audio', 'Accessories', 3590000, 4190000, 3190000, 20, 6, 25, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&auto=format&fit=crop&q=80',
'Huyền thoại tai nghe gaming tái xuất với thời lượng pin không tưởng lên tới 120 giờ chỉ sau một lần sạc và công nghệ âm thanh vòm DTS Headphone:X Spatial Audio.

🎧 ĐỈNH CAO ÊM ÁI VÀ THỜI LƯỢNG PIN 120 GIỜ
• Đệm mút hoạt tính bọc giả da cao cấp êm ái suốt cả ngày dài thi đấu.
• Củ loa nghiêng 53mm được các kỹ sư âm thanh tinh chỉnh riêng cho game bắn súng FPS.
• Micro 10mm khử tiếng ồn với đèn LED báo tắt tiếng tích hợp.
• Kết nối không dây 2.4GHz không độ trễ qua cổng USB-C tương thích PC, PS5 và Nintendo Switch.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 40. Loa Bluetooth di động JBL Charge 5
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (40, 'TG-ACC-015', 'Loa Bluetooth di động JBL Charge 5 Chống Nước IP67 Pin 20h', 'Accessories', 3490000, 3990000, 2990000, 30, 11, 40, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800&auto=format&fit=crop&q=80',
'Chất âm JBL Pro Sound mạnh mẽ với củ loa trầm độc lập, loa tweeter riêng biệt và hai bộ tản âm bass thụ động cho tiếng bass sâu lắng uy lực.

🔊 ÂM THANH MẠNH MẼ KHÁNG NƯỚC KHÁNG BỤI IP67
• Chuẩn chống bụi chống nước IP67 tuyệt đối, thoải mái mang đi biển, hồ bơi hoặc dã ngoại ngoài trời.
• Thời lượng pin lên đến 20 giờ phát nhạc liên tục.
• Tích hợp tính năng pin sạc dự phòng sạc ngược cho điện thoại qua cổng USB-A.
• Chế độ PartyBoost ghép nối nhiều loa JBL tương thích để tạo nên bữa tiệc âm nhạc bùng nổ.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 41. Trạm sạc đa năng Anker 737 GaNPrime 120W 4 Cổng
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (41, 'TG-ACC-016', 'Trạm sạc Anker 737 GaNPrime 120W 4 Cổng Thông Minh', 'Accessories', 1890000, 2290000, 1590000, 40, 15, 50, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80',
'Trạm sạc bàn siêu mạnh mẽ với công nghệ GaNPrime 120W sạc cùng lúc cả MacBook Pro, iPad và iPhone ở tốc độ cao nhất.

⚡ CÔNG NGHỆ PHÂN BỔ NGUỒN ĐIỆN POWERIQ 4.0
• 3 cổng USB-C và 1 cổng USB-A với công suất cổng đơn lên tới 100W sạc nhanh laptop.
• Tự động phát hiện và phân bổ công suất thông minh theo thời gian thực tới từng thiết bị.
• Cảm biến nhiệt ActiveShield 2.0 theo dõi nhiệt độ hơn 3 triệu lần mỗi ngày để bảo vệ an toàn tối đa.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 42. Pin sạc dự phòng Baseus Blade HD 100W Màn hình LED Siêu Mỏng
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (42, 'TG-ACC-017', 'Pin sạc dự phòng Baseus Blade HD 100W Màn hình LED Siêu Mỏng', 'Accessories', 1290000, 1590000, 990000, 50, 19, 60, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1609592807664-4a572a1ec737?w=800&auto=format&fit=crop&q=80',
'Pin dự phòng công suất khủng 100W mỏng nhất thế giới với độ dày chỉ 18mm, thiết kế dạng phẳng dễ dàng nhét vừa ngăn đựng laptop trong balo.

🔋 CÔNG SUẤT 100W SẠC NHANH LAPTOP VÀ ĐIỆN THOẠI
• Dung lượng chuẩn 20.000mAh hỗ trợ các giao thức sạc nhanh PD 3.0, QC 4.0+, PPS.
• Màn hình LED hiển thị thời gian sạc còn lại, mức pin và công suất W thực tế theo thời gian thực.
• 2 cổng USB-C và 2 cổng USB-A sạc đồng thời 4 thiết bị cùng lúc an toàn.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 43. Laptop ASUS TUF Gaming F15
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (43, 'TG-LAP-014', 'Laptop ASUS TUF Gaming F15 FX507ZC4 Core i5 RTX 3050', 'Laptop', 18490000, 20990000, 16990000, 25, 8, 30, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80',
'Laptop Gaming độ bền chuẩn quân đội MIL-STD-810H với chip Intel Core i5-12500H 12 nhân 16 luồng và card rời RTX 3050 4GB.

⚡ CHIẾN GAME BỀN BỈ
• CPU: Intel Core i5-12500H (12 nhân, 16 luồng, xung nhịp Turbo 4.5GHz).
• VGA: NVIDIA GeForce RTX 3050 4GB GDDR6 (có MUX Switch).
• RAM: 16GB DDR4 3200MHz, SSD 512GB NVMe PCIe 4.0.
• Màn hình: 15.6 inch FHD 144Hz IPS chống chói Adaptive-Sync.
• Pin 56Wh hỗ trợ sạc nhanh qua cổng Type-C.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 44. Laptop Gaming MSI Katana 15
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (44, 'TG-LAP-015', 'Laptop Gaming MSI Katana 15 B13VFK Core i7 RTX 4060', 'Laptop', 27990000, 31500000, 25990000, 15, 5, 18, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80',
'Thanh kiếm Katana gaming sắc bén với Intel Core i7-13620H thế hệ 13 và card đồ họa RTX 4060 8GB GDDR6 cực mạnh.

⚔️ VŨ KHÍ GAMING SẮC BÉN
• CPU: Intel Core i7-13620H (10 nhân, 16 luồng, xung nhịp Turbo 4.9GHz).
• VGA: NVIDIA GeForce RTX 4060 8GB GDDR6 Full TGP 105W DLSS 3.
• RAM: 16GB DDR5 5200MHz, SSD 1TB PCIe NVMe Gen 4.
• Màn hình: 15.6 inch FHD 144Hz IPS viền siêu mỏng.
• Bàn phím 4 vùng LED RGB với cụm phím WASD nổi bật.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 45. Laptop LG Gram 16 2024
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (45, 'TG-LAP-016', 'Laptop LG Gram 16 2024 Siêu Nhẹ 1.19kg Intel Core Ultra 7', 'Laptop', 34990000, 38990000, 32490000, 10, 2, 12, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&auto=format&fit=crop&q=80',
'Đỉnh cao laptop 16 inch siêu nhẹ chỉ 1.19kg, khung hợp kim Magie Nano chuẩn độ bền quân đội Mỹ và chip Intel Core Ultra 7.

🪶 SIÊU NHẸ VÀ PIN KHỦNG 24 GIỜ
• CPU: Intel Core Ultra 7 155H thế hệ mới tích hợp Intel AI Boost NPU.
• RAM: 16GB LPDDR5X 7467MHz băng thông cực lớn, SSD 512GB NVMe.
• Màn hình: 16.0 inch 2.5K WQXGA (2560x1600) IPS chống chói 99% DCI-P3.
• Dung lượng pin khủng 77Wh cho thời lượng làm việc văn phòng tới 24 giờ liên tục.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 46. Màn hình Gaming LG UltraGear 27GR95QE-B OLED 240Hz
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (46, 'TG-ACC-018', 'Màn hình Gaming LG UltraGear 27GR95QE-B OLED 240Hz 0.03ms', 'Accessories', 18990000, 22000000, 16990000, 8, 3, 10, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80',
'Màn hình OLED chơi game đầu tiên trên thế giới với tần số quét 240Hz và thời gian phản hồi 0.03ms cực nhanh.

🖥️ OLED 240Hz VÔ ĐỐI VỀ TỐC ĐỘ
• Tấm nền OLED 27 inch 2K QHD (2560x1440) tỷ lệ tương phản 1.500.000:1.
• Tần số quét 240Hz, phản hồi 0.03ms GtG triệt tiêu hoàn toàn bóng mờ.
• Độ phủ màu 98.5% DCI-P3 chuẩn điện ảnh HDR10.
• Tương thích NVIDIA G-Sync và AMD FreeSync Premium.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 47. Bàn phím cơ DareU EK87 V2
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (47, 'TG-ACC-019', 'Bàn phím cơ DareU EK87 V2 Multi-LED D-Switch', 'Accessories', 590000, 690000, 490000, 50, 24, 80, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80',
'Bàn phím cơ quốc dân dành cho học sinh, sinh viên với layout TKL 87 phím và switch D-Switch độc quyền bền bỉ 50 triệu lần bấm.

⌨️ BÀN PHÍM CƠ QUỐC DÂN GIÁ TỐT
• Layout TKL 87 phím gọn gàng, khung vỏ cứng cáp đầm tay.
• Switch D-Switch (Blue / Brown / Red) cảm giác gõ tốt trong tầm giá.
• Đèn nền Multi-LED Rainbow rực rỡ nhiều hiệu ứng.
• Keycap ABS Double-Shot siêu bền không mờ chữ.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 48. Chuột gaming không dây Pulsar X2V2 Wireless
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (48, 'TG-ACC-020', 'Chuột gaming không dây Pulsar X2V2 Wireless Siêu Nhẹ 52g', 'Accessories', 2290000, 2590000, 1990000, 20, 7, 25, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80',
'Chuột eSports bắn súng đỉnh cao với trọng lượng siêu nhẹ chỉ 52g và cảm biến PixArt PAW3395 26.000 DPI.

🎯 TRỌNG LƯỢNG SIÊU NHẸ 52G
• Trọng lượng lông vũ 52 gram không đục lỗ, cầm đối xứng hoàn hảo.
• Cảm biến flagship PixArt PAW3395 26K DPI, 650 IPS, 50G.
• Switch quang học chống nảy đúp (double-click) tuyệt đối.
• Hỗ trợ polling rate lên tới 4000Hz (với dongle 4K rời).
• Thời lượng pin lên tới 100 giờ liên tục.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 49. Tai nghe Chống Ồn Sennheiser Momentum 4 Wireless
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (49, 'TG-ACC-021', 'Tai nghe Chống Ồn Sennheiser Momentum 4 Wireless Pin 60h', 'Accessories', 7990000, 9490000, 6990000, 12, 4, 15, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80',
'Tuyệt tác âm thanh Audiophile từ Đức với thời lượng pin kỷ lục 60 giờ và công nghệ chống ồn thích ứng Adaptive ANC.

🎵 CHẤT ÂM AUDIOPHILE & PIN 60 GIỜ
• Màng loa 42mm audiophile mang lại chất âm tinh khiết giàu chi tiết.
• Chống ồn thích ứng thế hệ mới và chế độ xuyên âm tự nhiên Transparency Mode.
• Pin 60 giờ phát nhạc liên tục (bật cả chống ồn ANC).
• Hỗ trợ codec chất lượng cao aptX Adaptive, AAC, SBC.
• Thiết kế bọc vải cao cấp sang trọng, đệm tai mút hoạt tính cực êm.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- 50. Đế sạc không dây 3-in-1 Belkin BoostCharge Pro MagSafe 15W
INSERT INTO tblproduct (id, code, name, product_category, price, mrp_price, discount_price, discount_limit, discount_sold, stock, active, create_date, image, description)
VALUES (50, 'TG-ACC-022', 'Đế sạc không dây 3-in-1 Belkin BoostCharge Pro MagSafe 15W', 'Accessories', 2990000, 3590000, 2590000, 30, 11, 35, 'Active', CURRENT_TIMESTAMP,
'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80',
'Đế sạc không dây cao cấp đạt chứng nhận Apple Made for MagSafe (MFM), sạc đồng thời iPhone, Apple Watch và AirPods.

⚡ SẠC MAGSAFE 15W CHUẨN APPLE
• Sạc nhanh không dây chuẩn MagSafe 15W cho iPhone 12/13/14/15/16.
• Sạc nhanh Apple Watch từ Series 7/8/9/Ultra nhanh hơn 33%.
• Đế sạc AirPods chuẩn Qi 5W riêng biệt.
• Thiết kế kim loại mạ crom sang trọng tô điểm cho bàn làm việc.')
ON CONFLICT (id) DO UPDATE SET 
    code = EXCLUDED.code, name = EXCLUDED.name, product_category = EXCLUDED.product_category,
    price = EXCLUDED.price, mrp_price = EXCLUDED.mrp_price, discount_price = EXCLUDED.discount_price,
    discount_limit = EXCLUDED.discount_limit, discount_sold = EXCLUDED.discount_sold, stock = EXCLUDED.stock,
    active = EXCLUDED.active, image = EXCLUDED.image, description = EXCLUDED.description;

-- Cập nhật sequence của PostgreSQL cho bảng tblproduct để tránh conflict ID
SELECT setval(pg_get_serial_sequence('tblproduct', 'id'), coalesce(max(id), 1)) FROM tblproduct;


