package org.example.registerpolo.Controller;

import org.example.registerpolo.Entity.KhuyenMai;
import org.example.registerpolo.Entity.SanPham;
import org.example.registerpolo.Entity.SanPham_KhuyenMai;
import org.example.registerpolo.Repository.KhuyenMaiRepo;
import org.example.registerpolo.Repository.SanPhamKhuyenMaiRepo;
import org.example.registerpolo.Repository.SanPhamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/khuyen-mai-san-pham")
public class SanPhamKhuyenMaiController {

    @Autowired
    private SanPhamKhuyenMaiRepo sanPhamKhuyenMaiRepo;

    @Autowired
    private SanPhamRepo sanPhamRepo;

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepo;

    @GetMapping
    public String hienThiTatCaSanPhamKhuyenMai(Model model) {
        List<SanPham_KhuyenMai> sanPhamKhuyenMais = sanPhamKhuyenMaiRepo.findAll();
        model.addAttribute("sanPhamKhuyenMais", sanPhamKhuyenMais);
        return "khuyen-mai-san-pham/danh-sach";
    }

    @GetMapping("/them-moi")
    public String hienThiFormThemMoi(Model model) {
        model.addAttribute("sanPhamKhuyenMai", new SanPham_KhuyenMai());
        
        // Lấy danh sách sản phẩm và khuyến mãi để hiển thị trong dropdown
        List<SanPham> sanPhams = sanPhamRepo.findAll();
        List<KhuyenMai> khuyenMais = khuyenMaiRepo.findAll();
        
        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("khuyenMais", khuyenMais);
        
        return "khuyen-mai-san-pham/them-moi";
    }

    @PostMapping("/them-moi")
    public String themMoiSanPhamKhuyenMai(@ModelAttribute SanPham_KhuyenMai sanPhamKhuyenMai, RedirectAttributes redirectAttributes) {
        try {
            // Thiết lập trạng thái mặc định nếu chưa được thiết lập
            if (sanPhamKhuyenMai.getTrangThai() == null) {
                sanPhamKhuyenMai.setTrangThai(true);
            }
            
            sanPhamKhuyenMaiRepo.save(sanPhamKhuyenMai);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới khuyến mãi cho sản phẩm thành công!");
            return "redirect:/khuyen-mai-san-pham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm khuyến mãi cho sản phẩm: " + e.getMessage());
            return "redirect:/khuyen-mai-san-pham/them-moi";
        }
    }

    @GetMapping("/chi-tiet/{id}")
    public String xemChiTietSanPhamKhuyenMai(@PathVariable Integer id, Model model) {
        Optional<SanPham_KhuyenMai> sanPhamKhuyenMaiOpt = sanPhamKhuyenMaiRepo.findById(id);
        if (sanPhamKhuyenMaiOpt.isPresent()) {
            model.addAttribute("sanPhamKhuyenMai", sanPhamKhuyenMaiOpt.get());
            return "khuyen-mai-san-pham/chi-tiet";
        } else {
            return "redirect:/khuyen-mai-san-pham";
        }
    }

    @GetMapping("/sua/{id}")
    public String hienThiFormCapNhat(@PathVariable Integer id, Model model) {
        Optional<SanPham_KhuyenMai> sanPhamKhuyenMaiOpt = sanPhamKhuyenMaiRepo.findById(id);
        if (sanPhamKhuyenMaiOpt.isPresent()) {
            model.addAttribute("sanPhamKhuyenMai", sanPhamKhuyenMaiOpt.get());
            
            // Lấy danh sách sản phẩm và khuyến mãi để hiển thị trong dropdown
            List<SanPham> sanPhams = sanPhamRepo.findAll();
            List<KhuyenMai> khuyenMais = khuyenMaiRepo.findAll();
            
            model.addAttribute("sanPhams", sanPhams);
            model.addAttribute("khuyenMais", khuyenMais);
            
            return "khuyen-mai-san-pham/cap-nhat";
        } else {
            return "redirect:/khuyen-mai-san-pham";
        }
    }

    @PostMapping("/sua/{id}")
    public String capNhatSanPhamKhuyenMai(@PathVariable Integer id, @ModelAttribute SanPham_KhuyenMai sanPhamKhuyenMai, RedirectAttributes redirectAttributes) {
        try {
            Optional<SanPham_KhuyenMai> sanPhamKhuyenMaiOpt = sanPhamKhuyenMaiRepo.findById(id);
            if (sanPhamKhuyenMaiOpt.isPresent()) {
                SanPham_KhuyenMai existingSanPhamKhuyenMai = sanPhamKhuyenMaiOpt.get();
                existingSanPhamKhuyenMai.setSanPham(sanPhamKhuyenMai.getSanPham());
                existingSanPhamKhuyenMai.setKhuyenMai(sanPhamKhuyenMai.getKhuyenMai());
                existingSanPhamKhuyenMai.setTrangThai(sanPhamKhuyenMai.getTrangThai());
                
                sanPhamKhuyenMaiRepo.save(existingSanPhamKhuyenMai);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khuyến mãi sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khuyến mãi sản phẩm!");
            }
            return "redirect:/khuyen-mai-san-pham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khuyến mãi sản phẩm: " + e.getMessage());
            return "redirect:/khuyen-mai-san-pham/sua/" + id;
        }
    }

    @GetMapping("/xoa/{id}")
    public String xoaSanPhamKhuyenMai(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<SanPham_KhuyenMai> sanPhamKhuyenMaiOpt = sanPhamKhuyenMaiRepo.findById(id);
            if (sanPhamKhuyenMaiOpt.isPresent()) {
                sanPhamKhuyenMaiRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa khuyến mãi sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khuyến mãi sản phẩm!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khuyến mãi sản phẩm: " + e.getMessage());
        }
        return "redirect:/khuyen-mai-san-pham";
    }
}