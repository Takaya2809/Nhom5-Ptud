package entity;

import java.sql.Date;
import java.util.Objects;

/**
 * Thực thể Thuốc trong hệ thống phòng khám nha khoa.
 */
public class Thuoc {
    private String maThuoc;
    private String tenThuoc;
    private String hoatChat;
    private String donViTinh;

    /**
     * Giá mua vào từ nhà cung cấp (chỉ vai trò Quản lý được xem và điều chỉnh).
     * Ràng buộc nghiệp vụ: 0 < giaNhap <= giaBan.
     */
    private double giaNhap;

    /**
     * Giá bán cho bệnh nhân (là giá duy nhất dùng khi lập hóa đơn).
     * Ràng buộc nghiệp vụ: giaBan >= giaNhap > 0.
     */
    private double giaBan;

    private int soLuongTon;
    private int dinhMucToiThieu;
    private Date hanSuDung;
    private String cachDungMacDinh;
    private String trangThai;
    private NCC nhaCungCap;

    public Thuoc() {
    }

    public Thuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    /**
     * Constructor tương thích dữ liệu cũ
     */
    public Thuoc(String maThuoc, String tenThuoc, String hoatChat, String donViTinh,
            double donGia, int soLuongTon, int dinhMucToiThieu, Date hanSuDung,
            String cachDungMacDinh, String trangThai) {
        this(maThuoc, tenThuoc, hoatChat, donViTinh, donGia * 0.8, donGia,
                soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai, null);
    }

    /**
     * Constructor đầy đủ chuẩn hóa
     */
    public Thuoc(String maThuoc, String tenThuoc, String hoatChat, String donViTinh,
            double giaNhap, double giaBan, int soLuongTon, int dinhMucToiThieu,
            Date hanSuDung, String cachDungMacDinh, String trangThai, NCC nhaCungCap) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.hoatChat = hoatChat;
        this.donViTinh = donViTinh;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.dinhMucToiThieu = dinhMucToiThieu;
        this.hanSuDung = hanSuDung;
        this.cachDungMacDinh = cachDungMacDinh;
        this.trangThai = trangThai;
        this.nhaCungCap = nhaCungCap;
    }

    public String getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(String maThuoc) {
        this.maThuoc = maThuoc;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        this.hoatChat = hoatChat;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    /**
     * Gán giá nhập thuốc (chỉ vai trò Quản lý được phép thực hiện).
     * @param giaNhap Giá mua vào từ nhà cung cấp (bắt buộc > 0)
     */
    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0) {
            throw new IllegalArgumentException("Giá nhập thuốc phải lớn hơn 0");
        }
        if (this.giaBan > 0 && giaNhap > this.giaBan) {
            throw new IllegalArgumentException("Ràng buộc nghiệp vụ: Giá nhập (" + giaNhap + ") không được lớn hơn giá bán (" + this.giaBan + ")");
        }
        this.giaNhap = giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }

    /**
     * Gán giá bán thuốc cho bệnh nhân.
     * @param giaBan Giá bán ra (bắt buộc >= giá nhập > 0)
     */
    public void setGiaBan(double giaBan) {
        if (giaBan <= 0) {
            throw new IllegalArgumentException("Giá bán thuốc phải lớn hơn 0");
        }
        if (this.giaNhap > 0 && giaBan < this.giaNhap) {
            throw new IllegalArgumentException("Ràng buộc nghiệp vụ: Giá bán (" + giaBan + ") không được nhỏ hơn giá nhập (" + this.giaNhap + ")");
        }
        this.giaBan = giaBan;
    }

    /**
     * Tương thích ngược: donGia trả về giaBan
     */
    public double getDonGia() {
        return giaBan > 0 ? giaBan : giaNhap;
    }

    public void setDonGia(double donGia) {
        this.giaBan = donGia;
        if (this.giaNhap <= 0) {
            this.giaNhap = donGia * 0.8;
        }
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getDinhMucToiThieu() {
        return dinhMucToiThieu;
    }

    public void setDinhMucToiThieu(int dinhMucToiThieu) {
        this.dinhMucToiThieu = dinhMucToiThieu;
    }

    public Date getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(Date hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public String getCachDungMacDinh() {
        return cachDungMacDinh;
    }

    public void setCachDungMacDinh(String cachDungMacDinh) {
        this.cachDungMacDinh = cachDungMacDinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public NCC getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NCC nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public String getMaNCC() {
        return nhaCungCap != null ? nhaCungCap.getMaNCC() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Thuoc))
            return false;
        Thuoc thuoc = (Thuoc) o;
        return Objects.equals(maThuoc, thuoc.maThuoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThuoc);
    }

    @Override
    public String toString() {
        return tenThuoc + " (" + donViTinh + ") - " + String.format("%,.0f đ", getDonGia());
    }
}
