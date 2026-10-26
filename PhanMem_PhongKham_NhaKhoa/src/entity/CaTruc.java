package entity;

import java.sql.Date;
import java.sql.Time;

public class CaTruc {
    private String maCa;
    private String tenCa; // Ca Sáng, Ca Chiều, Ca Tối
    private Date ngayTruc;
    private Time gioBatDau;
    private Time gioKetThuc;
    private int soLuongBacSiToiThieu;
    private int soLuongLeTanToiThieu;
    private int soLuongKhamToiDa;
    private String trangThai; // Đã lên lịch, Đang trực, Đã hoàn thành, Hủy

    public CaTruc() {}

    public CaTruc(String maCa, String tenCa, Date ngayTruc, Time gioBatDau, Time gioKetThuc,
                  int soLuongBacSiToiThieu, int soLuongLeTanToiThieu, int soLuongKhamToiDa, String trangThai) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.ngayTruc = ngayTruc;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.soLuongBacSiToiThieu = soLuongBacSiToiThieu;
        this.soLuongLeTanToiThieu = soLuongLeTanToiThieu;
        this.soLuongKhamToiDa = soLuongKhamToiDa;
        this.trangThai = trangThai;
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }

    public Date getNgayTruc() { return ngayTruc; }
    public void setNgayTruc(Date ngayTruc) { this.ngayTruc = ngayTruc; }

    public Time getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(Time gioBatDau) { this.gioBatDau = gioBatDau; }

    public Time getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(Time gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public int getSoLuongBacSiToiThieu() { return soLuongBacSiToiThieu; }
    public void setSoLuongBacSiToiThieu(int soLuongBacSiToiThieu) { this.soLuongBacSiToiThieu = soLuongBacSiToiThieu; }

    public int getSoLuongLeTanToiThieu() { return soLuongLeTanToiThieu; }
    public void setSoLuongLeTanToiThieu(int soLuongLeTanToiThieu) { this.soLuongLeTanToiThieu = soLuongLeTanToiThieu; }

    public int getSoLuongKhamToiDa() { return soLuongKhamToiDa; }
    public void setSoLuongKhamToiDa(int soLuongKhamToiDa) { this.soLuongKhamToiDa = soLuongKhamToiDa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenCa + " (" + (ngayTruc != null ? ngayTruc.toString() : "") + " - " + maCa + ")";
    }
}
