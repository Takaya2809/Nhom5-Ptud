package entity;

import java.sql.Timestamp;
import java.util.Objects;

public class LichSuGiaDichVu {
    private int maLichSu;
    private DichVu dichVu;
    private double giaCu;
    private double giaMoi;
    private Timestamp ngayHieuLuc;
    private NhanVien nguoiDoi;
    private String lyDo;

    public LichSuGiaDichVu() {
    }

    public LichSuGiaDichVu(int maLichSu) {
        this.maLichSu = maLichSu;
    }

    public LichSuGiaDichVu(int maLichSu, String maDichVu, double giaCu, double giaMoi,
            Timestamp ngayHieuLuc, String nguoiDoi, String lyDo) {
        this.maLichSu = maLichSu;
        this.dichVu = maDichVu != null ? new DichVu(maDichVu) : null;
        this.giaCu = giaCu;
        this.giaMoi = giaMoi;
        this.ngayHieuLuc = ngayHieuLuc;
        this.nguoiDoi = nguoiDoi != null ? new NhanVien(nguoiDoi) : null;
        this.lyDo = lyDo;
    }

    public LichSuGiaDichVu(int maLichSu, DichVu dichVu, double giaCu, double giaMoi,
            Timestamp ngayHieuLuc, NhanVien nguoiDoi, String lyDo) {
        this.maLichSu = maLichSu;
        this.dichVu = dichVu;
        this.giaCu = giaCu;
        this.giaMoi = giaMoi;
        this.ngayHieuLuc = ngayHieuLuc;
        this.nguoiDoi = nguoiDoi;
        this.lyDo = lyDo;
    }

    public int getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(int maLichSu) {
        this.maLichSu = maLichSu;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
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

    public double getGiaCu() {
        return giaCu;
    }

    public void setGiaCu(double giaCu) {
        this.giaCu = giaCu;
    }

    public double getGiaMoi() {
        return giaMoi;
    }

    public void setGiaMoi(double giaMoi) {
        this.giaMoi = giaMoi;
    }

    public Timestamp getNgayHieuLuc() {
        return ngayHieuLuc;
    }

    public void setNgayHieuLuc(Timestamp ngayHieuLuc) {
        this.ngayHieuLuc = ngayHieuLuc;
    }

    public NhanVien getNguoiDoi() {
        return nguoiDoi;
    }

    public void setNguoiDoi(NhanVien nguoiDoi) {
        this.nguoiDoi = nguoiDoi;
    }

    public String getNguoiDoiStr() {
        return nguoiDoi != null ? nguoiDoi.getMaNV() : null;
    }

    public void setNguoiDoi(String nguoiDoi) {
        if (this.nguoiDoi == null)
            this.nguoiDoi = new NhanVien(nguoiDoi);
        else
            this.nguoiDoi.setMaNV(nguoiDoi);
    }

    public String getNguoiThayDoi() {
        return getNguoiDoiStr();
    }

    public void setNguoiThayDoi(String nguoiThayDoi) {
        setNguoiDoi(nguoiThayDoi);
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LichSuGiaDichVu))
            return false;
        LichSuGiaDichVu that = (LichSuGiaDichVu) o;
        return maLichSu == that.maLichSu;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLichSu);
    }

    @Override
    public String toString() {
        return "LichSuGia #" + maLichSu + " [" + getMaDichVu() + ": " + giaCu + " -> " + giaMoi + "]";
    }
}
