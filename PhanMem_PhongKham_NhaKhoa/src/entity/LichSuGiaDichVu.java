package entity;

import java.sql.Timestamp;

public class LichSuGiaDichVu {
    private int maLichSu;
    private String maDichVu;
    private double giaCu;
    private double giaMoi;
    private Timestamp ngayHieuLuc;
    private String nguoiDoi;
    private String lyDo;

    public LichSuGiaDichVu() {}

    public LichSuGiaDichVu(int maLichSu, String maDichVu, double giaCu, double giaMoi,
                           Timestamp ngayHieuLuc, String nguoiDoi, String lyDo) {
        this.maLichSu = maLichSu;
        this.maDichVu = maDichVu;
        this.giaCu = giaCu;
        this.giaMoi = giaMoi;
        this.ngayHieuLuc = ngayHieuLuc;
        this.nguoiDoi = nguoiDoi;
        this.lyDo = lyDo;
    }

    public int getMaLichSu() { return maLichSu; }
    public void setMaLichSu(int maLichSu) { this.maLichSu = maLichSu; }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public double getGiaCu() { return giaCu; }
    public void setGiaCu(double giaCu) { this.giaCu = giaCu; }

    public double getGiaMoi() { return giaMoi; }
    public void setGiaMoi(double giaMoi) { this.giaMoi = giaMoi; }

    public Timestamp getNgayHieuLuc() { return ngayHieuLuc; }
    public void setNgayHieuLuc(Timestamp ngayHieuLuc) { this.ngayHieuLuc = ngayHieuLuc; }

    public String getNguoiDoi() { return nguoiDoi; }
    public void setNguoiDoi(String nguoiDoi) { this.nguoiDoi = nguoiDoi; }

    public String getNguoiThayDoi() { return nguoiDoi; }
    public void setNguoiThayDoi(String nguoiThayDoi) { this.nguoiDoi = nguoiThayDoi; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
