/* =========================================================================================
   HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA (DENTAL CLINIC MANAGEMENT SYSTEM)
   DATABASE SCRIPT TỔNG HỢP: CHUẨN HÓA 3NF & TƯƠNG THÍCH 100% GIAO DIỆN JAVA
   Hệ quản trị CSDL: Microsoft SQL Server (T-SQL)
   Database Name: QLPHONGKHAM
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
   PHẦN I: KHỞI TẠO CÁC BẢNG DỮ LIỆU CHUẨN HÓA
========================================================================================= */

-- 1. BẢNG VAI TRÒ
CREATE TABLE VaiTro (
    maVaiTro VARCHAR(20) PRIMARY KEY,
    tenVaiTro NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(255) NULL,
    danhSachQuyen NVARCHAR(MAX) NOT NULL
);

-- 2. BẢNG NHÂN VIÊN
CREATE TABLE NhanVien (
    maNV VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    chucDanh NVARCHAR(50) NOT NULL, -- Bác sĩ, Nha sĩ, Y tá, Lễ tân, Quản lý, Thu ngân
    bangCapChungChi NVARCHAR(200) NULL,
    ngayVaoLam DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang làm việc',
    chuyenMon NVARCHAR(100) NULL,
    soChungChiHanhNghe VARCHAR(50) NULL
);

-- 3. BẢNG TÀI KHOẢN
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
    ngayTao DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Hoạt động',
    soLanSaiMatKhau INT DEFAULT 0,
    maNV VARCHAR(20) UNIQUE FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION ON UPDATE CASCADE,
    maVaiTro VARCHAR(20) FOREIGN KEY REFERENCES VaiTro(maVaiTro) ON DELETE NO ACTION ON UPDATE CASCADE
);

-- 4. BẢNG CA TRỰC
CREATE TABLE CaTruc (
    maCa VARCHAR(50) PRIMARY KEY,
    tenCa NVARCHAR(50) NOT NULL,
    ngayTruc DATE NOT NULL,
    gioBatDau TIME NOT NULL,
    gioKetThuc TIME NOT NULL,
    soLuongBacSiToiThieu INT DEFAULT 1,
    soLuongLeTanToiThieu INT DEFAULT 1,
    soLuongKhamToiDa INT DEFAULT 20,
    trangThai NVARCHAR(50) DEFAULT N'Đã lên lịch'
);

-- 5. BẢNG PHÂN CÔNG CA TRỰC
CREATE TABLE PhanCongCaTruc (
    maPhanCong INT IDENTITY(1,1) PRIMARY KEY,
    maNV VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION ON UPDATE CASCADE,
    maCa VARCHAR(50) FOREIGN KEY REFERENCES CaTruc(maCa) ON DELETE CASCADE ON UPDATE CASCADE,
    vaiTroTrongCa NVARCHAR(50) NOT NULL,
    trangThaiDiemDanh NVARCHAR(50) DEFAULT N'Chưa đến',
    thoiGianDiemDanh DATETIME NULL,
    ghiChu NVARCHAR(255) NULL
);

-- 6. BẢNG DỊCH VỤ NHA KHOA
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(150) NOT NULL UNIQUE,
    giaNiemYet FLOAT NOT NULL,
    thoiGianDuKien INT DEFAULT 30,
    trangThai NVARCHAR(50) DEFAULT N'Đang cung cấp',
    moTa NVARCHAR(500) NULL
);

-- 7. BẢNG BỆNH NHÂN
CREATE TABLE BenhNhan (
    maBN VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,
    diaChi NVARCHAR(255) NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    ngaySinh DATE NOT NULL,
    tienSuBenh NVARCHAR(MAX) NULL,
    loaiKhach NVARCHAR(50) DEFAULT N'Khách mới',
    diemTichLuy INT DEFAULT 0
);

-- 8. BẢNG VẬT TƯ & THIẾT BỊ
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

-- 9. BẢNG CHÍNH SÁCH ƯU ĐÃI
CREATE TABLE UuDai (
    maUuDai VARCHAR(30) PRIMARY KEY,
    tenUuDai NVARCHAR(150) NOT NULL,
    phanTramGiam FLOAT DEFAULT 0,
    dieuKien NVARCHAR(255) NULL,
    ngayBD DATE NOT NULL,
    ngayKT DATE NOT NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang áp dụng'
);

-- 10. BẢNG HÓA ĐƠN KHÁM (Phục vụ trực tiếp giao diện Java Swing hiện có)
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

-- 11. BẢNG NHÀ CUNG CẤP VẬT TƯ (Bổ sung chuẩn 3NF)
CREATE TABLE NCC (
    maNCC VARCHAR(20) PRIMARY KEY,
    tenNCC NVARCHAR(150) NOT NULL UNIQUE,
    soDienThoai VARCHAR(15) NOT NULL,
    email VARCHAR(100) NULL,
    diaChi NVARCHAR(255) NOT NULL,
    nguoiLienHe NVARCHAR(100) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đang hợp tác'
);

-- 12. BẢNG PHIẾU NHẬP KHO VẬT TƯ
CREATE TABLE PhieuNhapKho (
    maPhieuNhap VARCHAR(30) PRIMARY KEY,
    maNCC VARCHAR(20) FOREIGN KEY REFERENCES NCC(maNCC) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNVLap VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maNVDuyet VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayNhap DATETIME DEFAULT GETDATE(),
    tongTien FLOAT DEFAULT 0,
    trangThai NVARCHAR(30) DEFAULT N'Đã nhập kho',
    ghiChu NVARCHAR(255) NULL
);

-- 13. BẢNG CHI TIẾT PHIẾU NHẬP KHO
CREATE TABLE ChiTietPhieuNhapKho (
    maCTPN INT IDENTITY(1,1) PRIMARY KEY,
    maPhieuNhap VARCHAR(30) FOREIGN KEY REFERENCES PhieuNhapKho(maPhieuNhap) ON DELETE CASCADE,
    maVatTu VARCHAR(20) FOREIGN KEY REFERENCES VatTu(maVatTu) ON DELETE NO ACTION,
    soLuongNhap INT NOT NULL,
    giaNhap FLOAT NOT NULL,
    thanhTien AS (soLuongNhap * giaNhap) PERSISTED
);

-- 14. BẢNG LỊCH HẸN KHÁM
CREATE TABLE LichHen (
    maLichHen VARCHAR(30) PRIMARY KEY,
    maBN VARCHAR(20) FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNhaSi VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayHen DATE NOT NULL,
    gioHen TIME NOT NULL,
    lyDoKham NVARCHAR(255) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đã lên lịch',
    ngayTao DATETIME DEFAULT GETDATE()
);

-- 15. BẢNG HỒ SƠ BỆNH ÁN
CREATE TABLE HoSoBenhAn (
    maHoSo VARCHAR(30) PRIMARY KEY,
    maBN VARCHAR(20) FOREIGN KEY REFERENCES BenhNhan(maBN) ON DELETE NO ACTION ON UPDATE CASCADE,
    maNhaSi VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    maLichHen VARCHAR(30) NULL FOREIGN KEY REFERENCES LichHen(maLichHen) ON DELETE SET NULL,
    ngayKham DATETIME DEFAULT GETDATE(),
    trieuChung NVARCHAR(MAX) NOT NULL,
    chanDoan NVARCHAR(MAX) NOT NULL,
    ketQuaDieuTri NVARCHAR(MAX) NULL,
    ghiChu NVARCHAR(500) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Hoàn thành'
);

-- 16. BẢNG CHI TIẾT ĐIỀU TRỊ (M-N giữa HoSoBenhAn và DichVu)
CREATE TABLE ChiTietDieuTri (
    maCTDT INT IDENTITY(1,1) PRIMARY KEY,
    maHoSo VARCHAR(30) FOREIGN KEY REFERENCES HoSoBenhAn(maHoSo) ON DELETE CASCADE,
    maDichVu VARCHAR(20) FOREIGN KEY REFERENCES DichVu(maDichVu) ON DELETE NO ACTION,
    viTriRang VARCHAR(50) NOT NULL, -- Sơ đồ răng FDI (R11-R48, Toàn hàm)
    soLuong INT DEFAULT 1,
    donGia FLOAT NOT NULL,
    thanhTien AS (soLuong * donGia) PERSISTED,
    ghiChu NVARCHAR(255) NULL
);

-- 17. BẢNG THUỐC
CREATE TABLE Thuoc (
    maThuoc VARCHAR(20) PRIMARY KEY,
    tenThuoc NVARCHAR(150) NOT NULL,
    hoatChat NVARCHAR(150) NULL,
    donViTinh NVARCHAR(30) NOT NULL,
    donGia FLOAT NOT NULL,
    soLuongTon INT DEFAULT 0,
    dinhMucToiThieu INT DEFAULT 20,
    hanSuDung DATE NULL,
    cachDungMacDinh NVARCHAR(255) NULL,
    trangThai NVARCHAR(30) DEFAULT N'Đang kinh doanh'
);

-- 18. BẢNG ĐƠN THUỐC
CREATE TABLE DonThuoc (
    maDonThuoc VARCHAR(30) PRIMARY KEY,
    maHoSo VARCHAR(30) UNIQUE FOREIGN KEY REFERENCES HoSoBenhAn(maHoSo) ON DELETE CASCADE,
    maNhaSi VARCHAR(20) FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE NO ACTION,
    ngayLap DATETIME DEFAULT GETDATE(),
    loiDan NVARCHAR(500) NULL,
    tongTienThuoc FLOAT DEFAULT 0,
    trangThai NVARCHAR(30) DEFAULT N'Đã xuất thuốc'
);

-- 19. BẢNG CHI TIẾT ĐƠN THUỐC
CREATE TABLE ChiTietDonThuoc (
    maCTDTk INT IDENTITY(1,1) PRIMARY KEY,
    maDonThuoc VARCHAR(30) FOREIGN KEY REFERENCES DonThuoc(maDonThuoc) ON DELETE CASCADE,
    maThuoc VARCHAR(20) FOREIGN KEY REFERENCES Thuoc(maThuoc) ON DELETE NO ACTION,
    soLuong INT NOT NULL,
    donGia FLOAT NOT NULL,
    lieuDung NVARCHAR(100) NOT NULL,
    cachDung NVARCHAR(100) NOT NULL,
    soNgayDung INT DEFAULT 5,
    thanhTien AS (soLuong * donGia) PERSISTED
);

-- 20. BẢNG QUY ĐỊNH HỆ THỐNG
CREATE TABLE QuyDinhHeThong (
    maQuyDinh VARCHAR(50) PRIMARY KEY,
    tenQuyDinh NVARCHAR(150) NOT NULL,
    giaTri NVARCHAR(255) NOT NULL,
    kieuDuLieu VARCHAR(20) DEFAULT 'STRING',
    moTa NVARCHAR(255) NULL,
    ngayCapNhat DATETIME DEFAULT GETDATE(),
    maNVCapNhat VARCHAR(20) NULL FOREIGN KEY REFERENCES NhanVien(maNV) ON DELETE SET NULL
);

-- 21. BẢNG NHẬT KÝ HỆ THỐNG (AUDIT LOGS)
CREATE TABLE LichSuHeThong (
    maLog INT IDENTITY(1,1) PRIMARY KEY,
    thoiGian DATETIME DEFAULT GETDATE(),
    tenDangNhap VARCHAR(50) NULL,
    hanhDong NVARCHAR(100) NOT NULL,
    chiTiet NVARCHAR(500) NULL
);
GO

/* =========================================================================================
   PHẦN II: THIẾT KẾ CHỈ MỤC TỐI ƯU HIỆU SUẤT TRUY VẤN (INDEXES)
========================================================================================= */
CREATE NONCLUSTERED INDEX IX_BenhNhan_SDT ON BenhNhan(soDienThoai);
CREATE NONCLUSTERED INDEX IX_BenhNhan_HoTen ON BenhNhan(hoTen);
CREATE NONCLUSTERED INDEX IX_LichHen_Ngay_Gio ON LichHen(ngayHen, gioHen) INCLUDE (trangThai, maBN, maNhaSi);
CREATE NONCLUSTERED INDEX IX_HoSoBenhAn_BN_Ngay ON HoSoBenhAn(maBN, ngayKham DESC);
CREATE NONCLUSTERED INDEX IX_HoaDonKham_NgayLap ON HoaDonKham(ngayLap DESC, trangThai);
CREATE NONCLUSTERED INDEX IX_VatTu_TonKho ON VatTu(soLuongTon, dinhMucToiThieu);
CREATE NONCLUSTERED INDEX IX_Thuoc_TonKho ON Thuoc(soLuongTon, dinhMucToiThieu);
GO

/* =========================================================================================
   PHẦN III: DỮ LIỆU MẪU ĐẦY ĐỦ (SEED DATA)
========================================================================================= */

-- 1. VaiTro
INSERT INTO VaiTro VALUES
('VT_QL', N'Quản lý', N'Toàn quyền quản trị, điều hành và cấu hình hệ thống', N'ALL'),
('VT_BS', N'Nha sĩ/Bác sĩ', N'Khám, chẩn đoán lâm sàng, điều trị và kê đơn', N'KHAM,DIEUTRI,KEDON'),
('VT_LT', N'Lễ tân', N'Tiếp đón, đặt lịch hẹn, tạo hồ sơ bệnh nhân', N'HOSO,LICHHEN,TIEPDON'),
('VT_TN', N'Thu ngân', N'Lập hóa đơn, áp dụng ưu đãi và thu viện phí', N'HOADON,THANHTOAN'),
('VT_YT', N'Y tá/Phụ tá', N'Quản lý kho vật tư y tế, khử khuẩn và hỗ trợ điều trị', N'VATTU,KHUKHUAN,HOTRO');

-- 2. NhanVien
INSERT INTO NhanVien (maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai, chuyenMon, soChungChiHanhNghe) VALUES
('QL001', N'Trần Thị Hoàng Lan', '1985-05-15', N'Nữ', '0903123456', 'lan.tran@nhakhoaabc.vn', N'Quản lý', N'ThS. Quản trị Y tế - ĐH Y Dược', '2020-01-10', N'Đang làm việc', N'Quản trị Y tế', NULL),
('BS001', N'BS.CKI Nguyễn Minh Tuấn', '1988-08-20', N'Nam', '0912345678', 'tuan.nguyen@nhakhoaabc.vn', N'Bác sĩ', N'ĐH Y Dược TP.HCM', '2020-03-01', N'Đang làm việc', N'Răng Hàm Mặt & Implant', '004562/BYT-CCHN'),
('BS002', N'ThS.BS Lê Thu Hằng', '1990-11-12', N'Nữ', '0918765432', 'hang.le@nhakhoaabc.vn', N'Bác sĩ', N'ThS Răng Hàm Mặt', '2021-06-15', N'Đang làm việc', N'Chỉnh nha - Niềng răng', '008912/BYT-CCHN'),
('BS003', N'BS Phạm Quốc Đạt', '1993-02-28', N'Nam', '0922334455', 'dat.pham@nhakhoaabc.vn', N'Bác sĩ', N'Bác sĩ RHM', '2022-01-05', N'Đang làm việc', N'Nội nha & Tiểu phẫu', '012345/BYT-CCHN'),
('LT001', N'Phạm Ngọc Trâm', '1998-04-25', N'Nữ', '0934567890', 'tram.pham@nhakhoaabc.vn', N'Lễ tân', N'Cử nhân QTKD', '2022-02-01', N'Đang làm việc', N'CSKH', NULL),
('TN001', N'Đỗ Hoài Phương', '1997-09-18', N'Nữ', '0945678901', 'phuong.do@nhakhoaabc.vn', N'Thu ngân', N'Cử nhân Kế toán', '2022-03-10', N'Đang làm việc', N'Thu ngân', NULL),
('YT001', N'Nguyễn Thị Kim Oanh', '1995-12-05', N'Nữ', '0978901234', 'oanh.nguyen@nhakhoaabc.vn', N'Y tá', N'Điều dưỡng Nha khoa', '2021-08-01', N'Đang làm việc', N'Phụ mổ & Vô trùng', '034567/BYT-DD'),
('YT002', N'Võ Thị Mỹ Duyên', '1999-07-20', N'Nữ', '0971239876', 'duyen.vo@nhakhoaabc.vn', N'Y tá', N'Điều dưỡng Nha khoa', '2023-04-15', N'Đang làm việc', N'Quản lý kho vật tư', '045678/BYT-DD');

-- 3. TaiKhoan (Mật khẩu: 123456)
INSERT INTO TaiKhoan (tenDangNhap, matKhau, ngayTao, trangThai, soLanSaiMatKhau, maNV, maVaiTro) VALUES
('admin', '123456', GETDATE(), N'Hoạt động', 0, 'QL001', 'VT_QL'),
('bacsi_tuan', '123456', GETDATE(), N'Hoạt động', 0, 'BS001', 'VT_BS'),
('bacsi_hang', '123456', GETDATE(), N'Hoạt động', 0, 'BS002', 'VT_BS'),
('bacsi_dat', '123456', GETDATE(), N'Hoạt động', 0, 'BS003', 'VT_BS'),
('letan_tram', '123456', GETDATE(), N'Hoạt động', 0, 'LT001', 'VT_LT'),
('thungan_phuong', '123456', GETDATE(), N'Hoạt động', 0, 'TN001', 'VT_TN'),
('yta_oanh', '123456', GETDATE(), N'Hoạt động', 0, 'YT001', 'VT_YT'),
('yta_duyen', '123456', GETDATE(), N'Hoạt động', 0, 'YT002', 'VT_YT');

-- 4. DichVu
INSERT INTO DichVu VALUES
('DV001', N'Khám & Tư vấn tổng quát răng miệng', 100000, 20, N'Đang cung cấp', N'Kiểm tra sức khỏe răng miệng toàn diện và tư vấn phác đồ'),
('DV002', N'Cạo vôi răng & Đánh bóng siêu âm (EMS)', 350000, 30, N'Đang cung cấp', N'Làm sạch cao răng mảng bám không đau bằng công nghệ siêu âm Thụy Sĩ'),
('DV003', N'Trám răng thẩm mỹ Composite Nano 3M', 450000, 45, N'Đang cung cấp', N'Phục hình răng sâu, mẻ bằng vật liệu Composite Nano nhập khẩu Mỹ'),
('DV004', N'Tẩy trắng răng công nghệ Laser Whitening', 2500000, 60, N'Đang cung cấp', N'Bật tone răng trắng sáng tức thì, an toàn không ê buốt'),
('DV005', N'Nhổ răng khôn mọc lệch (Tiểu phẫu)', 1800000, 45, N'Đang cung cấp', N'Nhổ răng khôn bằng sóng siêu âm Piezotome hạn chế xâm lấn'),
('DV006', N'Điều trị tủy răng (Nội nha 1 chân)', 1200000, 60, N'Đang cung cấp', N'Điều trị tủy và hàn bít ống tủy bằng hệ thống Protaper'),
('DV007', N'Răng sứ thẩm mỹ Zirconia cao cấp', 4000000, 90, N'Đang cung cấp', N'Răng toàn sứ chính hãng bảo hành 10 năm'),
('DV008', N'Cấy ghép Implant Straumann Thụy Sĩ', 28000000, 60, N'Đang cung cấp', N'Trụ Implant cao cấp tích hợp xương nhanh chóng'),
('DV009', N'Chụp phim X-quang Panorex toàn cảnh', 200000, 15, N'Đang cung cấp', N'Chụp khảo sát xương hàm và cung răng trước điều trị');

-- 5. CaTruc
INSERT INTO CaTruc VALUES
('CA_20261020_SANG', N'Ca Sáng', '2026-10-20', '08:00:00', '12:00:00', 2, 1, 20, N'Đang trực'),
('CA_20261020_CHIEU', N'Ca Chiều', '2026-10-20', '13:30:00', '17:30:00', 2, 1, 25, N'Đã lên lịch'),
('CA_20261020_TOI', N'Ca Tối', '2026-10-20', '18:00:00', '21:00:00', 1, 1, 15, N'Đã lên lịch'),
('CA_20261021_SANG', N'Ca Sáng', '2026-10-21', '08:00:00', '12:00:00', 2, 1, 20, N'Đã lên lịch'),
('CA_20261021_CHIEU', N'Ca Chiều', '2026-10-21', '13:30:00', '17:30:00', 2, 1, 25, N'Đã lên lịch');

-- 6. PhanCongCaTruc
INSERT INTO PhanCongCaTruc (maNV, maCa, vaiTroTrongCa, trangThaiDiemDanh, ghiChu) VALUES
('BS001', 'CA_20261020_SANG', N'Bác sĩ chính', N'Đã điểm danh', N'Trực phòng khám số 1'),
('BS002', 'CA_20261020_SANG', N'Bác sĩ chính', N'Đã điểm danh', N'Trực phòng chỉnh nha'),
('LT001', 'CA_20261020_SANG', N'Lễ tân', N'Đã điểm danh', N'Quầy đón tiếp sảnh A'),
('YT001', 'CA_20261020_SANG', N'Phụ tá', N'Đã điểm danh', N'Hỗ trợ vô trùng & dụng cụ'),
('BS003', 'CA_20261020_CHIEU', N'Bác sĩ chính', N'Chưa đến', N'Trực phòng tiểu phẫu'),
('LT001', 'CA_20261020_CHIEU', N'Lễ tân', N'Chưa đến', N'Quầy tiếp tân');

-- 7. VatTu
INSERT INTO VatTu VALUES
('VT001', N'Găng tay y tế Nitrile không bột (Hộp 100 chiếc)', N'Tiêu hao', N'Hộp', 45, 10, 120000, N'Còn hàng'),
('VT002', N'Khẩu trang y tế 4 lớp kháng khuẩn (Hộp 50 chiếc)', N'Tiêu hao', N'Hộp', 60, 15, 65000, N'Còn hàng'),
('VT003', N'Thuốc tê tiêm Lidocaine 2% (Hộp 50 ống)', N'Tiêu hao', N'Hộp', 8, 10, 480000, N'Cảnh báo hết hàng'),
('VT004', N'Vật liệu trám thẩm mỹ Composite 3M Z350', N'Tiêu hao', N'Tuýp', 5, 8, 850000, N'Cảnh báo hết hàng'),
('VT005', N'Gạc phẫu thuật vô trùng (Gói 100 miếng)', N'Tiêu hao', N'Gói', 35, 10, 45000, N'Còn hàng'),
('VT006', N'Bộ mũi khoan kim cương nha khoa Mani', N'Dụng cụ', N'Bộ', 12, 5, 350000, N'Còn hàng'),
('VT007', N'Kim tiêm nha khoa dùng 1 lần (Hộp 100 kim)', N'Tiêu hao', N'Hộp', 22, 10, 110000, N'Còn hàng'),
('VT008', N'Khay lấy dấu răng Inox nha khoa', N'Dụng cụ', N'Bộ', 15, 6, 280000, N'Còn hàng'),
('VT009', N'Ghế máy nha khoa cao cấp Anya AY-A3000', N'Thiết bị', N'Cái', 4, 1, 65000000, N'Còn hàng'),
('VT010', N'Máy cạo vôi siêu âm Woodpecker UDS-K', N'Thiết bị', N'Cái', 5, 2, 4500000, N'Còn hàng');

-- 8. BenhNhan
INSERT INTO BenhNhan VALUES
('BN001', N'Nguyễn Văn An', '0901234567', N'123 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM', N'Nam', '1992-06-14', N'Dị ứng Penicillin', N'VIP', 1200),
('BN002', N'Trần Thị Bích Ngọc', '0909876543', N'45 Hai Bà Trưng, P. Tân Định, Q.1, TP.HCM', N'Nữ', '1996-03-22', N'Tiền sử cao huyết áp nhẹ', N'Thân thiết', 550),
('BN003', N'Lê Minh Cường', '0988776655', N'88 Lê Lợi, P. Bến Thành, Q.1, TP.HCM', N'Nam', '1984-10-09', N'Không có', N'Thường', 180),
('BN004', N'Vũ Quỳnh Diệp', '0977665544', N'234 Cách Mạng Tháng 8, Q.3, TP.HCM', N'Nữ', '2001-01-30', N'Răng nhạy cảm, dễ ê buốt', N'Khách mới', 20),
('BN005', N'Hoàng Tuấn Kiệt', '0966554433', N'56 Hoàng Hoa Thám, P.12, Tân Bình, TP.HCM', N'Nam', '1999-12-05', N'Không có', N'Thường', 120);

-- 9. UuDai
INSERT INTO UuDai VALUES
('UD_SINHNHAT', N'Ưu đãi Sinh nhật Phòng khám ABC - Giảm 15% tất cả dịch vụ', 15, N'Áp dụng toàn bộ dịch vụ cho mọi khách hàng', '2026-10-01', '2026-10-31', N'Đang áp dụng'),
('UD_VIP', N'Đặc quyền Bệnh nhân Thân thiết & VIP - Giảm 10%', 10, N'Áp dụng cho khách hàng có thẻ VIP', '2026-01-01', '2026-12-31', N'Đang áp dụng'),
('UD_CAUVOI', N'Khuyến mại Chăm sóc nụ cười: Giảm 20% Cạo vôi răng', 20, N'Chỉ áp dụng dịch vụ Cạo vôi răng', '2026-10-01', '2026-10-15', N'Đang áp dụng');

-- 10. HoaDonKham (Dữ liệu cho giao diện Swing)
INSERT INTO HoaDonKham (ngayLap, maBN, maNV, maDichVu, tongTien, giamGia, thanhTien, phuongThucTT, trangThai) VALUES
('2026-10-18 09:15:00', 'BN001', 'BS001', 'DV002', 350000, 35000, 315000, N'Chuyển khoản', N'Đã thanh toán'),
('2026-10-18 10:30:00', 'BN002', 'BS001', 'DV004', 2500000, 250000, 2250000, N'Thẻ', N'Đã thanh toán'),
('2026-10-19 14:00:00', 'BN003', 'BS002', 'DV003', 450000, 0, 450000, N'Tiền mặt', N'Đã thanh toán'),
('2026-10-19 16:00:00', 'BN004', 'BS002', 'DV005', 1800000, 270000, 1530000, N'Chuyển khoản', N'Đã thanh toán'),
('2026-10-20 09:00:00', 'BN005', 'BS001', 'DV002', 350000, 70000, 280000, N'Tiền mặt', N'Đã thanh toán');

-- 11. NCC
INSERT INTO NCC VALUES
('NCC001', N'Công ty Thiết bị Y tế 3M Việt Nam', '02838123456', 'contact@3m-dental.vn', N'Q.7, TP.HCM', N'Nguyễn Hoàng Nam', N'Đang hợp tác'),
('NCC002', N'Công ty CP Dược Hậu Giang', '02923891234', 'kinhdoanh@dhgpharma.com.vn', N'Hậu Giang', N'Lê Văn Hùng', N'Đang hợp tác'),
('NCC003', N'Nhà phân phối Implant Straumann', '02439876543', 'info@straumann-vn.com', N'Ba Đình, Hà Nội', N'Vũ Bích Thảo', N'Đang hợp tác');

-- 12. PhieuNhapKho & ChiTietPhieuNhapKho
INSERT INTO PhieuNhapKho VALUES
('PNK20261010_01', 'NCC001', 'YT002', 'QL001', '2026-10-10 09:30:00', 9550000, N'Đã nhập kho', N'Nhập găng tay và composite tháng 10');

INSERT INTO ChiTietPhieuNhapKho (maPhieuNhap, maVatTu, soLuongNhap, giaNhap) VALUES
('PNK20261010_01', 'VT001', 30, 120000),
('PNK20261010_01', 'VT004', 7, 850000);

-- 13. LichHen
INSERT INTO LichHen (maLichHen, maBN, maNhaSi, ngayHen, gioHen, lyDoKham, ghiChu, trangThai) VALUES
('LH_20261020_001', 'BN001', 'BS001', '2026-10-20', '08:30:00', N'Cạo vôi răng định kỳ & Khám răng ê buốt', N'Khách VIP đặt trước', N'Đang khám'),
('LH_20261020_002', 'BN002', 'BS002', '2026-10-20', '09:30:00', N'Tái khám siết mắc cài niềng răng', N'Chỉnh nha', N'Đã xác nhận'),
('LH_20261020_003', 'BN003', 'BS003', '2026-10-20', '14:00:00', N'Đau nhức dữ dội răng cấm hàm dưới', N'Nghi tủy', N'Đã lên lịch'),
('LH_20261020_004', 'BN004', 'BS001', '2026-10-20', '15:30:00', N'Tư vấn tẩy trắng răng Laser', N'Khách mới', N'Đã lên lịch');

-- 14. HoSoBenhAn & ChiTietDieuTri
INSERT INTO HoSoBenhAn VALUES
('HS_20261018_001', 'BN001', 'BS001', NULL, '2026-10-18 09:15:00', N'Đau buốt nhẹ khi ăn lạnh', N'Sâu ngà răng hàm R46, vôi răng độ 2', N'Đã cạo vôi và trám composite hoàn tất', N'Dặn dò dùng chỉ nha khoa', N'Hoàn thành');

INSERT INTO ChiTietDieuTri (maHoSo, maDichVu, viTriRang, soLuong, donGia, ghiChu) VALUES
('HS_20261018_001', 'DV002', N'Toàn hàm', 1, 350000, N'Làm sạch vôi răng 2 hàm'),
('HS_20261018_001', 'DV003', N'Răng 46', 1, 450000, N'Trám xoang loại II Composite');

-- 15. Thuoc
INSERT INTO Thuoc VALUES
('TH001', N'Augmentin 1g (Amoxicillin/Clavulanate)', N'Amoxicillin + Clavulanate', N'Viên', 18000, 120, 30, '2027-12-31', N'Uống 1v x 2 lần/ngày sau ăn', N'Đang kinh doanh'),
('TH002', N'Rodogyl (Spiramycin + Metronidazol)', N'Spiramycin + Metronidazol', N'Viên', 8500, 150, 40, '2027-10-31', N'Uống 2v x 2 lần/ngày', N'Đang kinh doanh'),
('TH003', N'Efferalgan 500mg sủi (Paracetamol)', N'Paracetamol 500mg', N'Viên', 4000, 200, 50, '2028-05-30', N'Hòa tan 1 viên khi đau', N'Đang kinh doanh'),
('TH004', N'Ibuprofen 400mg Stada', N'Ibuprofen 400mg', N'Viên', 3000, 180, 40, '2027-08-15', N'Uống 1v sau ăn no', N'Đang kinh doanh'),
('TH005', N'Nước súc miệng Kin Gingival 250ml', N'Chlorhexidine 0.12%', N'Chai', 135000, 25, 10, '2027-06-30', N'Súc miệng 15ml x 2 lần/ngày', N'Đang kinh doanh');

-- 16. QuyDinhHeThong
INSERT INTO QuyDinhHeThong VALUES
('QD_MAX_KHAM_CA', N'Số lượt khám tối đa mỗi ca cho 1 Bác sĩ', '15', 'INT', N'Đảm bảo chất lượng khám', GETDATE(), 'QL001'),
('QD_MIN_HUY_LICH', N'Thời gian báo trước tối thiểu khi hủy/dời lịch (giờ)', '2', 'INT', N'Báo trước ít nhất 2 giờ', GETDATE(), 'QL001'),
('QD_DIEM_VIP', N'Ngưỡng điểm tích lũy nâng hạng Thẻ VIP', '1000', 'INT', N'10.000đ được 1 điểm', GETDATE(), 'QL001');

-- 17. LichSuHeThong
INSERT INTO LichSuHeThong (thoiGian, tenDangNhap, hanhDong, chiTiet) VALUES
('2026-10-20 07:45:00', 'admin', N'Đăng nhập hệ thống', N'Quản lý Trần Thị Hoàng Lan đăng nhập hệ thống thành công'),
('2026-10-20 08:05:00', 'letan_tram', N'Tiếp nhận bệnh nhân', N'Tiếp nhận bệnh nhân Nguyễn Văn An (BN001) khám cạo vôi');
GO

PRINT N'=========================================================================================';
PRINT N'ĐÃ KHỞI TẠO THÀNH CÔNG DATABASE QLPHONGKHAM (TƯƠNG THÍCH 100% GIAO DIỆN & CHUẨN HÓA 3NF)!';
PRINT N'=========================================================================================';
GO
