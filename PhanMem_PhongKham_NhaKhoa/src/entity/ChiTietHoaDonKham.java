package entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể Chi tiết hóa đơn khám bệnh.
 * Một chi tiết biểu diễn một mặt hàng được thanh toán (Thuốc hoặc Dịch vụ).
 * Lựa chọn thiết kế: sử dụng hai trường đối tượng dichVu và thuoc, trong đó đúng một trường khác null.
 * Nghiệp vụ: KHÔNG lưu thành tiền, KHÔNG lưu cách dùng/liều dùng (thuộc đơn thuốc).
 */
public class ChiTietHoaDonKham implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maCTHD;
    private HoaDonKham hoaDon;
    private DichVu dichVu;
    private Thuoc thuoc;
    private NhanVien nhanVien; // Bác sĩ / Nhân viên trực tiếp thực hiện dịch vụ hoặc kê đơn
    private int soLuong;
    private double donGia; // Giá chốt tại thời điểm lập hóa đơn

    public ChiTietHoaDonKham() {
    }

    public ChiTietHoaDonKham(int maCTHD) {
        this.maCTHD = maCTHD;
    }

    /**
     * Khởi tạo chi tiết hóa đơn cho Dịch vụ
     */
    public ChiTietHoaDonKham(DichVu dichVu, int soLuong, double donGia) {
        this.dichVu = dichVu;
        this.thuoc = null;
        this.soLuong = Math.max(1, soLuong);
        this.donGia = donGia > 0 ? donGia : (dichVu != null ? dichVu.getGiaNiemYet() : 0);
    }

    /**
     * Khởi tạo chi tiết hóa đơn cho Thuốc
     */
    public ChiTietHoaDonKham(Thuoc thuoc, int soLuong, double donGia) {
        this.thuoc = thuoc;
        this.dichVu = null;
        this.soLuong = Math.max(1, soLuong);
        this.donGia = donGia > 0 ? donGia : (thuoc != null ? thuoc.getGiaBan() : 0);
    }

    /**
     * Khởi tạo đầy đủ
     */
    public ChiTietHoaDonKham(int maCTHD, HoaDonKham hoaDon, DichVu dichVu, Thuoc thuoc, int soLuong, double donGia) {
        this.maCTHD = maCTHD;
        this.hoaDon = hoaDon;
        this.dichVu = dichVu;
        this.thuoc = thuoc;
        this.soLuong = Math.max(1, soLuong);
        this.donGia = donGia;
    }

    public int getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(int maCTHD) {
        this.maCTHD = maCTHD;
    }

    public HoaDonKham getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDonKham hoaDon) {
        this.hoaDon = hoaDon;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
        if (dichVu != null) {
            this.thuoc = null;
        }
    }

    public Thuoc getThuoc() {
        return thuoc;
    }

    public void setThuoc(Thuoc thuoc) {
        this.thuoc = thuoc;
        if (thuoc != null) {
            this.dichVu = null;
        }
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getMaNV() {
        return nhanVien != null ? nhanVien.getMaNV() : null;
    }

    public String getTenNV() {
        return nhanVien != null ? nhanVien.getHoTen() : "";
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = Math.max(1, soLuong);
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    /**
     * Phương thức tính thành tiền theo yêu cầu: soLuong * donGia (không lưu trường dữ liệu).
     */
    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public boolean isDichVu() {
        return dichVu != null;
    }

    public boolean isThuoc() {
        return thuoc != null;
    }

    public String getLoaiMatHang() {
        return isDichVu() ? "Dịch vụ" : "Thuốc";
    }

    public String getMaMatHang() {
        if (isDichVu()) return dichVu.getMaDichVu();
        if (isThuoc()) return thuoc.getMaThuoc();
        return "";
    }

    public String getTenMatHang() {
        if (isDichVu()) return dichVu.getTenDichVu();
        if (isThuoc()) return thuoc.getTenThuoc();
        return "";
    }

    public String getDonViTinh() {
        if (isThuoc()) return thuoc.getDonViTinh();
        return "Lần";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDonKham)) return false;
        ChiTietHoaDonKham that = (ChiTietHoaDonKham) o;
        return maCTHD == that.maCTHD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTHD);
    }

    @Override
    public String toString() {
        return getLoaiMatHang() + ": " + getTenMatHang() + " [SL: " + soLuong + ", ĐG: " + donGia + ", TT: " + tinhThanhTien() + "]";
    }
}

