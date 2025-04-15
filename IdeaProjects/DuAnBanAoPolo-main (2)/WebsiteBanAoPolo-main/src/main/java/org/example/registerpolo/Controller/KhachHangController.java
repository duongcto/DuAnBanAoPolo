package org.example.registerpolo.Controller;

import org.example.registerpolo.Entity.KhachHang;
import org.example.registerpolo.Repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

public class KhachHangController {

    @Autowired
    private KhachHangRepository khachHangRepo;

    // Danh sách khách hàng
    @GetMapping("khach-hang")
    public String danhSachKhachHang(Model model) {
        model.addAttribute("khachHang", new KhachHang());
        model.addAttribute("listKhachHang", khachHangRepo.findAll());
        return "KhachHang/khach-hang";
    }

    // Chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String detailKhachHang(@PathVariable int id, Model model) {
        KhachHang kh = khachHangRepo.findById(id).orElse(null);
        model.addAttribute("khachHang", kh);
        return "KhachHang/UpdateKhachHang";
    }


    // Form cập nhật khách hàng
    @PostMapping("/update/{id}")
    public String updateKhachHang(@PathVariable int id, @ModelAttribute("khachHang") KhachHang khachHang) {
        khachHangRepo.save(khachHang);
        return "redirect:/khach-hang";
    }

    // Hiển thị form thêm khách hàng
    @GetMapping("/KhachHang/AddKhachHang")
    public String formThemKH(Model model) {
        KhachHang kh = new KhachHang();
        kh.setTrangThai(true); // Đặt trạng thái mặc định
        model.addAttribute("khachHang", kh);
        return "khachhang/AddKhachHang";
    }

    // Xử lý lưu khách hàng
    @PostMapping("/AddKhachHang")
    public String saveKhachHang(@ModelAttribute("khachHang") KhachHang kh) {
        kh.setTrangThai(true); // Đảm bảo trạng thái true
        kh.setMaKH(generateMaKH()); // Sinh mã KH tự động
        System.out.println("ID trước khi lưu: " + kh.getId()); // nên là null
        khachHangRepo.save(kh);
        return "redirect:/khach-hang";
    }

    // Generate mã KH tự động dạng KH001, KH002, ...
    public String generateMaKH() {
        KhachHang lastKH = khachHangRepo.findLastKhachHang();
        if (lastKH == null || lastKH.getMaKH() == null) {
            return "KH001";
        }

        String lastMa = lastKH.getMaKH(); // ví dụ: KH007
        long number = Long.parseLong(lastMa.substring(2)); // ✅ an toàn với số lớn
        return String.format("KH%03d", number + 1); // KH008
    }

}
