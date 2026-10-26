package dao;

import entity.HoaDonKham;
import entity.UuDai;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Hóa Đơn Khám
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class HoaDonDAO {

    public List<HoaDonKham> getAllHoaDon() {
        // TODO: Cài đặt truy vấn danh sách hóa đơn từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themHoaDon(HoaDonKham hd) {
        // TODO: Cài đặt lưu hóa đơn khám vào CSDL tại đây
        return false;
    }

    public List<UuDai> getAllUuDai() {
        // TODO: Cài đặt truy vấn danh sách ưu đãi từ CSDL tại đây
        return new ArrayList<>();
    }
}
