-- 1. BẢNG QUẢN TRỊ VIÊN (ADMINS) - Mục (a)
CREATE TABLE admins (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL, -- Tên đăng nhập
    password VARCHAR(255) NOT NULL,       -- Mật khẩu
    full_name VARCHAR(100) NOT NULL       -- Tên hiển thị
);

-- 2. BẢNG KHÁCH HÀNG (CUSTOMERS) - Mục (c)
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL, -- Tên đăng nhập (email)
    password VARCHAR(255) NOT NULL,       -- Mật khẩu
    full_name VARCHAR(100) NOT NULL,      -- Tên khách hàng
    address TEXT,                         -- Địa chỉ (để ship hàng)
    phone VARCHAR(15)                     -- Số điện thoại
);

-- 3. BẢNG SẢN PHẨM (PRODUCTS) - Mục (e)
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(15, 2) NOT NULL,     -- Giá bán thực tế
    mrp_price DECIMAL(15, 2),          -- Giá niêm yết (Maximum Retail Price)
    image VARCHAR(500),                -- Link hình ảnh
    category VARCHAR(100),             -- Tên danh mục (lưu thẳng tên cho đơn giản theo ý bạn)
    status VARCHAR(20) DEFAULT 'ACTIVE' -- Trạng thái sản phẩm
);

-- 4. BẢNG GIỎ HÀNG (CART) - Mục (b)
CREATE TABLE cart (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(id), -- Giỏ của ai
    product_id INT REFERENCES products(id),   -- Mua cái gì
    quantity INT DEFAULT 1                    -- Số lượng
);

-- 5. BẢNG ĐƠN HÀNG (ORDERS) - Mục (d)
-- Lưu ý: Để lưu được nhiều sản phẩm trong 1 đơn, ta cần tách ra Order (thông tin chung) và OrderDetail.
-- Nhưng để bám sát ý bạn "lưu product_id, tên sản phẩm trong bảng đơn hàng", 
-- hệ thống dưới đây sẽ lưu thông tin dòng thời gian (snapshot).

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(id),
    payment_id VARCHAR(100),            -- Mã thanh toán (nếu có)
    customer_name VARCHAR(100),         -- Tên khách lúc đặt (Lưu cứng để nhỡ khách đổi tên không ảnh hưởng)
    customer_address TEXT,              -- Địa chỉ giao hàng
    total_amount DECIMAL(15, 2),        -- Tổng tiền
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING'
);

-- Bảng phụ để lưu chi tiết từng món trong đơn (Bắt buộc phải có nếu 1 đơn mua nhiều món)
CREATE TABLE order_details (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id),
    product_id INT REFERENCES products(id),
    product_name VARCHAR(200),  -- Lưu tên SP lúc mua (phòng trường hợp sau này admin sửa tên SP)
    quantity INT,
    price DECIMAL(15, 2)        -- Giá lúc mua
);

-- DATA MẪU (Insert thử để test)
INSERT INTO admins (username, password, full_name) VALUES ('admin', 'admin123', 'Super Admin');
INSERT INTO customers (username, password, full_name, address) VALUES ('khach1', '123456', 'Nguyen Van A', 'Ha Noi');
INSERT INTO products (product_name, price, mrp_price, image, category) VALUES 
('iPhone 15 Pro', 25000000, 28000000, 'iphone.jpg', 'Dien thoai'),
('Samsung S24', 20000000, 22000000, 'samsung.jpg', 'Dien thoai');