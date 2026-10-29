package entity;

import java.sql.Timestamp;

public class QuyDinh {
    private String maQuyDinh;
    private String tenQuyDinh;
    private String giaTri;
    private String kieuDuLieu;
    private String moTa;
    private Timestamp ngayCapNhat;
    private String maNVCapNhat;

    public QuyDinh() {}

    public QuyDinh(String maQuyDinh, String tenQuyDinh, String giaTri, String kieuDuLieu, String moTa, Timestamp ngayCapNhat, String maNVCapNhat) {
        this.maQuyDinh = maQuyDinh;
        this.tenQuyDinh = tenQuyDinh;
        this.giaTri = giaTri;
        this.kieuDuLieu = kieuDuLieu;
        this.moTa = moTa;
        this.ngayCapNhat = ngayCapNhat;
        this.maNVCapNhat = maNVCapNhat;
    }

    public String getMaQuyDinh() { return maQuyDinh; }
    public void setMaQuyDinh(String maQuyDinh) { this.maQuyDinh = maQuyDinh; }

    public String getTenQuyDinh() { return tenQuyDinh; }
    public void setTenQuyDinh(String tenQuyDinh) { this.tenQuyDinh = tenQuyDinh; }

    public String getGiaTri() { return giaTri; }
    public void setGiaTri(String giaTri) { this.giaTri = giaTri; }

    public String getKieuDuLieu() { return kieuDuLieu; }
    public void setKieuDuLieu(String kieuDuLieu) { this.kieuDuLieu = kieuDuLieu; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Timestamp getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Timestamp ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public String getMaNVCapNhat() { return maNVCapNhat; }
    public void setMaNVCapNhat(String maNVCapNhat) { this.maNVCapNhat = maNVCapNhat; }
}
