package dao;

import entity.LichHen;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Lịch Hẹn
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class LichHenDAO {

    public List<LichHen> getAllLichHen() {
        // TODO: Cài đặt truy vấn danh sách lịch hẹn từ CSDL tại đây
        return new ArrayList<>();
    }

    public List<LichHen> getLichHenHomNay() {
        // TODO: Cài đặt truy vấn danh sách lịch hẹn hôm nay từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themLichHen(LichHen lh) {
        // TODO: Cài đặt thêm lịch hẹn mới vào CSDL tại đây
        return false;
    }

    public boolean capNhatTrangThai(String maLichHen, String trangThai) {
        // TODO: Cài đặt cập nhật trạng thái lịch hẹn trong CSDL tại đây
        return false;
    }
}
