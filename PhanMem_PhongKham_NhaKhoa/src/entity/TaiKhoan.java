package entity;

import java.sql.Timestamp;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhau;
    private Timestamp ngayTao;
    private String trangThai; // Hoạt động, Bị khóa, Chờ đổi mật khẩu
    private int soLanSaiMatKhau;
    private String maNV;
    private String maVaiTro;

    // Các trường hiển thị bổ trợ
    private String hoTenNV;
    private String tenVaiTro;

    public TaiKhoan() {}

    public TaiKhoan(String tenDangNhap, String matKhau, Timestamp ngayTao,
                    String trangThai, int soLanSaiMatKhau, String maNV, String maVaiTro) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.soLanSaiMatKhau = soLanSaiMatKhau;
        this.maNV = maNV;
        this.maVaiTro = maVaiTro;
    }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getSoLanSaiMatKhau() { return soLanSaiMatKhau; }
    public void setSoLanSaiMatKhau(int soLanSaiMatKhau) { this.soLanSaiMatKhau = soLanSaiMatKhau; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(String maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getHoTenNV() { return hoTenNV; }
    public void setHoTenNV(String hoTenNV) { this.hoTenNV = hoTenNV; }

    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }

    @Override
    public String toString() {
        return tenDangNhap + " (" + (tenVaiTro != null ? tenVaiTro : maVaiTro) + ")";
    }
}
