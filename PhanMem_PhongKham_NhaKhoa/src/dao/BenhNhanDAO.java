package dao;

import entity.BenhNhan;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Bệnh Nhân
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class BenhNhanDAO {

    public List<BenhNhan> getAllBenhNhan() {
        // TODO: Cài đặt truy vấn danh sách bệnh nhân từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themBenhNhan(BenhNhan bn) {
        // TODO: Cài đặt thêm bệnh nhân vào CSDL tại đây
        return false;
    }

    public boolean capNhatBenhNhan(BenhNhan bn) {
        // TODO: Cài đặt cập nhật thông tin bệnh nhân trong CSDL tại đây
        return false;
    }

    public List<BenhNhan> timKiem(String keyword) {
        // TODO: Cài đặt tìm kiếm bệnh nhân trong CSDL tại đây
        return new ArrayList<>();
    }

    public String sinhMaBNMoi() {
        // TODO: Cài đặt tự động sinh mã bệnh nhân mới từ CSDL tại đây
        return "BN001";
    }
}
