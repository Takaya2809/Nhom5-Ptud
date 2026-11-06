package entity;

import java.util.Objects;

public class PhanCongCaTruc {
    private int maPhanCong;
    private NhanVien nhanVien;
    private CaTruc caTruc;
    private String vaiTroTrongCa; // Bác sĩ chính, Phụ tá, Lễ tân
    private String trangThaiDiemDanh; // Chưa đến, Đã điểm danh, Vắng mặt
    private String ghiChu;

    public PhanCongCaTruc() {
    }

    public PhanCongCaTruc(int maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public PhanCongCaTruc(int maPhanCong, String maNV, String maCa,
            String vaiTroTrongCa, String trangThaiDiemDanh, String ghiChu) {
        this.maPhanCong = maPhanCong;
        this.nhanVien = maNV != null ? new NhanVien(maNV) : null;
        this.caTruc = maCa != null ? new CaTruc(maCa) : null;
        this.vaiTroTrongCa = vaiTroTrongCa;
        this.trangThaiDiemDanh = trangThaiDiemDanh;
        this.ghiChu = ghiChu;
    }

    public PhanCongCaTruc(int maPhanCong, NhanVien nhanVien, CaTruc caTruc,
            String vaiTroTrongCa, String trangThaiDiemDanh, String ghiChu) {
        this.maPhanCong = maPhanCong;
        this.nhanVien = nhanVien;
        this.caTruc = caTruc;
        this.vaiTroTrongCa = vaiTroTrongCa;
        this.trangThaiDiemDanh = trangThaiDiemDanh;
        this.ghiChu = ghiChu;
    }

    public int getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(int maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public CaTruc getCaTruc() {
        return caTruc;
    }

    public void setCaTruc(CaTruc caTruc) {
        this.caTruc = caTruc;
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

    public String getMaCa() {
        return caTruc != null ? caTruc.getMaCa() : null;
    }

    public void setMaCa(String maCa) {
        if (this.caTruc == null)
            this.caTruc = new CaTruc(maCa);
        else
            this.caTruc.setMaCa(maCa);
    }

    public String getVaiTroTrongCa() {
        return vaiTroTrongCa;
    }

    public void setVaiTroTrongCa(String vaiTroTrongCa) {
        this.vaiTroTrongCa = vaiTroTrongCa;
    }

    public String getTrangThaiDiemDanh() {
        return trangThaiDiemDanh;
    }

    public void setTrangThaiDiemDanh(String trangThaiDiemDanh) {
        this.trangThaiDiemDanh = trangThaiDiemDanh;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getHoTenNV() {
        return nhanVien != null ? nhanVien.getHoTen() : null;
    }

    public void setHoTenNV(String hoTenNV) {
        if (this.nhanVien == null)
            this.nhanVien = new NhanVien();
        this.nhanVien.setHoTen(hoTenNV);
    }

    public String getTenCa() {
        return caTruc != null ? caTruc.getTenCa() : null;
    }

    public void setTenCa(String tenCa) {
        if (this.caTruc == null)
            this.caTruc = new CaTruc();
        this.caTruc.setTenCa(tenCa);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PhanCongCaTruc))
            return false;
        PhanCongCaTruc that = (PhanCongCaTruc) o;
        return maPhanCong == that.maPhanCong;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhanCong);
    }

    @Override
    public String toString() {
        return "PhanCong #" + maPhanCong + " [" + getMaNV() + " - " + getMaCa() + "]";
    }
}
