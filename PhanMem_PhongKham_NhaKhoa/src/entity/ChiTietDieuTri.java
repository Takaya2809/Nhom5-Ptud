package entity;

public class ChiTietDieuTri {
    private int maCTDT;
    private String maHoSo;
    private String maDichVu;
    private String viTriRang;
    private int soLuong;
    private double donGia;
    private double thanhTien;
    private String ghiChu;

    private String tenDichVu;

    public ChiTietDieuTri() {}

    public ChiTietDieuTri(int maCTDT, String maHoSo, String maDichVu, String viTriRang,
                          int soLuong, double donGia, double thanhTien, String ghiChu) {
        this.maCTDT = maCTDT;
        this.maHoSo = maHoSo;
        this.maDichVu = maDichVu;
        this.viTriRang = viTriRang;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.ghiChu = ghiChu;
    }

    public int getMaCTDT() { return maCTDT; }
    public void setMaCTDT(int maCTDT) { this.maCTDT = maCTDT; }

    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public String getViTriRang() { return viTriRang; }
    public void setViTriRang(String viTriRang) { this.viTriRang = viTriRang; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
}
