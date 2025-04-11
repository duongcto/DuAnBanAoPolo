package org.example.registerpolo.Controller;

import org.example.registerpolo.Entity.SanPham;
import org.example.registerpolo.Repository.SanPhamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/san-pham")
public class SanPhamController {

    @Autowired
    private SanPhamRepo sanPhamRepo;

    @GetMapping
    public String hienThiTatCaSanPham(Model model) {
        List<SanPham> sanPhams = sanPhamRepo.findAll();
        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("sanPham", new SanPham());
        return "san-pham/danh-sach";
    }

    @GetMapping("/them-moi")
    public String hienThiFormThemMoi(Model model) {
        model.addAttribute("sanPham", new SanPham());
        return "san-pham/them-moi";
    }

    @PostMapping("/them-moi")
    public String themMoiSanPham(@ModelAttribute SanPham sanPham, RedirectAttributes redirectAttributes) {
        try {
            // Thiết lập trạng thái mặc định nếu chưa được thiết lập
            if (sanPham.getTrangThai() == null) {
                sanPham.setTrangThai(true);
            }
            sanPhamRepo.save(sanPham);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới sản phẩm thành công!");
            return "redirect:/san-pham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "redirect:/san-pham/them-moi";
        }
    }

    @GetMapping("/chi-tiet/{id}")
    public String xemChiTietSanPham(@PathVariable Integer id, Model model) {
        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
        if (sanPhamOpt.isPresent()) {
            model.addAttribute("sanPham", sanPhamOpt.get());
            return "san-pham/chi-tiet";
        } else {
            return "redirect:/san-pham";
        }
    }

    @GetMapping("/sua/{id}")
    public String hienThiFormCapNhat(@PathVariable Integer id, Model model) {
        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
        if (sanPhamOpt.isPresent()) {
            model.addAttribute("sanPham", sanPhamOpt.get());
            return "san-pham/cap-nhat";
        } else {
            return "redirect:/san-pham";
        }
    }

    @PostMapping("/sua/{id}")
    public String capNhatSanPham(@PathVariable Integer id, @ModelAttribute SanPham sanPham, RedirectAttributes redirectAttributes) {
        try {
            Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
            if (sanPhamOpt.isPresent()) {
                SanPham existingSanPham = sanPhamOpt.get();
                existingSanPham.setMa(sanPham.getMa());
                existingSanPham.setTen(sanPham.getTen());
                existingSanPham.setTrangThai(sanPham.getTrangThai());
                
                sanPhamRepo.save(existingSanPham);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
            }
            return "redirect:/san-pham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return "redirect:/san-pham/sua/" + id;
        }
    }

    @GetMapping("/xoa/{id}")
    public String xoaSanPham(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
            if (sanPhamOpt.isPresent()) {
                sanPhamRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/san-pham";
    }
} 