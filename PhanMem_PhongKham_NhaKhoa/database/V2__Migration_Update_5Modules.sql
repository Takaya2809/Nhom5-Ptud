/* =========================================================================================
   MIGRATION SCRIPT V2: BỔ SUNG CẤU TRÚC CHO 5 MODULE HOÀN THIỆN
   Hệ quản trị: SQL Server (T-SQL) - Database: QLPHONGKHAM
   Tính chất: Idempotent (Chạy nhiều lần không gây lỗi trùng lặp)
========================================================================================= */

USE QLPHONGKHAM;
GO

-- 1. Bổ sung cột nhomDichVu cho bảng DichVu
IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID('DichVu') AND name = 'nhomDichVu'
)
BEGIN
    ALTER TABLE DichVu ADD nhomDichVu NVARCHAR(50) DEFAULT N'Khám & tư vấn';
    PRINT N'[Migration] Đã bổ sung cột nhomDichVu vào bảng DichVu.';
END
GO

-- Cập nhật phân nhóm mặc định cho các dịch vụ nha khoa hiện tại nếu chưa có
UPDATE DichVu SET nhomDichVu = N'Implant' WHERE tenDichVu LIKE N'%Implant%' AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Chỉnh nha' WHERE (tenDichVu LIKE N'%Niềng răng%' OR tenDichVu LIKE N'%Chỉnh nha%') AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Phục hình sứ' WHERE (tenDichVu LIKE N'%sứ%' OR tenDichVu LIKE N'%Phục hình%') AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Nhổ răng' WHERE tenDichVu LIKE N'%Nhổ răng%' AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Trám & nội nha' WHERE (tenDichVu LIKE N'%Trám%' OR tenDichVu LIKE N'%tủy%') AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Tẩy trắng' WHERE tenDichVu LIKE N'%Tẩy trắng%' AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Nha chu' WHERE (tenDichVu LIKE N'%vôi%' OR tenDichVu LIKE N'%Nha chu%') AND (nhomDichVu IS NULL OR nhomDichVu = N'Khám & tư vấn');
UPDATE DichVu SET nhomDichVu = N'Khám & tư vấn' WHERE nhomDichVu IS NULL;
GO

-- 2. Tạo bảng LichSuGiaDichVu
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'LichSuGiaDichVu')
BEGIN
    CREATE TABLE LichSuGiaDichVu (
        maLichSu INT IDENTITY(1,1) PRIMARY KEY,
        maDichVu VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE CASCADE,
        giaCu FLOAT NOT NULL,
        giaMoi FLOAT NOT NULL,
        ngayHieuLuc DATETIME DEFAULT GETDATE(),
        nguoiDoi VARCHAR(50) NULL,
        lyDo NVARCHAR(255) NULL
    );
    CREATE NONCLUSTERED INDEX IX_LichSuGia_MaDV ON LichSuGiaDichVu(maDichVu, ngayHieuLuc DESC);
    PRINT N'[Migration] Đã tạo mới bảng LichSuGiaDichVu.';
END
GO

-- 3. Bổ sung cột trangThai cho bảng BenhNhan nếu chưa có (dùng cho soft delete)
IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID('BenhNhan') AND name = 'trangThai'
)
BEGIN
    ALTER TABLE BenhNhan ADD trangThai NVARCHAR(50) DEFAULT N'Đang theo dõi';
    PRINT N'[Migration] Đã bổ sung cột trangThai vào bảng BenhNhan.';
END
GO

-- 4. Bổ sung cột thoiGianDiemDanh cho bảng PhanCongCaTruc nếu chưa có
IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID('PhanCongCaTruc') AND name = 'thoiGianDiemDanh'
)
BEGIN
    ALTER TABLE PhanCongCaTruc ADD thoiGianDiemDanh DATETIME NULL;
    PRINT N'[Migration] Đã bổ sung cột thoiGianDiemDanh vào bảng PhanCongCaTruc.';
END
GO

-- 5. Bổ sung các cấu hình quy định ngưỡng điểm phân hạng thẻ thành viên
IF NOT EXISTS (SELECT 1 FROM QuyDinhHeThong WHERE maQuyDinh = 'QD_DIEM_VIP')
BEGIN
    INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat)
    VALUES ('QD_DIEM_VIP', N'Ngưỡng điểm tích lũy nâng hạng Thẻ VIP', '1000', 'INT', N'Tích lũy từ 1000 điểm trở lên xếp hạng VIP', GETDATE(), 'QL001');
END

IF NOT EXISTS (SELECT 1 FROM QuyDinhHeThong WHERE maQuyDinh = 'QD_DIEM_THAN_THIET')
BEGIN
    INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat)
    VALUES ('QD_DIEM_THAN_THIET', N'Ngưỡng điểm tích lũy nâng hạng Thẻ Thân thiết', '500', 'INT', N'Tích lũy từ 500 điểm đến dưới 1000 điểm', GETDATE(), 'QL001');
END

IF NOT EXISTS (SELECT 1 FROM QuyDinhHeThong WHERE maQuyDinh = 'QD_DIEM_THUONG')
BEGIN
    INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat)
    VALUES ('QD_DIEM_THUONG', N'Ngưỡng điểm tích lũy nâng hạng Thẻ Thường', '100', 'INT', N'Tích lũy từ 100 điểm đến dưới 500 điểm (dưới 100 là Khách mới)', GETDATE(), 'QL001');
END
GO

PRINT N'[Migration] Hoàn tất cập nhật Migration V2 cho QLPHONGKHAM.';
