package entity;

import java.io.Serializable;
import java.util.Objects;

public class ThongKeCaTruc implements Serializable {
    private static final long serialVersionUID = 1L;

    private CaTruc caTruc;
    private NhanVien nhanVien;
    private String khungGio;
    private int soLuongBN;
    private String vatTuTieuHao;
    private String trangThai;

    public ThongKeCaTruc() {
    }

    public ThongKeCaTruc(String caTrucStr, String khungGio, int soLuongBN, String vatTuTieuHao, String trangThai) {
        this.caTruc = caTrucStr != null ? new CaTruc(caTrucStr) : null;
        this.khungGio = khungGio;
        this.soLuongBN = soLuongBN;
        this.vatTuTieuHao = vatTuTieuHao;
        this.trangThai = trangThai;
    }

    public ThongKeCaTruc(CaTruc caTruc, NhanVien nhanVien, String khungGio, int soLuongBN, String vatTuTieuHao,
            String trangThai) {
        this.caTruc = caTruc;
        this.nhanVien = nhanVien;
        this.khungGio = khungGio;
        this.soLuongBN = soLuongBN;
        this.vatTuTieuHao = vatTuTieuHao;
        this.trangThai = trangThai;
    }

    public CaTruc getCaTrucObj() {
        return caTruc;
    }

    public void setCaTrucObj(CaTruc caTruc) {
        this.caTruc = caTruc;
    }

    public String getCaTruc() {
        return caTruc != null ? caTruc.getTenCa() != null ? caTruc.getTenCa() : caTruc.getMaCa() : "";
    }

    public void setCaTruc(String caTrucStr) {
        if (this.caTruc == null)
            this.caTruc = new CaTruc(caTrucStr);
        else
            this.caTruc.setMaCa(caTrucStr);
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getKhungGio() {
        return khungGio;
    }

    public void setKhungGio(String khungGio) {
        this.khungGio = khungGio;
    }

    public int getSoLuongBN() {
        return soLuongBN;
    }

    public void setSoLuongBN(int soLuongBN) {
        this.soLuongBN = soLuongBN;
    }

    public String getVatTuTieuHao() {
        return vatTuTieuHao;
    }

    public void setVatTuTieuHao(String vatTuTieuHao) {
        this.vatTuTieuHao = vatTuTieuHao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ThongKeCaTruc))
            return false;
        ThongKeCaTruc that = (ThongKeCaTruc) o;
        return Objects.equals(caTruc, that.caTruc) && Objects.equals(khungGio, that.khungGio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caTruc, khungGio);
    }

    @Override
    public String toString() {
        return "ThongKeCaTruc{" +
                "caTruc=" + getCaTruc() +
                ", khungGio='" + khungGio + '\'' +
                ", soLuongBN=" + soLuongBN +
                ", vatTuTieuHao='" + vatTuTieuHao + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
