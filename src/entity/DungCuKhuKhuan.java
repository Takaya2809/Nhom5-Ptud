package entity;

import java.io.Serializable;
import java.util.Objects;

public class DungCuKhuKhuan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maDungCu;
    private String tenDungCu;
    private String loaiDungCu;
    private String trangThai;
    private String thoiGianCapNhat;
    private NCC nhaCungCap;

    public DungCuKhuKhuan() {
    }

    public DungCuKhuKhuan(String maDungCu) {
        this.maDungCu = maDungCu;
    }

    public DungCuKhuKhuan(String maDungCu, String tenDungCu, String loaiDungCu, String trangThai,
            String thoiGianCapNhat) {
        this(maDungCu, tenDungCu, loaiDungCu, trangThai, thoiGianCapNhat, null);
    }

    public DungCuKhuKhuan(String maDungCu, String tenDungCu, String loaiDungCu, String trangThai,
            String thoiGianCapNhat, NCC nhaCungCap) {
        this.maDungCu = maDungCu;
        this.tenDungCu = tenDungCu;
        this.loaiDungCu = loaiDungCu;
        this.trangThai = trangThai;
        this.thoiGianCapNhat = thoiGianCapNhat;
        this.nhaCungCap = nhaCungCap;
    }

    public String getMaDungCu() {
        return maDungCu;
    }

    public void setMaDungCu(String maDungCu) {
        this.maDungCu = maDungCu;
    }

    public String getTenDungCu() {
        return tenDungCu;
    }

    public void setTenDungCu(String tenDungCu) {
        this.tenDungCu = tenDungCu;
    }

    public String getLoaiDungCu() {
        return loaiDungCu;
    }

    public void setLoaiDungCu(String loaiDungCu) {
        this.loaiDungCu = loaiDungCu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getThoiGianCapNhat() {
        return thoiGianCapNhat;
    }

    public void setThoiGianCapNhat(String thoiGianCapNhat) {
        this.thoiGianCapNhat = thoiGianCapNhat;
    }

    public NCC getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NCC nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DungCuKhuKhuan))
            return false;
        DungCuKhuKhuan that = (DungCuKhuKhuan) o;
        return Objects.equals(maDungCu, that.maDungCu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDungCu);
    }

    @Override
    public String toString() {
        return tenDungCu + " (" + maDungCu + " - " + trangThai + ")";
    }
}
