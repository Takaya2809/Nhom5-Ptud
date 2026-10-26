package entity;

import java.sql.Timestamp;

public class HoaDonKham {
    private int maHD;
    private Timestamp ngayLap;
    private String maBN;
    private String maNV;
    private String maDichVu;
    private double tongTien;
    private double giamGia;
    private double thanhTien;
    private String phuongThucTT; // Tiền mặt, Chuyển khoản, Thẻ
    private String trangThai; // Đã thanh toán, Chờ thanh toán

    // Thuộc tính hiển thị
    private String tenBN;
    private String tenNV;
    private String tenDichVu;

    public HoaDonKham() {}

    public HoaDonKham(int maHD, Timestamp ngayLap, String maBN, String maNV,
                      String maDichVu, double tongTien, double giamGia, double thanhTien,
                      String phuongThucTT, String trangThai) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maBN = maBN;
        this.maNV = maNV;
        this.maDichVu = maDichVu;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.thanhTien = thanhTien;
        this.phuongThucTT = phuongThucTT;
        this.trangThai = trangThai;
    }

    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }

    public Timestamp getNgayLap() { return ngayLap; }
    public void setNgayLap(Timestamp ngayLap) { this.ngayLap = ngayLap; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public String getPhuongThucTT() { return phuongThucTT; }
    public void setPhuongThucTT(String phuongThucTT) { this.phuongThucTT = phuongThucTT; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenBN() { return tenBN; }
    public void setTenBN(String tenBN) { this.tenBN = tenBN; }

    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
}
