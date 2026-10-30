-- Flyway Migration V4: Cập nhật mô tả chi tiết, chuyên nghiệp và đầy đủ cho các sản phẩm TechGear

UPDATE tblproduct 
SET description = 'Laptop Gaming ASUS ROG Strix G16 (2024) là cỗ máy chiến game đỉnh cao thuộc phân khúc cao cấp, được thiết kế tối ưu cho game thủ chuyên nghiệp và nhà sáng tạo nội dung.

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
• Kết nối: Thunderbolt 4, HDMI 2.1, USB 3.2 Gen 2, RJ45 2.5G LAN và Wi-Fi 6E siêu tốc.'
WHERE id = 1;

UPDATE tblproduct 
SET description = 'Apple MacBook Pro 14 M3 Pro là sự kết hợp hoàn hảo giữa thiết kế kim loại nguyên khối unibody đẳng cấp cùng sức mạnh tính toán vượt trội trên tiến trình 3nm tiên tiến nhất thế giới.

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
• Cổng kết nối Pro: 3x Thunderbolt 4, cổng HDMI xuất màn hình 8K, khe đọc thẻ SDXC tốc độ cao, jack 3.5mm cho tai nghe trở kháng cao và cổng sạc từ tính MagSafe 3 tiện lợi.'
WHERE id = 2;

UPDATE tblproduct 
SET description = 'AKKO 3087 v2 RGB là mẫu bàn phím cơ TKL (87 phím) kinh điển được đông đảo game thủ và dân văn phòng săn đón nhờ build chắc chắn, cảm giác gõ êm ái và mức giá cực kỳ dễ tiếp cận.

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
• Cáp kết nối Type-C rời bọc dù chắc chắn, dễ dàng tháo lắp khi di chuyển.'
WHERE id = 3;

UPDATE tblproduct 
SET description = 'Logitech G Pro X Superlight 2 là đỉnh cao của dòng chuột thi đấu eSports chuyên nghiệp, được nghiên cứu và thiết kế cùng các vận động viên thể thao điện tử hàng đầu thế giới để đạt đến sự chuẩn xác và phản xạ tuyệt đối.

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
• Đế chuột 100% PTFE nguyên chất không pha tạp chất cho cảm giác lướt chuột êm ái mượt mà trên mọi bề mặt pad.'
WHERE id = 4;

UPDATE tblproduct 
SET description = 'Sony WH-1000XM5 là mẫu tai nghe chống ồn over-ear cao cấp số 1 thế giới hiện nay, thiết lập chuẩn mực mới về khả năng cách ly tạp âm, chất âm Hi-Res Audio tinh tế cùng thiết kế không khớp nối sang trọng.

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
• Tính năng thông minh Speak-to-Chat tự động dừng nhạc khi bạn cất tiếng nói, và kết nối đa điểm đồng thời 2 thiết bị (Multipoint Connection).'
WHERE id = 5;
