package entity;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private double giaNiemYet;
    private int thoiGianDuKien;
    private String trangThai; // Đang cung cấp, Tạm ngừng cung cấp
    private String moTa;
    private String nhomDichVu; // Khám & tư vấn, Nhổ răng, Trám & nội nha, Phục hình sứ, Implant, Chỉnh nha, Tẩy trắng, Nha chu...

    public DichVu() {}

    public DichVu(String maDichVu, String tenDichVu, double giaNiemYet,
                  int thoiGianDuKien, String trangThai, String moTa) {
        this(maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa, "Khám & tư vấn");
    }

    public DichVu(String maDichVu, String tenDichVu, double giaNiemYet,
                  int thoiGianDuKien, String trangThai, String moTa, String nhomDichVu) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.giaNiemYet = giaNiemYet;
        this.thoiGianDuKien = thoiGianDuKien;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.nhomDichVu = (nhomDichVu != null && !nhomDichVu.trim().isEmpty()) ? nhomDichVu : "Khám & tư vấn";
    }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public double getGiaNiemYet() { return giaNiemYet; }
    public void setGiaNiemYet(double giaNiemYet) { this.giaNiemYet = giaNiemYet; }

    public int getThoiGianDuKien() { return thoiGianDuKien; }
    public void setThoiGianDuKien(int thoiGianDuKien) { this.thoiGianDuKien = thoiGianDuKien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getNhomDichVu() { return nhomDichVu; }
    public void setNhomDichVu(String nhomDichVu) { this.nhomDichVu = nhomDichVu; }

    @Override
    public String toString() {
        return tenDichVu + " (" + String.format("%,.0f VNĐ", giaNiemYet) + ")";
    }
}
