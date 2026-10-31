package entity;

/**
 * Lớp định nghĩa tập trung các hằng số trạng thái y tế trong toàn bộ hệ thống
 * Thay thế việc so sánh chuỗi lỏng lẻo bằng contains()
 */
public final class TrangThaiConstants {

    private TrangThaiConstants() {}

    // 1. Trạng thái Lịch Hẹn
    public static final String LH_DA_LEN_LICH   = "Đã lên lịch";
    public static final String LH_DA_XAC_NHAN   = "Đã xác nhận";
    public static final String LH_CHO_KHAM      = "Chờ khám";
    public static final String LH_DANG_KHAM     = "Đang khám";
    public static final String LH_HOAN_TAT      = "Đã hoàn tất";
    public static final String LH_HOAN_THANH    = LH_HOAN_TAT;
    public static final String LH_DA_HUY        = "Đã hủy";
    public static final String LH_VANG_MAT      = "Vắng mặt";
    public static final String LH_KHONG_DEN     = LH_VANG_MAT;

    // 2. Trạng thái Điểm danh / Phân công ca trực
    public static final String DD_CHUA_DEN      = "Chưa đến";
    public static final String DD_DA_DEN_TRUC   = "Đã đến trực";
    public static final String DD_DANG_TRUC     = "Đang trực";
    public static final String DD_HOAN_THANH_CA = "Hoàn thành ca";
    public static final String DD_VANG_MAT      = "Vắng mặt";
    public static final String DD_DOI_CA        = "Đổi ca";

    // 3. Trạng thái Ca trực
    public static final String CA_DA_LEN_LICH   = "Đã lên lịch";
    public static final String CA_DANG_DIEN_RA  = "Đang diễn ra";
    public static final String CA_DA_KET_THUC   = "Đã kết thúc";
    public static final String CA_DA_HUY        = "Đã hủy";

    // 4. Trạng thái Hóa đơn
    public static final String HD_DA_THANH_TOAN = "Đã thanh toán";
    public static final String HD_CHO_THANH_TOAN= "Chờ thanh toán";
    public static final String HD_DA_HUY        = "Đã hủy";

    // 5. Trạng thái Dịch vụ
    public static final String DV_DANG_CUNG_CAP = "Đang cung cấp";
    public static final String DV_TAM_NGUNG     = "Tạm ngừng cung cấp";

    // 6. Trạng thái Bệnh nhân
    public static final String BN_DANG_THEO_DOI = "Đang theo dõi";
    public static final String BN_NGUNG_THEO_DOI= "Ngừng theo dõi";

    // 7. Phân hạng thẻ bệnh nhân
    public static final String THE_KHACH_MOI    = "Khách mới";
    public static final String THE_THUONG       = "Thường";
    public static final String THE_THAN_THIET   = "Thân thiết";
    public static final String THE_VIP          = "VIP";

    // 8. Trạng thái Hồ sơ bệnh án
    public static final String BA_DANG_DIEU_TRI = "Đang điều trị";
    public static final String BA_HOAN_THANH    = "Hoàn thành";
    public static final String BA_CHUYEN_VIEN   = "Chuyển viện";

    // 9. Trạng thái Đơn thuốc
    public static final String DT_DA_XUAT       = "Đã xuất thuốc";
    public static final String DT_CHO_XUAT      = "Chờ xuất thuốc";
    public static final String DT_DA_HUY        = "Đã hủy";

    // 10. Vai trò nhân sự
    public static final String VT_QUAN_LY       = "Quản lý";
    public static final String VT_NHA_SI        = "Nha sĩ";
    public static final String VT_BAC_SI        = "Bác sĩ";
    public static final String VT_LE_TAN        = "Lễ tân";
    public static final String VT_THU_NGAN      = "Thu ngân";
    public static final String VT_Y_TA          = "Y tá";
}
