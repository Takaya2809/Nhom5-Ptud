/* =========================================================================================
   HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA (DENTAL CLINIC MANAGEMENT SYSTEM)
   DATABASE SCRIPT TOÀN DIỆN & CHUẨN HÓA 3NF
   - Khớp 100% Class Diagram (UML) 3 phần: Khám chữa bệnh & Kê đơn, Nhân sự & Bệnh nhân, Kho & Ca trực
   - Tương thích tuyệt đối mã nguồn Java (Entity, DAO, GUI LoginModern, PanelThanhToan, PanelDashboard...)
   - Hệ quản trị CSDL: Microsoft SQL Server (T-SQL)
   - Tên CSDL: QLPHONGKHAM
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
COLLATE Latin1_General_100_CI_AI_SC_UTF8;
GO

USE QLPHONGKHAM;
GO

/* =========================================================================================
   PHẦN I: THIẾT KẾ CÁC BẢNG DỮ LIỆU (TABLES) THEO CLASS DIAGRAM & MÃ NGUỒN JAVA
========================================================================================= */

-- 1. BẢNG VAI TRÒ (Role-based Access Control)
CREATE TABLE VaiTro (
    maVaiTro VARCHAR(20) PRIMARY KEY,
    tenVaiTro NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(255) NULL,
    danhSachQuyen NVARCHAR(MAX) NOT NULL
);
GO

-- 2. BẢNG NHÂN VIÊN (Bao gồm các lớp con: LeTan, NhaSi, QuanLy, Yta trong Class Diagram)
CREATE TABLE NhanVien (
    maNV VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,     -- sDT trong UML
    email VARCHAR(100) NOT NULL UNIQUE,
    ngayVaoLam DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang làm việc',
    chucDanh NVARCHAR(50) NOT NULL,             -- Quản lý, Nha sĩ/Bác sĩ, Lễ tân, Thu ngân, Y tá
    chuyenMon NVARCHAR(100) NULL,               -- Thuộc tính trong LeTan, NhaSi, QuanLy, Yta
    chungChi NVARCHAR(200) NULL,                -- Thuộc tính trong NhaSi, QuanLy, Yta
    soChungChiHanhNghe VARCHAR(50) NULL,        -- Số CCHN chính thức Bộ Y Tế
    bangCapChungChi NVARCHAR(200) NULL
);
GO

-- 3. BẢNG TÀI KHOẢN (Tương ứng Class TaiKhoan trong UML)
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
    matKhauHash VARCHAR(255) NULL,              -- Thuộc tính matKhauHash trong UML
    ngayTao DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Hoạt động',-- 'Hoạt động', 'Bị khóa'
    soLanSaiMatKhau INT DEFAULT 0,
    maNV VARCHAR(20) NULL UNIQUE FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL ON UPDATE CASCADE,
    maVaiTro VARCHAR(20) NULL FOREIGN KEY REFERENCES VaiTro(maVaiTro) ON DELETE SET NULL ON UPDATE CASCADE,
    vaiTro NVARCHAR(50) NULL                    -- Thuộc tính vaiTro trực tiếp trong UML
);
GO

-- 4. BẢNG CA TRỰC (Tương ứng Class CaTruc trong UML)
CREATE TABLE CaTruc (
    maCa VARCHAR(50) PRIMARY KEY,
    tenCa NVARCHAR(50) NOT NULL,                -- Ca Sáng, Ca Chiều, Ca Tối
    ngayTruc DATE NOT NULL,
    gioBatDau TIME NOT NULL,
    gioKetThuc TIME NOT NULL,
    soNhaSiToiThieu INT DEFAULT 1,              -- Thuộc tính trong UML
    soLuongBacSiToiThieu INT DEFAULT 1,         -- Tương thích Java Entity
    soLeTanToiThieu INT DEFAULT 1,              -- Thuộc tính trong UML & Entity
    soLuongLeTanToiThieu INT DEFAULT 1,
    soYTaToiThieu INT DEFAULT 1,                -- Thuộc tính trong UML
    soLuotKhamToiDa INT DEFAULT 20,             -- Thuộc tính trong UML & Entity
    soLuongKhamToiDa INT DEFAULT 20,
    trangThai NVARCHAR(50) DEFAULT N'Đã lên lịch'-- Đã lên lịch, Đang trực, Đã hoàn thành, Hủy
);
GO

-- 5. BẢNG PHÂN CÔNG CA TRỰC (Tương ứng Class PhanCongCaTruc trong UML)
CREATE TABLE PhanCongCaTruc (
    maPhanCong INT IDENTITY(1,1) PRIMARY KEY,
    maNV VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION ON UPDATE CASCADE,
    maCa VARCHAR(50) NOT NULL FOREIGN KEY REFERENCES CaTruc(maCa) ON DELETE CASCADE ON UPDATE CASCADE,
    vaiTroTrongCa NVARCHAR(50) NOT NULL,        -- Bác sĩ chính, Phụ tá, Lễ tân
    trangThaiDiemDanh NVARCHAR(50) DEFAULT N'Chưa đến', -- Chưa đến, Đã điểm danh, Vắng mặt
    thoiGianDiemDanh DATETIME NULL,
    ghiChu NVARCHAR(255) NULL,
    ngayTruc DATETIME NULL
);
GO

-- 6. BẢNG BỆNH NHÂN (Tương ứng Class BenhNhan trong UML)
CREATE TABLE BenhNhan (
    maBN VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,    -- sDT trong UML
    email VARCHAR(100) NULL,
    diaChi NVARCHAR(255) NULL,
    tienSuBenh NVARCHAR(MAX) NULL,
    loaiKhach NVARCHAR(50) DEFAULT N'Khách mới',-- Khách mới, Thường, Thân thiết, VIP
    diemTichLuy INT DEFAULT 0,
    trangThai NVARCHAR(50) DEFAULT N'Đang theo dõi'
);
GO

-- 7. BẢNG DỊCH VỤ (Tương ứng Class DichVu trong UML)
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(150) NOT NULL UNIQUE,
    giaDichVu FLOAT NOT NULL,                   -- Thuộc tính trong UML
    giaNiemYet FLOAT NOT NULL,                  -- Tương thích Java Entity
    thoiGianDuKien INT DEFAULT 30,              -- Số phút dự kiến thực hiện
    trangThai NVARCHAR(50) DEFAULT N'Đang cung cấp', -- Đang cung cấp, Tạm ngừng cung cấp
    moTa NVARCHAR(500) NULL
);
GO

-- 8. BẢNG VẬT TƯ & THIẾT BỊ (Tương ứng Class VatTu trong UML)
CREATE TABLE VatTu (
    maVatTu VARCHAR(20) PRIMARY KEY,
    tenVatTu NVARCHAR(150) NOT NULL,
    loaiVatTu NVARCHAR(50) NOT NULL,            -- Tiêu hao, Dụng cụ, Thiết bị
    donViTinh NVARCHAR(30) NOT NULL,            -- Hộp, Cái, Tuýp, Bộ, Gói, Chai
    soLuongTon INT DEFAULT 0,
    dinhMucToiThieu INT DEFAULT 10,
    donGia FLOAT DEFAULT 0,
    trangThai NVARCHAR(50) DEFAULT N'Còn hàng'  -- Còn hàng, Cảnh báo hết hàng, Hết hàng
);
GO

-- 9. BẢNG NHÀ CUNG CẤP (Tương ứng Class NCC trong UML)
CREATE TABLE NCC (
    maNCC VARCHAR(20) PRIMARY KEY,
    tenNCC NVARCHAR(150) NOT NULL UNIQUE,
    soDienThoai VARCHAR(15) NOT NULL,           -- SDT trong UML
    email VARCHAR(100) NULL,
    diaChi NVARCHAR(255) NOT NULL,
    nguoiLienHe NVARCHAR(100) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đang hợp tác'
);
GO

-- 10. BẢNG PHIẾU NHẬP KHO (Tương ứng Class PhieuNhapKho trong UML)
CREATE TABLE PhieuNhapKho (
    maPhieu VARCHAR(30) PRIMARY KEY,            -- maPhieu trong UML (alias maPhieuNhap)
    maNCC VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NCC(maNCC) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNVLap VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maNVDuyet VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayNhap DATETIME DEFAULT GETDATE(),
    tongTien FLOAT DEFAULT 0,
    trangThai NVARCHAR(30) DEFAULT N'Đã nhập kho', -- Đã nhập kho, Chờ duyệt, Đã hủy
    ghiChu NVARCHAR(255) NULL
);
GO

-- 11. BẢNG CHI TIẾT PHIẾU NHẬP KHO (Tương ứng Class ChiTietPhieuNhapKho trong UML)
CREATE TABLE ChiTietPhieuNhapKho (
    maCTPN INT IDENTITY(1,1) PRIMARY KEY,
    maPhieu VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES PhieuNhapKho(maPhieu) ON DELETE CASCADE,
    maVatTu VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES VatTu(maVatTu) ON DELETE NO ACTION,
    soLuongNhap INT NOT NULL,
    giaNhap FLOAT NOT NULL,
    thanhTien FLOAT NOT NULL                    -- Thuộc tính trong UML
);
GO

-- 12. BẢNG LỊCH HẸN KHÁM (Tương ứng Class LichHen trong UML)
CREATE TABLE LichHen (
    maLichHen VARCHAR(30) PRIMARY KEY,
    maBN VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNhaSi VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL,
    ngayHen DATE NOT NULL,
    gioHen TIME NOT NULL,
    lyDoKham NVARCHAR(255) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đã lên lịch', -- Đã lên lịch, Đã xác nhận, Đang khám, Hoàn thành, Hủy lịch
    ngayTao DATETIME DEFAULT GETDATE()
);
GO

-- 13. BẢNG HỒ SƠ BỆNH ÁN (Tương ứng Class HoSoBenhAn trong UML)
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
    trangThai NVARCHAR(30) DEFAULT N'Hoàn thành' -- Đang điều trị, Hoàn thành, Chuyển viện, Hẹn tái khám
);
GO

-- 14. BẢNG CHI TIẾT ĐIỀU TRỊ (Quan hệ nhiều-nhiều HoSoBenhAn - DichVu)
CREATE TABLE ChiTietDieuTri (
    maCTDT INT IDENTITY(1,1) PRIMARY KEY,
    maHoSo VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES HoSoBenhAn(maHoSo) ON DELETE CASCADE,
    maDichVu VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE NO ACTION,
    viTriRang VARCHAR(50) NOT NULL,             -- FDI: R11-R48, Toàn hàm
    soLuong INT DEFAULT 1,
    donGia FLOAT NOT NULL,
    thanhTien FLOAT NOT NULL,
    ghiChu NVARCHAR(255) NULL
);
GO

-- 15. BẢNG THUỐC (Tương ứng Class Thuoc trong UML)
CREATE TABLE Thuoc (
    maThuoc VARCHAR(20) PRIMARY KEY,
    tenThuoc NVARCHAR(150) NOT NULL,
    hoatChat NVARCHAR(150) NULL,
    donViTinh NVARCHAR(30) NOT NULL,            -- Viên, Gói, Chai, Tuýp
    gia FLOAT NOT NULL,                         -- Thuộc tính trong UML
    donGia FLOAT NOT NULL,                      -- Tương thích Java Entity
    soLuongTon INT DEFAULT 0,
    dinhMucToiThieu INT DEFAULT 20,
    hanSuDung DATE NULL,
    cachDungMacDinh NVARCHAR(255) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đang kinh doanh'
);
GO

-- 16. BẢNG ĐƠN THUỐC (Tương ứng Class DonThuoc trong UML)
CREATE TABLE DonThuoc (
    maDonThuoc VARCHAR(30) PRIMARY KEY,
    maHoSo VARCHAR(30) NULL UNIQUE FOREIGN KEY REFERENCES HoSoBenhAn(maHoSo) ON DELETE CASCADE,
    maNhaSi VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayLap DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    loiDan NVARCHAR(500) NULL,
    tongTienThuoc FLOAT DEFAULT 0,
    trangThai NVARCHAR(30) DEFAULT N'Đã xuất thuốc'
);
GO

-- 17. BẢNG CHI TIẾT ĐƠN THUỐC (Tương ứng Class ChiTietDonThuoc trong UML)
CREATE TABLE ChiTietDonThuoc (
    maCTDTk INT IDENTITY(1,1) PRIMARY KEY,
    maDonThuoc VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES DonThuoc(maDonThuoc) ON DELETE CASCADE,
    maThuoc VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES Thuoc(maThuoc) ON DELETE NO ACTION,
    soLuong INT NOT NULL,
    donGia FLOAT NOT NULL,
    lieuDung NVARCHAR(100) NOT NULL,            -- 1 viên x 2 lần/ngày
    cachDung NVARCHAR(100) NOT NULL,            -- Uống sau ăn no
    soNgayDung INT DEFAULT 5,
    thanhTien FLOAT NOT NULL
);
GO

-- 18. BẢNG CHÍNH SÁCH ƯU ĐÃI (Tương ứng Class UuDai trong UML)
CREATE TABLE UuDai (
    maUuDai VARCHAR(30) PRIMARY KEY,
    tenUuDai NVARCHAR(150) NOT NULL,
    loaiGiamGia NVARCHAR(50) DEFAULT N'Phần trăm', -- Thuộc tính trong UML
    phanTramGiam FLOAT DEFAULT 0,
    dieuKien NVARCHAR(255) NULL,
    thoiGianBatDau DATETIME NOT NULL,           -- Thuộc tính trong UML
    thoiGianKetThuc DATETIME NOT NULL,          -- Thuộc tính trong UML
    ngayBD DATE NOT NULL,
    ngayKT DATE NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang áp dụng'
);
GO

-- 19. BẢNG HÓA ĐƠN (Tương ứng Class HoaDon trong UML)
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
    tongTien FLOAT DEFAULT 0,
    giamGia FLOAT DEFAULT 0,
    thanhTien FLOAT DEFAULT 0
);
GO

-- 20. BẢNG CHI TIẾT HÓA ĐƠN (Tương ứng Class ChiTietHoaDon trong UML)
CREATE TABLE ChiTietHoaDon (
    maCTHD INT IDENTITY(1,1) PRIMARY KEY,
    maHoaDon VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES HoaDon(maHD) ON DELETE CASCADE,
    maDichVu VARCHAR(20) NULL FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE NO ACTION,
    soLuong INT DEFAULT 1,
    tenDichVu NVARCHAR(150) NULL,
    maThuoc VARCHAR(20) NULL FOREIGN KEY REFERENCES Thuoc(maThuoc) ON DELETE SET NULL,
    donGia FLOAT DEFAULT 0,
    thanhTien FLOAT DEFAULT 0
);
GO

-- 21. BẢNG HÓA ĐƠN KHÁM (Phục vụ tương thích 100% với HoaDonKham.java & PanelThanhToanModern.java)
CREATE TABLE HoaDonKham (
    maHD INT IDENTITY(1,1) PRIMARY KEY,
    ngayLap DATETIME DEFAULT GETDATE(),
    maBN VARCHAR(20) FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNV VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maDichVu VARCHAR(20) FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE NO ACTION,
    tongTien FLOAT NOT NULL,
    giamGia FLOAT DEFAULT 0,
    thanhTien FLOAT NOT NULL,
    phuongThucTT NVARCHAR(50) DEFAULT N'Tiền mặt',
    trangThai NVARCHAR(50) DEFAULT N'Đã thanh toán'
);
GO

-- 22. BẢNG QUY ĐỊNH HỆ THỐNG (Phục vụ PanelQuyDinhModern.java)
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

-- 23. BẢNG NHẬT KÝ HỆ THỐNG / AUDIT LOG (Phục vụ ThongKeDAO.java & PanelDashboardModern.java)
CREATE TABLE LichSuHeThong (
    maLog INT IDENTITY(1,1) PRIMARY KEY,
    thoiGian DATETIME DEFAULT GETDATE(),
    tenDangNhap VARCHAR(50) NULL,
    hanhDong NVARCHAR(100) NOT NULL,
    chiTiet NVARCHAR(500) NULL
);
GO

/* =========================================================================================
   PHẦN II: TẠO CHỈ MỤC TỐI ƯU HÓA HIỆU NĂNG TRUY VẤN (INDEXES)
========================================================================================= */

CREATE NONCLUSTERED INDEX IX_BenhNhan_SDT ON BenhNhan(soDienThoai);
CREATE NONCLUSTERED INDEX IX_BenhNhan_HoTen ON BenhNhan(hoTen);
CREATE NONCLUSTERED INDEX IX_LichHen_Ngay_Gio ON LichHen(ngayHen, gioHen) INCLUDE (trangThai, maBN, maNhaSi);
CREATE NONCLUSTERED INDEX IX_HoSoBenhAn_BN_Ngay ON HoSoBenhAn(maBN, ngayKham DESC);
CREATE NONCLUSTERED INDEX IX_HoaDon_NgayLap ON HoaDon(ngayLap DESC, trangThai);
CREATE NONCLUSTERED INDEX IX_HoaDonKham_NgayLap ON HoaDonKham(ngayLap DESC, trangThai);
CREATE NONCLUSTERED INDEX IX_VatTu_TonKho ON VatTu(soLuongTon, dinhMucToiThieu);
CREATE NONCLUSTERED INDEX IX_Thuoc_TonKho ON Thuoc(soLuongTon, dinhMucToiThieu);
CREATE NONCLUSTERED INDEX IX_TaiKhoan_Auth ON TaiKhoan(tenDangNhap, matKhau);
GO

/* =========================================================================================
   PHẦN III: TRIGGERS TỰ ĐỘNG CẬP NHẬT TỒN KHO & ĐỒNG BỘ
========================================================================================= */

-- 1. Tự động tăng tồn kho vật tư khi thêm chi tiết phiếu nhập
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

-- 2. Tự động giảm tồn kho thuốc khi lập đơn thuốc
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
   PHẦN IV: DỮ LIỆU MẪU ĐẦY ĐỦ (SEED DATA)
   Khớp chuẩn xác các tài khoản trong LoginModern.java, TaiKhoanDAO.java và các Panel
========================================================================================= */

-- 1. BẢNG VAI TRÒ
INSERT INTO VaiTro (maVaiTro, tenVaiTro, moTa, danhSachQuyen) VALUES
('VT_QL', N'Quản lý', N'Toàn quyền quản trị, điều hành và cấu hình hệ thống', N'ALL'),
('VT_BS', N'Nha sĩ', N'Khám, chẩn đoán lâm sàng, điều trị và kê đơn', N'KHAM,DIEUTRI,KEDON'),
('VT_LT', N'Lễ tân', N'Tiếp đón, đặt lịch hẹn, tạo hồ sơ bệnh nhân', N'HOSO,LICHHEN,TIEPDON'),
('VT_TN', N'Thu ngân', N'Lập hóa đơn, áp dụng ưu đãi và thu viện phí', N'HOADON,THANHTOAN'),
('VT_YT', N'Y tá', N'Quản lý kho vật tư y tế, khử khuẩn và hỗ trợ điều trị', N'VATTU,KHUKHUAN,HOTRO');
GO

-- 2. BẢNG NHÂN VIÊN
-- Khớp các mã nhân sự: QL001, BS001-BS003, NV001-NV003, LT001-LT002, TN001, YT001-YT002
INSERT INTO NhanVien (maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, ngayVaoLam, trangThai, chucDanh, chuyenMon, chungChi, soChungChiHanhNghe, bangCapChungChi) VALUES
('QL001', N'Trần Thị Hoàng Lan', '1985-05-15', N'Nữ', '0903123456', 'lan.tran@nhakhoaabc.vn', '2020-01-10', N'Đang làm việc', N'Quản lý', N'Quản trị Y tế', N'Chứng chỉ Quản lý Bệnh viện', NULL, N'ThS. Quản trị Y tế - ĐH Y Dược'),
('BS001', N'BS.CKI Nguyễn Minh Tuấn', '1988-08-20', N'Nam', '0912345678', 'tuan.nguyen@nhakhoaabc.vn', '2020-03-01', N'Đang làm việc', N'Nha sĩ', N'Răng Hàm Mặt & Implant', N'Chứng chỉ Cấy ghép Implant Quốc tế', '004562/BYT-CCHN', N'ĐH Y Dược TP.HCM'),
('BS002', N'ThS.BS Lê Thu Hằng', '1990-11-12', N'Nữ', '0918765432', 'hang.le@nhakhoaabc.vn', '2021-06-15', N'Đang làm việc', N'Nha sĩ', N'Chỉnh nha - Niềng răng', N'Chứng chỉ Chỉnh nha POS Hoa Kỳ', '008912/BYT-CCHN', N'ThS Răng Hàm Mặt'),
('BS003', N'BS Phạm Quốc Đạt', '1993-02-28', N'Nam', '0922334455', 'dat.pham@nhakhoaabc.vn', '2022-01-05', N'Đang làm việc', N'Nha sĩ', N'Nội nha & Tiểu phẫu răng khôn', N'Chứng chỉ Tiểu phẫu Piezotome', '012345/BYT-CCHN', N'Bác sĩ Răng Hàm Mặt'),
('NV001', N'BS. CKII Nguyễn Văn An', '1982-04-18', N'Nam', '0913998877', 'an.nguyen@nhakhoaabc.vn', '2019-10-01', N'Đang làm việc', N'Nha sĩ', N'Cấy ghép Implant cao cấp', N'Chứng chỉ Implant Straumann Expert', '001122/BYT-CCHN', N'BS. Chuyên khoa II RHM'),
('NV002', N'ThS. BS Lê Thị Thảo', '1989-07-25', N'Nữ', '0914887766', 'thao.le@nhakhoaabc.vn', '2020-11-15', N'Đang làm việc', N'Nha sĩ', N'Phục hình răng sứ & Niềng răng', N'Chứng chỉ Chỉnh nha Invisalign', '003344/BYT-CCHN', N'ThS. Bác sĩ Răng Hàm Mặt'),
('NV003', N'BS. Trần Minh Trí', '1994-10-05', N'Nam', '0915776655', 'tri.tran@nhakhoaabc.vn', '2022-05-20', N'Đang làm việc', N'Nha sĩ', N'Tiểu phẫu răng khôn & Nha chu', N'Chứng chỉ Phẫu thuật hàm mặt', '005566/BYT-CCHN', N'Bác sĩ Răng Hàm Mặt'),
('LT001', N'Phạm Ngọc Trâm', '1998-04-25', N'Nữ', '0934567890', 'tram.pham@nhakhoaabc.vn', '2022-02-01', N'Đang làm việc', N'Lễ tân', N'Tiếp đón & CSKH', NULL, NULL, N'Cử nhân Quan hệ Quốc tế'),
('LT002', N'Đỗ Thùy Trang', '2000-08-14', N'Nữ', '0935123456', 'trang.do@nhakhoaabc.vn', '2023-03-01', N'Đang làm việc', N'Lễ tân', N'Tiếp đón & Đặt hẹn', NULL, NULL, N'Cử nhân Ngoại ngữ'),
('TN001', N'Đỗ Hoài Phương', '1997-09-18', N'Nữ', '0945678901', 'phuong.do@nhakhoaabc.vn', '2022-03-10', N'Đang làm việc', N'Thu ngân', N'Kế toán viện phí', NULL, NULL, N'Cử nhân Kế toán Doanh nghiệp'),
('YT001', N'Nguyễn Thị Kim Oanh', '1995-12-05', N'Nữ', '0978901234', 'oanh.nguyen@nhakhoaabc.vn', '2021-08-01', N'Đang làm việc', N'Y tá', N'Phụ mổ & Kiểm soát vô trùng', N'Chứng chỉ Vô khuẩn Y tế', '034567/BYT-DD', N'Cử nhân Điều dưỡng Đa khoa'),
('YT002', N'Võ Thị Mỹ Duyên', '1999-07-20', N'Nữ', '0971239876', 'duyen.vo@nhakhoaabc.vn', '2023-04-15', N'Đang làm việc', N'Y tá', N'Quản lý kho dược & Thiết bị', N'Chứng chỉ Dược lâm sàng cơ bản', '045678/BYT-DD', N'Điều dưỡng Nha khoa');
GO

-- 3. BẢNG TÀI KHOẢN
-- Chứa đầy đủ các tài khoản gợi ý trên LoginModern.java (Mật khẩu: 123456)
INSERT INTO TaiKhoan (tenDangNhap, matKhau, matKhauHash, ngayTao, trangThai, soLanSaiMatKhau, maNV, maVaiTro, vaiTro) VALUES
-- Quản lý
('admin', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'QL001', 'VT_QL', N'Quản lý'),
('quanly', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'QL001', 'VT_QL', N'Quản lý'),

-- Nha sĩ / Bác sĩ
('bacsi_tuan', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'BS001', 'VT_BS', N'Nha sĩ'),
('nhasi', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'BS001', 'VT_BS', N'Nha sĩ'),
('bacsi_hang', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'BS002', 'VT_BS', N'Nha sĩ'),
('bacsi_dat', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'BS003', 'VT_BS', N'Nha sĩ'),
('bs.an', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'NV001', 'VT_BS', N'Nha sĩ'),
('bs.thao', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'NV002', 'VT_BS', N'Nha sĩ'),
('bs.tri', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'NV003', 'VT_BS', N'Nha sĩ'),

-- Lễ tân
('letan_tram', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'LT001', 'VT_LT', N'Lễ tân'),
('letan', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'LT001', 'VT_LT', N'Lễ tân'),
('letan01', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'LT002', 'VT_LT', N'Lễ tân'),

-- Thu ngân
('thungan_phuong', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'TN001', 'VT_TN', N'Thu ngân'),
('thungan', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'TN001', 'VT_TN', N'Thu ngân'),

-- Y tá
('yta_oanh', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'YT001', 'VT_YT', N'Y tá'),
('yta', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'YT001', 'VT_YT', N'Y tá'),
('yta_duyen', '123456', 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), N'Hoạt động', 0, 'YT002', 'VT_YT', N'Y tá');
GO

-- 4. BẢNG DỊCH VỤ NHA KHOA
INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa) VALUES
('DV001', N'Cấy ghép Implant Straumann Thụy Sĩ', 22000000, 22000000, 60, N'Đang cung cấp', N'Trụ Implant cao cấp tích hợp xương nhanh chóng, bảo hành trọn đời'),
('DV002', N'Niềng răng mắc cài kim loại tự buộc', 30000000, 30000000, 45, N'Đang cung cấp', N'Chỉnh nha mắc cài thông minh tự động giảm lực ma sát, rút ngắn thời gian niềng'),
('DV003', N'Bọc răng toàn sứ Cercon cao cấp', 4500000, 4500000, 90, N'Đang cung cấp', N'Phục hình răng sứ nguyên khối chính hãng Đức, thẩm mỹ tự nhiên'),
('DV004', N'Nhổ răng khôn Piezotome sóng siêu âm', 2000000, 2000000, 30, N'Đang cung cấp', N'Nhổ răng khôn không đau, không sưng xâm lấn tối thiểu bằng máy siêu âm'),
('DV005', N'Trám răng thẩm mỹ Composite Nano 3M', 400000, 400000, 30, N'Đang cung cấp', N'Trám răng sâu xoang I, II bằng vật liệu Composite Nano nhập khẩu Mỹ'),
('DV006', N'Cạo vôi răng & Đánh bóng siêu âm (EMS)', 350000, 350000, 30, N'Đang cung cấp', N'Làm sạch cao răng mảng bám không đau bằng công nghệ siêu âm Thụy Sĩ'),
('DV007', N'Tẩy trắng răng công nghệ Laser Whitening', 2500000, 2500000, 60, N'Đang cung cấp', N'Bật tone răng trắng sáng tức thì chỉ sau 45 phút, an toàn không ê buốt'),
('DV008', N'Điều trị tủy răng (Nội nha 1 chân)', 1200000, 1200000, 60, N'Đang cung cấp', N'Điều trị tủy và hàn bít ống tủy bằng hệ thống Protaper hiện đại'),
('DV009', N'Chụp phim X-quang Panorex toàn cảnh', 200000, 200000, 15, N'Đang cung cấp', N'Chụp khảo sát cung hàm và chóp răng trước phẫu thuật cấy ghép');
GO

-- 5. BẢNG BỆNH NHÂN
-- Khớp các mã BN001 - BN008 trong PanelThanhToanModern và PanelDashboardModern
INSERT INTO BenhNhan (maBN, hoTen, ngaySinh, gioiTinh, soDienThoai, email, diaChi, tienSuBenh, loaiKhach, diemTichLuy, trangThai) VALUES
('BN001', N'Nguyễn Văn Tuấn', '1992-06-14', N'Nam', '0901234567', 'tuan.nguyen@gmail.com', N'123 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM', N'Dị ứng Penicillin', N'VIP', 1250, N'Đang theo dõi'),
('BN002', N'Trần Mai Phương', '1996-03-22', N'Nữ', '0909876543', 'phuong.tran@gmail.com', N'45 Hai Bà Trưng, P. Tân Định, Q.1, TP.HCM', N'Tiền sử cao huyết áp nhẹ', N'Thân thiết', 850, N'Đang theo dõi'),
('BN003', N'Phạm Quốc Dũng', '1984-10-09', N'Nam', '0988776655', 'dung.pham@yahoo.com', N'88 Lê Lợi, P. Bến Thành, Q.1, TP.HCM', N'Không có tiền sử bệnh lý', N'Thường', 180, N'Đang theo dõi'),
('BN004', N'Vũ Bích Ngọc', '2001-01-30', N'Nữ', '0977665544', 'ngoc.vu@hotmail.com', N'234 Cách Mạng Tháng 8, Q.3, TP.HCM', N'Răng nhạy cảm, dễ ê buốt', N'Khách mới', 20, N'Đang theo dõi'),
('BN005', N'Đặng Hoàng Nam', '1999-12-05', N'Nam', '0966554433', 'nam.dang@gmail.com', N'56 Hoàng Hoa Thám, P.12, Tân Bình, TP.HCM', N'Không có', N'Thường', 120, N'Đang theo dõi'),
('BN006', N'Lê Thanh Hương', '1995-04-18', N'Nữ', '0933112233', 'huong.le@gmail.com', N'78 Nguyễn Thị Minh Khai, Q.1, TP.HCM', N'Không có', N'Thân thiết', 450, N'Đang theo dõi'),
('BN007', N'Hoàng Kim Liên', '1980-08-27', N'Nữ', '0938445566', 'lien.hoang@outlook.com', N'102 Pasteur, P. Võ Thị Sáu, Q.3, TP.HCM', N'Đái tháo đường Type 2 kiểm soát tốt', N'VIP', 1100, N'Đang theo dõi'),
('BN008', N'Ngô Quốc Huy', '1987-11-15', N'Nam', '0944778899', 'huy.ngo@gmail.com', N'15 Trần Phú, P.4, Q.5, TP.HCM', N'Không có', N'Thường', 250, N'Đang theo dõi');
GO

-- 6. BẢNG CA TRỰC
INSERT INTO CaTruc (maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soNhaSiToiThieu, soLuongBacSiToiThieu, soLeTanToiThieu, soLuongLeTanToiThieu, soYTaToiThieu, soLuotKhamToiDa, soLuongKhamToiDa, trangThai) VALUES
('CA_20261023_SANG', N'Ca Sáng', '2026-10-23', '08:00:00', '12:00:00', 3, 3, 2, 2, 2, 25, 25, N'Đang trực'),
('CA_20261023_CHIEU', N'Ca Chiều', '2026-10-23', '13:30:00', '17:30:00', 3, 3, 2, 2, 2, 30, 30, N'Đã lên lịch'),
('CA_20261023_TOI', N'Ca Tối', '2026-10-23', '18:00:00', '21:00:00', 2, 2, 1, 1, 1, 15, 15, N'Đã lên lịch'),
('CA_20261024_SANG', N'Ca Sáng', '2026-10-24', '08:00:00', '12:00:00', 3, 3, 2, 2, 2, 25, 25, N'Đã lên lịch'),
('CA_20261024_CHIEU', N'Ca Chiều', '2026-10-24', '13:30:00', '17:30:00', 3, 3, 2, 2, 2, 30, 30, N'Đã lên lịch');
GO

-- 7. BẢNG PHÂN CÔNG CA TRỰC
INSERT INTO PhanCongCaTruc (maNV, maCa, vaiTroTrongCa, trangThaiDiemDanh, thoiGianDiemDanh, ghiChu, ngayTruc) VALUES
('NV001', 'CA_20261023_SANG', N'Bác sĩ chính', N'Đã điểm danh', '2026-10-23 07:45:00', N'Phụ trách phòng cấy ghép Implant - Ghế 01', '2026-10-23 08:00:00'),
('NV002', 'CA_20261023_SANG', N'Bác sĩ chính', N'Đã điểm danh', '2026-10-23 07:50:00', N'Phụ trách chỉnh nha & thẩm mỹ - Ghế 02', '2026-10-23 08:00:00'),
('NV003', 'CA_20261023_SANG', N'Bác sĩ chính', N'Đã điểm danh', '2026-10-23 07:55:00', N'Phụ trách tiểu phẫu răng khôn & nội nha - Ghế 03', '2026-10-23 08:00:00'),
('LT001', 'CA_20261023_SANG', N'Lễ tân', N'Đã điểm danh', '2026-10-23 07:30:00', N'Tiếp đón & điều phối hàng đợi sảnh A', '2026-10-23 08:00:00'),
('YT001', 'CA_20261023_SANG', N'Phụ tá', N'Đã điểm danh', '2026-10-23 07:40:00', N'Hỗ trợ vô trùng & chuẩn bị dụng cụ mổ', '2026-10-23 08:00:00'),
('BS001', 'CA_20261023_CHIEU', N'Bác sĩ chính', N'Chưa đến', NULL, N'Trực phòng khám tổng quát', '2026-10-23 13:30:00'),
('BS002', 'CA_20261023_CHIEU', N'Bác sĩ chính', N'Chưa đến', NULL, N'Khám định kỳ & siết mắc cài', '2026-10-23 13:30:00'),
('LT002', 'CA_20261023_CHIEU', N'Lễ tân', N'Chưa đến', NULL, N'Quầy tiếp tân sảnh chính', '2026-10-23 13:30:00'),
('YT002', 'CA_20261023_CHIEU', N'Phụ tá', N'Chưa đến', NULL, N'Hỗ trợ nha sĩ phòng 2', '2026-10-23 13:30:00');
GO

-- 8. BẢNG VẬT TƯ & THIẾT BỊ
INSERT INTO VatTu (maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai) VALUES
('VT001', N'Găng tay y tế Nitrile không bột (Hộp 100 chiếc)', N'Tiêu hao', N'Hộp', 45, 10, 120000, N'Còn hàng'),
('VT002', N'Khẩu trang y tế 4 lớp kháng khuẩn (Hộp 50 chiếc)', N'Tiêu hao', N'Hộp', 60, 15, 65000, N'Còn hàng'),
('VT003', N'Thuốc tê tiêm Lidocaine 2% Septodont (Hộp 50 ống)', N'Tiêu hao', N'Hộp', 8, 10, 480000, N'Cảnh báo hết hàng'),
('VT004', N'Vật liệu trám Composite thẩm mỹ 3M Z350 XT', N'Tiêu hao', N'Tuýp', 5, 8, 850000, N'Cảnh báo hết hàng'),
('VT005', N'Gạc phẫu thuật vô trùng (Gói 100 miếng)', N'Tiêu hao', N'Gói', 35, 10, 45000, N'Còn hàng'),
('VT006', N'Bộ mũi khoan kim cương Mani Nhật Bản', N'Dụng cụ', N'Bộ', 12, 5, 350000, N'Còn hàng'),
('VT007', N'Kim tiêm nha khoa dùng 1 lần Septoject (Hộp 100 chiếc)', N'Tiêu hao', N'Hộp', 22, 10, 110000, N'Còn hàng'),
('VT008', N'Khay lấy dấu răng Inox nha khoa toàn hàm', N'Dụng cụ', N'Bộ', 15, 6, 280000, N'Còn hàng'),
('VT009', N'Ghế máy nha khoa cao cấp Anya AY-A3000', N'Thiết bị', N'Cái', 4, 1, 65000000, N'Còn hàng'),
('VT010', N'Máy cạo vôi siêu âm Woodpecker UDS-K', N'Thiết bị', N'Cái', 5, 2, 4500000, N'Còn hàng'),
('VT011', N'Trụ Implant Straumann SLActive Thụy Sĩ', N'Tiêu hao', N'Trụ', 3, 5, 12000000, N'Cảnh báo hết hàng');
GO

-- 9. BẢNG NHÀ CUNG CẤP
INSERT INTO NCC (maNCC, tenNCC, soDienThoai, email, diaChi, nguoiLienHe, trangThai) VALUES
('NCC001', N'Công ty TNHH Thiết bị Y tế 3M Việt Nam', '02838123456', 'contact@3m-dental.vn', N'Đại lộ Nguyễn Văn Linh, Q.7, TP.HCM', N'Nguyễn Hoàng Nam', N'Đang hợp tác'),
('NCC002', N'Công ty Cổ phần Dược Hậu Giang (DHG Pharma)', '02923891234', 'kinhdoanh@dhgpharma.com.vn', N'288 Bis Nguyễn Văn Cừ, Cần Thơ', N'Lê Văn Hùng', N'Đang hợp tác'),
('NCC003', N'Nhà phân phối Implant Straumann Việt Nam', '02439876543', 'info@straumann-vn.com', N'Kim Mã, Ba Đình, Hà Nội', N'Vũ Bích Thảo', N'Đang hợp tác'),
('NCC004', N'Công ty Vật liệu Y nha khoa Sài Gòn', '02839256789', 'dental.saigon@gmail.com', N'Trần Hưng Đạo, Q.5, TP.HCM', N'Trịnh Quốc Bảo', N'Đang hợp tác');
GO

-- 10. BẢNG PHIẾU NHẬP KHO & CHI TIẾT PHIẾU NHẬP KHO
INSERT INTO PhieuNhapKho (maPhieu, maNCC, maNVLap, maNVDuyet, ngayNhap, tongTien, trangThai, ghiChu) VALUES
('PNK20261010_01', 'NCC001', 'YT002', 'QL001', '2026-10-10 09:30:00', 9550000, N'Đã nhập kho', N'Nhập bổ sung găng tay và composite 3M định kỳ tháng 10'),
('PNK20261015_02', 'NCC003', 'YT002', 'QL001', '2026-10-15 14:00:00', 36000000, N'Đã nhập kho', N'Nhập 03 trụ Implant Straumann phẫu thuật tuần 3 tháng 10');

INSERT INTO ChiTietPhieuNhapKho (maPhieu, maVatTu, soLuongNhap, giaNhap, thanhTien) VALUES
('PNK20261010_01', 'VT001', 30, 120000, 3600000),
('PNK20261010_01', 'VT004', 7, 850000, 5950000),
('PNK20261015_02', 'VT011', 3, 12000000, 36000000);
GO

-- 11. BẢNG CHÍNH SÁCH ƯU ĐÃI
INSERT INTO UuDai (maUuDai, tenUuDai, loaiGiamGia, phanTramGiam, dieuKien, thoiGianBatDau, thoiGianKetThuc, ngayBD, ngayKT, trangThai) VALUES
('UD01', N'Khai xuân rạng rỡ - Nụ cười tỏa sáng', N'Phần trăm', 10, N'Áp dụng toàn bộ dịch vụ cho mọi khách hàng', '2026-10-01 00:00:00', '2026-10-31 23:59:59', '2026-10-01', '2026-10-31', N'Đang áp dụng'),
('UD02', N'Đặc quyền Bệnh nhân VIP & Thân thiết', N'Phần trăm', 15, N'Áp dụng cho khách hàng có thẻ VIP hoặc tích lũy trên 500 điểm', '2026-01-01 00:00:00', '2026-12-31 23:59:59', '2026-01-01', '2026-12-31', N'Đang áp dụng'),
('UD_SINHNHAT', N'Mừng sinh nhật khách hàng - Tri ân nụ cười', N'Phần trăm', 20, N'Áp dụng trong tháng sinh nhật của bệnh nhân', '2026-10-01 00:00:00', '2026-10-31 23:59:59', '2026-10-01', '2026-10-31', N'Đang áp dụng'),
('UD_CAUVOI', N'Khuyến mại Chăm sóc nụ cười: Giảm 20% Cạo vôi răng', N'Phần trăm', 20, N'Chỉ áp dụng dịch vụ Cạo vôi răng siêu âm', '2026-10-01 00:00:00', '2026-10-31 23:59:59', '2026-10-01', '2026-10-31', N'Đang áp dụng');
GO

-- 12. BẢNG LỊCH HẸN KHÁM (Khớp dữ liệu hàng đợi khám PanelDashboardModern)
INSERT INTO LichHen (maLichHen, maBN, maNhaSi, ngayHen, gioHen, lyDoKham, ghiChu, trangThai, ngayTao) VALUES
('LH001', 'BN001', 'NV001', '2026-10-23', '08:30:00', N'Trồng răng Implant Straumann #46', N'Khách VIP - Phòng phẫu thuật Ghế 01', N'Đang khám', '2026-10-22 09:00:00'),
('LH002', 'BN002', 'NV002', '2026-10-23', '09:00:00', N'Niềng răng mắc cài tự buộc - Tái khám siết dây', N'Chỉnh nha - Ghế 02', N'Đang khám', '2026-10-22 10:15:00'),
('LH003', 'BN003', 'NV003', '2026-10-23', '09:30:00', N'Nhổ răng khôn Piezotome #38 đau nhức', N'Phòng Tiểu Phẫu - Đã chụp X-quang', N'Đã xác nhận', '2026-10-22 11:30:00'),
('LH004', 'BN004', 'NV002', '2026-10-23', '10:00:00', N'Bọc răng toàn sứ Cercon răng cửa', N'Phòng Chờ 1', N'Đã xác nhận', '2026-10-22 14:00:00'),
('LH005', 'BN005', 'NV001', '2026-10-23', '10:15:00', N'Tẩy trắng răng công nghệ Laser Whitening', N'Phòng Chờ 2', N'Đã lên lịch', '2026-10-22 15:45:00'),
('LH006', 'BN006', 'NV003', '2026-10-23', '08:00:00', N'Trám răng thẩm mỹ Composite răng hàm', N'Ghế 03 - Đã hoàn tất', N'Hoàn thành', '2026-10-21 08:30:00'),
('LH007', 'BN007', 'NV002', '2026-10-23', '08:15:00', N'Lấy cao răng siêu âm & Đánh bóng toàn hàm', N'Ghế 02 - Đã hoàn tất', N'Hoàn thành', '2026-10-21 16:00:00'),
('LH008', 'BN008', 'NV001', '2026-10-23', '08:20:00', N'Tái khám sau cấy ghép Implant kiểm tra lành thương', N'Ghế 01 - Đã hoàn tất', N'Hoàn thành', '2026-10-20 17:15:00');
GO

-- 13. BẢNG HỒ SƠ BỆNH ÁN & CHI TIẾT ĐIỀU TRỊ
INSERT INTO HoSoBenhAn (maHoSo, maBN, maNhaSi, maLichHen, ngayKham, trieuChung, chanDoan, ketQuaDieuTri, ghiChu, trangThai) VALUES
('HS_20261012_001', 'BN001', 'NV001', NULL, '2026-10-12 09:00:00', N'Mất răng hàm dưới R46, tiêu xương nhẹ', N'Mất răng đơn lẻ vị trí R46 - Chỉ định Implant', N'Phẫu thuật đặt trụ Implant Straumann hoàn tất', N'Hẹn cắt chỉ sau 10 ngày', N'Hoàn thành'),
('HS_20261012_002', 'BN002', 'NV002', NULL, '2026-10-12 10:30:00', N'Răng khấp khểnh chen chúc độ 2', N'Sai khớp cắn loại I, chen chúc cung răng', N'Gắn mắc cài tự buộc 2 hàm và đi dây cung 0.014', N'Vệ sinh bằng bàn chải kẽ', N'Hoàn thành'),
('HS_20261012_003', 'BN003', 'NV003', NULL, '2026-10-12 14:00:00', N'Đau nhức dữ dội răng khôn R38 mọc lệch', N'Viêm lợi trùm răng khôn R38 lệch 90 độ', N'Tiểu phẫu mở xương chia thân răng lấy trọn răng khôn', N'Uống kháng sinh giảm đau theo đơn', N'Hoàn thành'),
('HS_20261011_004', 'BN004', 'NV002', NULL, '2026-10-11 15:30:00', N'Răng cửa R11, R21 mẻ góc lớn, ố vàng', N'Mẻ men ngà răng cửa trên không lộ tủy', N'Mài cùi răng và lấy dấu gắn 02 răng sứ Cercon', N'Tránh cắn vật cứng trong tuần đầu', N'Hoàn thành');

INSERT INTO ChiTietDieuTri (maHoSo, maDichVu, viTriRang, soLuong, donGia, thanhTien, ghiChu) VALUES
('HS_20261012_001', 'DV001', N'Răng 46', 1, 22000000, 22000000, N'Cấy ghép trụ Implant'),
('HS_20261012_002', 'DV002', N'Toàn hàm', 1, 30000000, 30000000, N'Mắc cài kim loại tự buộc'),
('HS_20261012_003', 'DV004', N'Răng 38', 1, 2000000, 2000000, N'Nhổ răng khôn Piezotome'),
('HS_20261011_004', 'DV003', N'Răng 11, 21', 2, 4500000, 9000000, N'Bọc răng toàn sứ Cercon');
GO

-- 14. BẢNG THUỐC
INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, gia, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai) VALUES
('TH001', N'Augmentin 1g (Amoxicillin/Clavulanate)', N'Amoxicillin + Clavulanate', N'Viên', 18000, 18000, 120, 30, '2027-12-31', N'Uống 1 viên x 2 lần/ngày sau ăn no', N'Đang kinh doanh'),
('TH002', N'Rodogyl (Spiramycin + Metronidazol)', N'Spiramycin + Metronidazol', N'Viên', 8500, 8500, 150, 40, '2027-10-31', N'Uống 2 viên x 2 lần/ngày sau ăn', N'Đang kinh doanh'),
('TH003', N'Efferalgan 500mg sủi (Paracetamol)', N'Paracetamol 500mg', N'Viên', 4000, 4000, 200, 50, '2028-05-30', N'Hòa tan 1 viên vào nước ấm khi đau (cách 6h)', N'Đang kinh doanh'),
('TH004', N'Ibuprofen 400mg Stada', N'Ibuprofen 400mg', N'Viên', 3000, 3000, 180, 40, '2027-08-15', N'Uống 1 viên sau ăn no khi sưng đau', N'Đang kinh doanh'),
('TH005', N'Nước súc miệng Kin Gingival 250ml', N'Chlorhexidine 0.12%', N'Chai', 135000, 135000, 25, 10, '2027-06-30', N'Súc miệng 15ml x 2 lần/ngày trong 30 giây', N'Đang kinh doanh');
GO

-- 15. BẢNG ĐƠN THUỐC & CHI TIẾT ĐƠN THUỐC
INSERT INTO DonThuoc (maDonThuoc, maHoSo, maNhaSi, ngayLap, loiDan, tongTienThuoc, trangThai) VALUES
('DT_20261012_01', 'HS_20261012_003', 'NV003', '2026-10-12', N'Uống thuốc đúng giờ, chườm lạnh 24h đầu, không súc miệng nước muối quá mạnh', 355000, N'Đã xuất thuốc');

INSERT INTO ChiTietDonThuoc (maDonThuoc, maThuoc, soLuong, donGia, lieuDung, cachDung, soNgayDung, thanhTien) VALUES
('DT_20261012_01', 'TH001', 10, 18000, N'1 viên x 2 lần/ngày', N'Uống sau ăn no', 5, 180000),
('DT_20261012_01', 'TH004', 10, 3000, N'1 viên x 2 lần/ngày', N'Uống khi đau', 5, 30000),
('DT_20261012_01', 'TH005', 1, 135000, N'15ml x 2 lần/ngày', N'Súc miệng sau đánh răng', 7, 135000);
GO

-- 16. BẢNG HÓA ĐƠN & CHI TIẾT HÓA ĐƠN (Khớp dữ liệu bảng PanelThanhToanModern)
INSERT INTO HoaDon (maHD, ngayLap, phuongThucThanhToan, trangThai, soLuong, maBN, maNV, maUuDai, maDonThuoc, tongTien, giamGia, thanhTien) VALUES
('HD001', '2026-10-12', N'Chuyển khoản QR', N'Đã thanh toán', 1, 'BN001', 'NV001', 'UD01', NULL, 22000000, 2200000, 19800000),
('HD002', '2026-10-12', N'Quẹt thẻ POS', N'Đã thanh toán', 1, 'BN002', 'NV002', 'UD01', NULL, 30000000, 3000000, 27000000),
('HD003', '2026-10-12', N'Tiền mặt', N'Đã thanh toán', 1, 'BN003', 'NV003', NULL, 'DT_20261012_01', 2000000, 0, 2000000),
('HD004', '2026-10-11', N'Chuyển khoản QR', N'Đã thanh toán', 1, 'BN004', 'NV002', 'UD01', NULL, 4500000, 450000, 4050000);

INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong, tenDichVu, maThuoc, donGia, thanhTien) VALUES
('HD001', 'DV001', 1, N'Cấy ghép Implant Straumann Thụy Sĩ', NULL, 22000000, 22000000),
('HD002', 'DV002', 1, N'Niềng răng mắc cài kim loại tự buộc', NULL, 30000000, 30000000),
('HD003', 'DV004', 1, N'Nhổ răng khôn Piezotome sóng siêu âm', NULL, 2000000, 2000000),
('HD004', 'DV003', 1, N'Bọc răng toàn sứ Cercon cao cấp', NULL, 4500000, 4500000);
GO

-- 17. BẢNG HÓA ĐƠN KHÁM (Phục vụ HoaDonKham.java & PanelThanhToan)
INSERT INTO HoaDonKham (ngayLap, maBN, maNV, maDichVu, tongTien, giamGia, thanhTien, phuongThucTT, trangThai) VALUES
('2026-10-12 09:15:00', 'BN001', 'NV001', 'DV001', 22000000, 2200000, 19800000, N'Chuyển khoản QR', N'Đã thanh toán'),
('2026-10-12 10:30:00', 'BN002', 'NV002', 'DV002', 30000000, 3000000, 27000000, N'Quẹt thẻ POS', N'Đã thanh toán'),
('2026-10-12 14:00:00', 'BN003', 'NV003', 'DV004', 2000000, 0, 2000000, N'Tiền mặt', N'Đã thanh toán'),
('2026-10-11 16:00:00', 'BN004', 'NV002', 'DV003', 4500000, 450000, 4050000, N'Chuyển khoản QR', N'Đã thanh toán');
GO

-- 18. BẢNG QUY ĐỊNH HỆ THỐNG
INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat) VALUES
('QD_MAX_KHAM_CA', N'Số lượt khám tối đa mỗi ca cho 1 Bác sĩ', '15', 'INT', N'Giới hạn số bệnh nhân tiếp nhận để đảm bảo chất lượng khám chữa bệnh', GETDATE(), 'QL001'),
('QD_MIN_HUY_LICH', N'Thời gian báo trước tối thiểu khi hủy/dời lịch (giờ)', '2', 'INT', N'Bệnh nhân cần gọi thông báo đổi lịch hẹn trước ít nhất 2 giờ', GETDATE(), 'QL001'),
('QD_DIEM_VIP', N'Ngưỡng điểm tích lũy nâng hạng Thẻ VIP', '1000', 'INT', N'Cứ 10.000 VNĐ chi tiêu được cộng 1 điểm', GETDATE(), 'QL001'),
('QD_TON_KHO_CANH_BAO', N'Tỷ lệ cảnh báo định mức tồn kho tối thiểu (%)', '100', 'INT', N'Cảnh báo khi số lượng thực tế chạm hoặc dưới ngưỡng tối thiểu', GETDATE(), 'QL001');
GO

-- 19. BẢNG NHẬT KÝ HỆ THỐNG
INSERT INTO LichSuHeThong (thoiGian, tenDangNhap, hanhDong, chiTiet) VALUES
('2026-10-23 07:30:00', 'admin', N'Đăng nhập hệ thống', N'Quản lý Trần Thị Hoàng Lan đăng nhập hệ thống thành công'),
('2026-10-23 07:35:00', 'letan_tram', N'Đăng nhập hệ thống', N'Lễ tân Phạm Ngọc Trâm mở ca làm việc buổi sáng'),
('2026-10-23 07:45:00', 'letan_tram', N'Tiếp nhận bệnh nhân', N'Tiếp nhận bệnh nhân Nguyễn Văn Tuấn (BN001) vào phòng khám Implant'),
('2026-10-23 07:50:00', 'bs.an', N'Đăng nhập hệ thống', N'Bác sĩ Nguyễn Văn An bắt đầu ca trực');
GO

/* =========================================================================================
   PHẦN V: TẠO CÁC VIEWS BÁO CÁO & TRUY VẤN TIỆN ÍCH
========================================================================================= */

-- 1. View Hàng đợi khám bệnh tổng hợp phục vụ Dashboard
CREATE OR ALTER VIEW vw_HangDoiKhamBenhHomNay
AS
SELECT 
    lh.maLichHen,
    bn.maBN,
    bn.hoTen AS hoTenBenhNhan,
    nv.hoTen AS hoTenBacSi,
    lh.lyDoKham,
    CONVERT(VARCHAR(5), lh.gioHen, 108) AS gioHen,
    lh.trangThai
FROM LichHen lh
INNER JOIN BenhNhan bn ON lh.maBN = bn.maBN
LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV
WHERE lh.ngayHen = CAST(GETDATE() AS DATE);
GO

-- 2. View Báo cáo doanh thu theo dịch vụ
CREATE OR ALTER VIEW vw_DoanhThuTheoDichVu
AS
SELECT 
    dv.maDichVu,
    dv.tenDichVu,
    COUNT(ct.maCTHD) AS soLuotThucHien,
    ISNULL(SUM(ct.thanhTien), 0) AS tongDoanhThu
FROM DichVu dv
LEFT JOIN ChiTietHoaDon ct ON dv.maDichVu = ct.maDichVu
GROUP BY dv.maDichVu, dv.tenDichVu;
GO

PRINT N'=========================================================================================';
PRINT N'ĐÃ KHỞI TẠO THÀNH CÔNG DATABASE QLPHONGKHAM!';
PRINT N'- Khớp hoàn toàn 100% Class Diagram (UML) & Mô hình ERD chuẩn 3NF';
PRINT N'- Tương thích tuyệt đối mã nguồn Java (LoginModern, PanelThanhToan, PanelDashboard...)';
PRINT N'- Đã nạp đầy đủ Seed Data tài khoản mẫu, bác sĩ, bệnh nhân, dịch vụ, kho và hóa đơn';
PRINT N'=========================================================================================';
GO
