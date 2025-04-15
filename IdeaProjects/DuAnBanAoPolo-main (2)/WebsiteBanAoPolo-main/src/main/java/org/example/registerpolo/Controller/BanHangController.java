package org.example.registerpolo.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.registerpolo.Entity.*;
import org.example.registerpolo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller

public class BanHangController {

    @Autowired
    private HoaDonRepo hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepository;

    @Autowired
    private SPChiTietRepo sanPhamChiTietRepository;

    @Autowired
    private NhanVienRepo nhanVienRepository;
    @Autowired
    private MauSacRepo mauSacRepo;
    @Autowired
    private KichThuocRepo kichThuocRepo;
    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepository;
    @Autowired
    private HttpSession session;

    @GetMapping("/ban-hang")
    public String hienThiTrangBanHang(@RequestParam(value = "loaiKhach", required = false) String loaiKhach,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "mauSac", required = false) Integer idMau,
                                      @RequestParam(value = "kichThuoc", required = false) Integer idSize,
                                      @RequestParam(value = "idHoaDon", required = false) Integer idHoaDon, Model model) {
        // Danh sách hóa đơn chưa thanh toán
        List<HoaDon> hoaDonChuaThanhToan = hoaDonRepository.findByTrangThai(false);
        model.addAttribute("hoaDonList", hoaDonChuaThanhToan);

        // Dữ liệu chung
        model.addAttribute("danhSachSanPham", sanPhamChiTietRepository.findAll());
        model.addAttribute("danhSachKhuyenMai", khuyenMaiRepository.findAll());

        // Nhân viên đăng nhập giả lập
        NhanVien nhanVien = nhanVienRepository.findById(1).orElse(null);
        model.addAttribute("nhanVien", nhanVien);

        // Nếu người dùng chọn một hóa đơn (idHoaDon != null), thì load thông tin hóa đơn đó
        HoaDon hoaDon;
        List<HoaDonChiTiet> chiTietList = new ArrayList<>();

        if (idHoaDon != null) {
            hoaDon = hoaDonRepository.findById(idHoaDon).orElse(new HoaDon());
            chiTietList = hoaDonChiTietRepository.findByHoaDon_Id(idHoaDon);
        } else {
            hoaDon = new HoaDon();
            hoaDon.setNhanVien(nhanVien);
            hoaDon.setNgayMuaHang(LocalDate.now());
            hoaDon.setKhachHang(null); // Chưa có khách
        }
        List<SPChiTiet> danhSachSanPham = sanPhamChiTietRepository.filterSanPham(keyword, idMau, idSize);
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        BigDecimal tongTien = BigDecimal.ZERO;

        if (hoaDon != null && hoaDon.getHoaDonChiTietList() != null) {
            for (HoaDonChiTiet cthd : hoaDon.getHoaDonChiTietList()) {
                BigDecimal donGia = cthd.getDonGia();
                int soLuong = cthd.getSoLuong();
                tongTien = tongTien.add(donGia.multiply(BigDecimal.valueOf(soLuong)));
            }
        }

        model.addAttribute("tongTien", tongTien);
        // Load danh sách màu và size cho dropdown
        model.addAttribute("loaiKhach", loaiKhach);
        model.addAttribute("dsMauSac", mauSacRepo.findAll());
        model.addAttribute("dsKichThuoc", kichThuocRepo.findAll());
        model.addAttribute("idHoaDonDangChon", idHoaDon); // id của hóa đơn đang được chọn
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hoaDonChiTiet", chiTietList);

        return "ban-hang";
    }


    @PostMapping("/ban-hang/tao-hoa-don")
    public String taoHoaDon(RedirectAttributes redirectAttributes) {
        // Lấy danh sách hóa đơn chưa thanh toán
        List<HoaDon> hoaDonChuaTT = hoaDonRepository.findByTrangThai(false);

        // Kiểm tra nếu đã đủ 5 hóa đơn thì không cho tạo nữa
        if (hoaDonChuaTT.size() >= 5) {
            redirectAttributes.addFlashAttribute("error", "Chỉ được tạo tối đa 5 hóa đơn chưa thanh toán.");
            return "redirect:/ban-hang";
        }

        // Nếu chưa đủ thì tạo hóa đơn mới
        HoaDon hoaDon = new HoaDon();
        hoaDon.setTrangThai(false);
        hoaDon.setNgayMuaHang(LocalDate.now());

        KhachHang khachHang = khachHangRepository.findById(1).orElse(null);
        hoaDon.setKhachHang(khachHang);

        hoaDon.setNhanVien(nhanVienRepository.findById(1).orElse(null));

        hoaDonRepository.save(hoaDon);

        return "redirect:/ban-hang";
    }
    @PostMapping("/ban-hang/xoa")
    public String xoaSanPhamTrongHoaDon(@RequestParam("idChiTiet") Integer idChiTiet) {
        HoaDonChiTiet chiTiet = hoaDonChiTietRepository.findById(idChiTiet).orElse(null);
        if (chiTiet != null) {
            // Cập nhật lại số lượng tồn kho sản phẩm
            SPChiTiet sp = chiTiet.getSpChiTiet();
            sp.setSoLuong(sp.getSoLuong() + chiTiet.getSoLuong());
            sanPhamChiTietRepository.save(sp);

            // Xóa chi tiết hóa đơn
            hoaDonChiTietRepository.deleteById(idChiTiet);
        }
        return "redirect:/ban-hang?idHoaDon=" + chiTiet.getHoaDon().getId(); // quay lại hóa đơn hiện tại
    }
    @PostMapping("/ban-hang/sua-so-luong")
    public String suaSoLuong(@RequestParam("idChiTiet") Integer idChiTiet,
                             @RequestParam("soLuongMoi") String soLuongStr,
                             RedirectAttributes redirect) {
        try {
            int soLuongMoi = Integer.parseInt(soLuongStr);
            HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(idChiTiet).orElse(null);
            if (hdct == null) {
                redirect.addFlashAttribute("error", "Hóa đơn chi tiết không tồn tại.");
                return "redirect:/ban-hang";
            }

            SPChiTiet sp = hdct.getSpChiTiet();

            int soLuongHienTai = hdct.getSoLuong();
            int thayDoi = soLuongMoi - soLuongHienTai;

            if (soLuongMoi < 0) {
                redirect.addFlashAttribute("error", "Vui lòng nhập số lớn hơn hoặc bằng 0.");
            } else if (soLuongMoi == 0) {
                hoaDonChiTietRepository.delete(hdct);
                sp.setSoLuong(sp.getSoLuong() + soLuongHienTai);
                sanPhamChiTietRepository.save(sp);
            } else if (thayDoi > sp.getSoLuong()) {
                redirect.addFlashAttribute("error", "Vui lòng nhập số hợp lệ (không vượt quá tồn kho).");
            } else {
                hdct.setSoLuong(soLuongMoi);
                hoaDonChiTietRepository.save(hdct);
                sp.setSoLuong(sp.getSoLuong() - thayDoi);
                sanPhamChiTietRepository.save(sp);
            }

        } catch (NumberFormatException e) {
            redirect.addFlashAttribute("error", "Vui lòng chỉ nhập số.");
        }
        return "redirect:/ban-hang";
    }
    @PostMapping("/ban-hang/them")
    public String themSanPhamVaoHoaDon(@RequestParam("idSanPham") Integer idSP,
                                       @RequestParam("idHoaDon") Integer idHoaDon) {
        // Lấy hóa đơn và sản phẩm chi tiết
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElse(null);
        SPChiTiet sp = sanPhamChiTietRepository.findById(idSP).orElse(null);

        if (hoaDon == null || sp == null) {
            return "redirect:/ban-hang?idHoaDon=" + idHoaDon;
        }

        // Kiểm tra sản phẩm đã có trong hóa đơn chưa
        HoaDonChiTiet daCo = hoaDonChiTietRepository.findByHoaDon_IdAndSpChiTiet_Id(idHoaDon, idSP);

        if (daCo != null) {
            daCo.setSoLuong(daCo.getSoLuong() + 1);
            hoaDonChiTietRepository.save(daCo);
        } else {
            HoaDonChiTiet moi = new HoaDonChiTiet();
            moi.setHoaDon(hoaDon);
            moi.setSpChiTiet(sp);
            moi.setSoLuong(1);
            moi.setDonGia(sp.getDonGia());
            moi.setTrangThai(true);
            hoaDonChiTietRepository.save(moi);
        }

        // Trừ tồn kho
        sp.setSoLuong(sp.getSoLuong() - 1);
        sanPhamChiTietRepository.save(sp);

        return "redirect:/ban-hang?idHoaDon=" + idHoaDon;
    }
    @PostMapping("/ban-hang/chon-khach-la")
    public String chonKhachLa(@RequestParam("idHoaDon") Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElse(null);
        if (hoaDon != null) {
            hoaDon.setKhachHang(null); // đánh dấu khách lạ
            hoaDonRepository.save(hoaDon);
        }
        return "redirect:/ban-hang?idHoaDon=" + idHoaDon;
    }
    @GetMapping("/khach-hang/tim-kiem")
    public String timKiemKhachHang(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "idHoaDon", required = false) Integer idHoaDon,
            @RequestParam(value = "loaiKhach", required = false) String loaiKhach,
            RedirectAttributes redirectAttributes
    ) {
        KhachHang khachHang = khachHangRepository.findBySdt(keyword);

        if (khachHang != null) {
            if (idHoaDon != null) {
                HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElse(null);
                if (hoaDon != null) {
                    hoaDon.setKhachHang(khachHang);
                    hoaDonRepository.save(hoaDon);
                }
                redirectAttributes.addAttribute("idHoaDon", idHoaDon);
            }
            redirectAttributes.addAttribute("loaiKhach", loaiKhach); // Truyền lại
            return "redirect:/ban-hang";
        }

        redirectAttributes.addFlashAttribute("loiKhach", "Không tìm thấy khách hàng với số điện thoại: " + keyword);
        if (idHoaDon != null) {
            redirectAttributes.addAttribute("idHoaDon", idHoaDon);
        }
        redirectAttributes.addAttribute("loaiKhach", loaiKhach); // Truyền lại
        return "redirect:/ban-hang";
    }
    @PostMapping("/ban-hang/thanh-toan")
    public String thanhToan(
            @RequestParam("idHoaDon") Integer idHoaDon,
            @RequestParam("tienSauGiam") BigDecimal tienSauGiam,
            @RequestParam(value = "maGiamGia", required = false) Integer maGiamGia
    ) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElse(null);

        if (hoaDon != null) {
            hoaDon.setTrangThai(true);
            hoaDon.setTongTien(tienSauGiam);

            if (maGiamGia != null) {
                KhuyenMai khuyenMai = khuyenMaiRepository.findById(maGiamGia).orElse(null);
                hoaDon.setKhuyenMai(khuyenMai);
            }

            hoaDonRepository.save(hoaDon);
        }

        return "redirect:/ban-hang";
    }

    // Các hàm thêm sản phẩm, tạo hóa đơn, thanh toán sẽ thêm tiếp nếu cần
}


