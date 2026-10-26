package entity;

public class NCC {
    private String maNCC;
    private String tenNCC;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String nguoiLienHe;
    private String trangThai;

    public NCC() {}

    public NCC(String maNCC, String tenNCC, String soDienThoai, String email,
               String diaChi, String nguoiLienHe, String trangThai) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.nguoiLienHe = nguoiLienHe;
        this.trangThai = trangThai;
    }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getNguoiLienHe() { return nguoiLienHe; }
    public void setNguoiLienHe(String nguoiLienHe) { this.nguoiLienHe = nguoiLienHe; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenNCC + " (" + soDienThoai + ")";
    }
}
