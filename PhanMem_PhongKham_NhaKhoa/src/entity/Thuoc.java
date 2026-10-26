package entity;

import java.sql.Date;

public class Thuoc {
    private String maThuoc;
    private String tenThuoc;
    private String hoatChat;
    private String donViTinh;
    private double donGia;
    private int soLuongTon;
    private int dinhMucToiThieu;
    private Date hanSuDung;
    private String cachDungMacDinh;
    private String trangThai;

    public Thuoc() {}

    public Thuoc(String maThuoc, String tenThuoc, String hoatChat, String donViTinh,
                 double donGia, int soLuongTon, int dinhMucToiThieu, Date hanSuDung,
                 String cachDungMacDinh, String trangThai) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.hoatChat = hoatChat;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.soLuongTon = soLuongTon;
        this.dinhMucToiThieu = dinhMucToiThieu;
        this.hanSuDung = hanSuDung;
        this.cachDungMacDinh = cachDungMacDinh;
        this.trangThai = trangThai;
    }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getHoatChat() { return hoatChat; }
    public void setHoatChat(String hoatChat) { this.hoatChat = hoatChat; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public int getDinhMucToiThieu() { return dinhMucToiThieu; }
    public void setDinhMucToiThieu(int dinhMucToiThieu) { this.dinhMucToiThieu = dinhMucToiThieu; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }

    public String getCachDungMacDinh() { return cachDungMacDinh; }
    public void setCachDungMacDinh(String cachDungMacDinh) { this.cachDungMacDinh = cachDungMacDinh; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenThuoc + " (" + donViTinh + ") - " + String.format("%,.0f đ", donGia);
    }
}
