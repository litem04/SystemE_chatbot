level 1: cải thiện cái 2,3 ( upload ảnh và quản lý tồn kho)

level 2: bổ sung cả 3, nếu thấy phần thanh toán khó thì bổ sung sau

level 3 : bổ sung cái 7, 8 ( thống kê dashboard, đánh giá sản phẩm )

bạn nên nhớ đây là đồ án tốt nghiệp của tôi. cái tôi muốn hướng tới 1 cái web gần bằng opencart và có tích hợp chatbot AI .











*tiến tớ 
- thanh toán online( có thể)



*chatbot ai 
-những câu hỏi cơ bản (chính sách ...)
tích hợp RAG
	
*đã gửi email xác nhận được 
 đã làm flash sale (2 đơn hàng thì Tự tách ra 1 cái giá giảm, 1 cái giá gốc trong cùng 1 đơn hàng )
 + nhiều account mua 1 mặt hàng cùng 1 thời điểm thì tình huống xảy ra là gì ( test giả lập)
 + làm thêm voucher giảm giá
 + khi 1 mặt hàng giảm giá với sl nhất định, khi sl giảm hết thì quay về như cũ ( vd: chỉ áp dụng 5 đơn hàng giảm đầu tiên -> giá về như cũ )
 quản lý hồ sơ cá nhâ
đánh giá/ bình luận 
xuất hóa đơn/ 

lúc import sản phẩm cầm nhập chi tiết thông tin sản phẩm như này;
- Màn hình: 6.7 inch Super Retina XDR
- Chip: A17 Pro
- Camera: 48MP Chính, 12MP Ultra Wide
- Pin: Xem video 29 giờ

--cần fix 



- thông báo bên client luôn mặc định là 2 dù đã xem rồi nhưng chưa hết thông báo- hãy sửa hông báo phụ thuộc vào thay đổi tình trạng đơn hàng 

- làm thêm các sản phẩm tạm ẩn -đang báo || hiện tại tạm ẩn và đang báo đều hiển thị bên client -> cần fix


-bị lỗi tổng tiền và thanh toán  trong bill

chatbot chưa lưu được tin nhắn đang hỏi trước đó, nên không trả lời liền mạch với 1 sản phẩm khi hỏi trước đó




System Prompt rõ ràng:

Vai trò: Bạn là nhân viên tư vấn của cửa hàng [Tên Web].

Phong cách: Lịch sự, chuyên nghiệp, luôn hướng khách hàng đến việc mua hàng.

Quy tắc: Chỉ trả lời dựa trên dữ liệu từ DB, không tự bịa đặt cấu hình nếu không tìm thấy.

	




Chatbot có khả năng chuyển đổi: Nếu chatbot không trả lời được, nó sẽ gửi thông báo cho Admin để người thật nhảy vào hỗ trợ.

+
Xây dựng hệ thống thương mại điện tử thiết bị số tích hợp Chatbot hỗ trợ khách hàng
Phát triển Chatbot tư vấn sản phẩm thông minh cho hệ thống bán lẻ điện thoại, laptop trực tuyến.
Giải pháp tự động hóa tư vấn và quản lý bán hàng thiết bị điện tử thông qua nền tảng Web App và Chatbot.
+
Điểm cộng: Giải thích cho hội đồng: "Em sử dụng cơ chế IPN (Webhook) để cập nhật trạng thái đơn hàng, đảm bảo tính toàn vẹn dữ liệu ngay cả khi người dùng mất kết nối mạng phía client".

data mô phỏng 
INSERT INTO tblproduct (name, price, mrp_price, image, description, category, active, create_date) VALUES 
-- ==============================================================
-- 1. MOBILE (ĐIỆN THOẠI) - Đủ hãng: Apple, Samsung, Xiaomi, Oppo
-- ==============================================================
('iPhone 15 Pro Max 256GB', '29990000', '34990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_3.png', 'Flagship Apple, Khung Titan, Chip A17 Pro, Camera Zoom 5x, Pin trâu cả ngày.', 'Mobile', 'Active', NOW()),
('iPhone 14 128GB', '16990000', '22990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-14_1.png', 'Thiết kế nhỏ gọn, Camera kép chụp đêm tốt, Hiệu năng ổn định lâu dài.', 'Mobile', 'Active', NOW()),
('iPhone 11 64GB', '8990000', '11990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-11.png', 'Giá rẻ cho học sinh sinh viên, vẫn mượt mà, chụp ảnh đẹp.', 'Mobile', 'Active', NOW()),

('Samsung Galaxy S24 Ultra', '28990000', '33990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s24-ultra-xam-222.png', 'Điện thoại AI, Bút S-Pen thần thánh, Camera mắt thần bóng đêm 200MP.', 'Mobile', 'Active', NOW()),
('Samsung Galaxy A55 5G', '9690000', '10990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-a55-5g-xanh-1.png', 'Thiết kế khung kim loại cao cấp, Chống nước IP67, Pin 5000mAh.', 'Mobile', 'Active', NOW()),
('Samsung Galaxy Z Flip5', '16990000', '25990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-z-flip5-kem-1.png', 'Điện thoại gập nhỏ gọn, Màn hình phụ lớn, Thời trang sành điệu.', 'Mobile', 'Active', NOW()),

('Xiaomi 14 5G', '19990000', '22990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-14-trang-1.png', 'Cấu hình khủng Snap 8 Gen 3, Camera Leica chuyên nghiệp, Sạc siêu nhanh.', 'Mobile', 'Active', NOW()),
('Xiaomi Redmi Note 13', '4590000', '5290000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13_1__1.png', 'Điện thoại giá rẻ quốc dân, Màn hình AMOLED 120Hz, Pin trâu.', 'Mobile', 'Active', NOW()),

('OPPO Reno11 F 5G', '8490000', '8990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/o/p/oppo-reno11-f-xanh-1.png', 'Chuyên gia chân dung, Thiết kế mặt lưng vân đá, Sạc nhanh SuperVOOC.', 'Mobile', 'Active', NOW()),

-- ==============================================================
-- 2. LAPTOP (MÁY TÍNH) - Đủ loại: Gaming, Văn phòng, Mỏng nhẹ
-- ==============================================================
('MacBook Air M1 2020', '18490000', '22990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/a/i/air_m2.png', 'Laptop văn phòng tốt nhất, Pin 18 tiếng, Mỏng nhẹ sang trọng.', 'Laptop', 'Active', NOW()),
('MacBook Pro 14 M3', '39990000', '45000000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/macbook_pro_14_inch_m3_space_gray_01.png', 'Dành cho dân đồ họa, Render video 4K, Màn hình XDR 120Hz siêu đẹp.', 'Laptop', 'Active', NOW()),

('Asus TUF Gaming F15', '19490000', '23990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/l/a/laptop-asus-tuf-gaming-f15-fx507zc4-hn074w-thumbnails.png', 'Laptop Gaming giá rẻ, Chiến mượt LOL, Valorant, Độ bền chuẩn quân đội.', 'Laptop', 'Active', NOW()),
('Asus ROG Strix G16', '32990000', '35000000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/l/a/laptop-asus-rog-strix-g16-g614ju-n3135w-thumbnails.png', 'Quái vật Gaming, LED RGB cực ngầu, Tản nhiệt 3 quạt mát lạnh.', 'Laptop', 'Active', NOW()),

('Dell Inspiron 15', '12490000', '15990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_11__1_7.png', 'Bền bỉ nồi đồng cối đá, Phù hợp văn phòng, học tập, Màn hình lớn.', 'Laptop', 'Active', NOW()),
('MSI Modern 14', '10990000', '14000000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/l/a/laptop-msi-modern-14-c7m-220vn-thumbnails.png', 'Laptop sinh viên giá rẻ, Nhỏ gọn dễ mang đi học, Cấu hình đủ dùng.', 'Laptop', 'Active', NOW()),
('Lenovo ThinkBook 14', '15990000', '18990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/l/a/laptop-lenovo-thinkbook-14-g6-irl-21kg00b1vn-thumbnails.png', 'Thiết kế doanh nhân, Bảo mật vân tay, Vỏ kim loại chắc chắn.', 'Laptop', 'Active', NOW()),

-- ==============================================================
-- 3. ACCESSORIES (PHỤ KIỆN) - Tai nghe, Chuột, Phím, Sạc
-- ==============================================================
('Apple AirPods Pro 2', '5990000', '6990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/a/p/apple-airpods-pro-2-usb-c_1_.png', 'Tai nghe chống ồn chủ động tốt nhất, Âm thanh không gian, Kết nối iPhone cực nhanh.', 'Accessories', 'Active', NOW()),
('Sony WH-1000XM5', '8490000', '9490000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/o/sony_wh-1000xm5_2_.png', 'Tai nghe chụp tai chống ồn đỉnh cao, Pin 30 giờ, Đeo êm ái.', 'Accessories', 'Active', NOW()),
('Tai nghe Marshall Major IV', '3490000', '4290000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/a/tai-nghe-chup-tai-marshall-major-iv-den-2.png', 'Thiết kế cổ điển (Retro), Pin siêu trâu 80 giờ, Âm bass mạnh mẽ.', 'Accessories', 'Active', NOW()),

('Chuột Logitech G102', '399000', '599000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/g/1/g102-gen2-black-1.jpg', 'Chuột Gaming quốc dân giá rẻ, LED RGB đẹp, Độ nhạy cao.', 'Accessories', 'Active', NOW()),
('Chuột Logitech MX Master 3S', '2190000', '2690000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/c/h/chuot-khong-day-logitech-mx-master-3s-1.jpg', 'Chuột văn phòng tốt nhất thế giới, Cuộn siêu nhanh, Dùng được trên mặt kính.', 'Accessories', 'Active', NOW()),

('Bàn phím cơ Keychron K2 Pro', '2690000', '3000000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/b/a/ban-phim-co-khong-day-keychron-k2-pro-qmk-via-nhom-rgb-hotswap-red-switch-1.jpg', 'Bàn phím cơ cho Mac và Win, Gõ sướng tay, Kết nối 3 thiết bị.', 'Accessories', 'Active', NOW()),
('Sạc dự phòng Anker 20000mAh', '990000', '1400000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/p/i/pin-sac-du-phong-anker-335-powercore-20000mah-20w-a1286_1.png', 'Pin dự phòng dung lượng lớn, Sạc nhanh iPhone, An toàn chống cháy nổ.', 'Accessories', 'Active', NOW()),
('Loa JBL Flip 6', '2490000', '2990000', 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/l/o/loa-bluetooth-jbl-flip-6_3_.png', 'Loa Bluetooth kháng nước, Âm thanh lớn, Phù hợp tiệc tùng du lịch.', 'Accessories', 'Active', NOW());