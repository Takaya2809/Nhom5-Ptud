package entity;

import java.util.Objects;

public class VatTu {
    private String maVatTu;
    private String tenVatTu;
    private String loaiVatTu; // Tiêu hao, Dụng cụ, Thiết bị
    private String donViTinh; // Hộp, Cái, Ống, Lọ, Bộ, Gói
    private int soLuongTon;
    private int dinhMucToiThieu;
    private double donGia;
    private String trangThai; // Còn hàng, Cảnh báo hết hàng, Hết hàng, Ngừng sử dụng
    private NCC nhaCungCap;

    public VatTu() {}

    public VatTu(String maVatTu) {
        this.maVatTu = maVatTu;
    }

    public VatTu(String maVatTu, String tenVatTu, String loaiVatTu, String donViTinh,
                 int soLuongTon, int dinhMucToiThieu, double donGia, String trangThai) {
        this(maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai, null);
    }

    public VatTu(String maVatTu, String tenVatTu, String loaiVatTu, String donViTinh,
                 int soLuongTon, int dinhMucToiThieu, double donGia, String trangThai, NCC nhaCungCap) {
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.loaiVatTu = loaiVatTu;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.dinhMucToiThieu = dinhMucToiThieu;
        this.donGia = donGia;
        this.trangThai = trangThai;
        this.nhaCungCap = nhaCungCap;
    }

    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }

    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String tenVatTu) { this.tenVatTu = tenVatTu; }

    public String getLoaiVatTu() { return loaiVatTu; }
    public void setLoaiVatTu(String loaiVatTu) { this.loaiVatTu = loaiVatTu; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public int getDinhMucToiThieu() { return dinhMucToiThieu; }
    public void setDinhMucToiThieu(int dinhMucToiThieu) { this.dinhMucToiThieu = dinhMucToiThieu; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public NCC getNhaCungCap() { return nhaCungCap; }
    public void setNhaCungCap(NCC nhaCungCap) { this.nhaCungCap = nhaCungCap; }

    public String getMaNCC() {
        return nhaCungCap != null ? nhaCungCap.getMaNCC() : null;
    }

    public boolean isDuoiDinhMuc() {
        return soLuongTon <= dinhMucToiThieu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VatTu)) return false;
        VatTu vatTu = (VatTu) o;
        return Objects.equals(maVatTu, vatTu.maVatTu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maVatTu);
    }

    @Override
    public String toString() {
        return tenVatTu + " (" + soLuongTon + " " + donViTinh + ")";
    }
}
