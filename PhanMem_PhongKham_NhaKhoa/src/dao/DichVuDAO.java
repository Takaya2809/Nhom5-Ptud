package dao;

import entity.DichVu;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Danh Mục Dịch Vụ
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class DichVuDAO {

    public List<DichVu> getAllDichVu() {
        // TODO: Cài đặt truy vấn danh sách dịch vụ từ CSDL tại đây
        return new ArrayList<>();
    }

    public List<DichVu> getDichVuHoatDong() {
        // TODO: Cài đặt truy vấn danh sách dịch vụ đang hoạt động từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themDichVu(DichVu dv) {
        // TODO: Cài đặt thêm dịch vụ vào CSDL tại đây
        return false;
    }

    public boolean capNhatDichVu(DichVu dv) {
        // TODO: Cài đặt cập nhật dịch vụ trong CSDL tại đây
        return false;
    }

    public boolean chuyenTrangThai(String maDichVu, String trangThaiMoi) {
        // TODO: Cài đặt chuyển trạng thái dịch vụ trong CSDL tại đây
        return false;
    }

    public List<DichVu> timKiem(String keyword) {
        // TODO: Cài đặt tìm kiếm dịch vụ trong CSDL tại đây
        return new ArrayList<>();
    }

    public String sinhMaDVMoi() {
        // TODO: Cài đặt tự động sinh mã dịch vụ mới từ CSDL tại đây
        return "DV001";
    }
}
