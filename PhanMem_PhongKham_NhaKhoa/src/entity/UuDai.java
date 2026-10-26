package entity;

import java.sql.Date;

public class UuDai {
    private String maUuDai;
    private String tenUuDai;
    private double phanTramGiam;
    private String dieuKien;
    private Date ngayBD;
    private Date ngayKT;
    private String trangThai;

    public UuDai() {}

    public UuDai(String maUuDai, String tenUuDai, double phanTramGiam, String dieuKien,
                 Date ngayBD, Date ngayKT, String trangThai) {
        this.maUuDai = maUuDai;
        this.tenUuDai = tenUuDai;
        this.phanTramGiam = phanTramGiam;
        this.dieuKien = dieuKien;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
        this.trangThai = trangThai;
    }

    public String getMaUuDai() { return maUuDai; }
    public void setMaUuDai(String maUuDai) { this.maUuDai = maUuDai; }

    public String getTenUuDai() { return tenUuDai; }
    public void setTenUuDai(String tenUuDai) { this.tenUuDai = tenUuDai; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public String getDieuKien() { return dieuKien; }
    public void setDieuKien(String dieuKien) { this.dieuKien = dieuKien; }

    public Date getNgayBD() { return ngayBD; }
    public void setNgayBD(Date ngayBD) { this.ngayBD = ngayBD; }

    public Date getNgayKT() { return ngayKT; }
    public void setNgayKT(Date ngayKT) { this.ngayKT = ngayKT; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenUuDai + " (-" + (int)phanTramGiam + "%)";
    }
}
