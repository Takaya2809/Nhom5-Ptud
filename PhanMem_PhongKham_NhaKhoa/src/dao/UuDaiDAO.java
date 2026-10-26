package dao;

import entity.UuDai;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Chính Sách Ưu Đãi
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class UuDaiDAO {

    public List<UuDai> getAllUuDai() {
        // TODO: Cài đặt truy vấn danh sách ưu đãi từ CSDL tại đây
        return new ArrayList<>();
    }

    public List<UuDai> getUuDaiDangApDung() {
        // TODO: Cài đặt truy vấn danh sách ưu đãi đang áp dụng từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themUuDai(UuDai ud) {
        // TODO: Cài đặt thêm chương trình ưu đãi vào CSDL tại đây
        return false;
    }

    public boolean capNhatUuDai(UuDai ud) {
        // TODO: Cài đặt cập nhật chính sách ưu đãi trong CSDL tại đây
        return false;
    }

    public boolean doiTrangThai(String maUuDai, String trangThaiMoi) {
        // TODO: Cài đặt đổi trạng thái ưu đãi trong CSDL tại đây
        return false;
    }

    public boolean xoaUuDai(String maUuDai) {
        // TODO: Cài đặt xóa ưu đãi khỏi CSDL tại đây
        return false;
    }

    public List<UuDai> timKiemUuDai(String keyword) {
        // TODO: Cài đặt tìm kiếm ưu đãi trong CSDL tại đây
        return new ArrayList<>();
    }

    public String sinhMaUuDaiMoi() {
        // TODO: Cài đặt tự động sinh mã ưu đãi mới từ CSDL tại đây
        return "UD001";
    }
}
