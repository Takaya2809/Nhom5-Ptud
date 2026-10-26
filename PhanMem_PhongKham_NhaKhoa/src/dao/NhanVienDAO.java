package dao;

import entity.NhanVien;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Nhân Viên & Bác Sĩ
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class NhanVienDAO {

    public List<NhanVien> getAllNhanVien() {
        // TODO: Cài đặt truy vấn danh sách nhân viên từ CSDL tại đây
        return new ArrayList<>();
    }

    public NhanVien getNhanVienByMa(String maNV) {
        // TODO: Cài đặt tìm nhân viên theo mã từ CSDL tại đây
        return null;
    }

    public boolean themNhanVien(NhanVien nv) {
        // TODO: Cài đặt thêm nhân viên vào CSDL tại đây
        return false;
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        // TODO: Cài đặt cập nhật thông tin nhân viên trong CSDL tại đây
        return false;
    }

    public boolean chuyenTrangThaiNghiViec(String maNV) {
        // TODO: Cài đặt cập nhật trạng thái nghỉ việc trong CSDL tại đây
        return false;
    }

    public List<NhanVien> timKiem(String keyword) {
        // TODO: Cài đặt tìm kiếm nhân viên trong CSDL tại đây
        return new ArrayList<>();
    }

    public String sinhMaNVMoi(String chucDanh) {
        // TODO: Cài đặt tự động sinh mã nhân viên mới từ CSDL tại đây
        if (chucDanh != null && (chucDanh.contains("Bác sĩ") || chucDanh.contains("Nha sĩ"))) {
            return "BS001";
        }
        return "NV001";
    }
}
