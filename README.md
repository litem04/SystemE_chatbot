# TechGear E-Commerce & AI Assistant

[![Java CI with Maven](https://github.com/litem04/SystemE_chatbot/actions/workflows/ci.yml/badge.svg)](https://github.com/litem04/SystemE_chatbot/actions/workflows/ci.yml)

Ứng dụng thương mại điện tử thiết bị công nghệ hiện đại, xây dựng bằng Spring Boot 3 & Java 17, tích hợp trợ lý AI thông minh sử dụng LangChain4j, RAG (Retrieval-Augmented Generation) với PostgreSQL pgvector và function calling.

## Tính năng chính

- **Xác thực & Phân quyền:** Đăng ký, đăng nhập bảo mật (BCrypt), quản lý hồ sơ và phân quyền quản trị (Admin / Customer).
- **Thương mại điện tử toàn diện:** Danh mục sản phẩm, tìm kiếm, giỏ hàng, đặt hàng ACID, đánh giá sản phẩm và xuất hóa đơn PDF.
- **Thanh toán VietQR & Webhook:** Tự động tạo mã VietQR theo chuẩn Napas, webhook xử lý tự động với Constant-Time Secret Verification chống Timing Attacks và cơ chế Idempotency chống trùng lặp giao dịch.
- **Quản trị hệ thống (Admin):** Bảng điều khiển doanh thu, biểu đồ, quản trị danh mục/sản phẩm, quản lý đơn hàng và khách hàng.
- **Trợ lý ảo AI (LangChain4j):** Hỗ trợ tra cứu sản phẩm, giải đáp thông tin giỏ hàng và đơn hàng; tìm kiếm tương đồng qua vector database (PostgreSQL/pgvector) cùng cơ chế Function Calling gọi các Tool nội bộ.

## Công nghệ sử dụng

- **Backend:** Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring MVC, Thymeleaf, WebSocket.
- **Database:** PostgreSQL 16 và pgvector (Vector Database).
- **AI & RAG:** LangChain4j 0.35.0, Google Gemini API; hỗ trợ Ollama (Qwen 2.5) dự phòng cục bộ.
- **DevOps & Testing:** Docker & Docker Compose, Flyway Database Migration, JUnit 5, Mockito, GitHub Actions CI.

## Kiến trúc phần mềm

Ứng dụng áp dụng mô hình **Package-by-Feature (Modular Monolith)**:
Các package được tổ chức xung quanh các miền nghiệp vụ (Domain): `admin`, `assistant`, `cart`, `catalog`, `identity`, `order`, `payment`, `review`, `shared`. Cách tiếp cận này đảm bảo tính gắn kết cao (High Cohesion), giảm thiểu phụ thuộc chéo (Low Coupling) và sẵn sàng tách thành Microservices khi quy mô mở rộng.

```text
HTTP Request / Thymeleaf View → Controller → Domain Service → JPA Repository → PostgreSQL
                                                    └→ LangChain4j AI / pgvector RAG
                                                    └→ VietQR Webhook & WebSocket Notification
```

## Yêu cầu môi trường

- JDK 17 (cần có `JAVA_HOME` hợp lệ).
- Docker Desktop hoặc Docker Engine với Docker Compose.
- Git.
- Gemini API key (tùy chọn: dùng cho chatbot Gemini; nếu không cấu hình key, ứng dụng web và các chức năng khác vẫn hoạt động bình thường).

## Hướng dẫn chạy ứng dụng

### 1. Khởi chạy Database qua Docker Compose

Từ thư mục gốc dự án, khởi động hai database (cơ sở dữ liệu chính và vector database):

```powershell
docker compose up -d db vector-db
```

*(Trên Linux / macOS: `docker compose up -d db vector-db`)*

### 2. Khởi chạy ứng dụng Spring Boot

Chạy ứng dụng trực tiếp bằng Maven Wrapper:

**Trên Windows (PowerShell):**
```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
.\mvnw.cmd spring-boot:run
```

**Trên Linux / macOS:**
```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

Flyway sẽ tự động kích hoạt và áp dụng toàn bộ các migration (`V1` đến `V6`) khi ứng dụng khởi động.

Profile `dev` sẽ tự động khởi tạo dữ liệu mẫu cho cửa hàng và tài khoản thử nghiệm:

| Vai trò | Email đăng nhập | Mật khẩu |
|---|---|---|
| Admin | `admin@gmail.com` | `admin123` |
| Khách hàng | `user@gmail.com` | `user123` |

### 3. Truy cập hệ thống

- **Trang mua hàng (Client Store):** [http://localhost:8081](http://localhost:8081)
- **Trang quản trị (Admin):** [http://localhost:8081/admin/dashboard](http://localhost:8081/admin/dashboard)
- **Công cụ quản lý DB (Adminer):**
  ```powershell
  docker compose up -d adminer
  ```
  Truy cập tại [http://localhost:8080](http://localhost:8080).

## Cấu hình biến môi trường (Tùy chọn)

Bạn có thể thiết lập các biến môi trường trước khi chạy ứng dụng:

| Biến | Mục đích | Mặc định |
|---|---|---|
| `GEMINI_API_KEY` | API Key cho Chatbot Gemini | *(để trống, không commit key)* |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | Tài khoản SMTP gửi email | *(để trống)* |
| `WEBHOOK_SECRET` | Secret xác thực Webhook thanh toán | `dev_secret_key_change_me_in_prod` |
| `BANK_ID`, `BANK_ACCOUNT_NO`, `BANK_ACCOUNT_NAME` | Thông tin tài khoản VietQR | Demo values |

## Kiểm thử (Running Tests)

Ứng dụng bao gồm bộ kiểm thử toàn diện: Unit Tests, Concurrency Integration Tests (kiểm tra chống race condition và overselling khi nhiều luồng mua sản phẩm cuối cùng), và Security IDOR Tests.

Chạy bộ test trên Windows:
```powershell
.\mvnw.cmd test
```

Trên Linux / macOS:
```bash
./mvnw test
```

*Lưu ý: Bộ test sử dụng cấu hình database H2 In-Memory độc lập, không phụ thuộc vào Docker.*

## Cấu trúc thư mục

```text
.
├── .github/workflows/   Pipeline CI tự động kiểm thử và đóng gói (GitHub Actions)
├── src/
│   ├── main/java/com/techgear/
│   │   ├── admin/       Quản trị, thống kê doanh thu và báo cáo
│   │   ├── assistant/   Trợ lý ảo AI, RAG Vector Search, Function Calling Tools
│   │   ├── cart/        Quản lý giỏ hàng người dùng
│   │   ├── catalog/     Sản phẩm, danh mục và tìm kiếm
│   │   ├── identity/    Xác thực, phân quyền và tài khoản (Security)
│   │   ├── order/       Xử lý đơn hàng, kho hàng (Pessimistic Locking)
│   │   ├── payment/     Thanh toán VietQR, Webhook và WebSocket
│   │   ├── review/      Đánh giá sản phẩm
│   │   ├── shared/      Cấu hình chung, Exception Handler, Data Seeder, Utils
│   │   └── TechGearApplication.java
│   ├── main/resources/  Cấu hình application, Flyway Migrations, Templates Thymeleaf
│   └── test/            Bộ test Concurrency, IDOR Security, Service và Controller
├── docker-compose.yml   Docker Compose cho PostgreSQL, pgvector và Adminer
├── Dockerfile           Multi-stage build Docker image
├── pom.xml              Cấu hình Maven Dependencies & Plugins
└── README.md
```
