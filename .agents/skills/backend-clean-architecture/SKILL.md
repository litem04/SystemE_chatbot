---
name: backend-clean-architecture
description: >-
  Bộ quy chuẩn kiến trúc sạch (Clean Architecture), bảo mật chuyên sâu (AuthN/AuthZ/CSRF/Webhook), xử lý dữ liệu và Clean Code chuẩn doanh nghiệp dùng chung cho mọi dự án Backend (Java / Spring Boot / NodeJS / Go).
---

# Enterprise Backend Engineering Standards (Bộ Quy Chuẩn Backend Doanh Nghiệp)

Bộ quy chuẩn này áp dụng cho việc thiết kế, viết mới, refactor và review mã nguồn trên **mọi dự án Backend**, nhằm đảm bảo hệ thống bảo mật cấp độ sản phẩm, kiến trúc mạch lạc, dễ mở rộng và đạt tiêu chuẩn tuyển dụng Middle / Senior.

---

## 1. Kiến Trúc Phân Tầng & Trách Nhiệm Đơn Lẻ (Layered Architecture & Separation of Concerns)

Hệ thống phải tuân thủ nghiêm ngặt mô hình phân tầng một chiều:
`Client -> Controller / Handler -> Service / Use Case -> Repository / DAO -> Database`

### 1.1. Controller (Presentation Layer) - "Thin Controller"
- **Nhiệm vụ duy nhất**: Nhận HTTP request, validate dữ liệu đầu vào (DTO), gọi Service tương ứng, và trả về HTTP response với Status Code chuẩn (200, 201, 204, 400, 401, 403, 404, 500).
- **100% Nghiệp vụ phải nằm ở Service**:
  - KHÔNG viết logic nghiệp vụ (tính toán, điều kiện if-else phức tạp, trừ kho, tính tiền) trong Controller.
  - KHÔNG xử lý thao tác với File I/O (upload file, tạo folder, sinh UUID, kiểm tra MIME type) trong Controller; phải tạo `FileStorageService`.
  - KHÔNG xử lý logic phiên người dùng (Session Registry, đăng xuất cưỡng bức) trong Controller; phải đưa vào Service.
  - KHÔNG inject hoặc gọi trực tiếp `Repository` từ Controller.
  - KHÔNG nhận hoặc trả về trực tiếp Database Entity. Bắt buộc dùng Request DTO và Response DTO để tránh lộ cấu trúc DB và chống lỗ hổng Mass Assignment.

### 1.2. Service (Business Logic Layer) - "Fat Service"
- Chứa toàn bộ nghiệp vụ của ứng dụng: kiểm tra điều kiện, phối hợp các Repository, tích hợp bên thứ ba (Mail, SMS, Payment, AI).
- Là nơi duy nhất quản lý ranh giới giao dịch dữ liệu (`@Transactional`).
- Quăng ra các Business Exception có nghĩa (ví dụ: `ResourceNotFoundException`, `InsufficientStockException`, `DuplicateResourceException`).

### 1.3. Repository (Data Access Layer)
- Chỉ chịu trách nhiệm đọc/ghi dữ liệu từ Database.
- Khi cần thống kê, tổng hợp dữ liệu (`SUM`, `COUNT`, `AVG`, `GROUP BY`), bắt buộc viết câu truy vấn ở Database (JPQL, HQL, Native SQL). Tuyệt đối **KHÔNG** dùng `findAll()` kéo toàn bộ bảng lên RAM rồi dùng vòng lặp để tính toán.
- Mọi API lấy danh sách đều phải hỗ trợ phân trang (`Pageable`, `limit/offset`).

---

## 2. Xác Thực, Phân Quyền & Bảo Mật Web (AuthN, AuthZ & Web Security)

### 2.1. Quản lý Mật khẩu (Password Management)
- **Tuyệt đối không lưu plain-text**: Luôn sử dụng thuật toán băm chậm có salt (`BCryptPasswordEncoder` với work factor >= 10, hoặc `Argon2`).
- Mã hóa mật khẩu ngay tại tầng Service khi Đăng ký (`register`) hoặc Đổi mật khẩu (`changePassword`).

### 2.2. Luồng Xác thực & Chính Sách CSRF (Cross-Site Request Forgery)
- **Tách biệt rõ ràng 2 mô hình**:
  - *Stateful (Session-based + Server-Side Rendering)*: Dùng Session Cookie (`JSESSIONID`). **BẮT BUỘC BẬT CSRF** để bảo vệ các form trình duyệt. Các form HTML phải sử dụng token CSRF (`th:action="@{...}"` hoặc thẻ input hidden `_csrf`). Chỉ cấu hình bỏ qua CSRF (`ignoringRequestMatchers`) cho các Webhook công khai hoặc API gọi từ service-to-service.
  - *Stateless (Token-based / JWT)*: Dùng Header `Authorization: Bearer <token>`. Tắt CSRF vì client không tự động gửi credentials qua cookie khi bị gọi chéo nguồn.
- Tuyệt đối không tự chế logic check mật khẩu bằng `user.getPassword().equals(input)` trong Controller. Phải ủy quyền cho Security Provider (`DaoAuthenticationProvider`, `AuthenticationManager`).

### 2.3. Phân quyền & Chống Lỗ Hổng IDOR (Authorization & Access Control)
- **Role-based Access Control (RBAC)**: Bảo vệ endpoint theo Role/Authority (`@PreAuthorize("hasRole('ADMIN')")` hoặc config tại `SecurityFilterChain`).
- **Chống lộ dữ liệu chéo (IDOR - Insecure Direct Object Reference)**:
  - Khi người dùng thao tác với tài nguyên cá nhân (xem đơn hàng, sửa hồ sơ, xem giỏ hàng): **Luôn lấy User ID từ thông tin đăng nhập trong Security Context / Token**.
  - **Cấm kỵ**: Không tin tưởng `userId` truyền lên từ URL Param hay Request Body mà không kiểm tra quyền sở hữu (`resource.getUserId().equals(currentUser.getId())`).

---

## 3. Bảo Mật Cấp Độ Tài Chính Cho Webhook & Callbacks (Payment Webhook Hardening)

Mọi Webhook nhận thanh toán từ bên thứ ba (VietQR, VNPay, Momo, Stripe) bắt buộc phải đáp ứng đủ 4 tiêu chí bảo mật sau:

1. **Bắt buộc Secret Header (Mandatory Secret)**:
   - Header chứa secret token (ví dụ: `X-Webhook-Secret`) phải là bắt buộc (`required = true`). Nếu thiếu hoặc rỗng, lập tức trả về `401 Unauthorized`.
2. **So sánh Thời gian Cố định (Constant-Time Comparison)**:
   - Tuyệt đối **không dùng** `String.equals()` để so sánh Secret Token hoặc Chữ ký Signature vì có nguy cơ dính tấn công **Timing Attack**.
   - Bắt buộc sử dụng `MessageDigest.isEqual(input.getBytes(), secret.getBytes())`.
3. **Đối soát Số tiền & Đơn hàng (Amount Verification)**:
   - Bắt buộc kiểm tra số tiền thực tế nhận được trong webhook payload có khớp chính xác với tổng số tiền cần thanh toán của đơn hàng (`order.getTotalAmount()`) hay không.
   - Nếu số tiền không khớp, lập tức ghi log cảnh báo và không chuyển trạng thái đơn hàng sang `PAID`.
4. **Cơ chế Chống Xử lý Trùng (Idempotency)**:
   - Lưu trữ `transactionId` từ cổng thanh toán vào đơn hàng.
   - Trước khi xử lý, kiểm tra xem giao dịch này đã được ghi nhận hoặc đơn hàng đã ở trạng thái `PAID` chưa. Nếu đã xử lý rồi, bỏ qua và trả về phản hồi thành công an toàn.

---

## 4. Quản Lý Giao Dịch, DTOs & Toàn Vẹn Dữ Liệu (Transactions, DTOs & Integrity)

### 4.1. Ranh giới Giao dịch (`@Transactional`)
- Mọi hàm Service thực hiện **từ 2 thao tác ghi (Insert/Update/Delete) trở lên** bắt buộc phải gắn `@Transactional`.
- Cấu hình `@Transactional(rollbackFor = Exception.class)` để đảm bảo rollback cả khi gặp Checked Exception.
- Gắn `@Transactional(readOnly = true)` cho các hàm chỉ đọc để tăng tốc độ truy vấn và giải phóng tài nguyên Hibernate.

### 4.2. Kiểu Dữ Liệu Chuẩn & Enum Hóa (Data Types & Enums)
- **Tiền tệ & Số học chính xác**: Luôn sử dụng `BigDecimal` (hoặc lưu dạng số nguyên cents/đồng `Long`). **Tuyệt đối không dùng `Double`, `Float`, `String`** để tính toán tiền.
- **Trạng thái (Status)**: Toàn bộ trạng thái nghiệp vụ (đơn hàng, thanh toán, phương thức) **bắt buộc dùng `Enum`** (`@Enumerated(EnumType.STRING)`). Tuyệt đối không dùng chuỗi String tự do.
- **ID Khóa chính**: Dùng `Long` hoặc `UUID`. Tránh dùng `Integer` cho các bảng có khả năng tăng trưởng lớn.

### 4.3. DTO & Jakarta Bean Validation
- Không bao giờ bind thẳng Entity CSDL từ form request của người dùng.
- Tạo DTO riêng cho từng thao tác (ví dụ: `RegisterRequest`, `PlaceOrderRequest`).
- Áp dụng các annotation validation chuẩn: `@NotBlank`, `@Email`, `@Size(min=...)`, `@Min(...)`, `@Pattern(...)` và kích hoạt bằng `@Valid` tại Controller.

---

## 5. Xử Lý Lỗi Tập Trung & Chuẩn Hóa Response (Exception Handling)

- Không bao giờ "nuốt" exception bằng khối `try { ... } catch (Exception e) {}` rỗng hoặc chỉ `e.printStackTrace()`.
- Dùng `@RestControllerAdvice` (hoặc `@ControllerAdvice`) để bắt và chuẩn hóa toàn bộ lỗi trong hệ thống thành một cấu trúc đồng nhất:
  ```json
  {
    "timestamp": "2026-09-23T21:00:00Z",
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

## 6. Bảo Mật File Upload & Lưu Trữ

- Xử lý file upload phải nằm trong Service chuyên biệt (`FileStorageService`).
- Tuyệt đối không dùng trực tiếp `file.getOriginalFilename()` để lưu vào ổ đĩa.
- Bắt buộc sinh tên file ngẫu nhiên: `UUID.randomUUID().toString() + extension`.
- Kiểm tra danh sách đuôi file cho phép (whitelist: `.jpg`, `.png`, `.webp`, `.pdf`) và giới hạn dung lượng file tối đa (File Size Limit).

---

## 7. Vệ Sinh Mã Nguồn & Kiểm Thử Tự Động (Clean Code & Testing)

1. **Zero Hardcoded & Zero Leaked Secrets**:
   - Tuyệt đối không hardcode credentials trong source code.
   - **Đặc biệt**: Không để secret thật đã bị lộ làm giá trị mặc định (fallback default) trong file properties (ví dụ: `${API_KEY:real_secret_here}` là SAI). Phải để `${API_KEY:}` rỗng hoặc bắt buộc cung cấp qua biến môi trường.
2. **Không để lại code rác**:
   - Xóa bỏ toàn bộ code cũ bị comment cả đoạn dài.
   - Xóa bỏ log thử nghiệm `System.out.println()`. Sử dụng Logger chuẩn (`SLF4J` / `log.info()`, `log.error()`).
   - Xóa bỏ hoàn toàn các câu chat hoặc comment từ AI/Chatbot trước khi hoàn thiện.
3. **Kiểm thử tự động Toàn diện (Test Suite)**:
   - **Service Tests**: Viết Unit Test với JUnit 5 & Mockito cho các luồng nghiệp vụ quan trọng (đặt hàng, tính toán chiết khấu, trừ kho).
   - **Security Tests**: Viết kiểm thử bảo vệ phân quyền với `@WithMockUser` (kiểm tra truy cập trái phép bị chặn 401/403).
   - **Webhook Tests**: Viết test giả lập webhook với MockMvc kiểm tra đầy đủ các kịch bản: thiếu secret, sai secret, sai số tiền, trùng lặp giao dịch (idempotency) và thành công.
   - **Concurrency Tests**: Viết test đa luồng thực tế với `ExecutorService` và `CountDownLatch` để kiểm chứng hệ thống không bị bán âm (overselling) và không bị race condition khi nhận webhook song song.

---

## 8. Kiểm Soát Đồng Thời & Chống Race Condition (Concurrency Control & High Load Safety)

### 8.1. Khóa Bi Quan (Pessimistic Locking) Chống Overselling
- Khi trừ tồn kho cho sản phẩm có giới hạn số lượng trong môi trường nhiều người mua cùng lúc:
  - Khai báo truy vấn khóa bi quan trong Repository:
    ```java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
    ```
  - **Lưu ý Hibernate First-Level Cache**: Nếu entity đã được tải trước đó trong transaction (ví dụ qua liên kết từ giỏ hàng), bắt buộc gọi `entityManager.refresh(product)` sau khi khóa để làm tươi dữ liệu từ database, tránh đọc tồn kho cũ trong bộ nhớ đệm (cache).
  - Sử dụng quan hệ `FetchType.LAZY` trên các association `@ManyToOne` để tránh việc vô tình tải và cache entity sớm.

### 8.2. Khóa Bản Ghi Chống Race Condition Trong Webhook
- Khi tiếp nhận Webhook thanh toán:
  - Bắt buộc khóa bản ghi đơn hàng bằng `@Lock(LockModeType.PESSIMISTIC_WRITE)` ngay đầu luồng `@Transactional`.
  - Các request đồng thời từ cổng thanh toán sẽ phải xếp hàng chờ giao dịch đầu tiên hoàn tất (commit).
  - Giao dịch thứ hai sau khi lấy được khóa sẽ đọc lại trạng thái mới nhất (`isPaid() == true`) và trả về `200 OK` an toàn, ngăn chặn việc cập nhật lặp lại và bắn thông báo trùng lặp.

### 8.3. Triết Lý Bảo Mật "Fail-Closed" (Chặn Mặc Định)
- Cấu hình phân quyền mạng/URL luôn theo nguyên tắc:
  - Khai báo rõ ràng danh sách tài nguyên và endpoint công khai (Whitelist).
  - Khóa toàn bộ các endpoint còn lại bằng `.anyRequest().authenticated()`.
  - Kích hoạt `@EnableMethodSecurity(prePostEnabled = true)` để bảo vệ bổ sung ở tầng Service với `@PreAuthorize`.

