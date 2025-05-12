# Cấu hình Microservices

Tài liệu này mô tả cách thiết lập và cấu hình ứng dụng microservices.

## Cấu trúc cấu hình

Dự án sử dụng mô hình phân tách cấu hình:

1. **File cấu hình ứng dụng** (`/config/{service}/application-docker.yml`)
   - Chứa cấu hình non-sensitive của ứng dụng
   - Được mount vào container qua volumes

2. **Biến môi trường nhạy cảm** (`secrets.env`)
   - Chứa thông tin nhạy cảm (credentials, secrets, etc.)
   - Được truyền vào container qua environment variables

3. **Cấu hình mạng và dependency** (`docker-compose.yml`)
   - Chứa cấu hình kết nối giữa các service
   - Thiết lập dependency và health check

## Thiết lập môi trường

### 1. Tạo file secrets.env

```bash
cp config/secrets-example.env .env
```

Chỉnh sửa file `.env` với thông tin thực tế của bạn.

### 2. Khởi động ứng dụng

```bash
docker-compose --env-file .env up -d
```

## Cấu hình cho từng service

### Eureka Server

File: `config/eureka/application-docker.yml`

Cấu hình chính:
- Port: 8761
- Self-preservation: disabled (dev environment)

### API Gateway

File: `config/gateway/application-docker.yml`

Cấu hình chính:
- Port: 8080
- Service Discovery: enabled
- Redis integration

### Auth Service

File: `config/auth/application-docker.yml`

Cấu hình chính:
- Port: 8081
- Database connection
- Email settings
- JWT configuration
- Swagger documentation

## Best Practices

1. **Không hardcode thông tin nhạy cảm**
   - Sử dụng biến môi trường từ file .env
   - Tách thông tin nhạy cảm ra khỏi cấu hình ứng dụng

2. **Tách biệt cấu hình theo môi trường**
   - Sử dụng Spring profiles (docker, dev, prod)
   - Mỗi môi trường có file cấu hình riêng

3. **Sử dụng health check**
   - Đảm bảo service phụ thuộc đã sẵn sàng trước khi khởi động service khác

4. **Quy ước đặt tên**
   - Container names: rõ ràng, mô tả chức năng
   - Volumes: đặt tên theo service sử dụng 