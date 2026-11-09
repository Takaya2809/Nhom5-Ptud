/* =========================================================================================
   HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA ABC (DENTAL CLINIC MANAGEMENT SYSTEM)
   DATABASE SCRIPT CHUẨN HÓA MỚI TOÀN DIỆN: QLPHONGKHAM_ChuanHoa.sql
   - Tối ưu chuẩn 3NF, loại bỏ hoàn toàn các trường dẫn xuất (Derived Attributes):
     + KHÔNG lưu cột 'thanhTien' và 'tongTien' trong bảng HoaDon và ChiTietHoaDon (tính toán runtime).
     + Chi tiết hóa đơn (ChiTietHoaDon / ChiTietHoaDonKham) bổ sung cột 'maNV' (Bác sĩ/Nhân viên thực hiện).
     + Mã hóa đơn (maHD) định danh dạng chuỗi đối tượng (HD001, HD002...).
     + Loại bỏ hoàn toàn bảng 'ChiTietDieuTri' (do phòng khám quản lý trực tiếp qua Dịch vụ & Hồ sơ).
     + Bảng TaiKhoan định danh bằng 'maNV', mật khẩu lưu mã băm SHA-256 tiêu chuẩn, cờ phaiDoiMatKhau.
   - Hệ quản trị CSDL: Microsoft SQL Server (T-SQL) - Database: QLPHONGKHAM
========================================================================================= */

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'QLPHONGKHAM')
BEGIN
    ALTER DATABASE QLPHONGKHAM SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QLPHONGKHAM;
END
GO

CREATE DATABASE QLPHONGKHAM
COLLATE Latin1_General_100_CI_AI_SC;
GO

USE QLPHONGKHAM;
GO

/* =========================================================================================
   PHẦN I: THIẾT KẾ CÁC BẢNG DỮ LIỆU CHUẨN HÓA
========================================================================================= */

-- 1. BẢNG VAI TRÒ (Role-based Access Control)
CREATE TABLE VaiTro (
    maVaiTro VARCHAR(20) PRIMARY KEY,
    tenVaiTro NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(255) NULL,
    danhSachQuyen NVARCHAR(MAX) NOT NULL
);
GO

-- 2. BẢNG NHÂN VIÊN (Bác sĩ, Lễ tân, Y tá, Quản lý)
CREATE TABLE NhanVien (
    maNV VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    ngayVaoLam DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang làm việc',
    chucDanh NVARCHAR(50) NOT NULL,             -- Quản lý, Nha sĩ, Lễ tân, Y tá / Phụ tá
    chuyenMon NVARCHAR(100) NULL,
    chungChi NVARCHAR(200) NULL,
    soChungChiHanhNghe VARCHAR(50) NULL,
    bangCapChungChi NVARCHAR(200) NULL
);
GO

-- 3. BẢNG TÀI KHOẢN (Định danh trực tiếp bằng maNV, Mật khẩu băm SHA-256)
CREATE TABLE TaiKhoan (
    maNV VARCHAR(20) PRIMARY KEY FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE CASCADE ON UPDATE CASCADE,
    tenDangNhap VARCHAR(50) NULL,               -- Hỗ trợ tương thích ngược (nhận maNV)
    matKhau VARCHAR(255) NOT NULL,              -- Lưu chuỗi băm SHA-256 (64 ký tự)
    matKhauHash VARCHAR(255) NULL,
    ngayTao DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Hoạt động',-- 'Hoạt động', 'Bị khóa'
    soLanSaiMatKhau INT DEFAULT 0,
    maVaiTro VARCHAR(20) NULL FOREIGN KEY REFERENCES VaiTro(maVaiTro) ON DELETE SET NULL ON UPDATE CASCADE,
    vaiTro NVARCHAR(50) NULL,
    phaiDoiMatKhau BIT DEFAULT 1                -- 1: Bắt buộc đổi mật khẩu khi đăng nhập bằng 123
);
GO

-- 4. BẢNG CA TRỰC NHA KHOA
CREATE TABLE CaTruc (
    maCa VARCHAR(50) PRIMARY KEY,
    tenCa NVARCHAR(50) NOT NULL,                -- Ca Sáng, Ca Chiều, Ca Tối
    ngayTruc DATE NOT NULL,
    gioBatDau TIME NOT NULL,
    gioKetThuc TIME NOT NULL,
    soNhaSiToiThieu INT DEFAULT 1,
    soLuongBacSiToiThieu INT DEFAULT 1,
    soLeTanToiThieu INT DEFAULT 1,
    soLuongLeTanToiThieu INT DEFAULT 1,
    soYTaToiThieu INT DEFAULT 1,
    soLuotKhamToiDa INT DEFAULT 20,
    soLuongKhamToiDa INT DEFAULT 20,
    trangThai NVARCHAR(50) DEFAULT N'Đã lên lịch'
);
GO

-- 5. BẢNG PHÂN CÔNG CA TRỰC
CREATE TABLE PhanCongCaTruc (
    maPhanCong INT IDENTITY(1,1) PRIMARY KEY,
    maNV VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION ON UPDATE CASCADE,
    maCa VARCHAR(50) NOT NULL FOREIGN KEY REFERENCES CaTruc(maCa) ON DELETE CASCADE ON UPDATE CASCADE,
    vaiTroTrongCa NVARCHAR(50) NOT NULL,
    trangThaiDiemDanh NVARCHAR(50) DEFAULT N'Chưa đến',
    thoiGianDiemDanh DATETIME NULL,
    ghiChu NVARCHAR(255) NULL,
    ngayTruc DATETIME NULL
);
GO

-- 6. BẢNG HỒ SƠ BỆNH NHÂN
CREATE TABLE BenhNhan (
    maBN VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NULL,
    diaChi NVARCHAR(255) NULL,
    tienSuBenh NVARCHAR(MAX) NULL,
    loaiKhach NVARCHAR(50) DEFAULT N'Khách mới',
    diemTichLuy INT DEFAULT 0,
    trangThai NVARCHAR(50) DEFAULT N'Đang theo dõi'
);
GO

-- 7. BẢNG DANH MỤC DỊCH VỤ NHA KHOA
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(150) NOT NULL UNIQUE,
    nhomDichVu NVARCHAR(50) DEFAULT N'Khám & tư vấn',
    giaDichVu FLOAT NOT NULL,
    giaNiemYet FLOAT NOT NULL,
    thoiGianDuKien INT DEFAULT 30,
    trangThai NVARCHAR(50) DEFAULT N'Đang cung cấp',
    moTa NVARCHAR(500) NULL
);
GO

-- 8. BẢNG LỊCH SỬ THAY ĐỔI GIÁ DỊCH VỤ
CREATE TABLE LichSuGiaDichVu (
    maLichSu INT IDENTITY(1,1) PRIMARY KEY,
    maDichVu VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE CASCADE,
    giaCu FLOAT NOT NULL,
    giaMoi FLOAT NOT NULL,
    ngayHieuLuc DATETIME DEFAULT GETDATE(),
    nguoiDoi VARCHAR(50) NULL,
    lyDo NVARCHAR(255) NULL
);
GO

-- 9. BẢNG KHO VẬT TƯ & THIẾT BỊ NHA KHOA
CREATE TABLE VatTu (
    maVatTu VARCHAR(20) PRIMARY KEY,
    tenVatTu NVARCHAR(150) NOT NULL,
    loaiVatTu NVARCHAR(50) NOT NULL,
    donViTinh NVARCHAR(30) NOT NULL,
    soLuongTon INT DEFAULT 0,
    dinhMucToiThieu INT DEFAULT 10,
    donGia FLOAT DEFAULT 0,
    trangThai NVARCHAR(50) DEFAULT N'Còn hàng'
);
GO

-- 10. BẢNG NHÀ CUNG CẤP DƯỢC & VẬT TƯ
CREATE TABLE NCC (
    maNCC VARCHAR(20) PRIMARY KEY,
    tenNCC NVARCHAR(150) NOT NULL UNIQUE,
    soDienThoai VARCHAR(15) NOT NULL,
    email VARCHAR(100) NULL,
    diaChi NVARCHAR(255) NOT NULL,
    nguoiLienHe NVARCHAR(100) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đang hợp tác'
);
GO

-- 11. BẢNG PHIẾU NHẬP KHO VẬT TƯ (KHÔNG LƯU tongTien - Tính toán runtime)
CREATE TABLE PhieuNhapKho (
    maPhieu VARCHAR(30) PRIMARY KEY,
    maNCC VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NCC(maNCC) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNVLap VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maNVDuyet VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayNhap DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(30) DEFAULT N'Đã nhập kho',
    ghiChu NVARCHAR(255) NULL
);
GO

-- 12. BẢNG CHI TIẾT PHIẾU NHẬP KHO (KHÔNG LƯU thanhTien)
CREATE TABLE ChiTietPhieuNhapKho (
    maCTPN INT IDENTITY(1,1) PRIMARY KEY,
    maPhieu VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES PhieuNhapKho(maPhieu) ON DELETE CASCADE,
    maVatTu VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES VatTu(maVatTu) ON DELETE NO ACTION,
    soLuongNhap INT NOT NULL,
    giaNhap FLOAT NOT NULL
);
GO

-- 13. BẢNG LỊCH HẸN KHÁM BỆNH
CREATE TABLE LichHen (
    maLichHen VARCHAR(30) PRIMARY KEY,
    maBN VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNhaSi VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL,
    maDichVu VARCHAR(20) NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE SET NULL,
    ngayHen DATE NOT NULL,
    gioHen TIME NOT NULL,
    lyDoKham NVARCHAR(255) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đã lên lịch',
    ngayTao DATETIME DEFAULT GETDATE()
);
GO

-- 14. BẢNG HỒ SƠ BỆNH ÁN (Quản lý lâm sàng, bỏ hoàn toàn bảng ChiTietDieuTri)
CREATE TABLE HoSoBenhAn (
    maHoSo VARCHAR(30) PRIMARY KEY,
    maBN VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNhaSi VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maLichHen VARCHAR(30) NULL FOREIGN KEY REFERENCES LichHen(maLichHen) ON DELETE SET NULL,
    ngayKham DATETIME DEFAULT GETDATE(),
    trieuChung NVARCHAR(MAX) NOT NULL,
    chanDoan NVARCHAR(MAX) NOT NULL,
    ketQuaDieuTri NVARCHAR(MAX) NULL,
    ghiChu NVARCHAR(500) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Hoàn thành',
    tinhTrangLamSang NVARCHAR(MAX) NULL,
    ketQuaCanLamSang NVARCHAR(MAX) NULL,
    viTriRang NVARCHAR(255) NULL,
    keHoachDieuTri NVARCHAR(MAX) NULL,
    noiDungCanThiep NVARCHAR(MAX) NULL,
    donThuoc NVARCHAR(MAX) NULL,
    huongDanSauDieuTri NVARCHAR(MAX) NULL,
    ngayTaiKham DATE NULL
);
GO

-- 15. BẢNG NHẬT KÝ BỆNH ÁN (Audit Trail lịch sử khám của Nha Sĩ)
CREATE TABLE NhatKyBenhAn (
    maAudit BIGINT IDENTITY(1,1) PRIMARY KEY,
    maHoSo VARCHAR(30) NOT NULL,
    maBN VARCHAR(20) NOT NULL,
    tenDangNhap VARCHAR(50) NOT NULL,
    hanhDong NVARCHAR(50) NOT NULL,
    thoiGian DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    noiDungTruoc NVARCHAR(MAX) NULL,
    noiDungSau NVARCHAR(MAX) NOT NULL
);
GO

-- 16. BẢNG THUỐC & DƯỢC PHẨM
CREATE TABLE Thuoc (
    maThuoc VARCHAR(20) PRIMARY KEY,
    tenThuoc NVARCHAR(150) NOT NULL,
    hoatChat NVARCHAR(150) NULL,
    donViTinh NVARCHAR(30) NOT NULL,
    gia FLOAT NOT NULL,
    donGia FLOAT NOT NULL,
    giaNhap FLOAT DEFAULT 0,
    giaBan FLOAT DEFAULT 0,
    soLuongTon INT DEFAULT 0,
    dinhMucToiThieu INT DEFAULT 20,
    hanSuDung DATE NULL,
    cachDungMacDinh NVARCHAR(255) NULL,
    maNCC VARCHAR(20) NULL FOREIGN KEY REFERENCES NCC(maNCC) ON DELETE SET NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đang kinh doanh'
);
GO

-- 17. BẢNG ĐƠN THUỐC (KHÔNG LƯU tongTienThuoc - Tính toán runtime)
CREATE TABLE DonThuoc (
    maDonThuoc VARCHAR(30) PRIMARY KEY,
    maHoSo VARCHAR(30) NULL UNIQUE FOREIGN KEY REFERENCES HoSoBenhAn(maHoSo) ON DELETE CASCADE,
    maNhaSi VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayLap DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    loiDan NVARCHAR(500) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đã xuất thuốc'
);
GO

-- 18. BẢNG CHI TIẾT ĐƠN THUỐC (KHÔNG LƯU thanhTien)
CREATE TABLE ChiTietDonThuoc (
    maCTDTk INT IDENTITY(1,1) PRIMARY KEY,
    maDonThuoc VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES DonThuoc(maDonThuoc) ON DELETE CASCADE,
    maThuoc VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Thuoc(maThuoc) ON DELETE NO ACTION,
    soLuong INT NOT NULL,
    donGia FLOAT NOT NULL,
    lieuDung NVARCHAR(100) NOT NULL,
    cachDung NVARCHAR(100) NOT NULL,
    soNgayDung INT DEFAULT 5
);
GO

-- 19. BẢNG CHÍNH SÁCH ƯU ĐÃI
CREATE TABLE UuDai (
    maUuDai VARCHAR(30) PRIMARY KEY,
    tenUuDai NVARCHAR(150) NOT NULL,
    loaiGiamGia NVARCHAR(50) DEFAULT N'Phần trăm',
    phanTramGiam FLOAT DEFAULT 0,
    dieuKien NVARCHAR(255) NULL,
    thoiGianBatDau DATETIME NOT NULL,
    thoiGianKetThuc DATETIME NOT NULL,
    ngayBD DATE NOT NULL,
    ngayKT DATE NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang áp dụng'
);
GO

-- 20. BẢNG HÓA ĐƠN KHÁM CHUẨN HÓA (KHÔNG LƯU tongTien / thanhTien - Tính toán runtime)
CREATE TABLE HoaDonKham (
    maHD INT IDENTITY(1,1) PRIMARY KEY,
    maHDStr VARCHAR(30) NULL,                   -- Mã định danh đối tượng (HD001, HD002...)
    ngayLap DATETIME DEFAULT GETDATE(),
    maBN VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNV VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION, -- Thu ngân / Lễ tân lập HĐ
    maDichVu VARCHAR(20) NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE SET NULL,
    maUuDai VARCHAR(30) NULL FOREIGN KEY REFERENCES UuDai(maUuDai) ON DELETE SET NULL,
    giamGia FLOAT DEFAULT 0,
    phuongThucTT NVARCHAR(50) DEFAULT N'Tiền mặt',
    trangThai NVARCHAR(50) DEFAULT N'Đã thanh toán'
);
GO

-- 21. BẢNG CHI TIẾT HÓA ĐƠN KHÁM (CÓ maNV thực hiện, KHÔNG LƯU thanhTien)
CREATE TABLE ChiTietHoaDonKham (
    maCTHD INT IDENTITY(1,1) PRIMARY KEY,
    maHD INT NOT NULL FOREIGN KEY REFERENCES HoaDonKham(maHD) ON DELETE CASCADE,
    maDichVu VARCHAR(20) NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE SET NULL,
    maThuoc VARCHAR(20) NULL FOREIGN KEY REFERENCES Thuoc(maThuoc) ON DELETE SET NULL,
    maNV VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL, -- Bác sĩ / Kỹ thuật viên trực tiếp thực hiện
    soLuong INT NOT NULL DEFAULT 1,
    donGia FLOAT NOT NULL DEFAULT 0
);
GO

-- 22. BẢNG HÓA ĐƠN TỔNG HỢP (Tương thích Class Diagram UML, KHÔNG CÓ tongTien / thanhTien)
CREATE TABLE HoaDon (
    maHD VARCHAR(30) PRIMARY KEY,
    ngayLap DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    phuongThucThanhToan NVARCHAR(50) NOT NULL DEFAULT N'Tiền mặt',
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Đã thanh toán',
    soLuong INT DEFAULT 1,
    maBN VARCHAR(20) NULL FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNV VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maUuDai VARCHAR(30) NULL FOREIGN KEY REFERENCES UuDai(maUuDai) ON DELETE SET NULL,
    maDonThuoc VARCHAR(30) NULL FOREIGN KEY REFERENCES DonThuoc(maDonThuoc) ON DELETE SET NULL,
    giamGia FLOAT DEFAULT 0
);
GO

-- 23. BẢNG CHI TIẾT HÓA ĐƠN TỔNG HỢP (CÓ maNV thực hiện, KHÔNG CÓ thanhTien)
CREATE TABLE ChiTietHoaDon (
    maCTHD INT IDENTITY(1,1) PRIMARY KEY,
    maHoaDon VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES HoaDon(maHD) ON DELETE CASCADE,
    maDichVu VARCHAR(20) NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE SET NULL,
    maThuoc VARCHAR(20) NULL FOREIGN KEY REFERENCES Thuoc(maThuoc) ON DELETE SET NULL,
    maNV VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL,
    tenDichVu NVARCHAR(150) NULL,
    soLuong INT DEFAULT 1,
    donGia FLOAT DEFAULT 0
);
GO

-- 24. BẢNG QUY ĐỊNH HỆ THỐNG
CREATE TABLE QuyDinhHeThong (
    maQuyDinh VARCHAR(50) PRIMARY KEY,
    tenQuyDinh NVARCHAR(150) NOT NULL,
    giaTri NVARCHAR(255) NOT NULL,
    kieuDuLieu VARCHAR(20) DEFAULT 'STRING',
    moTa NVARCHAR(255) NULL,
    ngayCapNhat DATETIME DEFAULT GETDATE(),
    maNVCapNhat VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL
);
GO

-- 25. BẢNG NHẬT KÝ HỆ THỐNG / AUDIT LOG
CREATE TABLE LichSuHeThong (
    maLog INT IDENTITY(1,1) PRIMARY KEY,
    thoiGian DATETIME DEFAULT GETDATE(),
    tenDangNhap VARCHAR(50) NULL,
    hanhDong NVARCHAR(100) NOT NULL,
    chiTiet NVARCHAR(500) NULL
);
GO

-- 26. BẢNG THEO DÕI KHỬ KHUẨN DỤNG CỤ (Y tá)
CREATE TABLE TheoDoiKhuKhuan (
    maDungCu VARCHAR(20) PRIMARY KEY,
    tenDungCu NVARCHAR(100) NOT NULL,
    loaiDungCu NVARCHAR(50),
    trangThai NVARCHAR(50),
    thoiGianCapNhat NVARCHAR(50)
);
GO

-- 27. BẢNG THỐNG KÊ CUỐI NGÀY & TIÊU HAO VẬT TƯ (Y tá)
CREATE TABLE ThongKeCuoiNgay (
    caTruc NVARCHAR(50) PRIMARY KEY,
    khungGio NVARCHAR(50),
    soLuongBN INT,
    vatTuTieuHao NVARCHAR(500),
    trangThai NVARCHAR(50)
);
GO

/* =========================================================================================
   PHẦN II: TẠO CÁC CHỈ MỤC TỐI ƯU HÓA HIỆU NĂNG (INDEXES)
========================================================================================= */

CREATE NONCLUSTERED INDEX IX_BenhNhan_SDT ON BenhNhan(soDienThoai);
CREATE NONCLUSTERED INDEX IX_BenhNhan_HoTen ON BenhNhan(hoTen);
CREATE NONCLUSTERED INDEX IX_LichHen_Ngay_Gio ON LichHen(ngayHen, gioHen) INCLUDE (trangThai, maBN, maNhaSi);
CREATE NONCLUSTERED INDEX IX_HoSoBenhAn_BN_Ngay ON HoSoBenhAn(maBN, ngayKham DESC);
CREATE NONCLUSTERED INDEX IX_HoaDonKham_NgayLap ON HoaDonKham(ngayLap DESC, trangThai);
CREATE NONCLUSTERED INDEX IX_VatTu_TonKho ON VatTu(soLuongTon, dinhMucToiThieu);
CREATE NONCLUSTERED INDEX IX_Thuoc_TonKho ON Thuoc(soLuongTon, dinhMucToiThieu);
CREATE NONCLUSTERED INDEX IX_TaiKhoan_Auth ON TaiKhoan(maNV, matKhau);
GO

/* =========================================================================================
   PHẦN III: TRIGGERS TỰ ĐỘNG ĐỒNG BỘ TỒN KHO VẬT TƯ & THUỐC
========================================================================================= */

CREATE OR ALTER TRIGGER trg_CapNhatTonKho_NhapVatTu
ON ChiTietPhieuNhapKho
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE vt
    SET vt.soLuongTon = vt.soLuongTon + i.soLuongNhap,
        vt.donGia = i.giaNhap,
        vt.trangThai = CASE 
            WHEN (vt.soLuongTon + i.soLuongNhap) > vt.dinhMucToiThieu THEN N'Còn hàng'
            ELSE N'Cảnh báo hết hàng'
        END
    FROM VatTu vt
    INNER JOIN inserted i ON vt.maVatTu = i.maVatTu;
END;
GO

CREATE OR ALTER TRIGGER trg_CapNhatTonKho_XuatThuoc
ON ChiTietDonThuoc
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE th
    SET th.soLuongTon = CASE WHEN th.soLuongTon >= i.soLuong THEN th.soLuongTon - i.soLuong ELSE 0 END,
        th.trangThai = CASE 
            WHEN (th.soLuongTon - i.soLuong) <= 0 THEN N'Hết hàng'
            WHEN (th.soLuongTon - i.soLuong) <= th.dinhMucToiThieu THEN N'Cảnh báo hết hàng'
            ELSE N'Đang kinh doanh'
        END
    FROM Thuoc th
    INNER JOIN inserted i ON th.maThuoc = i.maThuoc;
END;
GO

/* =========================================================================================
   PHẦN IV: CÁC VIEWS BÁO CÁO TÍNH TOÁN RUNTIME (KHÔNG DÙNG CỘT LƯU CỨNG)
========================================================================================= */

-- 1. View Chi tiết hóa đơn kèm thành tiền tính toán tự động
CREATE OR ALTER VIEW vw_ChiTietHoaDon_TinhTien
AS
SELECT 
    ct.maCTHD,
    ct.maHD,
    ct.maDichVu,
    dv.tenDichVu,
    ct.maThuoc,
    th.tenThuoc,
    ct.maNV AS maNVThucHien,
    nv.hoTen AS tenNVThucHien,
    ct.soLuong,
    ct.donGia,
    (ct.soLuong * ct.donGia) AS thanhTienTinhToan
FROM ChiTietHoaDonKham ct
LEFT JOIN DichVu dv ON ct.maDichVu = dv.maDichVu
LEFT JOIN Thuoc th ON ct.maThuoc = th.maThuoc
LEFT JOIN NhanVien nv ON ct.maNV = nv.maNV;
GO

-- 2. View Doanh thu tổng hợp theo hóa đơn (Tính toán động)
CREATE OR ALTER VIEW vw_HoaDon_TongHop
AS
SELECT 
    hd.maHD,
    hd.ngayLap,
    hd.maBN,
    bn.hoTen AS tenBenhNhan,
    hd.maNV AS maNVLap,
    nv.hoTen AS tenNVLap,
    hd.phuongThucTT,
    hd.trangThai,
    hd.giamGia,
    ISNULL(SUM(ct.soLuong * ct.donGia), 0) AS tongTienTruocGiam,
    CASE 
        WHEN ISNULL(SUM(ct.soLuong * ct.donGia), 0) - hd.giamGia > 0 
        THEN ISNULL(SUM(ct.soLuong * ct.donGia), 0) - hd.giamGia
        ELSE 0 
    END AS tongTienThucThu
FROM HoaDonKham hd
LEFT JOIN BenhNhan bn ON hd.maBN = bn.maBN
LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
LEFT JOIN ChiTietHoaDonKham ct ON hd.maHD = ct.maHD
GROUP BY hd.maHD, hd.ngayLap, hd.maBN, bn.hoTen, hd.maNV, nv.hoTen, hd.phuongThucTT, hd.trangThai, hd.giamGia;
GO

/* =========================================================================================
   PHẦN V: DỮ LIỆU MẪU ĐỒNG BỘ TOÀN DIỆN (SEED DATA)
========================================================================================= */

-- 1. BẢNG VAI TRÒ
INSERT INTO VaiTro (maVaiTro, tenVaiTro, moTa, danhSachQuyen) VALUES
('VT_QL', N'Quản lý', N'Toàn quyền quản trị, điều hành, tài chính và cấu hình hệ thống', N'ALL'),
('VT_BS', N'Nha sĩ', N'Khám, chẩn đoán lâm sàng, điều trị và kê đơn', N'KHAM,DIEUTRI,KEDON'),
('VT_LT', N'Lễ tân', N'Tiếp đón, đặt lịch hẹn, tạo hồ sơ bệnh nhân và thu viện phí', N'HOSO,LICHHEN,TIEPDON,THANHTOAN'),
('VT_YT', N'Y tá', N'Quản lý kho vật tư y tế, khử khuẩn và hỗ trợ điều trị', N'VATTU,KHUKHUAN,HOTRO');
GO

-- 2. BẢNG NHÂN VIÊN
INSERT INTO NhanVien (maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, ngayVaoLam, trangThai, chucDanh, chuyenMon, chungChi, soChungChiHanhNghe, bangCapChungChi) VALUES
('QL001', N'Trần Thị Hoàng Lan', '1985-05-15', N'Nữ', '0903123456', 'lan.tran@nhakhoaabc.vn', '2020-01-10', N'Đang làm việc', N'Quản lý', N'Quản trị Y tế', N'Chứng chỉ Quản lý Bệnh viện', NULL, N'ThS. Quản trị Y tế - ĐH Y Dược'),
('BS001', N'BS.CKI Nguyễn Minh Tuấn', '1988-08-20', N'Nam', '0912345678', 'tuan.nguyen@nhakhoaabc.vn', '2020-03-01', N'Đang làm việc', N'Nha sĩ', N'Răng Hàm Mặt & Implant', N'Chứng chỉ Cấy ghép Implant Quốc tế', '004562/BYT-CCHN', N'ĐH Y Dược TP.HCM'),
('BS002', N'ThS.BS Lê Thu Hằng', '1990-11-12', N'Nữ', '0918765432', 'hang.le@nhakhoaabc.vn', '2021-06-15', N'Đang làm việc', N'Nha sĩ', N'Chỉnh nha - Niềng răng', N'Chứng chỉ Chỉnh nha POS Hoa Kỳ', '008912/BYT-CCHN', N'ThS Răng Hàm Mặt'),
('BS003', N'BS Phạm Quốc Đạt', '1993-02-28', N'Nam', '0922334455', 'dat.pham@nhakhoaabc.vn', '2022-01-05', N'Đang làm việc', N'Nha sĩ', N'Nội nha & Tiểu phẫu răng khôn', N'Chứng chỉ Tiểu phẫu Piezotome', '012345/BYT-CCHN', N'Bác sĩ Răng Hàm Mặt'),
('LT001', N'Phạm Ngọc Trâm', '1998-04-25', N'Nữ', '0934567890', 'tram.pham@nhakhoaabc.vn', '2022-02-01', N'Đang làm việc', N'Lễ tân', N'Tiếp đón & CSKH', NULL, NULL, N'Cử nhân Quan hệ Quốc tế'),
('LT002', N'Đỗ Thùy Trang', '2000-08-14', N'Nữ', '0935123456', 'trang.do@nhakhoaabc.vn', '2023-03-01', N'Đang làm việc', N'Lễ tân', N'Tiếp đón & Đặt hẹn', NULL, NULL, N'Cử nhân Ngoại ngữ'),
('YT001', N'Nguyễn Thị Kim Oanh', '1995-12-05', N'Nữ', '0978901234', 'oanh.nguyen@nhakhoaabc.vn', '2021-08-01', N'Đang làm việc', N'Y tá', N'Phụ mổ & Kiểm soát vô trùng', N'Chứng chỉ Vô khuẩn Y tế', '034567/BYT-DD', N'Cử nhân Điều dưỡng Đa khoa'),
('YT002', N'Võ Thị Mỹ Duyên', '1999-07-20', N'Nữ', '0971239876', 'duyen.vo@nhakhoaabc.vn', '2023-04-15', N'Đang làm việc', N'Y tá', N'Quản lý kho dược & Thiết bị', N'Chứng chỉ Dược lâm sàng cơ bản', '045678/BYT-DD', N'Điều dưỡng Nha khoa');
GO

-- 3. BẢNG TÀI KHOẢN (Định danh theo maNV, Mật khẩu mẫu: 123 được băm SHA-256)
-- Chuỗi SHA-256 của '123' = 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'
INSERT INTO TaiKhoan (maNV, tenDangNhap, matKhau, matKhauHash, ngayTao, trangThai, soLanSaiMatKhau, maVaiTro, vaiTro, phaiDoiMatKhau) VALUES
('QL001', 'QL001', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_QL', N'Quản lý', 1),
('BS001', 'BS001', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_BS', N'Nha sĩ', 1),
('BS002', 'BS002', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_BS', N'Nha sĩ', 1),
('BS003', 'BS003', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_BS', N'Nha sĩ', 1),
('LT001', 'LT001', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_LT', N'Lễ tân', 1),
('LT002', 'LT002', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_LT', N'Lễ tân', 1),
('YT001', 'YT001', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_YT', N'Y tá', 1),
('YT002', 'YT002', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', GETDATE(), N'Hoạt động', 0, 'VT_YT', N'Y tá', 1);
GO

-- 4. BẢNG DỊCH VỤ NHA KHOA
INSERT INTO DichVu (maDichVu, tenDichVu, nhomDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa) VALUES
('DV001', N'Cấy ghép Implant Straumann Thụy Sĩ', N'Implant', 22000000, 22000000, 60, N'Đang cung cấp', N'Trụ Implant cao cấp tích hợp xương nhanh chóng, bảo hành trọn đời'),
('DV002', N'Niềng răng mắc cài kim loại tự buộc', N'Chỉnh nha', 30000000, 30000000, 45, N'Đang cung cấp', N'Chỉnh nha mắc cài thông minh tự động giảm lực ma sát, rút ngắn thời gian niềng'),
('DV003', N'Bọc răng toàn sứ Cercon cao cấp', N'Phục hình sứ', 4500000, 4500000, 90, N'Đang cung cấp', N'Phục hình răng sứ nguyên khối chính hãng Đức, thẩm mỹ tự nhiên'),
('DV004', N'Nhổ răng khôn Piezotome sóng siêu âm', N'Nhổ răng', 2000000, 2000000, 30, N'Đang cung cấp', N'Nhổ răng khôn không đau, không sưng xâm lấn tối thiểu bằng máy siêu âm'),
('DV005', N'Trám răng thẩm mỹ Composite Nano 3M', N'Trám & nội nha', 400000, 400000, 30, N'Đang cung cấp', N'Trám răng sâu xoang I, II bằng vật liệu Composite Nano nhập khẩu Mỹ'),
('DV006', N'Cạo vôi răng & Đánh bóng siêu âm (EMS)', N'Nha chu', 350000, 350000, 30, N'Đang cung cấp', N'Làm sạch cao răng mảng bám không đau bằng công nghệ siêu âm Thụy Sĩ'),
('DV007', N'Tẩy trắng răng công nghệ Laser Whitening', N'Tẩy trắng', 2500000, 2500000, 60, N'Đang cung cấp', N'Bật tone răng trắng sáng tức thì chỉ sau 45 phút, an toàn không ê buốt'),
('DV008', N'Điều trị tủy răng (Nội nha 1 chân)', N'Trám & nội nha', 1200000, 1200000, 60, N'Đang cung cấp', N'Điều trị tủy và hàn bít ống tủy bằng hệ thống Protaper hiện đại');
GO

-- 5. BẢNG BỆNH NHÂN
INSERT INTO BenhNhan (maBN, hoTen, ngaySinh, gioiTinh, soDienThoai, email, diaChi, tienSuBenh, loaiKhach, diemTichLuy, trangThai) VALUES
('BN001', N'Nguyễn Văn Tuấn', '1992-06-14', N'Nam', '0901234567', 'tuan.nguyen@gmail.com', N'123 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM', N'Dị ứng Penicillin', N'VIP', 1250, N'Đang theo dõi'),
('BN002', N'Trần Mai Phương', '1996-03-22', N'Nữ', '0909876543', 'phuong.tran@gmail.com', N'45 Hai Bà Trưng, P. Tân Định, Q.1, TP.HCM', N'Tiền sử cao huyết áp nhẹ', N'Thân thiết', 850, N'Đang theo dõi'),
('BN003', N'Phạm Quốc Dũng', '1984-10-09', N'Nam', '0988776655', 'dung.pham@yahoo.com', N'88 Lê Lợi, P. Bến Thành, Q.1, TP.HCM', N'Không có tiền sử bệnh lý', N'Thường', 180, N'Đang theo dõi'),
('BN004', N'Vũ Bích Ngọc', '2001-01-30', N'Nữ', '0977665544', 'ngoc.vu@hotmail.com', N'234 Cách Mạng Tháng 8, Q.3, TP.HCM', N'Răng nhạy cảm, dễ ê buốt', N'Khách mới', 20, N'Đang theo dõi');
GO

-- 6. BẢNG VẬT TƯ & THIẾT BỊ NHA KHOA
INSERT INTO VatTu (maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai) VALUES
('VT001', N'Găng tay y tế Nitrile không bột (Hộp 100 chiếc)', N'Tiêu hao', N'Hộp', 45, 10, 120000, N'Còn hàng'),
('VT002', N'Khẩu trang y tế 4 lớp kháng khuẩn (Hộp 50 chiếc)', N'Tiêu hao', N'Hộp', 60, 15, 65000, N'Còn hàng'),
('VT003', N'Thuốc tê tiêm Lidocaine 2% Septodont (Hộp 50 ống)', N'Tiêu hao', N'Hộp', 8, 10, 480000, N'Cảnh báo hết hàng'),
('VT004', N'Vật liệu trám Composite thẩm mỹ 3M Z350 XT', N'Tiêu hao', N'Tuýp', 5, 8, 850000, N'Cảnh báo hết hàng'),
('VT011', N'Trụ Implant Straumann SLActive Thụy Sĩ', N'Tiêu hao', N'Trụ', 3, 5, 12000000, N'Cảnh báo hết hàng');
GO

-- 7. BẢNG NHÀ CUNG CẤP
INSERT INTO NCC (maNCC, tenNCC, soDienThoai, email, diaChi, nguoiLienHe, trangThai) VALUES
('NCC001', N'Công ty TNHH Thiết bị Y tế 3M Việt Nam', '02838123456', 'contact@3m-dental.vn', N'Đại lộ Nguyễn Văn Linh, Q.7, TP.HCM', N'Nguyễn Hoàng Nam', N'Đang hợp tác'),
('NCC002', N'Công ty Cổ phần Dược Hậu Giang (DHG Pharma)', '02923891234', 'kinhdoanh@dhgpharma.com.vn', N'288 Bis Nguyễn Văn Cừ, Cần Thơ', N'Lê Văn Hùng', N'Đang hợp tác');
GO

-- 8. BẢNG THUỐC
INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, gia, donGia, giaNhap, giaBan, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai) VALUES
('TH001', N'Augmentin 1g (Amoxicillin/Clavulanate)', N'Amoxicillin + Clavulanate', N'Viên', 18000, 18000, 14000, 18000, 120, 30, '2027-12-31', N'Uống 1 viên x 2 lần/ngày sau ăn no', N'Đang kinh doanh'),
('TH002', N'Rodogyl (Spiramycin + Metronidazol)', N'Spiramycin + Metronidazol', N'Viên', 8500, 8500, 6500, 8500, 150, 40, '2027-10-31', N'Uống 2 viên x 2 lần/ngày sau ăn', N'Đang kinh doanh'),
('TH003', N'Efferalgan 500mg sủi (Paracetamol)', N'Paracetamol 500mg', N'Viên', 4000, 4000, 3000, 4000, 200, 50, '2028-05-30', N'Hòa tan 1 viên vào nước ấm khi đau (cách 6h)', N'Đang kinh doanh'),
('TH004', N'Ibuprofen 400mg Stada', N'Ibuprofen 400mg', N'Viên', 3000, 3000, 2200, 3000, 180, 40, '2027-08-15', N'Uống 1 viên sau ăn no khi sưng đau', N'Đang kinh doanh'),
('TH005', N'Nước súc miệng Kin Gingival 250ml', N'Chlorhexidine 0.12%', N'Chai', 135000, 135000, 110000, 135000, 25, 10, '2027-06-30', N'Súc miệng 15ml x 2 lần/ngày trong 30 giây', N'Đang kinh doanh');
GO

-- 9. BẢNG CHÍNH SÁCH ƯU ĐÃI
INSERT INTO UuDai (maUuDai, tenUuDai, loaiGiamGia, phanTramGiam, dieuKien, thoiGianBatDau, thoiGianKetThuc, ngayBD, ngayKT, trangThai) VALUES
('UD01', N'Khai xuân rạng rỡ - Nụ cười tỏa sáng', N'Phần trăm', 10, N'Áp dụng toàn bộ dịch vụ cho mọi khách hàng', '2026-10-01 00:00:00', '2026-10-31 23:59:59', '2026-10-01', '2026-10-31', N'Đang áp dụng'),
('UD02', N'Đặc quyền Bệnh nhân VIP & Thân thiết', N'Phần trăm', 15, N'Áp dụng cho khách hàng có thẻ VIP hoặc tích lũy trên 500 điểm', '2026-01-01 00:00:00', '2026-12-31 23:59:59', '2026-01-01', '2026-12-31', N'Đang áp dụng'),
('UD_SINHNHAT', N'Mừng sinh nhật khách hàng - Tri ân nụ cười', N'Phần trăm', 20, N'Áp dụng trong tháng sinh nhật của bệnh nhân', '2026-10-01 00:00:00', '2026-10-31 23:59:59', '2026-10-01', '2026-10-31', N'Đang áp dụng');
GO

-- 10. BẢNG HÓA ĐƠN KHÁM (KHÔNG LƯU tongTien / thanhTien)
INSERT INTO HoaDonKham (maHDStr, ngayLap, maBN, maNV, maDichVu, maUuDai, giamGia, phuongThucTT, trangThai) VALUES
('HD001', '2026-10-12 09:15:00', 'BN001', 'LT001', 'DV001', 'UD01', 2200000, N'Chuyển khoản QR', N'Đã thanh toán'),
('HD002', '2026-10-12 10:30:00', 'BN002', 'LT001', 'DV002', 'UD01', 3000000, N'Quẹt thẻ POS', N'Đã thanh toán'),
('HD003', '2026-10-12 14:00:00', 'BN003', 'LT002', 'DV004', NULL, 0, N'Tiền mặt', N'Đã thanh toán'),
('HD004', '2026-10-11 16:00:00', 'BN004', 'LT001', 'DV003', 'UD01', 450000, N'Chuyển khoản QR', N'Đã thanh toán');
GO

-- 11. BẢNG CHI TIẾT HÓA ĐƠN KHÁM (CÓ maNV Bác sĩ thực hiện, KHÔNG LƯU thanhTien)
INSERT INTO ChiTietHoaDonKham (maHD, maDichVu, maThuoc, maNV, soLuong, donGia) VALUES
(1, 'DV001', NULL, 'BS001', 1, 22000000),
(2, 'DV002', NULL, 'BS002', 1, 30000000),
(3, 'DV004', NULL, 'BS003', 1, 2000000),
(3, NULL, 'TH001', 'BS003', 10, 18000),
(3, NULL, 'TH004', 'BS003', 10, 3000),
(4, 'DV003', NULL, 'BS002', 1, 4500000);
GO

-- 12. BẢNG HÓA ĐƠN TỔNG HỢP & CHI TIẾT HÓA ĐƠN (Đồng bộ)
INSERT INTO HoaDon (maHD, ngayLap, phuongThucThanhToan, trangThai, soLuong, maBN, maNV, maUuDai, maDonThuoc, giamGia) VALUES
('HD001', '2026-10-12', N'Chuyển khoản QR', N'Đã thanh toán', 1, 'BN001', 'LT001', 'UD01', NULL, 2200000),
('HD002', '2026-10-12', N'Quẹt thẻ POS', N'Đã thanh toán', 1, 'BN002', 'LT001', 'UD01', NULL, 3000000),
('HD003', '2026-10-12', N'Tiền mặt', N'Đã thanh toán', 1, 'BN003', 'LT002', NULL, NULL, 0),
('HD004', '2026-10-11', N'Chuyển khoản QR', N'Đã thanh toán', 1, 'BN004', 'LT001', 'UD01', NULL, 450000);

INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, maThuoc, maNV, tenDichVu, soLuong, donGia) VALUES
('HD001', 'DV001', NULL, 'BS001', N'Cấy ghép Implant Straumann Thụy Sĩ', 1, 22000000),
('HD002', 'DV002', NULL, 'BS002', N'Niềng răng mắc cài kim loại tự buộc', 1, 30000000),
('HD003', 'DV004', NULL, 'BS003', N'Nhổ răng khôn Piezotome sóng siêu âm', 1, 2000000),
('HD004', 'DV003', NULL, 'BS002', N'Bọc răng toàn sứ Cercon cao cấp', 1, 4500000);
GO

-- 13. BẢNG QUY ĐỊNH HỆ THỐNG
INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat) VALUES
('QD_MAX_KHAM_CA', N'Số lượt khám tối đa mỗi ca cho 1 Bác sĩ', '15', 'INT', N'Giới hạn số bệnh nhân tiếp nhận để đảm bảo chất lượng khám chữa bệnh', GETDATE(), 'QL001'),
('QD_MIN_HUY_LICH', N'Thời gian báo trước tối thiểu khi hủy/dời lịch (giờ)', '2', 'INT', N'Bệnh nhân cần gọi thông báo đổi lịch hẹn trước ít nhất 2 giờ', GETDATE(), 'QL001'),
('QD_TON_KHO_CANH_BAO', N'Tỷ lệ cảnh báo định mức tồn kho tối thiểu (%)', '100', 'INT', N'Cảnh báo khi số lượng thực tế chạm hoặc dưới ngưỡng tối thiểu', GETDATE(), 'QL001'),
('QD_VAT_RATE', N'Tỷ lệ thuế giá trị gia tăng VAT (%)', '8', 'INT', N'Thuế suất VAT áp dụng khi lập hóa đơn dịch vụ', GETDATE(), 'QL001'),
('QD_DIEM_VIP', N'Ngưỡng điểm tích lũy nâng hạng Thẻ VIP', '1000', 'INT', N'Tích lũy từ 1000 điểm trở lên xếp hạng VIP', GETDATE(), 'QL001');
GO

-- 14. BẢNG THEO DÕI KHỬ KHUẨN DỤNG CỤ
INSERT INTO TheoDoiKhuKhuan (maDungCu, tenDungCu, loaiDungCu, trangThai, thoiGianCapNhat) VALUES
('DC001', N'Bộ kìm nhổ răng Inox', N'Dụng cụ phẫu thuật', N'Đã tiệt trùng (Sẵn sàng)', '01/11/2026 08:30'),
('DC002', N'Gương nha khoa & Thám trâm', N'Dụng cụ khám', N'Đang khử khuẩn (Autoclave)', '01/11/2026 11:00'),
('DC003', N'Tay khoan turbine tốc độ cao', N'Thiết bị cầm tay', N'Chưa khử khuẩn', N'Chưa xử lý');
GO

-- 15. BẢNG THỐNG KÊ CUỐI NGÀY
INSERT INTO ThongKeCuoiNgay (caTruc, khungGio, soLuongBN, vatTuTieuHao, trangThai) VALUES
(N'Ca Sáng', '08:00 - 12:00', 12, N'Găng tay y tế Nitrile (SL: 2), Khẩu trang y tế (SL: 2)', N'Đã chỉnh sửa'),
(N'Ca Chiều', '13:30 - 17:30', 15, N'Găng tay y tế Nitrile (SL: 2), Khẩu trang y tế (SL: 2)', N'Chưa gửi'),
(N'Ca Tối', '18:00 - 21:00', 8, N'Găng tay y tế Nitrile (SL: 2), Khẩu trang y tế (SL: 2)', N'Chưa gửi');
GO

-- 16. BẢNG CA TRỰC CHUẨN HÓA
INSERT INTO CaTruc (maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soLuongBacSiToiThieu, soLuongLeTanToiThieu, soLuongKhamToiDa, trangThai) VALUES
('CA_20261109_SANG', N'Ca Sáng', '2026-11-09', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261109_CHIEU', N'Ca Chiều', '2026-11-09', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261109_TOI', N'Ca Tối', '2026-11-09', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261110_SANG', N'Ca Sáng', '2026-11-10', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261110_CHIEU', N'Ca Chiều', '2026-11-10', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261110_TOI', N'Ca Tối', '2026-11-10', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261111_SANG', N'Ca Sáng', '2026-11-11', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261111_CHIEU', N'Ca Chiều', '2026-11-11', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261111_TOI', N'Ca Tối', '2026-11-11', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261112_SANG', N'Ca Sáng', '2026-11-12', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261112_CHIEU', N'Ca Chiều', '2026-11-12', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261112_TOI', N'Ca Tối', '2026-11-12', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261113_SANG', N'Ca Sáng', '2026-11-13', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261113_CHIEU', N'Ca Chiều', '2026-11-13', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261113_TOI', N'Ca Tối', '2026-11-13', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261114_SANG', N'Ca Sáng', '2026-11-14', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261114_CHIEU', N'Ca Chiều', '2026-11-14', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261114_TOI', N'Ca Tối', '2026-11-14', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261115_SANG', N'Ca Sáng', '2026-11-15', '08:00:00', '12:00:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261115_CHIEU', N'Ca Chiều', '2026-11-15', '13:30:00', '17:30:00', 1, 1, 20, N'Đã lên lịch'),
('CA_20261115_TOI', N'Ca Tối', '2026-11-15', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch');
GO

-- 17. BẢNG PHÂN CÔNG CA TRỰC
INSERT INTO PhanCongCaTruc (maNV, maCa, vaiTroTrongCa, trangThaiDiemDanh, ghiChu) VALUES
('BS001', 'CA_20261109_SANG', N'Bác sĩ chính', N'Đã điểm danh', N'Phụ trách phòng cấy ghép Implant'),
('LT001', 'CA_20261109_SANG', N'Lễ tân', N'Đã điểm danh', N'Trực quầy đón tiếp tầng 1'),
('BS002', 'CA_20261109_CHIEU', N'Bác sĩ chính', N'Chưa đến', N'Khám chỉnh nha & niềng răng'),
('BS003', 'CA_20261110_SANG', N'Bác sĩ chính', N'Chưa đến', N'Nha khoa tổng quát & điều trị tủy'),
('LT002', 'CA_20261110_SANG', N'Lễ tân', N'Chưa đến', N'Trực quầy thu ngân & thủ tục'),
('BS001', 'CA_20261111_SANG', N'Bác sĩ chính', N'Chưa đến', N'Khám định kỳ & phẫu thuật nha chu'),
('BS002', 'CA_20261112_CHIEU', N'Bác sĩ chính', N'Chưa đến', N'Chỉnh nha mắc cài sứ'),
('BS003', 'CA_20261113_SANG', N'Bác sĩ chính', N'Chưa đến', N'Tẩy trắng răng & phục hình sứ');
GO

PRINT N'=========================================================================================';
PRINT N'ĐÃ KHỞI TẠO THÀNH CÔNG DATABASE CHUẨN HÓA: QLPHONGKHAM (QLPHONGKHAM_ChuanHoa.sql)';
PRINT N'- KHÔNG CÓ cột thanhTien/tongTien trong bảng HoaDon và ChiTietHoaDon (tính toán runtime).';
PRINT N'- Chi tiết hóa đơn ĐÃ BỔ SUNG cột maNV (Bác sĩ/Nhân viên trực tiếp thực hiện).';
PRINT N'- ĐÃ LOẠI BỎ hoàn toàn bảng ChiTietDieuTri.';
PRINT N'- Bảng TaiKhoan định danh bằng maNV, mật khẩu băm SHA-256 an toàn, cờ phaiDoiMatKhau.';
PRINT N'=========================================================================================';
GO

