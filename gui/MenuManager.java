package gui;

import entity.VaiTro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * MenuManager: Quản lý danh sách các mục menu hướng đối tượng theo từng vai trò.
 * Áp dụng thiết kế hướng đối tượng:
 * - Tập hợp các MucMenu được cấu hình rõ ràng cho từng VaiTro.
 * - Cung cấp getMenu(VaiTro) trả về danh sách các mục menu phù hợp.
 * - Kiểm tra phân quyền truy cập thông qua coQuyen(VaiTro, cardKey).
 */
public class MenuManager {

    private static final Map<VaiTro, List<MucMenu>> MENU_BY_ROLE = new EnumMap<>(VaiTro.class);

    static {
        khoiTaoMenu();
    }

    private static void khoiTaoMenu() {
        // ==========================================
        // 1. MENU DÀNH CHO VAI TRÒ QUẢN LÝ (Admin / Quản lý)
        // ==========================================
        List<MucMenu> quanLyMenu = new ArrayList<>();
        // Báo cáo & Thống kê
        quanLyMenu.add(new MucMenu("Báo Cáo & Thống Kê", AppIcon.Type.REPORT, "THONG_KE", "BÁO CÁO & THỐNG KÊ", null, VaiTro.QUAN_LY));
        // Nghiệp vụ nha khoa
        quanLyMenu.add(new MucMenu("Hồ Sơ Bệnh Nhân", AppIcon.Type.PATIENT, "BENH_NHAN", "NGHIỆP VỤ NHA KHOA", null, VaiTro.QUAN_LY));
        quanLyMenu.add(new MucMenu("Dịch Vụ & Bảng Giá", AppIcon.Type.SERVICE, "DICH_VU", "NGHIỆP VỤ NHA KHOA", null, VaiTro.QUAN_LY));
        quanLyMenu.add(new MucMenu("Lịch Trực & Ca Khám", AppIcon.Type.CALENDAR, "LICH_TRUC", "NGHIỆP VỤ NHA KHOA", null, VaiTro.QUAN_LY));
        // Tài chính & Vật tư
        quanLyMenu.add(new MucMenu("Thu Ngân & Hóa Đơn", AppIcon.Type.PAYMENT, "THANH_TOAN", "TÀI CHÍNH & VẬT TƯ", null, VaiTro.QUAN_LY));
        quanLyMenu.add(new MucMenu("Chính Sách Ưu Đãi", AppIcon.Type.DISCOUNT, "UU_DAI", "TÀI CHÍNH & VẬT TƯ", null, VaiTro.QUAN_LY));
        quanLyMenu.add(new MucMenu("Kho Dược & Vật Tư", AppIcon.Type.INVENTORY, "KHO_VAT_TU", "TÀI CHÍNH & VẬT TƯ", null, VaiTro.QUAN_LY));
        // Quản trị & Hệ thống
        quanLyMenu.add(new MucMenu("Bác Sĩ & Nhân Sự", AppIcon.Type.STAFF, "NHAN_SU", "QUẢN TRỊ & HỆ THỐNG", null, VaiTro.QUAN_LY));
        quanLyMenu.add(new MucMenu("Quản Trị Tài Khoản", AppIcon.Type.ACCOUNT, "TAI_KHOAN", "QUẢN TRỊ & HỆ THỐNG", null, VaiTro.QUAN_LY));
        quanLyMenu.add(new MucMenu("Cài Đặt & Quy Chế", AppIcon.Type.SETTINGS, "QUY_DINH", "QUẢN TRỊ & HỆ THỐNG", null, VaiTro.QUAN_LY));
        MENU_BY_ROLE.put(VaiTro.QUAN_LY, Collections.unmodifiableList(quanLyMenu));

        // ==========================================
        // 2. MENU DÀNH CHO BÁC SĨ (Nha sĩ)
        // ==========================================
        List<MucMenu> bacSiMenu = new ArrayList<>();
        // Hồ sơ & Bệnh án
        bacSiMenu.add(new MucMenu("Hồ Sơ Bệnh Nhân", AppIcon.Type.PATIENT, "BENH_NHAN", "HỒ SƠ & BỆNH ÁN", null, VaiTro.BAC_SI));
        // Quy trình khám & Điều trị
        bacSiMenu.add(new MucMenu("Tiếp nhận và khám bệnh nhân", AppIcon.Type.PATIENT, "NHA_SI_TIEP_NHAN", "QUY TRÌNH KHÁM & ĐIỀU TRỊ", null, VaiTro.BAC_SI));
        bacSiMenu.add(new MucMenu("Chẩn đoán & Cận lâm sàng", AppIcon.Type.REPORT, "NHA_SI_CHAN_DOAN", "QUY TRÌNH KHÁM & ĐIỀU TRỊ", null, VaiTro.BAC_SI));
        bacSiMenu.add(new MucMenu("Tư vấn & Kế hoạch điều trị", AppIcon.Type.SERVICE, "NHA_SI_DIEU_TRI", "QUY TRÌNH KHÁM & ĐIỀU TRỊ", null, VaiTro.BAC_SI));
        bacSiMenu.add(new MucMenu("Tái khám & Đánh giá", AppIcon.Type.CALENDAR, "NHA_SI_TAI_KHAM", "QUY TRÌNH KHÁM & ĐIỀU TRỊ", null, VaiTro.BAC_SI));
        bacSiMenu.add(new MucMenu("Hoàn tất & Theo dõi điều trị", AppIcon.Type.REPORT, "NHA_SI_HOAN_TAT", "QUY TRÌNH KHÁM & ĐIỀU TRỊ", null, VaiTro.BAC_SI));
        // Lịch trực & Dịch vụ
        bacSiMenu.add(new MucMenu("Lịch Trực & Ca Khám", AppIcon.Type.CALENDAR, "LICH_TRUC", "LỊCH TRỰC & DỊCH VỤ", null, VaiTro.BAC_SI));
        bacSiMenu.add(new MucMenu("Dịch Vụ & Bảng Giá", AppIcon.Type.SERVICE, "DICH_VU", "LỊCH TRỰC & DỊCH VỤ", null, VaiTro.BAC_SI));
        MENU_BY_ROLE.put(VaiTro.BAC_SI, Collections.unmodifiableList(bacSiMenu));

        // ==========================================
        // 3. MENU DÀNH CHO LỄ TÂN
        // ==========================================
        List<MucMenu> leTanMenu = new ArrayList<>();
        // Tiếp đón & Đặt lịch
        leTanMenu.add(new MucMenu("Hồ Sơ Bệnh Nhân", AppIcon.Type.PATIENT, "BENH_NHAN", "TIẾP ĐÓN & ĐẶT LỊCH", null, VaiTro.LE_TAN));
        leTanMenu.add(new MucMenu("Lịch Trực & Ca Khám", AppIcon.Type.CALENDAR, "LICH_TRUC", "TIẾP ĐÓN & ĐẶT LỊCH", null, VaiTro.LE_TAN));
        leTanMenu.add(new MucMenu("Dịch Vụ & Bảng Giá", AppIcon.Type.SERVICE, "DICH_VU", "TIẾP ĐÓN & ĐẶT LỊCH", null, VaiTro.LE_TAN));
        // Thanh toán & Ưu đãi
        leTanMenu.add(new MucMenu("Thu Ngân & Hóa Đơn", AppIcon.Type.PAYMENT, "THANH_TOAN", "THANH TOÁN & ƯU ĐÃI", null, VaiTro.LE_TAN));
        leTanMenu.add(new MucMenu("Chính Sách Ưu Đãi", AppIcon.Type.DISCOUNT, "UU_DAI", "THANH TOÁN & ƯU ĐÃI", null, VaiTro.LE_TAN));
        // Báo cáo cuối ngày
        leTanMenu.add(new MucMenu("Thống Kê Cuối Ngày", AppIcon.Type.CHART, "THONG_KE_CUOI_NGAY", "BÁO CÁO QUẢN LÝ", null, VaiTro.LE_TAN));
        MENU_BY_ROLE.put(VaiTro.LE_TAN, Collections.unmodifiableList(leTanMenu));

        // ==========================================
        // 4. MENU DÀNH CHO Y TÁ
        // ==========================================
        List<MucMenu> yTaMenu = new ArrayList<>();
        // Hỗ trợ & Vật tư
        yTaMenu.add(new MucMenu("Vật Tư Tiêu Hao Theo Ca", AppIcon.Type.INVENTORY, "VAT_TU_TIEU_HAO", "HỖ TRỢ & VẬT TƯ", null, VaiTro.Y_TA));
        yTaMenu.add(new MucMenu("Theo Dõi Khử Khuẩn", AppIcon.Type.SHIELD, "KHU_KHUAN", "HỖ TRỢ & VẬT TƯ", null, VaiTro.Y_TA));
        // Lịch hẹn & Bệnh nhân
        yTaMenu.add(new MucMenu("Hồ sơ bệnh nhân", AppIcon.Type.PATIENT, "BENH_NHAN", "LỊCH HẸN & BỆNH NHÂN", null, VaiTro.Y_TA));
        yTaMenu.add(new MucMenu("Lịch Trực & Lịch Hẹn Khám", AppIcon.Type.CALENDAR, "LICH_TRUC", "LỊCH HẸN & BỆNH NHÂN", null, VaiTro.Y_TA));
        // Báo cáo cuối ngày
        yTaMenu.add(new MucMenu("Thống Kê Cuối Ngày", AppIcon.Type.CHART, "THONG_KE_CUOI_NGAY", "BÁO CÁO QUẢN LÝ", null, VaiTro.Y_TA));
        MENU_BY_ROLE.put(VaiTro.Y_TA, Collections.unmodifiableList(yTaMenu));
    }

    /**
     * Lấy danh sách mục menu dành cho vai trò tương ứng.
     * @param vaiTro Vai trò người dùng
     * @return Danh sách các mục menu (không bao giờ null)
     */
    public static List<MucMenu> getMenu(VaiTro vaiTro) {
        if (vaiTro == null) {
            vaiTro = VaiTro.QUAN_LY;
        }
        List<MucMenu> list = MENU_BY_ROLE.get(vaiTro);
        if (list == null) {
            return MENU_BY_ROLE.getOrDefault(VaiTro.QUAN_LY, Collections.emptyList());
        }
        return list;
    }

    /**
     * Kiểm tra vai trò có quyền truy cập chức năng với mã cardKey hay không.
     * @param vaiTro Vai trò người dùng
     * @param cardKey Mã định danh màn hình
     * @return true nếu được phép, false nếu bị hạn chế
     */
    public static boolean coQuyen(VaiTro vaiTro, String cardKey) {
        if (cardKey == null) {
            return false;
        }
        List<MucMenu> menu = getMenu(vaiTro);
        for (MucMenu item : menu) {
            if (item.getCardKey().equalsIgnoreCase(cardKey)) {
                return true;
            }
            if ((cardKey.startsWith("NHA_SI") || cardKey.equals("KHAM_DIEU_TRI"))
                    && item.getCardKey().startsWith("NHA_SI")) {
                return true;
            }
        }
        return false;
    }
}

