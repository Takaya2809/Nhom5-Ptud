package entity;

import java.sql.Date;

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
    private String trangThai; // Đang làm việc, Đã nghỉ việc, Tạm nghỉ

    public NhanVien() {}

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
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }

    public String getBangCapChungChi() { return bangCapChungChi; }
    public void setBangCapChungChi(String bangCapChungChi) { this.bangCapChungChi = bangCapChungChi; }

    public Date getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(Date ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return hoTen + " (" + maNV + " - " + chucDanh + ")";
    }
}
