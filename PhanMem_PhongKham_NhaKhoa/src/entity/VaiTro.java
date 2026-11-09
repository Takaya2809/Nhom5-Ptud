package entity;

/**
 * Enum đại diện cho các vai trò trong hệ thống quản lý phòng khám nha khoa
 */
public enum VaiTro {
    QUAN_LY("VT_QL", "Quản lý"),
    BAC_SI("VT_BS", "Nha sĩ"),
    LE_TAN("VT_LT", "Lễ tân"),
    Y_TA("VT_YT", "Y tá");

    private final String maVaiTro;
    private final String tenVaiTro;

    VaiTro(String maVaiTro, String tenVaiTro) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
    }

    public String getMaVaiTro() {
        return maVaiTro;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public String getTenHienThi() {
        return tenVaiTro;
    }

    public static VaiTro fromMa(String ma) {
        if (ma == null)
            return null;
        for (VaiTro vt : values()) {
            if (vt.maVaiTro.equalsIgnoreCase(ma.trim()) || vt.name().equalsIgnoreCase(ma.trim())) {
                return vt;
            }
        }
        return null;
    }

    public static VaiTro fromTen(String ten) {
        if (ten == null)
            return null;
        for (VaiTro vt : values()) {
            if (vt.tenVaiTro.equalsIgnoreCase(ten.trim()) || vt.name().equalsIgnoreCase(ten.trim())) {
                return vt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return tenVaiTro + " (" + maVaiTro + ")";
    }
}
