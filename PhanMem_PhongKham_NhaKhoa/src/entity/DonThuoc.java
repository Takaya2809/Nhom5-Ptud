package entity;

import java.sql.Timestamp;

public class DonThuoc {
    private String maDonThuoc;
    private String maHoSo;
    private String maNhaSi;
    private Timestamp ngayLap;
    private String loiDan;
    private double tongTienThuoc;
    private String trangThai;

    private String tenNhaSi;

    public DonThuoc() {}

    public DonThuoc(String maDonThuoc, String maHoSo, String maNhaSi, Timestamp ngayLap,
                    String loiDan, double tongTienThuoc, String trangThai) {
        this.maDonThuoc = maDonThuoc;
        this.maHoSo = maHoSo;
        this.maNhaSi = maNhaSi;
        this.ngayLap = ngayLap;
        this.loiDan = loiDan;
        this.tongTienThuoc = tongTienThuoc;
        this.trangThai = trangThai;
    }

    public String getMaDonThuoc() { return maDonThuoc; }
    public void setMaDonThuoc(String maDonThuoc) { this.maDonThuoc = maDonThuoc; }

    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }

    public String getMaNhaSi() { return maNhaSi; }
    public void setMaNhaSi(String maNhaSi) { this.maNhaSi = maNhaSi; }

    public Timestamp getNgayLap() { return ngayLap; }
    public void setNgayLap(Timestamp ngayLap) { this.ngayLap = ngayLap; }

    public String getLoiDan() { return loiDan; }
    public void setLoiDan(String loiDan) { this.loiDan = loiDan; }

    public double getTongTienThuoc() { return tongTienThuoc; }
    public void setTongTienThuoc(double tongTienThuoc) { this.tongTienThuoc = tongTienThuoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenNhaSi() { return tenNhaSi; }
    public void setTenNhaSi(String tenNhaSi) { this.tenNhaSi = tenNhaSi; }
}
