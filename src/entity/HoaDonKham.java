package entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Thực thể Hóa đơn khám chữa bệnh nha khoa.
 * Quy định nghiệp vụ:
 * - Hóa đơn chứa danh sách chi tiết hóa đơn (dsChiTiet) gồm các dịch vụ và
 * thuốc.
 * - KHÔNG lưu thuộc tính thành tiền / tổng tiền mà tính động bằng phương thức:
 * + tinhTongTienTruocGiam(): tổng thành tiền các chi tiết.
 * + tinhGiamGia(): số tiền giảm dựa theo chính sách ưu đãi (UuDai).
 * + tinhTongTien(): số tiền thực thu = tinhTongTienTruocGiam() - tinhGiamGia().
 */
public class HoaDonKham {
    private int maHD;
    private Timestamp ngayLap;
    private BenhNhan benhNhan;
    private NhanVien nhanVien; // Nhân viên lập hóa đơn
    private UuDai uuDai;
    private String phuongThucTT; // Tiền mặt, Chuyển khoản, Thẻ
    private String trangThai; // Đã thanh toán, Chờ thanh toán, Đã hủy

    // Danh sách chi tiết hóa đơn bắt buộc
    private List<ChiTietHoaDonKham> dsChiTiet = new ArrayList<>();

    // Biến dự phòng tạm thời lưu tỷ lệ giảm trực tiếp nếu không chọn entity UuDai
    private double phanTramGiamTrucTiep = 0;

    public HoaDonKham() {
    }

    public HoaDonKham(int maHD) {
        this.maHD = maHD;
    }

    /**
     * Constructor tương thích dữ liệu cũ
     */
    public HoaDonKham(int maHD, Timestamp ngayLap, String maBN, String maNV,
            String maDichVu, double tongTien, double giamGia, double thanhTien,
            String phuongThucTT, String trangThai) {
        this.maHD = maHD;
        this.ngayLap = ngayLap != null ? ngayLap : new Timestamp(System.currentTimeMillis());
        this.benhNhan = maBN != null ? new BenhNhan(maBN) : null;
        this.nhanVien = maNV != null ? new NhanVien(maNV) : null;
        this.phuongThucTT = phuongThucTT != null ? phuongThucTT : "Tiền mặt";
        this.trangThai = trangThai != null ? trangThai : "Đã thanh toán";

        // Tự tạo một chi tiết mặc định nếu có mã dịch vụ
        if (maDichVu != null && !maDichVu.trim().isEmpty()) {
            double donGia = (tongTien > 0) ? tongTien : thanhTien;
            DichVu dv = new DichVu(maDichVu);
            dv.setGiaNiemYet(donGia);
            ChiTietHoaDonKham ct = new ChiTietHoaDonKham(dv, 1, donGia);
            themChiTiet(ct);
        }
        if (tongTien > 0 && giamGia > 0) {
            this.phanTramGiamTrucTiep = (giamGia / tongTien) * 100.0;
        }
    }

    /**
     * Constructor chuẩn hóa với đối tượng
     */
    public HoaDonKham(int maHD, Timestamp ngayLap, BenhNhan benhNhan, NhanVien nhanVien,
            UuDai uuDai, String phuongThucTT, String trangThai) {
        this.maHD = maHD;
        this.ngayLap = ngayLap != null ? ngayLap : new Timestamp(System.currentTimeMillis());
        this.benhNhan = benhNhan;
        this.nhanVien = nhanVien;
        this.uuDai = uuDai;
        this.phuongThucTT = phuongThucTT != null ? phuongThucTT : "Tiền mặt";
        this.trangThai = trangThai != null ? trangThai : "Đã thanh toán";
    }

    // =========================================================================
    // QUẢN LÝ DANH SÁCH CHI TIẾT HÓA ĐƠN
    // =========================================================================

    public List<ChiTietHoaDonKham> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(List<ChiTietHoaDonKham> dsChiTiet) {
        this.dsChiTiet = dsChiTiet != null ? dsChiTiet : new ArrayList<>();
        for (ChiTietHoaDonKham ct : this.dsChiTiet) {
            ct.setHoaDon(this);
        }
    }

    public void themChiTiet(ChiTietHoaDonKham ct) {
        if (ct != null) {
            ct.setHoaDon(this);
            this.dsChiTiet.add(ct);
        }
    }

    public void xoaChiTiet(ChiTietHoaDonKham ct) {
        this.dsChiTiet.remove(ct);
    }

    public boolean coChiTiet() {
        return !dsChiTiet.isEmpty();
    }

    // =========================================================================
    // CÔNG THỨC TÍNH TIỀN NGHIỆP VỤ (KHÔNG LƯU CỘT CSDL)
    // =========================================================================

    /**
     * Tính tổng tiền trước khi giảm trừ = tổng tinhThanhTien() các chi tiết
     */
    public double tinhTongTienTruocGiam() {
        double tong = 0;
        for (ChiTietHoaDonKham ct : dsChiTiet) {
            tong += ct.tinhThanhTien();
        }
        return tong;
    }

    /**
     * Tính số tiền giảm giá dựa theo chính sách ưu đãi
     */
    public double tinhGiamGia() {
        double tong = tinhTongTienTruocGiam();
        if (tong <= 0)
            return 0;

        if (uuDai != null && uuDai.getPhanTramGiam() > 0) {
            return tong * (uuDai.getPhanTramGiam() / 100.0);
        }
        if (phanTramGiamTrucTiep > 0) {
            return tong * (phanTramGiamTrucTiep / 100.0);
        }
        return 0;
    }

    /**
     * Tính tổng tiền thanh toán thực tế = tinhTongTienTruocGiam() - tinhGiamGia()
     */
    public double tinhTongTien() {
        double tong = tinhTongTienTruocGiam();
        double giam = tinhGiamGia();
        return Math.max(0, tong - giam);
    }

    // =========================================================================
    // GETTER / SETTER TƯƠNG THÍCH ĐỂ GỌI PHƯƠNG THỨC TÍNH TIỀN
    // =========================================================================

    public double getTongTien() {
        return tinhTongTienTruocGiam();
    }

    public double getGiamGia() {
        return tinhGiamGia();
    }

    public double getThanhTien() {
        return tinhTongTien();
    }

    public void setTongTien(double tongTien) {
        // Thuộc tính tính toán động, không lưu trường
    }

    public void setGiamGia(double giamGia) {
        double tong = tinhTongTienTruocGiam();
        if (tong > 0 && giamGia >= 0) {
            this.phanTramGiamTrucTiep = (giamGia / tong) * 100.0;
        }
    }

    public void setThanhTien(double thanhTien) {
        // Thuộc tính tính toán động, không lưu trường
    }

    public double getPhanTramGiamTrucTiep() {
        return phanTramGiamTrucTiep;
    }

    public void setPhanTramGiamTrucTiep(double phanTramGiamTrucTiep) {
        this.phanTramGiamTrucTiep = phanTramGiamTrucTiep;
    }

    // =========================================================================
    // CÁC THUỘC TÍNH THỰC THỂ
    // =========================================================================

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public Timestamp getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Timestamp ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public UuDai getUuDai() {
        return uuDai;
    }

    public void setUuDai(UuDai uuDai) {
        this.uuDai = uuDai;
    }

    public String getPhuongThucTT() {
        return phuongThucTT;
    }

    public void setPhuongThucTT(String phuongThucTT) {
        this.phuongThucTT = phuongThucTT;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Getter / Setter tiện ích
    public String getMaBN() {
        return benhNhan != null ? benhNhan.getMaBN() : null;
    }

    public void setMaBN(String maBN) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan(maBN);
        else
            this.benhNhan.setMaBN(maBN);
    }

    public String getTenBN() {
        return benhNhan != null ? benhNhan.getHoTen() : null;
    }

    public void setTenBN(String tenBN) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan();
        this.benhNhan.setHoTen(tenBN);
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

    public String getTenNV() {
        return nhanVien != null ? nhanVien.getHoTen() : null;
    }

    public void setTenNV(String tenNV) {
        if (this.nhanVien == null)
            this.nhanVien = new NhanVien();
        this.nhanVien.setHoTen(tenNV);
    }

    public String getMaDichVu() {
        if (!dsChiTiet.isEmpty()) {
            for (ChiTietHoaDonKham ct : dsChiTiet) {
                if (ct.isDichVu())
                    return ct.getDichVu().getMaDichVu();
            }
            return dsChiTiet.get(0).getMaMatHang();
        }
        return null;
    }

    public void setMaDichVu(String maDichVu) {
        if (maDichVu != null && !maDichVu.trim().isEmpty()) {
            boolean tonTai = false;
            for (ChiTietHoaDonKham ct : dsChiTiet) {
                if (ct.isDichVu() && maDichVu.equals(ct.getDichVu().getMaDichVu())) {
                    tonTai = true;
                    break;
                }
            }
            if (!tonTai) {
                themChiTiet(new ChiTietHoaDonKham(new DichVu(maDichVu), 1, 0));
            }
        }
    }

    public String getTenDichVu() {
        if (!dsChiTiet.isEmpty()) {
            for (ChiTietHoaDonKham ct : dsChiTiet) {
                if (ct.isDichVu() && ct.getDichVu() != null && ct.getDichVu().getTenDichVu() != null) {
                    return ct.getDichVu().getTenDichVu();
                }
            }
            return dsChiTiet.get(0).getTenMatHang();
        }
        return "Dịch vụ nha khoa";
    }

    public void setTenDichVu(String tenDichVu) {
        if (!dsChiTiet.isEmpty()) {
            for (ChiTietHoaDonKham ct : dsChiTiet) {
                if (ct.isDichVu() && ct.getDichVu() != null) {
                    ct.getDichVu().setTenDichVu(tenDichVu);
                    return;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HoaDonKham))
            return false;
        HoaDonKham that = (HoaDonKham) o;
        return maHD == that.maHD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD);
    }

    @Override
    public String toString() {
        return "HoaDonKham[" + maHD + ", BN=" + getMaBN() + ", SL_CT=" + dsChiTiet.size() + ", TT=" + tinhTongTien()
                + "]";
    }
}
