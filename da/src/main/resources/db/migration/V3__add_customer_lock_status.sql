-- V3: Thêm trường trạng thái khóa tài khoản khách hàng (ERP-style Customer Lock Status)
ALTER TABLE tblcustomer ADD COLUMN IF NOT EXISTS is_locked BOOLEAN DEFAULT FALSE;
