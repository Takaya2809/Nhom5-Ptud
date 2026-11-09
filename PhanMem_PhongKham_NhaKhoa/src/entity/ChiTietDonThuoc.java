package entity;

import java.util.Objects;

/**
 * Thực thể Chi tiết đơn thuốc.
 * Nghiệp vụ chuẩn hóa: KHÔNG lưu trường thanhTien (tính toán runtime: soLuong *
 * donGia).
 */
public class ChiTietDonThuoc {
    private int maCTDTk;
    private DonThuoc donThuoc;
    private Thuoc thuoc;
    private int soLuong;
    private double donGia;
    private String lieuDung;
    private String cachDung;
    private int soNgayDung;

    public ChiTietDonThuoc() {
    }

    public ChiTietDonThuoc(int maCTDTk) {
        this.maCTDTk = maCTDTk;
    }

    public ChiTietDonThuoc(int maCTDTk, String maDonThuoc, String maThuoc, int soLuong,
            double donGia, String lieuDung, String cachDung,
            int soNgayDung, double thanhTien) {
        this.maCTDTk = maCTDTk;
        this.donThuoc = maDonThuoc != null ? new DonThuoc(maDonThuoc) : null;
        this.thuoc = maThuoc != null ? new Thuoc(maThuoc) : null;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.lieuDung = lieuDung;
        this.cachDung = cachDung;
        this.soNgayDung = soNgayDung;
    }

    public ChiTietDonThuoc(int maCTDTk, DonThuoc donThuoc, Thuoc thuoc, int soLuong,
            double donGia, String lieuDung, String cachDung, int soNgayDung) {
        this.maCTDTk = maCTDTk;
        this.donThuoc = donThuoc;
        this.thuoc = thuoc;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.lieuDung = lieuDung;
        this.cachDung = cachDung;
        this.soNgayDung = soNgayDung;
    }

    public int getMaCTDTk() {
        return maCTDTk;
    }

    public void setMaCTDTk(int maCTDTk) {
        this.maCTDTk = maCTDTk;
    }

    public DonThuoc getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(DonThuoc donThuoc) {
        this.donThuoc = donThuoc;
    }

    public Thuoc getThuoc() {
        return thuoc;
    }

    public void setThuoc(Thuoc thuoc) {
        this.thuoc = thuoc;
    }

    public String getMaDonThuoc() {
        return donThuoc != null ? donThuoc.getMaDonThuoc() : null;
    }

    public void setMaDonThuoc(String maDonThuoc) {
        if (this.donThuoc == null)
            this.donThuoc = new DonThuoc(maDonThuoc);
        else
            this.donThuoc.setMaDonThuoc(maDonThuoc);
    }

    public String getMaThuoc() {
        return thuoc != null ? thuoc.getMaThuoc() : null;
    }

    public void setMaThuoc(String maThuoc) {
        if (this.thuoc == null)
            this.thuoc = new Thuoc(maThuoc);
        else
            this.thuoc.setMaThuoc(maThuoc);
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getLieuDung() {
        return lieuDung;
    }

    public void setLieuDung(String lieuDung) {
        this.lieuDung = lieuDung;
    }

    public String getCachDung() {
        return cachDung;
    }

    public void setCachDung(String cachDung) {
        this.cachDung = cachDung;
    }

    public int getSoNgayDung() {
        return soNgayDung;
    }

    public void setSoNgayDung(int soNgayDung) {
        this.soNgayDung = soNgayDung;
    }

    /**
     * Tính thành tiền động tại thời gian chạy (Runtime calculation)
     */
    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public double getThanhTien() {
        return tinhThanhTien();
    }

    public void setThanhTien(double thanhTien) {
        // Không lưu trường thanhTien trong CSDL chuẩn hóa - tính toán động qua
        // tinhThanhTien()
    }

    public String getTenThuoc() {
        return thuoc != null ? thuoc.getTenThuoc() : null;
    }

    public void setTenThuoc(String tenThuoc) {
        if (this.thuoc == null)
            this.thuoc = new Thuoc();
        this.thuoc.setTenThuoc(tenThuoc);
    }

    public String getDonViTinh() {
        return thuoc != null ? thuoc.getDonViTinh() : null;
    }

    public void setDonViTinh(String donViTinh) {
        if (this.thuoc == null)
            this.thuoc = new Thuoc();
        this.thuoc.setDonViTinh(donViTinh);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ChiTietDonThuoc))
            return false;
        ChiTietDonThuoc that = (ChiTietDonThuoc) o;
        return maCTDTk == that.maCTDTk;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTDTk);
    }

    @Override
    public String toString() {
        return "CTDonThuoc[" + getTenThuoc() + " x " + soLuong + "]";
    }
}
