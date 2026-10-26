package entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class LichHen {
    private String maLichHen;
    private String maBN;
    private String maNhaSi;
    private Date ngayHen;
    private Time gioHen;
    private String lyDoKham;
    private String ghiChu;
    private String trangThai; // Đã lên lịch, Đã xác nhận, Đang khám, Hoàn thành, Hủy lịch, Dời lịch
    private Timestamp ngayTao;

    // Trường bổ trợ hiển thị
    private String tenBenhNhan;
    private String sdtBenhNhan;
    private String tenNhaSi;

    public LichHen() {}

    public LichHen(String maLichHen, String maBN, String maNhaSi, Date ngayHen,
                   Time gioHen, String lyDoKham, String ghiChu, String trangThai) {
        this.maLichHen = maLichHen;
        this.maBN = maBN;
        this.maNhaSi = maNhaSi;
        this.ngayHen = ngayHen;
        this.gioHen = gioHen;
        this.lyDoKham = lyDoKham;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaLichHen() { return maLichHen; }
    public void setMaLichHen(String maLichHen) { this.maLichHen = maLichHen; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getMaNhaSi() { return maNhaSi; }
    public void setMaNhaSi(String maNhaSi) { this.maNhaSi = maNhaSi; }

    public Date getNgayHen() { return ngayHen; }
    public void setNgayHen(Date ngayHen) { this.ngayHen = ngayHen; }

    public Time getGioHen() { return gioHen; }
    public void setGioHen(Time gioHen) { this.gioHen = gioHen; }

    public String getLyDoKham() { return lyDoKham; }
    public void setLyDoKham(String lyDoKham) { this.lyDoKham = lyDoKham; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }

    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }

    public String getSdtBenhNhan() { return sdtBenhNhan; }
    public void setSdtBenhNhan(String sdtBenhNhan) { this.sdtBenhNhan = sdtBenhNhan; }

    public String getTenNhaSi() { return tenNhaSi; }
    public void setTenNhaSi(String tenNhaSi) { this.tenNhaSi = tenNhaSi; }

    @Override
    public String toString() {
        return "LichHen[" + maLichHen + ", BN=" + maBN + ", NS=" + maNhaSi + ", " + ngayHen + " " + gioHen + ", TT=" + trangThai + "]";
    }
}
