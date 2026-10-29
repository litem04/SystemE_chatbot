-- Flyway Migration V5: Thêm chỉ mục (Indexes) cho các khóa ngoại và các cột truy vấn thường xuyên
-- Giúp tăng tốc độ JOIN, truy vấn lịch sử đơn hàng và chống Full Table Scan (Sequential Scan) khi DELETE/UPDATE bảng cha

-- 1. Chỉ mục khóa ngoại cho bảng order_details
CREATE INDEX IF NOT EXISTS idx_order_details_order_id ON order_details(order_id);
CREATE INDEX IF NOT EXISTS idx_order_details_product_id ON order_details(product_id);

-- 2. Chỉ mục cho giỏ hàng tblcart
CREATE INDEX IF NOT EXISTS idx_tblcart_customer_id ON tblcart(customer_id);
CREATE INDEX IF NOT EXISTS idx_tblcart_product_id ON tblcart(product_id);

-- 3. Chỉ mục khóa ngoại cho bảng product_review
CREATE INDEX IF NOT EXISTS idx_product_review_product_id ON product_review(product_id);
CREATE INDEX IF NOT EXISTS idx_product_review_customer_id ON product_review(customer_id);

-- 4. Chỉ mục tối ưu truy vấn đơn hàng (tblorders) theo khách hàng và trạng thái
CREATE INDEX IF NOT EXISTS idx_tblorders_email_id ON tblorders(email_id);
CREATE INDEX IF NOT EXISTS idx_tblorders_order_status ON tblorders(order_status);
CREATE INDEX IF NOT EXISTS idx_tblorders_order_date ON tblorders(order_date);

-- 5. Chỉ mục tối ưu lọc sản phẩm (tblproduct) theo danh mục và cảnh báo tồn kho
CREATE INDEX IF NOT EXISTS idx_tblproduct_category ON tblproduct(product_category);
CREATE INDEX IF NOT EXISTS idx_tblproduct_stock ON tblproduct(stock);
