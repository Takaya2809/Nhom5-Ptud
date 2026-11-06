-- ==========================================================================================
-- SCRIPT CẬP NHẬT CƠ SỞ DỮ LIỆU: QLPHONGKHAM
-- GIAI ĐOẠN 4: CHUẨN HÓA CSDL & LOẠI BỎ CÁC CỘT/BẢNG THỪA THEO YÊU CẦU THIẾT KẾ MỚI
-- ==========================================================================================
USE QLPHONGKHAM;
GO

-- ==========================================================================================
-- 1. BƯỚC AN TOÀN: SAO LƯU DỮ LIỆU CÁC BẢNG TRƯỚC KHI THỰC HIỆN THAY ĐỔI SCHEMA (DROP/ALTER)
-- ==========================================================================================
PRINT N'-- Đang tiến hành tạo bảng sao lưu dữ liệu an toàn...';

IF OBJECT_ID('dbo.HoaDon_Backup', 'U') IS NULL AND OBJECT_ID('dbo.HoaDon', 'U') IS NOT NULL
BEGIN
    SELECT * INTO dbo.HoaDon_Backup FROM dbo.HoaDon;
    PRINT N'-> Đã sao lưu dữ liệu bảng HoaDon vào HoaDon_Backup.';
END

IF OBJECT_ID('dbo.HoaDonKham_Backup', 'U') IS NULL AND OBJECT_ID('dbo.HoaDonKham', 'U') IS NOT NULL
BEGIN
    SELECT * INTO dbo.HoaDonKham_Backup FROM dbo.HoaDonKham;
    PRINT N'-> Đã sao lưu dữ liệu bảng HoaDonKham vào HoaDonKham_Backup.';
END

IF OBJECT_ID('dbo.ChiTietHoaDon_Backup', 'U') IS NULL AND OBJECT_ID('dbo.ChiTietHoaDon', 'U') IS NOT NULL
BEGIN
    SELECT * INTO dbo.ChiTietHoaDon_Backup FROM dbo.ChiTietHoaDon;
    PRINT N'-> Đã sao lưu dữ liệu bảng ChiTietHoaDon vào ChiTietHoaDon_Backup.';
END

IF OBJECT_ID('dbo.ChiTietDieuTri_Backup', 'U') IS NULL AND OBJECT_ID('dbo.ChiTietDieuTri', 'U') IS NOT NULL
BEGIN
    SELECT * INTO dbo.ChiTietDieuTri_Backup FROM dbo.ChiTietDieuTri;
    PRINT N'-> Đã sao lưu dữ liệu bảng ChiTietDieuTri vào ChiTietDieuTri_Backup.';
END
GO

-- ==========================================================================================
-- 2. XOÁ HOÀN TOÀN BẢNG ChiTietDieuTri (DO HỆ THỐNG KHÔNG CÓ NGHIỆP VỤ ĐIỀU TRỊ)
-- ==========================================================================================
PRINT N'-- Đang kiểm tra và loại bỏ bảng ChiTietDieuTri...';

IF OBJECT_ID('dbo.ChiTietDieuTri', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.ChiTietDieuTri;
    PRINT N'-> Đã xoá thành công bảng ChiTietDieuTri.';
END
ELSE
BEGIN
    PRINT N'-> Bảng ChiTietDieuTri không tồn tại hoặc đã được xoá trước đó.';
END
GO

-- ==========================================================================================
-- 3. LOẠI BỎ THÀNH TIỀN / TỔNG TIỀN TRONG BẢNG HÓA ĐƠN & CHI TIẾT HÓA ĐƠN
-- (Quy tắc nghiệp vụ: Thành tiền & Tổng tiền tính động bằng phương thức, không lưu CSDL)
-- ==========================================================================================
PRINT N'-- Đang loại bỏ các cột tính toán tổng tiền, thành tiền, cách dùng/liều dùng...';

-- 3.1. Bảng HoaDon
IF COL_LENGTH('dbo.HoaDon', 'tongTien') IS NOT NULL
    ALTER TABLE dbo.HoaDon DROP COLUMN tongTien;

IF COL_LENGTH('dbo.HoaDon', 'thanhTien') IS NOT NULL
    ALTER TABLE dbo.HoaDon DROP COLUMN thanhTien;

-- 3.2. Bảng HoaDonKham
IF COL_LENGTH('dbo.HoaDonKham', 'tongTien') IS NOT NULL
    ALTER TABLE dbo.HoaDonKham DROP COLUMN tongTien;

IF COL_LENGTH('dbo.HoaDonKham', 'thanhTien') IS NOT NULL
    ALTER TABLE dbo.HoaDonKham DROP COLUMN thanhTien;

-- 3.3. Bảng ChiTietHoaDon
IF COL_LENGTH('dbo.ChiTietHoaDon', 'thanhTien') IS NOT NULL
    ALTER TABLE dbo.ChiTietHoaDon DROP COLUMN thanhTien;

IF COL_LENGTH('dbo.ChiTietHoaDon', 'cachDung') IS NOT NULL
    ALTER TABLE dbo.ChiTietHoaDon DROP COLUMN cachDung;

IF COL_LENGTH('dbo.ChiTietHoaDon', 'lieuDung') IS NOT NULL
    ALTER TABLE dbo.ChiTietHoaDon DROP COLUMN lieuDung;

PRINT N'-> Đã loại bỏ các cột tính toán và thuộc tính đơn thuốc khỏi hóa đơn.';
GO

-- ==========================================================================================
-- 4. BỔ SUNG & ĐỒNG BỘ CỘT GIÁ NHẬP / GIÁ BÁN TRONG BẢNG Thuoc
-- ==========================================================================================
PRINT N'-- Đang đồng bộ cấu trúc giá nhập và giá bán cho bảng Thuoc...';

-- Thêm cột giaNhap nếu chưa có
IF COL_LENGTH('dbo.Thuoc', 'giaNhap') IS NULL
BEGIN
    ALTER TABLE dbo.Thuoc ADD giaNhap FLOAT DEFAULT 0;
    -- Đồng bộ dữ liệu cũ: nếu có cột gia thì lấy từ gia, ngược lại lấy 80% donGia
    IF COL_LENGTH('dbo.Thuoc', 'gia') IS NOT NULL
        EXEC('UPDATE dbo.Thuoc SET giaNhap = gia WHERE gia > 0;');
    ELSE IF COL_LENGTH('dbo.Thuoc', 'donGia') IS NOT NULL
        EXEC('UPDATE dbo.Thuoc SET giaNhap = donGia * 0.8 WHERE donGia > 0;');
    PRINT N'-> Đã thêm cột giaNhap vào bảng Thuoc.';
END

-- Thêm cột giaBan nếu chưa có
IF COL_LENGTH('dbo.Thuoc', 'giaBan') IS NULL
BEGIN
    ALTER TABLE dbo.Thuoc ADD giaBan FLOAT DEFAULT 0;
    -- Đồng bộ dữ liệu cũ: lấy từ donGia
    IF COL_LENGTH('dbo.Thuoc', 'donGia') IS NOT NULL
        EXEC('UPDATE dbo.Thuoc SET giaBan = donGia WHERE donGia > 0;');
    PRINT N'-> Đã thêm cột giaBan vào bảng Thuoc.';
END

-- Thêm khoá ngoại maNCC vào Thuoc nếu chưa có
IF COL_LENGTH('dbo.Thuoc', 'maNCC') IS NULL
BEGIN
    ALTER TABLE dbo.Thuoc ADD maNCC VARCHAR(20) NULL;
    IF OBJECT_ID('dbo.NCC', 'U') IS NOT NULL
        ALTER TABLE dbo.Thuoc ADD CONSTRAINT FK_Thuoc_NCC FOREIGN KEY (maNCC) REFERENCES dbo.NCC(maNCC);
    PRINT N'-> Đã thêm khoá ngoại maNCC vào bảng Thuoc.';
END
GO

-- ==========================================================================================
-- 5. TẠO BẢNG ChiTietHoaDonKham PHỤC VỤ HOADONKHAM & CHITIETHOADONKHAM
-- ==========================================================================================
PRINT N'-- Đang kiểm tra và khởi tạo bảng ChiTietHoaDonKham...';

IF OBJECT_ID('dbo.ChiTietHoaDonKham', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietHoaDonKham (
        maCTHD INT IDENTITY(1,1) PRIMARY KEY,
        maHD INT NOT NULL,
        maDichVu VARCHAR(20) NULL,
        maThuoc VARCHAR(20) NULL,
        soLuong INT NOT NULL DEFAULT 1,
        donGia FLOAT NOT NULL DEFAULT 0,
        CONSTRAINT FK_CTHDKham_HD FOREIGN KEY (maHD) REFERENCES dbo.HoaDonKham(maHD) ON DELETE CASCADE,
        CONSTRAINT FK_CTHDKham_DV FOREIGN KEY (maDichVu) REFERENCES dbo.DichVu(maDichVu),
        CONSTRAINT FK_CTHDKham_Thuoc FOREIGN KEY (maThuoc) REFERENCES dbo.Thuoc(maThuoc)
    );
    PRINT N'-> Đã tạo mới thành công bảng ChiTietHoaDonKham.';
END
ELSE
BEGIN
    PRINT N'-> Bảng ChiTietHoaDonKham đã tồn tại.';
END
GO

-- ==========================================================================================
-- 6. BỔ SUNG CỘT maDichVu VÀO BẢNG LichHen (LIÊN KẾT ĐỐI TƯỢNG DichVu)
-- ==========================================================================================
PRINT N'-- Đang kiểm tra bổ sung cột maDichVu vào bảng LichHen...';

IF COL_LENGTH('dbo.LichHen', 'maDichVu') IS NULL
BEGIN
    ALTER TABLE dbo.LichHen ADD maDichVu VARCHAR(20) NULL;
    IF OBJECT_ID('dbo.DichVu', 'U') IS NOT NULL
        ALTER TABLE dbo.LichHen ADD CONSTRAINT FK_LichHen_DichVu FOREIGN KEY (maDichVu) REFERENCES dbo.DichVu(maDichVu);
    PRINT N'-> Đã thêm thành công khoá ngoại maDichVu vào bảng LichHen.';
END
ELSE
BEGIN
    PRINT N'-> Cột maDichVu đã tồn tại trong bảng LichHen.';
END
GO

PRINT N'-- ==========================================================================================';
PRINT N'-- HOÀN TẤT CẬP NHẬT CSDL QLPHONGKHAM CHO CÁC GIAI ĐOẠN!';
PRINT N'-- ==========================================================================================';

