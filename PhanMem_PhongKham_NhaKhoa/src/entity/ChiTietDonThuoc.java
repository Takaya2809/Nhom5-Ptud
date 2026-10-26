package entity;

public class ChiTietDonThuoc {
    private int maCTDTk;
    private String maDonThuoc;
    private String maThuoc;
    private int soLuong;
    private double donGia;
    private String lieuDung;
    private String cachDung;
    private int soNgayDung;
    private double thanhTien;

    private String tenThuoc;
    private String donViTinh;

    public ChiTietDonThuoc() {}

    public ChiTietDonThuoc(int maCTDTk, String maDonThuoc, String maThuoc, int soLuong,
                           double donGia, String lieuDung, String cachDung,
                           int soNgayDung, double thanhTien) {
        this.maCTDTk = maCTDTk;
        this.maDonThuoc = maDonThuoc;
        this.maThuoc = maThuoc;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.lieuDung = lieuDung;
        this.cachDung = cachDung;
        this.soNgayDung = soNgayDung;
        this.thanhTien = thanhTien;
    }

    public int getMaCTDTk() { return maCTDTk; }
    public void setMaCTDTk(int maCTDTk) { this.maCTDTk = maCTDTk; }

    public String getMaDonThuoc() { return maDonThuoc; }
    public void setMaDonThuoc(String maDonThuoc) { this.maDonThuoc = maDonThuoc; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getLieuDung() { return lieuDung; }
    public void setLieuDung(String lieuDung) { this.lieuDung = lieuDung; }

    public String getCachDung() { return cachDung; }
    public void setCachDung(String cachDung) { this.cachDung = cachDung; }

    public int getSoNgayDung() { return soNgayDung; }
    public void setSoNgayDung(int soNgayDung) { this.soNgayDung = soNgayDung; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
}
