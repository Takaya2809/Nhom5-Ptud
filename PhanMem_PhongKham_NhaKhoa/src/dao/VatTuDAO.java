package dao;

import entity.VatTu;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Vật Tư & Dược Phẩm
 * (Khung giao tiếp CSDL - Đã lược bỏ logic để sẵn sàng cài đặt mới)
 */
public class VatTuDAO {

    public List<VatTu> getAllVatTu() {
        // TODO: Cài đặt truy vấn danh sách vật tư từ CSDL tại đây
        return new ArrayList<>();
    }

    public List<VatTu> getVatTuCanhBao() {
        // TODO: Cài đặt truy vấn danh sách vật tư cảnh báo tồn kho từ CSDL tại đây
        return new ArrayList<>();
    }

    public boolean themVatTu(VatTu vt) {
        // TODO: Cài đặt thêm vật tư vào CSDL tại đây
        return false;
    }

    public boolean capNhatVatTu(VatTu vt) {
        // TODO: Cài đặt cập nhật vật tư trong CSDL tại đây
        return false;
    }

    public boolean nhapXuatKho(String maVT, int soLuongThayDoi) {
        // TODO: Cài đặt nhập xuất tồn kho vật tư trong CSDL tại đây
        return false;
    }

    public boolean thietLapDinhMuc(String maVT, int dinhMucMoi) {
        // TODO: Cài đặt thiết lập định mức tối thiểu trong CSDL tại đây
        return false;
    }

    public List<VatTu> timKiem(String keyword) {
        // TODO: Cài đặt tìm kiếm vật tư trong CSDL tại đây
        return new ArrayList<>();
    }

    public String sinhMaVTMoi() {
        // TODO: Cài đặt tự động sinh mã vật tư mới từ CSDL tại đây
        return "VT001";
    }
}
