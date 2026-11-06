package entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Thực thể Hồ sơ bệnh án trong quy trình khám và điều trị nha khoa.
 */
public class HoSoBenhAn {
    private String maHoSo;
    private BenhNhan benhNhan;
    private NhanVien bacSi;
    private LichHen lichHen;
    private LichHen lichHenTaiKham;
    private Timestamp ngayKham;
    private String trieuChung;
    private String chanDoan;
    private String ketQuaDieuTri;
    private String ghiChu;
    private String trangThai;
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

    public HoSoBenhAn() {
    }

    public HoSoBenhAn(String maHoSo) {
        this.maHoSo = maHoSo;
    }

    public HoSoBenhAn(String maHoSo, String maBN, String maNhaSi, String maLichHen,
            Timestamp ngayKham, String trieuChung, String chanDoan,
            String ketQuaDieuTri, String ghiChu, String trangThai) {
        this.maHoSo = maHoSo;
        this.benhNhan = maBN != null ? new BenhNhan(maBN) : null;
        this.bacSi = maNhaSi != null ? new NhanVien(maNhaSi) : null;
        this.lichHen = maLichHen != null ? new LichHen(maLichHen) : null;
        this.ngayKham = ngayKham;
        this.trieuChung = trieuChung;
        this.chanDoan = chanDoan;
        this.ketQuaDieuTri = ketQuaDieuTri;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public HoSoBenhAn(String maHoSo, BenhNhan benhNhan, NhanVien bacSi, LichHen lichHen,
            Timestamp ngayKham, String trieuChung, String chanDoan,
            String ketQuaDieuTri, String ghiChu, String trangThai) {
        this.maHoSo = maHoSo;
        this.benhNhan = benhNhan;
        this.bacSi = bacSi;
        this.lichHen = lichHen;
        this.ngayKham = ngayKham;
        this.trieuChung = trieuChung;
        this.chanDoan = chanDoan;
        this.ketQuaDieuTri = ketQuaDieuTri;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(String v) {
        maHoSo = v;
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

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public LichHen getLichHenTaiKham() {
        return lichHenTaiKham;
    }

    public void setLichHenTaiKham(LichHen lichHenTaiKham) {
        this.lichHenTaiKham = lichHenTaiKham;
    }

    public String getMaBN() {
        return benhNhan != null ? benhNhan.getMaBN() : null;
    }

    public void setMaBN(String v) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan(v);
        else
            this.benhNhan.setMaBN(v);
    }

    public String getMaNhaSi() {
        return bacSi != null ? bacSi.getMaNV() : null;
    }

    public void setMaNhaSi(String v) {
        if (this.bacSi == null)
            this.bacSi = new NhanVien(v);
        else
            this.bacSi.setMaNV(v);
    }

    public String getMaLichHen() {
        return lichHen != null ? lichHen.getMaLichHen() : null;
    }

    public void setMaLichHen(String v) {
        if (this.lichHen == null)
            this.lichHen = new LichHen(v);
        else
            this.lichHen.setMaLichHen(v);
    }

    public String getMaLichHenTaiKham() {
        return lichHenTaiKham != null ? lichHenTaiKham.getMaLichHen() : null;
    }

    public void setMaLichHenTaiKham(String v) {
        if (this.lichHenTaiKham == null)
            this.lichHenTaiKham = new LichHen(v);
        else
            this.lichHenTaiKham.setMaLichHen(v);
    }

    public Timestamp getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(Timestamp v) {
        ngayKham = v;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String v) {
        trieuChung = v;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String v) {
        chanDoan = v;
    }

    public String getKetQuaDieuTri() {
        return ketQuaDieuTri;
    }

    public void setKetQuaDieuTri(String v) {
        ketQuaDieuTri = v;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String v) {
        ghiChu = v;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String v) {
        trangThai = v;
    }

    public String getTenBenhNhan() {
        return benhNhan != null ? benhNhan.getHoTen() : null;
    }

    public void setTenBenhNhan(String v) {
        if (this.benhNhan == null)
            this.benhNhan = new BenhNhan();
        this.benhNhan.setHoTen(v);
    }

    public String getTenNhaSi() {
        return bacSi != null ? bacSi.getHoTen() : null;
    }

    public void setTenNhaSi(String v) {
        if (this.bacSi == null)
            this.bacSi = new NhanVien();
        this.bacSi.setHoTen(v);
    }

    public String getTinhTrangLamSang() {
        return tinhTrangLamSang;
    }

    public void setTinhTrangLamSang(String v) {
        tinhTrangLamSang = v;
    }

    public String getChiDinhCanLamSang() {
        return chiDinhCanLamSang;
    }

    public void setChiDinhCanLamSang(String v) {
        chiDinhCanLamSang = v;
    }

    public String getKetQuaCanLamSang() {
        return ketQuaCanLamSang;
    }

    public void setKetQuaCanLamSang(String v) {
        ketQuaCanLamSang = v;
    }

    public String getViTriRang() {
        return viTriRang;
    }

    public void setViTriRang(String v) {
        viTriRang = v;
    }

    public String getSoDoRang() {
        return soDoRang;
    }

    public void setSoDoRang(String v) {
        soDoRang = v;
    }

    public String getKeHoachDieuTri() {
        return keHoachDieuTri;
    }

    public void setKeHoachDieuTri(String v) {
        keHoachDieuTri = v;
    }

    public String getNoiDungCanThiep() {
        return noiDungCanThiep;
    }

    public void setNoiDungCanThiep(String v) {
        noiDungCanThiep = v;
    }

    public String getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(String v) {
        donThuoc = v;
    }

    public String getHuongDanSauDieuTri() {
        return huongDanSauDieuTri;
    }

    public void setHuongDanSauDieuTri(String v) {
        huongDanSauDieuTri = v;
    }

    public String getDanhGiaTaiKham() {
        return danhGiaTaiKham;
    }

    public void setDanhGiaTaiKham(String v) {
        danhGiaTaiKham = v;
    }

    public String getHuongXuLyTiepTheo() {
        return huongXuLyTiepTheo;
    }

    public void setHuongXuLyTiepTheo(String v) {
        huongXuLyTiepTheo = v;
    }

    public Date getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(Date v) {
        ngayTaiKham = v;
    }

    public Time getGioTaiKham() {
        return gioTaiKham;
    }

    public void setGioTaiKham(Time v) {
        gioTaiKham = v;
    }

    public boolean isXacNhanDongY() {
        return xacNhanDongY;
    }

    public void setXacNhanDongY(boolean v) {
        xacNhanDongY = v;
    }

    public boolean isHuyLichTaiKham() {
        return huyLichTaiKham;
    }

    public void setHuyLichTaiKham(boolean v) {
        huyLichTaiKham = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HoSoBenhAn))
            return false;
        HoSoBenhAn that = (HoSoBenhAn) o;
        return Objects.equals(maHoSo, that.maHoSo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoSo);
    }

    @Override
    public String toString() {
        return "HoSoBenhAn[" + maHoSo + ", BN=" + getMaBN() + ", Chẩn đoán=" + chanDoan + "]";
    }
}
