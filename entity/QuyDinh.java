package entity;

import java.sql.Timestamp;
import java.util.Objects;

public class QuyDinh {
    private String maQuyDinh;
    private String tenQuyDinh;
    private String giaTri;
    private String kieuDuLieu;
    private String moTa;
    private Timestamp ngayCapNhat;
    private NhanVien nguoiCapNhat;

    public QuyDinh() {
    }

    public QuyDinh(String maQuyDinh) {
        this.maQuyDinh = maQuyDinh;
    }

    public QuyDinh(String maQuyDinh, String tenQuyDinh, String giaTri, String kieuDuLieu, String moTa,
            Timestamp ngayCapNhat, String maNVCapNhat) {
        this.maQuyDinh = maQuyDinh;
        this.tenQuyDinh = tenQuyDinh;
        this.giaTri = giaTri;
        this.kieuDuLieu = kieuDuLieu;
        this.moTa = moTa;
        this.ngayCapNhat = ngayCapNhat;
        this.nguoiCapNhat = maNVCapNhat != null ? new NhanVien(maNVCapNhat) : null;
    }

    public QuyDinh(String maQuyDinh, String tenQuyDinh, String giaTri, String kieuDuLieu, String moTa,
            Timestamp ngayCapNhat, NhanVien nguoiCapNhat) {
        this.maQuyDinh = maQuyDinh;
        this.tenQuyDinh = tenQuyDinh;
        this.giaTri = giaTri;
        this.kieuDuLieu = kieuDuLieu;
        this.moTa = moTa;
        this.ngayCapNhat = ngayCapNhat;
        this.nguoiCapNhat = nguoiCapNhat;
    }

    public String getMaQuyDinh() {
        return maQuyDinh;
    }

    public void setMaQuyDinh(String maQuyDinh) {
        this.maQuyDinh = maQuyDinh;
    }

    public String getTenQuyDinh() {
        return tenQuyDinh;
    }

    public void setTenQuyDinh(String tenQuyDinh) {
        this.tenQuyDinh = tenQuyDinh;
    }

    public String getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    public String getKieuDuLieu() {
        return kieuDuLieu;
    }

    public void setKieuDuLieu(String kieuDuLieu) {
        this.kieuDuLieu = kieuDuLieu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Timestamp getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Timestamp ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public NhanVien getNguoiCapNhat() {
        return nguoiCapNhat;
    }

    public void setNguoiCapNhat(NhanVien nguoiCapNhat) {
        this.nguoiCapNhat = nguoiCapNhat;
    }

    public String getMaNVCapNhat() {
        return nguoiCapNhat != null ? nguoiCapNhat.getMaNV() : null;
    }

    public void setMaNVCapNhat(String maNVCapNhat) {
        if (this.nguoiCapNhat == null)
            this.nguoiCapNhat = new NhanVien(maNVCapNhat);
        else
            this.nguoiCapNhat.setMaNV(maNVCapNhat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof QuyDinh))
            return false;
        QuyDinh quyDinh = (QuyDinh) o;
        return Objects.equals(maQuyDinh, quyDinh.maQuyDinh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maQuyDinh);
    }

    @Override
    public String toString() {
        return tenQuyDinh + ": " + giaTri;
    }
}
