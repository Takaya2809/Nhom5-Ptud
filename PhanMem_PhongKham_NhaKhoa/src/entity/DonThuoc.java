package entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Thực thể Đơn thuốc.
 * Nghiệp vụ chuẩn hóa: KHÔNG lưu trường tongTienThuoc (tính toán runtime qua
 * danh sách chi tiết: tinhTongTien()).
 */
public class DonThuoc {
    private String maDonThuoc;
    private HoSoBenhAn hoSo;
    private BenhNhan benhNhan;
    private NhanVien bacSi;
    private Timestamp ngayLap;
    private String loiDan;
    private String trangThai;
    private List<ChiTietDonThuoc> dsChiTiet = new ArrayList<>();

    public DonThuoc() {
    }

    public DonThuoc(String maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }

    public DonThuoc(String maDonThuoc, String maHoSo, String maNhaSi, Timestamp ngayLap,
            String loiDan, double tongTienThuoc, String trangThai) {
        this.maDonThuoc = maDonThuoc;
        this.hoSo = maHoSo != null ? new HoSoBenhAn(maHoSo) : null;
        this.bacSi = maNhaSi != null ? new NhanVien(maNhaSi) : null;
        this.ngayLap = ngayLap;
        this.loiDan = loiDan;
        this.trangThai = trangThai;
    }

    public DonThuoc(String maDonThuoc, HoSoBenhAn hoSo, BenhNhan benhNhan, NhanVien bacSi,
            Timestamp ngayLap, String loiDan, double tongTienThuoc, String trangThai) {
        this.maDonThuoc = maDonThuoc;
        this.hoSo = hoSo;
        this.benhNhan = benhNhan;
        this.bacSi = bacSi;
        this.ngayLap = ngayLap;
        this.loiDan = loiDan;
        this.trangThai = trangThai;
    }

    public String getMaDonThuoc() {
        return maDonThuoc;
    }

    public void setMaDonThuoc(String maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }

    public HoSoBenhAn getHoSo() {
        return hoSo;
    }

    public void setHoSo(HoSoBenhAn hoSo) {
        this.hoSo = hoSo;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public NhanVien getBacSi() {
        return bacSi;
    }

    public void setBacSi(NhanVien bacSi) {
        this.bacSi = bacSi;
    }

    public NhanVien getNhaSi() {
        return bacSi;
    }

    public void setNhaSi(NhanVien nhaSi) {
        this.bacSi = nhaSi;
    }

    public String getMaHoSo() {
        return hoSo != null ? hoSo.getMaHoSo() : null;
    }

    public void setMaHoSo(String maHoSo) {
        if (this.hoSo == null)
            this.hoSo = new HoSoBenhAn(maHoSo);
        else
            this.hoSo.setMaHoSo(maHoSo);
    }

    public String getMaNhaSi() {
        return bacSi != null ? bacSi.getMaNV() : null;
    }

    public void setMaNhaSi(String maNhaSi) {
        if (this.bacSi == null)
            this.bacSi = new NhanVien(maNhaSi);
        else
            this.bacSi.setMaNV(maNhaSi);
    }

    public String getTenNhaSi() {
        return bacSi != null ? bacSi.getHoTen() : null;
    }

    public void setTenNhaSi(String tenNhaSi) {
        if (this.bacSi == null)
            this.bacSi = new NhanVien();
        this.bacSi.setHoTen(tenNhaSi);
    }

    public String getMaBN() {
        if (benhNhan != null)
            return benhNhan.getMaBN();
        if (hoSo != null)
            return hoSo.getMaBN();
        return null;
    }

    public Timestamp getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Timestamp ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getLoiDan() {
        return loiDan;
    }

    public void setLoiDan(String loiDan) {
        this.loiDan = loiDan;
    }

    /**
     * Tính tổng tiền đơn thuốc động từ danh sách chi tiết (Runtime calculation)
     */
    public double tinhTongTien() {
        if (dsChiTiet == null || dsChiTiet.isEmpty())
            return 0.0;
        double tong = 0.0;
        for (ChiTietDonThuoc ct : dsChiTiet) {
            tong += ct.tinhThanhTien();
        }
        return tong;
    }

    public double getTongTienThuoc() {
        return tinhTongTien();
    }

    public void setTongTienThuoc(double tongTienThuoc) {
        // Không lưu tongTienThuoc trong CSDL chuẩn hóa - tính toán runtime qua
        // tinhTongTien()
    }

    public List<ChiTietDonThuoc> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(List<ChiTietDonThuoc> dsChiTiet) {
        this.dsChiTiet = dsChiTiet != null ? dsChiTiet : new ArrayList<>();
    }

    public void themChiTiet(ChiTietDonThuoc ct) {
        if (ct != null) {
            this.dsChiTiet.add(ct);
        }
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DonThuoc))
            return false;
        DonThuoc donThuoc = (DonThuoc) o;
        return Objects.equals(maDonThuoc, donThuoc.maDonThuoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDonThuoc);
    }

    @Override
    public String toString() {
        return "DonThuoc[" + maDonThuoc + ", BS=" + getMaNhaSi() + ", Ngay=" + ngayLap + "]";
    }
}
