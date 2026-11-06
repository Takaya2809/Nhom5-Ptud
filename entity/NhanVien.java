package entity;

import java.sql.Date;
import java.util.Objects;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private Date ngaySinh;
    private String gioiTinh;
    private String soDienThoai;
    private String email;
    private String chucDanh;
    private String bangCapChungChi;
    private Date ngayVaoLam;
    private String trangThai;
    private VaiTro vaiTro;

    public NhanVien() {
    }

    public NhanVien(String maNV) {
        this.maNV = maNV;
        this.vaiTro = suyDoanVaiTro(maNV, null);
    }

    public NhanVien(String maNV, String hoTen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.vaiTro = suyDoanVaiTro(maNV, null);
    }

    public NhanVien(String maNV, String hoTen, Date ngaySinh, String gioiTinh,
            String soDienThoai, String email, String chucDanh,
            String bangCapChungChi, Date ngayVaoLam, String trangThai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.chucDanh = chucDanh;
        this.bangCapChungChi = bangCapChungChi;
        this.ngayVaoLam = ngayVaoLam;
        this.trangThai = trangThai;
        this.vaiTro = suyDoanVaiTro(maNV, chucDanh);
    }

    public NhanVien(String maNV, String hoTen, Date ngaySinh, String gioiTinh,
            String soDienThoai, String email, String chucDanh,
            String bangCapChungChi, Date ngayVaoLam, String trangThai, VaiTro vaiTro) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.chucDanh = chucDanh;
        this.bangCapChungChi = bangCapChungChi;
        this.ngayVaoLam = ngayVaoLam;
        this.trangThai = trangThai;
        this.vaiTro = (vaiTro != null) ? vaiTro : suyDoanVaiTro(maNV, chucDanh);
    }

    private static VaiTro suyDoanVaiTro(String maNV, String chucDanh) {
        if (maNV != null) {
            String upper = maNV.toUpperCase();
            if (upper.startsWith("QL"))
                return VaiTro.QUAN_LY;
            if (upper.startsWith("BS"))
                return VaiTro.BAC_SI;
            if (upper.startsWith("LT"))
                return VaiTro.LE_TAN;
            if (upper.startsWith("YT"))
                return VaiTro.Y_TA;
        }
        if (chucDanh != null) {
            String lower = chucDanh.toLowerCase();
            if (lower.contains("quản lý"))
                return VaiTro.QUAN_LY;
            if (lower.contains("bác sĩ") || lower.contains("nha sĩ"))
                return VaiTro.BAC_SI;
            if (lower.contains("lễ tân"))
                return VaiTro.LE_TAN;
            if (lower.contains("y tá") || lower.contains("điều dưỡng"))
                return VaiTro.Y_TA;
        }
        return VaiTro.BAC_SI;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
        if (this.vaiTro == null) {
            this.vaiTro = suyDoanVaiTro(maNV, this.chucDanh);
        }
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(String chucDanh) {
        this.chucDanh = chucDanh;
        if (this.vaiTro == null) {
            this.vaiTro = suyDoanVaiTro(this.maNV, chucDanh);
        }
    }

    public String getBangCapChungChi() {
        return bangCapChungChi;
    }

    public void setBangCapChungChi(String bangCapChungChi) {
        this.bangCapChungChi = bangCapChungChi;
    }

    public Date getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(Date ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NhanVien))
            return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNV, nhanVien.maNV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

    @Override
    public String toString() {
        return hoTen + " (" + maNV + " - " + chucDanh + ")";
    }
}
