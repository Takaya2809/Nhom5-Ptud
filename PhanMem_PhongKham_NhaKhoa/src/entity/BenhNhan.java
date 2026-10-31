package entity;

import java.sql.Date;

public class BenhNhan {
    private String maBN;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;
    private Date ngaySinh;
    private String tienSuBenh;
    private String loaiKhach; // Khách mới, Thường, Thân thiết, VIP
    private int diemTichLuy;
    private String trangThai; // Đang theo dõi, Ngừng theo dõi

    public BenhNhan() {}

    public BenhNhan(String maBN, String hoTen, String soDienThoai, String diaChi,
                    String gioiTinh, Date ngaySinh, String tienSuBenh,
                    String loaiKhach, int diemTichLuy) {
        this(maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, TrangThaiConstants.BN_DANG_THEO_DOI);
    }

    public BenhNhan(String maBN, String hoTen, String soDienThoai, String diaChi,
                    String gioiTinh, Date ngaySinh, String tienSuBenh,
                    String loaiKhach, int diemTichLuy, String trangThai) {
        this.maBN = maBN;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.tienSuBenh = tienSuBenh;
        this.loaiKhach = loaiKhach;
        this.diemTichLuy = diemTichLuy;
        this.trangThai = (trangThai != null && !trangThai.trim().isEmpty()) ? trangThai : TrangThaiConstants.BN_DANG_THEO_DOI;
    }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getTienSuBenh() { return tienSuBenh; }
    public void setTienSuBenh(String tienSuBenh) { this.tienSuBenh = tienSuBenh; }

    public String getLoaiKhach() { return loaiKhach; }
    public void setLoaiKhach(String loaiKhach) { this.loaiKhach = loaiKhach; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return hoTen + " (" + maBN + " - " + soDienThoai + ")";
    }
}
