package dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp truy xuất dữ liệu Thống Kê & Báo Cáo
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class ThongKeDAO {

    public double getTongDoanhThu() {
        // TODO: Cài đặt tính tổng doanh thu từ CSDL tại đây
        return 0.0;
    }

    public int getTongLuotKham() {
        // TODO: Cài đặt tính tổng lượt khám từ CSDL tại đây
        return 0;
    }

    public int getTongBenhNhan() {
        // TODO: Cài đặt tính tổng số bệnh nhân từ CSDL tại đây
        return 0;
    }

    public int getSoVatTuCanhBao() {
        // TODO: Cài đặt đếm số vật tư cảnh báo từ CSDL tại đây
        return 0;
    }

    public Map<String, Double> getDoanhThuTheoDichVu() {
        // TODO: Cài đặt thống kê doanh thu theo dịch vụ từ CSDL tại đây
        return new LinkedHashMap<>();
    }

    public Map<String, Integer> getHieuSuatBacSi() {
        // TODO: Cài đặt thống kê hiệu suất bác sĩ từ CSDL tại đây
        return new LinkedHashMap<>();
    }

    public List<String[]> getLichSuAuditLog() {
        // TODO: Cài đặt truy vấn lịch sử hoạt động hệ thống từ CSDL tại đây
        return new ArrayList<>();
    }

    public void ghiLog(String tenDangNhap, String hanhDong, String chiTiet) {
        // TODO: Cài đặt ghi nhận nhật ký hệ thống vào CSDL tại đây
    }
}
