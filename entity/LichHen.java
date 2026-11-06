package entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

public class LichHen {
    private String maLichHen;
    private BenhNhan benhNhan;
    private NhanVien bacSi;
    private DichVu dichVu;
    private Date ngayHen;
    private Time gioHen;
    private String lyDoKham;
    private String ghiChu;
    private String trangThai; // Đã lên lịch, Đã xác nhận, Đang khám, Hoàn thành, Hủy lịch, Dời lịch
    private Timestamp ngayTao;

    public LichHen() {
    }

    public LichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public LichHen(String maLichHen, String maBN, String maNhaSi, Date ngayHen,
            Time gioHen, String lyDoKham, String ghiChu, String trangThai) {
        this.maLichHen = maLichHen;
        this.benhNhan = maBN != null ? new BenhNhan(maBN) : null;
        this.bacSi = maNhaSi != null ? new NhanVien(maNhaSi) : null;
        this.ngayHen = ngayHen;
        this.gioHen = gioHen;
        this.lyDoKham = lyDoKham;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public LichHen(String maLichHen, BenhNhan benhNhan, NhanVien bacSi, DichVu dichVu,
            Date ngayHen, Time gioHen, String lyDoKham, String ghiChu, String trangThai) {
        this.maLichHen = maLichHen;
        this.benhNhan = benhNhan;
        this.bacSi = bacSi;
        this.dichVu = dichVu;
        this.ngayHen = ngayHen;
        this.gioHen = gioHen;
        this.lyDoKham = lyDoKham;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public NhanVien getBacSi() {
        return bacSi;
    }

    public void setBacSi(NhanVien bacSi) {
        this.bacSi = bacSi;
    }

    public NhanVien getNhaSi() {
        return bacSi;
    }

    public void setNhaSi(NhanVien nhaSi) {
        this.bacSi = nhaSi;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

    public String getMaBN() {
        return benhNhan != null ? benhNhan.getMaBN() : null;
    }

    public void setMaBN(String maBN) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan(maBN);
        else
            this.benhNhan.setMaBN(maBN);
    }

    public String getMaNhaSi() {
        return bacSi != null ? bacSi.getMaNV() : null;
    }

    public void setMaNhaSi(String maNhaSi) {
        if (this.bacSi == null)
            this.bacSi = new NhanVien(maNhaSi);
        else
            this.bacSi.setMaNV(maNhaSi);
    }

    public String getMaDichVu() {
        return dichVu != null ? dichVu.getMaDichVu() : null;
    }

    public void setMaDichVu(String maDichVu) {
        if (this.dichVu == null)
            this.dichVu = new DichVu(maDichVu);
        else
            this.dichVu.setMaDichVu(maDichVu);
    }

    public Date getNgayHen() {
        return ngayHen;
    }

    public void setNgayHen(Date ngayHen) {
        this.ngayHen = ngayHen;
    }

    public Time getGioHen() {
        return gioHen;
    }

    public void setGioHen(Time gioHen) {
        this.gioHen = gioHen;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTenBenhNhan() {
        return benhNhan != null ? benhNhan.getHoTen() : null;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan();
        this.benhNhan.setHoTen(tenBenhNhan);
    }

    public String getSdtBenhNhan() {
        return benhNhan != null ? benhNhan.getSoDienThoai() : null;
    }

    public void setSdtBenhNhan(String sdtBenhNhan) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan();
        this.benhNhan.setSoDienThoai(sdtBenhNhan);
    }

    public String getTenNhaSi() {
        return bacSi != null ? bacSi.getHoTen() : null;
    }

    public void setTenNhaSi(String tenNhaSi) {
        if (this.bacSi == null)
            this.bacSi = new NhanVien();
        this.bacSi.setHoTen(tenNhaSi);
    }

    public String getTenDichVu() {
        return dichVu != null ? dichVu.getTenDichVu() : null;
    }

    public void setTenDichVu(String tenDichVu) {
        if (this.dichVu == null)
            this.dichVu = new DichVu();
        this.dichVu.setTenDichVu(tenDichVu);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LichHen))
            return false;
        LichHen lichHen = (LichHen) o;
        return Objects.equals(maLichHen, lichHen.maLichHen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLichHen);
    }

    @Override
    public String toString() {
        return "LichHen[" + maLichHen + ", BN=" + getMaBN() + ", BS=" + getMaNhaSi() + ", " + ngayHen + " " + gioHen
                + ", TT=" + trangThai + "]";
    }
}
