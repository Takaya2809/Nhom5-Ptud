package entity;

import java.util.Objects;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private double giaNiemYet;
    private int thoiGianDuKien;
    private String trangThai; // Đang cung cấp, Tạm ngừng cung cấp
    private String moTa;
    private String nhomDichVu;

    public DichVu() {
    }

    public DichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public DichVu(String maDichVu, String tenDichVu, double giaNiemYet) {
        this(maDichVu, tenDichVu, giaNiemYet, 30, "Đang áp dụng", "", "Nha khoa tổng quát");
    }

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

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public double getGiaNiemYet() {
        return giaNiemYet;
    }

    public void setGiaNiemYet(double giaNiemYet) {
        this.giaNiemYet = giaNiemYet;
    }

    public double getDonGia() {
        return giaNiemYet;
    }

    public int getThoiGianDuKien() {
        return thoiGianDuKien;
    }

    public void setThoiGianDuKien(int thoiGianDuKien) {
        this.thoiGianDuKien = thoiGianDuKien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getNhomDichVu() {
        return nhomDichVu;
    }

    public void setNhomDichVu(String nhomDichVu) {
        this.nhomDichVu = nhomDichVu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DichVu))
            return false;
        DichVu dichVu = (DichVu) o;
        return Objects.equals(maDichVu, dichVu.maDichVu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDichVu);
    }

    @Override
    public String toString() {
        return tenDichVu + " (" + String.format("%,.0f VNĐ", giaNiemYet) + ")";
    }
}
