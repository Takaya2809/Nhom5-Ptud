package gui;

import entity.VaiTro;

import javax.swing.JPanel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Lớp biểu diễn một mục menu trong hệ thống (Menu Item).
 * Bao gồm:
 * - Tên hiển thị
 * - Icon đại diện (AppIcon.Type)
 * - CardKey định danh màn hình trong CardLayout
 * - Danh mục nhóm menu
 * - Supplier<JPanel> để tạo/nạp màn hình tương ứng
 * - Set<VaiTro> các vai trò được phép truy cập
 */
public class MucMenu {

    private String ten;
    private AppIcon.Type iconType;
    private String cardKey;
    private String danhMuc;
    private Supplier<JPanel> screenSupplier;
    private Set<VaiTro> vaiTroDuocTruyCap = new HashSet<>();

    public MucMenu(String ten, AppIcon.Type iconType, String cardKey, Supplier<JPanel> screenSupplier, VaiTro... vaiTros) {
        this(ten, iconType, cardKey, null, screenSupplier, vaiTros);
    }

    public MucMenu(String ten, AppIcon.Type iconType, String cardKey, String danhMuc, Supplier<JPanel> screenSupplier, VaiTro... vaiTros) {
        this.ten = ten;
        this.iconType = iconType;
        this.cardKey = cardKey;
        this.danhMuc = danhMuc;
        this.screenSupplier = screenSupplier;
        if (vaiTros != null && vaiTros.length > 0) {
            this.vaiTroDuocTruyCap.addAll(Arrays.asList(vaiTros));
        }
    }

    public boolean duocPhep(VaiTro vaiTro) {
        if (vaiTro == null) return false;
        return vaiTroDuocTruyCap.contains(vaiTro);
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public AppIcon.Type getIconType() {
        return iconType;
    }

    public void setIconType(AppIcon.Type iconType) {
        this.iconType = iconType;
    }

    public String getCardKey() {
        return cardKey;
    }

    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public Supplier<JPanel> getScreenSupplier() {
        return screenSupplier;
    }

    public void setScreenSupplier(Supplier<JPanel> screenSupplier) {
        this.screenSupplier = screenSupplier;
    }

    public Set<VaiTro> getVaiTroDuocTruyCap() {
        return vaiTroDuocTruyCap;
    }

    public void setVaiTroDuocTruyCap(Set<VaiTro> vaiTroDuocTruyCap) {
        this.vaiTroDuocTruyCap = vaiTroDuocTruyCap != null ? vaiTroDuocTruyCap : new HashSet<>();
    }

    @Override
    public String toString() {
        return ten + " [" + cardKey + "]";
    }
}

