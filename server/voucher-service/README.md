# Voucher Service

Voucher Service là một microservice quản lý voucher/mã giảm giá trong hệ thống e-commerce.

## Tính năng

- **CRUD Operations**: Tạo, đọc, cập nhật, xóa voucher
- **Apply Voucher**: Áp dụng voucher cho đơn hàng
- **Validation**: Kiểm tra tính hợp lệ của voucher
- **Integration**: Tích hợp với Catalog Service để lấy thông tin sản phẩm
- **Security**: Bảo mật với JWT authentication

## API Endpoints

### Voucher Management
- `POST /api/v1/vouchers` - Tạo voucher mới
- `GET /api/v1/vouchers/{id}` - Lấy voucher theo ID
- `GET /api/v1/vouchers/code/{code}` - Lấy voucher theo mã
- `GET /api/v1/vouchers` - Lấy danh sách voucher (có phân trang)
- `GET /api/v1/vouchers/all` - Lấy tất cả voucher
- `PUT /api/v1/vouchers/{id}` - Cập nhật voucher
- `DELETE /api/v1/vouchers/{id}` - Xóa voucher

### Voucher Application
- `POST /api/v1/vouchers/apply` - Áp dụng voucher
- `POST /api/v1/vouchers/confirm/{voucherCode}` - Xác nhận sử dụng voucher

## Voucher Types

- **PERCENTAGE**: Giảm giá theo phần trăm
- **FIXED_AMOUNT**: Giảm giá số tiền cố định

## Configuration

Service chạy trên port `8090` và kết nối với:
- MySQL Database
- Eureka Server
- Catalog Service

## Environment Variables

- `GL_DB_URL`: Database URL
- `GL_DB_USERNAME`: Database username
- `GL_DB_PASSWORD`: Database password
- `JWT_SECRET`: JWT secret key
- `CATALOG_SERVICE_URL`: Catalog service URL 