package entity;

public class VaiTro {
    private String maVaiTro;
    private String tenVaiTro;
    private String moTa;
    private String danhSachQuyen;

    public VaiTro() {}

    public VaiTro(String maVaiTro, String tenVaiTro, String moTa, String danhSachQuyen) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
        this.danhSachQuyen = danhSachQuyen;
    }

    public String getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(String maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getDanhSachQuyen() { return danhSachQuyen; }
    public void setDanhSachQuyen(String danhSachQuyen) { this.danhSachQuyen = danhSachQuyen; }

    @Override
    public String toString() {
        return tenVaiTro + " (" + maVaiTro + ")";
    }
}
