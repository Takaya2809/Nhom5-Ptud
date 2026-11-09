package entity;

import java.sql.Timestamp;
import java.util.Objects;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhau;
    private Timestamp ngayTao;
    private String trangThai; // Hoạt động, Bị khóa, Chờ đổi mật khẩu
    private int soLanSaiMatKhau;
    private NhanVien nhanVien;
    private VaiTro vaiTro;

    public TaiKhoan() {
    }

    public TaiKhoan(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public TaiKhoan(String tenDangNhap, String matKhau, Timestamp ngayTao,
            String trangThai, int soLanSaiMatKhau, String maNV, String maVaiTro) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.soLanSaiMatKhau = soLanSaiMatKhau;
        this.nhanVien = maNV != null ? new NhanVien(maNV) : null;
        this.vaiTro = VaiTro.fromMa(maVaiTro);
        if (this.vaiTro == null) {
            this.vaiTro = VaiTro.fromTen(maVaiTro);
        }
        if (this.nhanVien != null && this.vaiTro != null) {
            this.nhanVien.setVaiTro(this.vaiTro);
        }
    }

    public TaiKhoan(String tenDangNhap, String matKhau, Timestamp ngayTao,
            String trangThai, int soLanSaiMatKhau, NhanVien nhanVien, VaiTro vaiTro) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.soLanSaiMatKhau = soLanSaiMatKhau;
        this.nhanVien = nhanVien;
        this.vaiTro = vaiTro;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getSoLanSaiMatKhau() {
        return soLanSaiMatKhau;
    }

    public void setSoLanSaiMatKhau(int soLanSaiMatKhau) {
        this.soLanSaiMatKhau = soLanSaiMatKhau;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getMaNV() {
        return nhanVien != null ? nhanVien.getMaNV() : null;
    }

    public void setMaNV(String maNV) {
        if (this.nhanVien == null)
            this.nhanVien = new NhanVien(maNV);
        else
            this.nhanVien.setMaNV(maNV);
    }

    public String getMaVaiTro() {
        return vaiTro != null ? vaiTro.getMaVaiTro() : null;
    }

    public void setMaVaiTro(String maVaiTro) {
        this.vaiTro = VaiTro.fromMa(maVaiTro);
        if (this.vaiTro == null)
            this.vaiTro = VaiTro.fromTen(maVaiTro);
    }

    public String getHoTenNV() {
        return nhanVien != null ? nhanVien.getHoTen() : null;
    }

    public void setHoTenNV(String hoTenNV) {
        if (this.nhanVien == null)
            this.nhanVien = new NhanVien();
        this.nhanVien.setHoTen(hoTenNV);
    }

    public String getTenVaiTro() {
        return vaiTro != null ? vaiTro.getTenVaiTro() : null;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.vaiTro = VaiTro.fromTen(tenVaiTro);
        if (this.vaiTro == null)
            this.vaiTro = VaiTro.fromMa(tenVaiTro);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaiKhoan))
            return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        return Objects.equals(tenDangNhap, taiKhoan.tenDangNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenDangNhap);
    }

    @Override
    public String toString() {
        return tenDangNhap + " (" + (getTenVaiTro() != null ? getTenVaiTro() : getMaVaiTro()) + ")";
    }
}
