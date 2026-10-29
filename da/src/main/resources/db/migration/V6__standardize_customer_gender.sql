-- V6: Chuẩn hóa dữ liệu giới tính (gender) trong bảng tblcustomer về tiếng Việt thống nhất
UPDATE tblcustomer
SET gender = 'Nam'
WHERE LOWER(gender) = 'male' OR gender = 'nam';

UPDATE tblcustomer
SET gender = 'Nữ'
WHERE LOWER(gender) = 'female' OR LOWER(gender) = 'nu' OR gender = 'nữ';
