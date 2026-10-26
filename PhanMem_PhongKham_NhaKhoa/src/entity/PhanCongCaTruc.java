package entity;

public class PhanCongCaTruc {
    private int maPhanCong;
    private String maNV;
    private String maCa;
    private String vaiTroTrongCa; // Bác sĩ chính, Phụ tá, Lễ tân
    private String trangThaiDiemDanh; // Chưa đến, Đã điểm danh, Vắng mặt
    private String ghiChu;

    // Bổ trợ hiển thị
    private String hoTenNV;
    private String tenCa;

    public PhanCongCaTruc() {}

    public PhanCongCaTruc(int maPhanCong, String maNV, String maCa,
                          String vaiTroTrongCa, String trangThaiDiemDanh, String ghiChu) {
        this.maPhanCong = maPhanCong;
        this.maNV = maNV;
        this.maCa = maCa;
        this.vaiTroTrongCa = vaiTroTrongCa;
        this.trangThaiDiemDanh = trangThaiDiemDanh;
        this.ghiChu = ghiChu;
    }

    public int getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(int maPhanCong) { this.maPhanCong = maPhanCong; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public String getVaiTroTrongCa() { return vaiTroTrongCa; }
    public void setVaiTroTrongCa(String vaiTroTrongCa) { this.vaiTroTrongCa = vaiTroTrongCa; }

    public String getTrangThaiDiemDanh() { return trangThaiDiemDanh; }
    public void setTrangThaiDiemDanh(String trangThaiDiemDanh) { this.trangThaiDiemDanh = trangThaiDiemDanh; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getHoTenNV() { return hoTenNV; }
    public void setHoTenNV(String hoTenNV) { this.hoTenNV = hoTenNV; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
}
