# TechGear E-Commerce & AI Assistant

Hệ thống thương mại điện tử chuyên cung cấp thiết bị công nghệ tích hợp Trợ lý ảo AI thông minh (RAG + Function Calling).

## 1. Công nghệ Sử dụng
- **Backend**: Java 17, Spring Boot 3.2.4
- **Security**: Spring Security 6 (BCrypt Password Hashing, Role-based Access Control)
- **Database**: PostgreSQL 16, pgvector (Vector Database cho RAG)
- **AI & RAG**: LangChain4j 0.35.0, Google Gemini Flash, Ollama Fallback (qwen2.5)
- **Thanh toán & Real-time**: VietQR Dynamic Payment, Webhook xác thực bảo mật, WebSocket Stomp Realtime
- **Xuất hóa đơn**: OpenPDF (PDF Invoices)
- **Containerization**: Docker, Docker Compose

## 2. Kiến trúc Hệ thống (Clean Layered Architecture)
- **Controller Layer**: Tiếp nhận HTTP requests, điều hướng view hoặc JSON response, ủy quyền toàn bộ nghiệp vụ cho tầng Service.
- **Service Layer**: Xử lý logic nghiệp vụ toàn vẹn (tính toán chiết khấu, trừ tồn kho, gửi email), kiểm soát ranh giới giao dịch `@Transactional(rollbackFor = Exception.class)`.
- **Data Layer**: Sử dụng Spring Data JPA với `BigDecimal` cho toàn bộ các trường tài chính / tiền tệ, truy vấn tổng hợp trực tiếp từ DB.
- **AI Tools**: Tích hợp công cụ Function Calling cho phép AI tra cứu sản phẩm, xem giỏ hàng và thanh toán cho đúng tài khoản đang đăng nhập (chống IDOR).

## 3. Khởi chạy Ứng dụng
```bash
# 1. Khởi động cơ sở dữ liệu PostgreSQL và pgvector qua Docker
docker-compose up -d db vector-db

# 2. Chạy ứng dụng Spring Boot
./mvnw spring-boot:run
```
