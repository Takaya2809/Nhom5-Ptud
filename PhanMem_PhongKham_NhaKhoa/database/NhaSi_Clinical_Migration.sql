/* Run once on an existing QLPHONGKHAM database to add dentist encounter fields. */
IF COL_LENGTH('dbo.HoSoBenhAn', 'tinhTrangLamSang') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD tinhTrangLamSang NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'ketQuaCanLamSang') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD ketQuaCanLamSang NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'viTriRang') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD viTriRang NVARCHAR(255) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'keHoachDieuTri') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD keHoachDieuTri NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'noiDungCanThiep') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD noiDungCanThiep NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'donThuoc') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD donThuoc NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'huongDanSauDieuTri') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD huongDanSauDieuTri NVARCHAR(MAX) NULL;
IF COL_LENGTH('dbo.HoSoBenhAn', 'ngayTaiKham') IS NULL
    ALTER TABLE dbo.HoSoBenhAn ADD ngayTaiKham DATE NULL;

IF OBJECT_ID('dbo.NhatKyBenhAn', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.NhatKyBenhAn (
        maAudit BIGINT IDENTITY(1,1) PRIMARY KEY,
        maHoSo VARCHAR(30) NOT NULL,
        maBN VARCHAR(20) NOT NULL,
        tenDangNhap VARCHAR(50) NOT NULL,
        hanhDong NVARCHAR(50) NOT NULL,
        thoiGian DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        noiDungTruoc NVARCHAR(MAX) NULL,
        noiDungSau NVARCHAR(MAX) NOT NULL
    );
END;
