package entity;

import java.sql.Timestamp;

public class HoSoBenhAn {
    private String maHoSo;
    private String maBN;
    private String maNhaSi;
    private String maLichHen;
    private Timestamp ngayKham;
    private String trieuChung;
    private String chanDoan;
    private String ketQuaDieuTri;
    private String ghiChu;
    private String trangThai; // Đang điều trị, Hoàn thành, Chuyển viện, Hẹn tái khám

    // Trường bổ trợ
    private String tenBenhNhan;
    private String tenNhaSi;

    public HoSoBenhAn() {}

    public HoSoBenhAn(String maHoSo, String maBN, String maNhaSi, String maLichHen,
                      Timestamp ngayKham, String trieuChung, String chanDoan,
                      String ketQuaDieuTri, String ghiChu, String trangThai) {
        this.maHoSo = maHoSo;
        this.maBN = maBN;
        this.maNhaSi = maNhaSi;
        this.maLichHen = maLichHen;
        this.ngayKham = ngayKham;
        this.trieuChung = trieuChung;
        this.chanDoan = chanDoan;
        this.ketQuaDieuTri = ketQuaDieuTri;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }

    public String getMaBN() { return maBN; }
    public void setMaBN(String maBN) { this.maBN = maBN; }

    public String getMaNhaSi() { return maNhaSi; }
    public void setMaNhaSi(String maNhaSi) { this.maNhaSi = maNhaSi; }

    public String getMaLichHen() { return maLichHen; }
    public void setMaLichHen(String maLichHen) { this.maLichHen = maLichHen; }

    public Timestamp getNgayKham() { return ngayKham; }
    public void setNgayKham(Timestamp ngayKham) { this.ngayKham = ngayKham; }

    public String getTrieuChung() { return trieuChung; }
    public void setTrieuChung(String trieuChung) { this.trieuChung = trieuChung; }

    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String chanDoan) { this.chanDoan = chanDoan; }

    public String getKetQuaDieuTri() { return ketQuaDieuTri; }
    public void setKetQuaDieuTri(String ketQuaDieuTri) { this.ketQuaDieuTri = ketQuaDieuTri; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }

    public String getTenNhaSi() { return tenNhaSi; }
    public void setTenNhaSi(String tenNhaSi) { this.tenNhaSi = tenNhaSi; }

    @Override
    public String toString() {
        return "HoSoBenhAn[" + maHoSo + ", BN=" + maBN + ", Chẩn đoán=" + chanDoan + "]";
    }
}
