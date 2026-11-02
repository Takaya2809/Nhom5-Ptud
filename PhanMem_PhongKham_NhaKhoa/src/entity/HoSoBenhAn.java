package entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/** Clinical record used by dentist intake, diagnosis, treatment and follow-up workflows. */
public class HoSoBenhAn {
    private String maHoSo;
    private String maBN;
    private String maNhaSi;
    private String maLichHen;
    private String maLichHenTaiKham;
    private Timestamp ngayKham;
    private String trieuChung;
    private String chanDoan;
    private String ketQuaDieuTri;
    private String ghiChu;
    private String trangThai;
    private String tenBenhNhan;
    private String tenNhaSi;
    private String tinhTrangLamSang;
    private String chiDinhCanLamSang;
    private String ketQuaCanLamSang;
    private String viTriRang;
    private String soDoRang;
    private String keHoachDieuTri;
    private String noiDungCanThiep;
    private String donThuoc;
    private String huongDanSauDieuTri;
    private String danhGiaTaiKham;
    private String huongXuLyTiepTheo;
    private Date ngayTaiKham;
    private Time gioTaiKham;
    private boolean xacNhanDongY;
    private boolean huyLichTaiKham;

    public HoSoBenhAn() { }

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
    public void setMaHoSo(String v) { maHoSo = v; }
    public String getMaBN() { return maBN; }
    public void setMaBN(String v) { maBN = v; }
    public String getMaNhaSi() { return maNhaSi; }
    public void setMaNhaSi(String v) { maNhaSi = v; }
    public String getMaLichHen() { return maLichHen; }
    public void setMaLichHen(String v) { maLichHen = v; }
    public String getMaLichHenTaiKham() { return maLichHenTaiKham; }
    public void setMaLichHenTaiKham(String v) { maLichHenTaiKham = v; }
    public Timestamp getNgayKham() { return ngayKham; }
    public void setNgayKham(Timestamp v) { ngayKham = v; }
    public String getTrieuChung() { return trieuChung; }
    public void setTrieuChung(String v) { trieuChung = v; }
    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String v) { chanDoan = v; }
    public String getKetQuaDieuTri() { return ketQuaDieuTri; }
    public void setKetQuaDieuTri(String v) { ketQuaDieuTri = v; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String v) { ghiChu = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { trangThai = v; }
    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String v) { tenBenhNhan = v; }
    public String getTenNhaSi() { return tenNhaSi; }
    public void setTenNhaSi(String v) { tenNhaSi = v; }
    public String getTinhTrangLamSang() { return tinhTrangLamSang; }
    public void setTinhTrangLamSang(String v) { tinhTrangLamSang = v; }
    public String getChiDinhCanLamSang() { return chiDinhCanLamSang; }
    public void setChiDinhCanLamSang(String v) { chiDinhCanLamSang = v; }
    public String getKetQuaCanLamSang() { return ketQuaCanLamSang; }
    public void setKetQuaCanLamSang(String v) { ketQuaCanLamSang = v; }
    public String getViTriRang() { return viTriRang; }
    public void setViTriRang(String v) { viTriRang = v; }
    public String getSoDoRang() { return soDoRang; }
    public void setSoDoRang(String v) { soDoRang = v; }
    public String getKeHoachDieuTri() { return keHoachDieuTri; }
    public void setKeHoachDieuTri(String v) { keHoachDieuTri = v; }
    public String getNoiDungCanThiep() { return noiDungCanThiep; }
    public void setNoiDungCanThiep(String v) { noiDungCanThiep = v; }
    public String getDonThuoc() { return donThuoc; }
    public void setDonThuoc(String v) { donThuoc = v; }
    public String getHuongDanSauDieuTri() { return huongDanSauDieuTri; }
    public void setHuongDanSauDieuTri(String v) { huongDanSauDieuTri = v; }
    public String getDanhGiaTaiKham() { return danhGiaTaiKham; }
    public void setDanhGiaTaiKham(String v) { danhGiaTaiKham = v; }
    public String getHuongXuLyTiepTheo() { return huongXuLyTiepTheo; }
    public void setHuongXuLyTiepTheo(String v) { huongXuLyTiepTheo = v; }
    public Date getNgayTaiKham() { return ngayTaiKham; }
    public void setNgayTaiKham(Date v) { ngayTaiKham = v; }
    public Time getGioTaiKham() { return gioTaiKham; }
    public void setGioTaiKham(Time v) { gioTaiKham = v; }
    public boolean isXacNhanDongY() { return xacNhanDongY; }
    public void setXacNhanDongY(boolean v) { xacNhanDongY = v; }
    public boolean isHuyLichTaiKham() { return huyLichTaiKham; }
    public void setHuyLichTaiKham(boolean v) { huyLichTaiKham = v; }

    @Override
    public String toString() {
        return "HoSoBenhAn[" + maHoSo + ", BN=" + maBN + ", Chẩn đoán=" + chanDoan + "]";
    }
}
