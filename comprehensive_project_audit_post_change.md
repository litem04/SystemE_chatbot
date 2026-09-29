# Báo Cáo Review Sau Khi Thay Đổi Source

**Ngày review:** 2026-09-28  
**Phạm vi:** Source hiện tại trong `da/src/main`, `da/src/test`, cấu hình runtime và template.  
**Mục tiêu:** Xác nhận các finding P0/P1 của báo cáo trước đã được sửa đến đâu, tìm regression và đánh giá lại mức độ production readiness.

## 1. Kết luận nhanh

Source đã tiến bộ đáng kể so với phiên bản trước. Nhóm thay đổi lần này đã xử lý phần lớn các lỗi bảo mật web và race condition quan trọng:

- Webhook không còn fallback secret.
- `transaction_id` đã có unique constraint ở Entity.
- Webhook kiểm tra payment mode.
- Webhook kiểm tra transaction ID trên order khác.
- Webhook bắt format order reference chặt hơn.
- CSRF chỉ bỏ qua webhook.
- Logout/delete/cart mutation đã chuyển sang POST.
- Order status endpoint đã kiểm tra authentication và ownership.
- Admin product đã dùng DTO thay vì bind trực tiếp Entity.
- Product DTO đã có validation annotation.
- Customer cancel và admin status update đã dùng pessimistic lock.
- Cart đã loại bỏ đoạn AJAX GET cũ.
- Seed data đã được giới hạn bằng profile `dev`.

**Điểm đánh giá tại thời điểm lập báo cáo: 7.5/10.**  
Hệ thống hiện ở mức **pre-production tốt**, có thể tiếp tục test staging nhưng chưa nên gọi là production-ready cho hệ thống thương mại điện tử có thanh toán.

## 2. Trạng thái các blocker cũ

| Finding cũ | Trạng thái | Nhận xét |
|---|---|---|
| Webhook secret fallback | Đã sửa | `@Value("${payment.webhook.secret}")`, không còn default trong controller. |
| CSRF ignore quá rộng | Đã sửa | Chỉ ignore `/api/webhook/**`. |
| Public order-status IDOR | Đã sửa một phần tốt | Endpoint yêu cầu USER và kiểm tra email owner. Vẫn nên đưa ownership vào Service thay vì controller. |
| Mutation bằng GET | Gần như đã sửa | Controller/template đã dùng POST. Cần tiếp tục scan các JS/client call. |
| Product mass assignment | Đã sửa đáng kể | Có `ProductRequestDTO` và service copy whitelist field. |
| Webhook thiếu payment-mode check | Đã sửa | Chỉ nhận order `VIETQR`. |
| Webhook reference parser quá lỏng | Đã sửa | Chỉ nhận format `THANHTOAN DH<number>`. |
| Global transaction ID replay | Đã sửa một phần | Có `findByTransactionId()` và unique Entity column, nhưng cần migration/database constraint thật. |
| Cancel order race condition | Đã sửa đáng kể | Order và product đều được lock trong flow cancel/status update. |
| DTO validation | Đã sửa một phần | DTO có annotation, nhưng AdminController chưa dùng `@Valid`. |
| Seed credential production | Đã sửa một phần | Có `@Profile("dev")`, nhưng vẫn log password mẫu. |

## 3. Findings còn tồn tại

### P0.1. Cần xác nhận database đã tạo unique index thật

[Order.java](da/src/main/java/com/da/da/entity/Order.java) hiện có:

```java
@Column(name = "transaction_id", unique = true)
private String transactionId;
```

Đây là đúng ở mức mapping, nhưng project vẫn dùng:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Không có migration chính thức đảm bảo unique index tồn tại trên database đã deploy. Nếu database cũ đã tồn tại hoặc Hibernate không alter schema như kỳ vọng, protection vẫn chưa chắc chắn.

**Khuyến nghị:** tạo migration Flyway/Liquibase:

```sql
CREATE UNIQUE INDEX ux_tblorders_transaction_id
ON tblorders(transaction_id)
WHERE transaction_id IS NOT NULL;
```

Đồng thời test hai webhook khác order nhưng cùng transaction ID trên PostgreSQL thật.

### P0.2. Webhook idempotency vẫn có race ở mức application check

Flow hiện tại:

1. Lock order A.
2. Query transaction ID toàn hệ thống.
3. Nếu không tồn tại thì save.

Với hai order khác nhau và cùng transaction ID, hai transaction vẫn có thể cùng đọc thấy chưa tồn tại. Unique index là lớp bảo vệ cuối cùng bắt buộc phải có. Service cũng nên bắt `DataIntegrityViolationException` và trả response idempotent/rejected phù hợp.

### P1.1. `ProductRequestDTO` chưa được kích hoạt validation

[ProductRequestDTO.java](da/src/main/java/com/da/da/dto/ProductRequestDTO.java) đã có `@NotBlank`, `@NotNull`, `@Min`, nhưng [AdminController.java](da/src/main/java/com/da/da/controller/AdminController.java) đang dùng:

```java
@ModelAttribute ProductRequestDTO request
```

chưa có `@Valid` và `BindingResult`.

Kết quả là annotation validation có thể không chạy ở add/update product.

**Khuyến nghị:**

```java
public String addProduct(
        @Valid @ModelAttribute ProductRequestDTO request,
        BindingResult bindingResult,
        ...)
```

và xử lý lỗi trước khi gọi service.

### P1.2. Admin vẫn có thể bỏ qua các validation nghiệp vụ chưa đầy đủ

Service đã kiểm tra giá âm và discount price, nhưng nên bổ sung:

- `mrpPrice >= price` nếu nghiệp vụ yêu cầu.
- `discountLimit >= discountSold` hoặc policy rõ ràng.
- Tên/mô tả giới hạn độ dài.
- Không cho stock cực lớn gây overflow/abuse.
- Chuẩn hóa category.

### P1.3. Seed data vẫn ghi password vào log

[DataInitializer.java](da/src/main/java/com/da/da/config/DataInitializer.java) đã có `@Profile("dev")`, nên không chạy khi production không bật profile dev. Đây là cải thiện đúng.

Tuy nhiên log vẫn chứa:

```text
admin@gmail.com / admin123
user@gmail.com / user123
```

Nên chỉ log username hoặc thông báo seed hoàn tất, không log password.

### P1.4. Ownership của order-status vẫn nằm ở Controller

[OrderController.java](da/src/main/java/com/da/da/controller/OrderController.java) trực tiếp gọi Repository rồi so sánh email. Security đúng hơn trước, nhưng business authorization nên nằm trong Service để các caller khác không quên kiểm tra.

Nên tạo:

```java
public PaymentStatus getPaymentStatusForCustomer(Integer orderId, String email)
```

### P1.5. Database config vẫn có credential fallback

[application.properties](da/src/main/resources/application.properties) vẫn có fallback:

```properties
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:user}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:password}
vector.db.user=${VECTOR_DB_USER:postgres}
vector.db.password=${VECTOR_DB_PASSWORD:postgres}
```

Các giá trị này phù hợp local Docker nhưng không nên là default trong profile production. Nên tách:

- `application-dev.properties` cho local.
- `application-prod.properties` bắt buộc environment variables.
- Fail-fast khi production thiếu DB secret.

### P2.1. Vẫn còn nhiều `findAll()` không phân trang

Các khu vực còn lại:

- [AdminAiService.java](da/src/main/java/com/da/da/service/AdminAiService.java)
- [ChatbotManager.java](da/src/main/java/com/da/da/service/ChatbotManager.java)
- [IngestionService.java](da/src/main/java/com/da/da/service/IngestionService.java)
- [AdminCustomerService.java](da/src/main/java/com/da/da/service/AdminCustomerService.java)
- [ProductService.java](da/src/main/java/com/da/da/service/ProductService.java)

Đây là vấn đề scale/performance, chưa phải blocker chức năng khi dữ liệu nhỏ.

### P2.2. RAG vẫn dùng InMemoryEmbeddingStore

[LangChainConfig.java](da/src/main/java/com/da/da/config/LangChainConfig.java) vẫn trả về `InMemoryEmbeddingStore`. Container pgvector có trong Docker nhưng chưa được dùng làm vector store persistent.

Hệ quả:

- Restart app mất embedding.
- Multi-instance không chia sẻ dữ liệu.
- Ingest lặp có thể tạo dữ liệu trùng trong memory.

### P2.3. Upload chưa có giới hạn dung lượng và content inspection

[FileStorageService.java](da/src/main/java/com/da/da/service/FileStorageService.java) đã xử lý UUID/extension/MIME, nhưng chưa thấy:

- `spring.servlet.multipart.max-file-size`.
- Magic-byte inspection.
- Image dimension limit.
- Cleanup file cũ khi update.

### P2.4. REST exception contract chưa hoàn chỉnh

[GlobalExceptionHandler.java](da/src/main/java/com/da/da/config/GlobalExceptionHandler.java) vẫn là `@ControllerAdvice` phục vụ MVC redirect. API nên có `@RestControllerAdvice` riêng để trả JSON status/error/message thống nhất.

### P2.5. AI public endpoint chưa có rate limit

`/api/ai/chat` vẫn public, chưa thấy body limit/rate limit/timeout rõ ràng. Đây là rủi ro chi phí Gemini và resource exhaustion khi chạy internet-facing.

## 4. Kiểm tra security sau thay đổi

### Đã đạt

- `SecurityConfig` chỉ ignore CSRF cho webhook.
- `/admin/**` yêu cầu ADMIN.
- User routes yêu cầu USER.
- Logout dùng POST.
- Delete customer/product dùng POST.
- Cart remove/update dùng POST.
- Order status polling yêu cầu user và owner.
- Webhook secret bắt buộc từ property.
- Webhook amount và payment mode được kiểm tra.
- Constant-time comparison vẫn được giữ.
- Session object không còn password.

### Cần test bổ sung

- Render từng form và xác nhận CSRF token thực sự được gửi.
- POST admin add/update/delete không có CSRF phải trả 403.
- Webhook với secret property thiếu không khởi động production context.
- Hai transaction khác order dùng cùng transaction ID.
- Customer A polling order của Customer B.
- Cancel cùng order đồng thời.
- Admin Product request invalid thực sự bị reject sau khi thêm `@Valid`.

## 5. Kiểm tra nghiệp vụ và concurrency

### Điểm tốt

- Checkout lock product bằng `PESSIMISTIC_WRITE`.
- Cancel/status lock order.
- Cancel lock từng product trước khi hoàn kho.
- Webhook lock order.
- Amount dùng `BigDecimal.compareTo()`.
- Order detail giữ snapshot product name/price.

### Rủi ro còn lại

- Chưa có state machine đầy đủ cho mọi chuyển trạng thái.
- `OrderStatus.fromString()` vẫn có thể fallback về `PENDING` khi input sai.
- `PaymentMode.fromString()` cần đảm bảo payment mode không hợp lệ bị reject thay vì fallback.
- Email đang gửi trong transaction; email chậm/lỗi có thể ảnh hưởng latency dù exception được catch.
- Nên dùng outbox/event queue cho email và notification khi scale.

## 6. Test/build hiện tại

`get_errors` không phát hiện lỗi diagnostics trong source.

Maven test chưa chạy lại được trong environment hiện tại vì:

```text
The JAVA_HOME environment variable is not defined correctly
```

Do đó report này không tuyên bố test suite đã pass sau thay đổi. Các report cũ trong `target/surefire-reports` chỉ là bằng chứng của lần chạy trước, không phải validation mới.

Cần chạy trên JDK 17 hoặc 21 LTS:

```powershell
$env:JAVA_HOME = "<path-to-jdk-17-or-21>"
cd da
.\mvnw.cmd clean test
```

## 7. Scorecard sau thay đổi

| Hạng mục | Điểm | Đánh giá |
|---|---:|---|
| Layering/SRP | 6.5 | DTO và service boundary tốt hơn, controller vẫn gọi repository ở một số nơi. |
| AuthN/AuthZ | 7.5 | RBAC, session safe object và CSRF đã cải thiện rõ. |
| IDOR | 7.5 | Order-status đã được bảo vệ, ownership vẫn nên nằm trong service. |
| Order/concurrency | 8.0 | Lock checkout/cancel/status/webhook tốt hơn, cần test thật. |
| Webhook/payment | 7.5 | Secret fallback và payment mode đã sửa; cần migration unique index. |
| AI/RAG | 6.0 | Fallback tốt nhưng public abuse và in-memory vector store còn tồn tại. |
| Data/JPA | 6.5 | Unique mapping đã thêm, vẫn thiếu migration/constraints/pagination. |
| Validation/upload | 6.5 | DTO validation đã có nhưng AdminController chưa kích hoạt `@Valid`. |
| Exception/API contract | 5.5 | Vẫn thiếu REST advice riêng. |
| Testing | 6.5 | Có test quan trọng nhưng chưa rerun được trong environment hiện tại. |
| DevOps | 6.0 | Secret webhook tốt hơn, DB fallback/healthcheck/migration còn thiếu. |
| **Tổng thể** | **7.5** | **Pre-production tốt, chưa production-ready.** |

## 8. Việc cần làm tiếp theo

### Bắt buộc trước staging sign-off

1. Thêm `@Valid` và `BindingResult` cho add/update product.
2. Tạo migration unique index `transaction_id`.
3. Test duplicate transaction ID trên PostgreSQL thật.
4. Xóa password khỏi log seed.
5. Tách database credential fallback sang profile dev.
6. Chuyển order-status ownership vào Service.
7. Chạy toàn bộ test trên JDK 17/21.

### Sau khi staging ổn định

1. Bổ sung multipart limits và magic-byte validation.
2. Tách REST exception handler.
3. Rate limit AI/chat/webhook.
4. Pagination/query projection cho các `findAll()`.
5. Chuyển vector store sang persistent pgvector.
6. Dùng Flyway/Liquibase thay cho `ddl-auto=update`.
7. Thêm state-transition matrix và integration test.

## 9. Kết luận cuối

Bản source hiện tại đã sửa đúng nhiều vấn đề quan trọng của lần review trước. Đặc biệt, nhóm webhook, CSRF, mutation HTTP method, ownership và concurrency đã tiến bộ rõ rệt.

Các rủi ro còn lại không còn là lỗi sơ đẳng, nhưng vẫn đủ quan trọng để ngăn kết luận production-ready: database constraint chưa được bảo đảm bằng migration, DTO validation chưa được kích hoạt ở controller, credential fallback còn ở database/vector config, AI chưa có rate limit và test chưa thể chạy lại trên môi trường hiện tại.

**Kết luận:** hệ thống hiện đạt mức **7.5/10, pre-production tốt**. Sau khi hoàn tất nhóm việc bắt buộc và chạy test thành công trên JDK được hỗ trợ, có thể nâng lên khoảng **8.2-8.5/10** cho một production launch có kiểm soát.

---

## 10. Phụ lục Final Review - 2026-09-28

Phụ lục này cập nhật trạng thái sau vòng thay đổi mới nhất, dựa trên source hiện tại thay vì chỉ dựa vào report trước.

### 10.1. Các thay đổi đã xác nhận

- `payment.webhook.secret` trong [application.properties](da/src/main/resources/application.properties) không còn fallback; production phải cung cấp `WEBHOOK_SECRET`.
- Datasource và vector database trong cấu hình mặc định đã chuyển sang bắt buộc environment variables; fallback credential chỉ còn ở [application-dev.properties](da/src/main/resources/application-dev.properties).
- CSRF chỉ ignore `/api/webhook/**`.
- Logout, cart mutation, product/customer delete và admin order mutation đều dùng POST.
- Webhook dùng `@Valid`, kiểm tra payment mode, strict order reference và transaction đã dùng ở order khác.
- `Order.transactionId` có `unique = true`.
- Customer cancel/admin status update dùng pessimistic lock cho order; hoàn kho lock từng product.
- `ProductRequestDTO` có validation và AdminController đã dùng `@Valid`/`BindingResult`.
- Multipart file size đã giới hạn 5MB/file và 15MB/request.
- MVC và REST exception handler đã tách thành [GlobalExceptionHandler.java](da/src/main/java/com/da/da/exception/GlobalExceptionHandler.java) và [GlobalRestExceptionHandler.java](da/src/main/java/com/da/da/exception/GlobalRestExceptionHandler.java).
- Cart template không còn đoạn AJAX GET cũ gọi endpoint update bằng GET.
- `DataInitializer` chỉ chạy trong profile `dev`.

### 10.2. Findings còn lại sau final review

#### P0 - Migration database chưa được chứng minh

`unique = true` mới là JPA mapping. Project vẫn dùng `spring.jpa.hibernate.ddl-auto=update`, chưa có Flyway/Liquibase migration chứng minh unique index `transaction_id` đã tồn tại trên database production.

**Cần làm:** tạo migration unique index có điều kiện `WHERE transaction_id IS NOT NULL`, kiểm tra dữ liệu duplicate trước migration và bắt `DataIntegrityViolationException` trong webhook.

#### P1 - Secret dev vẫn được ghi vào log

[DataInitializer.java](da/src/main/java/com/da/da/config/DataInitializer.java) đã được giới hạn profile đúng, nhưng vẫn log `admin123` và `user123`. Đây không phải production exposure nếu profile được cấu hình đúng, nhưng vẫn là thói quen không an toàn và dễ bị copy sang môi trường khác.

#### P1 - Validation DTO đã hoạt động nhưng chưa đầy đủ về nghiệp vụ

`ProductRequestDTO` đã được `@Valid`, tuy nhiên `@Min` cho monetary field nên được thay bằng `@DecimalMin`, đồng thời cần kiểm tra nghiệp vụ `mrpPrice`, `discountLimit`, `discountSold`, độ dài description và các giới hạn số lượng.

#### P1 - Ownership order-status còn nằm ở Controller

`OrderController` vẫn trực tiếp query order và so sánh email. Kết quả hiện tại đã an toàn cho endpoint, nhưng các caller mới có thể quên kiểm tra ownership. Nên chuyển thành method service như `getPaymentStatusForCustomer()`.

#### P2 - Production schema vẫn phụ thuộc `ddl-auto=update`

Datasource credential đã được fail-closed hơn, nhưng schema migration vẫn chưa production-grade. Cần dùng Flyway/Liquibase, đặc biệt trước khi dựa vào unique constraint transaction.

#### P2 - AI/RAG và scale

`InMemoryEmbeddingStore` vẫn được dùng; nhiều report/tool vẫn `findAll()` toàn bảng; public AI chat chưa có rate limit, request limit riêng hoặc timeout policy đầy đủ. Đây là rủi ro vận hành/chi phí, không phải lỗi compile.

#### P2 - File upload

Multipart size đã được giới hạn, nhưng chưa thấy magic-byte/content inspection, dimension limit và cleanup file cũ khi update.

#### P2 - REST error leakage

`GlobalRestExceptionHandler` đã tách đúng hướng, nhưng `IllegalArgumentException` vẫn trả `ex.getMessage()` trực tiếp. Nên dùng error code/message whitelist cho production.

### 10.3. Validation thực tế

- `get_errors` không phát hiện lỗi diagnostics trong source hiện tại.
- `mvnw clean test` chưa chạy được vì terminal không có `JAVA_HOME` hợp lệ.
- Không được tuyên bố test pass sau final change cho đến khi chạy thành công trên JDK 17 hoặc 21 LTS.
- Các surefire report cũ chỉ là bằng chứng lịch sử, không thay thế cho clean test hiện tại.

### 10.4. Scorecard final

| Hạng mục | Điểm final | Nhận xét |
|---|---:|---|
| Layering/SRP | 6.5 | DTO/service tốt hơn, một số controller vẫn truy cập repository. |
| AuthN/AuthZ/CSRF | 8.0 | CSRF scope, POST mutation, safe session và owner check đã tốt hơn. |
| IDOR | 7.5 | Order-status được bảo vệ, nhưng ownership chưa tập trung ở service. |
| Order/concurrency | 8.0 | Lock order/product đã phủ cancel/status/checkout. |
| Webhook/payment | 7.5 | Secret/payment mode/reference/transaction check tốt; thiếu migration thật. |
| AI/RAG | 6.0 | Fallback tốt nhưng in-memory store, rate limit và `findAll()` còn tồn tại. |
| Data/JPA | 6.5 | Mapping tốt hơn nhưng chưa có migration/constraint verification. |
| Validation/upload | 7.0 | DTO validation và multipart limits đã có; content inspection còn thiếu. |
| Exception/API contract | 7.0 | MVC/REST đã tách; message leakage còn lại. |
| Testing | 6.5 | Source diagnostics sạch, nhưng clean test chưa chạy được. |
| DevOps/config | 7.0 | Env fail-closed và dev profile tốt hơn; vẫn thiếu migration/healthcheck/secret manager. |
| **Tổng thể** | **7.5** | **Pre-production tốt, chưa đủ bằng chứng để gọi production-ready.** |

### 10.5. Gate trước production

1. Tạo và chạy migration unique `transaction_id` trên staging PostgreSQL.
2. Chạy test đầy đủ bằng JDK 17/21 LTS.
3. Thêm test duplicate transaction giữa hai order khác nhau.
4. Bỏ password khỏi log seed.
5. Chuyển ownership order-status vào Service.
6. Thay `@Min` monetary validation bằng `@DecimalMin`.
7. Bổ sung upload magic-byte validation và error-message whitelist.
8. Thiết lập rate limit cho AI/webhook, healthcheck và readiness.

**Kết luận final:** Các thay đổi hiện tại đã xử lý tốt phần lớn P0/P1 trước đó và không còn lỗi tĩnh được công cụ phát hiện. Tuy nhiên, vì migration transaction chưa được kiểm chứng và test suite chưa chạy lại được do thiếu Java, hệ thống nên được đánh giá là **7.5/10, ready for controlled staging, chưa ready for unrestricted production**.
