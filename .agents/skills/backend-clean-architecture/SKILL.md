---
name: backend-clean-architecture
description: >-
  Bộ quy chuẩn kiến trúc sạch (Clean Architecture), bảo mật (AuthN/AuthZ), xử lý dữ liệu và Clean Code chuẩn doanh nghiệp dùng chung cho mọi dự án Backend (Java / Spring Boot / NodeJS / Go).
---

# Enterprise Backend Engineering Standards (Bộ Quy Chuẩn Backend Doanh Nghiệp)

Bộ quy chuẩn này áp dụng cho việc thiết kế, viết mới, refactor và review mã nguồn trên **mọi dự án Backend**, nhằm đảm bảo hệ thống bảo mật, kiến trúc mạch lạc, dễ mở rộng và sẵn sàng cho môi trường production.

---

## 1. Kiến Trúc Phân Tầng (Layered Architecture & Separation of Concerns)

Hệ thống phải tuân thủ nghiêm ngặt mô hình phân tầng một chiều:
`Client -> Controller / Handler -> Service / Use Case -> Repository / DAO -> Database`

### 1.1. Controller (Presentation Layer) - "Thin Controller"
- **Nhiệm vụ duy nhất**: Nhận HTTP request, validate dữ liệu đầu vào (DTO), gọi Service tương ứng, và trả về HTTP response với Status Code chuẩn (200, 201, 204, 400, 401, 403, 404, 500).
- **Cấm kỵ**:
  - KHÔNG viết logic nghiệp vụ (tính toán, điều kiện if-else phức tạp, trừ kho, tính tiền) trong Controller.
  - KHÔNG inject hoặc gọi trực tiếp `Repository` từ Controller.
  - KHÔNG nhận hoặc trả về trực tiếp Database Entity (`Product`, `Order`, `User`). Bắt buộc dùng Request DTO và Response DTO để tránh lộ cấu trúc DB hoặc dính lỗ hổng Mass Assignment.

### 1.2. Service (Business Logic Layer) - "Fat Service"
- Chứa toàn bộ nghiệp vụ của ứng dụng: kiểm tra điều kiện, phối hợp các Repository, tích hợp bên thứ ba (Mail, SMS, Payment, AI).
- Là nơi duy nhất quản lý ranh giới giao dịch dữ liệu (`@Transactional`).
- Quăng ra các Business Exception có nghĩa (ví dụ: `ResourceNotFoundException`, `InsufficientStockException`) thay vì trả về null hoặc chuỗi thông báo lỗi mơ hồ.

### 1.3. Repository (Data Access Layer)
- Chỉ chịu trách nhiệm đọc/ghi dữ liệu từ Database.
- Khi cần thống kê, tổng hợp dữ liệu (`SUM`, `COUNT`, `AVG`, `GROUP BY`), bắt buộc viết câu truy vấn ở Database (JPQL, HQL, Native SQL). Tuyệt đối **KHÔNG** dùng `findAll()` kéo toàn bộ bảng lên RAM rồi dùng vòng lặp để tính toán.
- Mọi API lấy danh sách đều phải hỗ trợ phân trang (`Pageable`, `limit/offset`).

---

## 2. Xác Thực & Phân Quyền Đúng Luồng (Authentication & Authorization)

### 2.1. Quản lý Mật khẩu (Password Management)
- **Tuyệt đối không lưu plain-text**: Luôn sử dụng thuật toán băm chậm có salt (`BCryptPasswordEncoder` với work factor >= 10, hoặc `Argon2`).
- Mã hóa mật khẩu ngay tại tầng Service khi Đăng ký (`register`) hoặc Đổi mật khẩu (`changePassword`).

### 2.2. Luồng Xác thực (Authentication Flow)
- **Tách biệt rõ ràng 2 mô hình**:
  - *Stateful (Session-based)*: Session tạo trên server, client giữ `JSESSIONID` cookie (HttpOnly, Secure, SameSite).
  - *Stateless (Token-based / JWT)*: Client gửi JWT qua Header `Authorization: Bearer <token>`. Filter chặn ở cửa kiểm tra chữ ký token và nạp `UserDetails` vào `SecurityContextHolder`.
- Tuyệt đối không tự chế logic check mật khẩu bằng `user.getPassword().equals(input)` trong Controller. Phải ủy quyền cho Security Provider (`DaoAuthenticationProvider`, `AuthenticationManager`).

### 2.3. Phân quyền & Chống Lỗ Hổng IDOR (Authorization & Access Control)
- **Role-based Access Control (RBAC)**: Bảo vệ endpoint theo Role/Authority (`@PreAuthorize("hasRole('ADMIN')")` hoặc config tại `SecurityFilterChain`).
- **Chống lộ dữ liệu chéo (IDOR - Insecure Direct Object Reference)**:
  - Khi người dùng thao tác với tài nguyên cá nhân (xem đơn hàng, sửa hồ sơ, xem giỏ hàng): **Luôn lấy User ID từ thông tin đăng nhập trong Security Context / Token**.
  - **Cấm kỵ**: Không tin tưởng `userId` truyền lên từ URL Param hay Request Body mà không kiểm tra quyền sở hữu (`resource.getUserId().equals(currentUser.getId())`).

---

## 3. Quản Lý Giao Dịch & Toàn Vẹn Dữ Liệu (Transactions & Integrity)

### 3.1. Ranh giới Giao dịch (`@Transactional`)
- Mọi hàm Service thực hiện **từ 2 thao tác ghi (Insert/Update/Delete) trở lên** bắt buộc phải gắn `@Transactional`.
- Cấu hình `@Transactional(rollbackFor = Exception.class)` để đảm bảo rollback cả khi gặp Checked Exception.
- Gắn `@Transactional(readOnly = true)` cho các hàm chỉ đọc để tăng tốc độ truy vấn và giải phóng tài nguyên Hibernate.

### 3.2. Kiểu Dữ Liệu Chuẩn (Data Types)
- **Tiền tệ & Số học chính xác**: Luôn sử dụng `BigDecimal` (hoặc lưu dạng số nguyên cents/đồng `Long`). **Tuyệt đối không dùng `Double`, `Float`, `String`** để tính toán tiền.
- **Trạng thái (Status)**: Luôn dùng `Enum` (ví dụ: `OrderStatus.PENDING`, `OrderStatus.CANCELLED`), map bằng `@Enumerated(EnumType.STRING)`. Không dùng chuỗi tự do.
- **ID Khóa chính**: Dùng `Long` hoặc `UUID`. Tránh dùng `Integer` cho các bảng có khả năng tăng trưởng lớn.

---

## 4. Xử Lý Lỗi Tập Trung & Chuẩn Hóa Response (Exception Handling)

- Không bao giờ "nuốt" exception bằng khối `try { ... } catch (Exception e) {}` rỗng hoặc chỉ `e.printStackTrace()`.
- Dùng `@RestControllerAdvice` (hoặc `@ControllerAdvice`) để bắt và chuẩn hóa toàn bộ lỗi trong hệ thống thành một cấu trúc đồng nhất:
  ```json
  {
    "timestamp": "2026-09-22T22:40:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Số lượng tồn kho không đủ",
    "path": "/api/orders"
  }
  ```
- Định nghĩa các Exception đặc thù theo nghiệp vụ:
  - `ResourceNotFoundException` -> Trả về HTTP 404
  - `BadRequestException` / `ValidationException` -> Trả về HTTP 400
  - `UnauthorizedException` -> Trả về HTTP 401
  - `ForbiddenException` -> Trả về HTTP 403

---

## 5. Bảo Mật Webhook, API Callback & Upload File

### 5.1. Webhook & Payment Callbacks
- Mọi Webhook nhận từ bên thứ 3 (VietQR, VNPay, Momo, Stripe) bắt buộc phải:
  1. Kiểm tra chữ ký bảo mật (**HMAC SHA256 Signature**) hoặc **Secret API Key** trong Header.
  2. Triển khai cơ chế **Idempotency** (chống xử lý 1 giao dịch nhiều lần nếu bên thứ 3 retry).

### 5.2. Xử lý File Upload An Toàn
- Tuyệt đối không dùng trực tiếp `file.getOriginalFilename()` để lưu vào ổ đĩa.
- Bắt buộc sinh tên file ngẫu nhiên: `UUID.randomUUID().toString() + extension`.
- Kiểm tra danh sách đuôi file cho phép (`.jpg`, `.png`, `.pdf`) và giới hạn dung lượng file tối đa (File Size Limit).

---

## 6. Vệ Sinh Mã Nguồn (Clean Code & Professional Practices)

1. **Zero Hardcoded Credentials**: Toàn bộ Secret Key, API Token, Mật khẩu Database/Mail phải đưa vào file môi trường (`.env`, `application-*.properties`) và inject qua biến môi trường.
2. **Không để lại code rác**:
   - Xóa bỏ toàn bộ code cũ bị comment cả đoạn dài.
   - Xóa bỏ log thử nghiệm `System.out.println()`. Sử dụng Logger chuẩn (`SLF4J` / `log.info()`, `log.error()`).
   - Xóa bỏ hoàn toàn các câu chat hoặc comment từ AI/Chatbot trước khi hoàn thiện.
3. **Tuân thủ RESTful Naming**:
   - Động từ HTTP phải đúng bản chất: `GET` (đọc, không đổi state), `POST` (tạo mới), `PUT/PATCH` (cập nhật), `DELETE` (xóa).
   - URI dùng danh từ số nhiều: `/api/orders`, `/api/products/{id}/reviews`.
4. **Kiểm thử tự động (Unit / Integration Tests)**:
   - Viết test với JUnit 5 & Mockito cho các luồng nghiệp vụ quan trọng (đặt hàng, tính toán chiết khấu, kiểm tra phân quyền).
