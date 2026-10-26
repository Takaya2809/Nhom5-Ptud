package dao;

import entity.CaTruc;
import entity.PhanCongCaTruc;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Ca Trực & Phân Công
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class CaTrucDAO {

    public List<CaTruc> getAllCaTruc() {
        // TODO: Cài đặt truy vấn danh sách ca trực từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themCaTruc(CaTruc ct) {
        // TODO: Cài đặt thêm ca trực vào CSDL tại đây
        return false;
    }

    public boolean capNhatCaTruc(CaTruc ct) {
        // TODO: Cài đặt cập nhật ca trực trong CSDL tại đây
        return false;
    }

    public List<PhanCongCaTruc> getPhanCongByCa(String maCa) {
        // TODO: Cài đặt truy vấn danh sách phân công theo ca từ CSDL tại đây
        return new ArrayList<>();
    }

    public List<PhanCongCaTruc> getAllPhanCong() {
        // TODO: Cài đặt truy vấn tất cả phân công ca trực từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themPhanCong(PhanCongCaTruc pc) {
        // TODO: Cài đặt thêm phân công ca trực vào CSDL tại đây
        return false;
    }

    public boolean xoaPhanCong(int maPhanCong) {
        // TODO: Cài đặt xóa phân công ca trực trong CSDL tại đây
        return false;
    }

    public boolean diemDanh(int maPhanCong, String trangThaiDiemDanh) {
        // TODO: Cài đặt điểm danh nhân sự ca trực trong CSDL tại đây
        return false;
    }

    public boolean doiCa(int maPhanCong, String maNVMoi, String ghiChu) {
        // TODO: Cài đặt đổi ca trực trong CSDL tại đây
        return false;
    }

    public boolean kiemTraDinhBien(String maCa) {
        // TODO: Cài đặt kiểm tra định biên nhân sự ca trực từ CSDL tại đây
        return true;
    }
}
