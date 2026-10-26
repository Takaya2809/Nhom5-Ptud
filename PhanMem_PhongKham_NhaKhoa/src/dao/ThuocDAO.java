package dao;

import entity.Thuoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Danh Mục Thuốc
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class ThuocDAO {

    public List<Thuoc> getAllThuoc() {
        // TODO: Cài đặt truy vấn danh mục thuốc từ CSDL tại đây
        return new ArrayList<>();
    }

    public List<Thuoc> getThuocCanhBaoTonKho() {
        // TODO: Cài đặt truy vấn danh sách thuốc cảnh báo tồn kho từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themThuoc(Thuoc th) {
        // TODO: Cài đặt thêm thuốc mới vào CSDL tại đây
        return false;
    }
}
