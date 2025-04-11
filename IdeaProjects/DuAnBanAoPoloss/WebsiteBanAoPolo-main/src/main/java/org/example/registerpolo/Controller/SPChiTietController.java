package org.example.registerpolo.Controller;

import org.example.registerpolo.Entity.KichThuoc;
import org.example.registerpolo.Entity.MauSac;
import org.example.registerpolo.Entity.SPChiTiet;
import org.example.registerpolo.Entity.SanPham;
import org.example.registerpolo.Repository.KichThuocRepo;
import org.example.registerpolo.Repository.MauSacRepo;
import org.example.registerpolo.Repository.SPChiTietRepo;
import org.example.registerpolo.Repository.SanPhamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/san-pham-chi-tiet")
public class SPChiTietController {

    @Autowired
    private SPChiTietRepo spChiTietRepo;

    @Autowired
    private SanPhamRepo sanPhamRepo;

    @Autowired
    private MauSacRepo mauSacRepo;

    @Autowired
    private KichThuocRepo kichThuocRepo;

    @GetMapping
    public String hienThiTatCaSPChiTiet(Model model) {
        List<SPChiTiet> spChiTiets = spChiTietRepo.findAll();
        model.addAttribute("spChiTiets", spChiTiets);
        return "sp-chi-tiet/danh-sach";
    }

    @GetMapping("/them-moi")
    public String hienThiFormThemMoi(Model model) {
        model.addAttribute("spChiTiet", new SPChiTiet());
        
        // Lấy danh sách sản phẩm, màu sắc, kích thước để hiển thị trong dropdown
        List<SanPham> sanPhams = sanPhamRepo.findAll();
        List<MauSac> mauSacs = mauSacRepo.findAll();
        List<KichThuoc> kichThuocs = kichThuocRepo.findAll();
        
        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("mauSacs", mauSacs);
        model.addAttribute("kichThuocs", kichThuocs);
        
        return "sp-chi-tiet/them-moi";
    }

    @PostMapping("/them-moi")
    public String themMoiSPChiTiet(@ModelAttribute SPChiTiet spChiTiet, RedirectAttributes redirectAttributes) {
        try {
            // Thiết lập trạng thái mặc định nếu chưa được thiết lập
            if (spChiTiet.getTrangThai() == null) {
                spChiTiet.setTrangThai(true);
            }
            
            // Mặc định số lượng là 0 nếu chưa được thiết lập
            if (spChiTiet.getSoLuong() == null) {
                spChiTiet.setSoLuong(0);
            }
            
            spChiTietRepo.save(spChiTiet);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới chi tiết sản phẩm thành công!");
            return "redirect:/san-pham-chi-tiet";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm chi tiết sản phẩm: " + e.getMessage());
            return "redirect:/san-pham-chi-tiet/them-moi";
        }
    }

    @GetMapping("/chi-tiet/{id}")
    public String xemChiTietSPChiTiet(@PathVariable Integer id, Model model) {
        Optional<SPChiTiet> spChiTietOpt = spChiTietRepo.findById(id);
        if (spChiTietOpt.isPresent()) {
            model.addAttribute("spChiTiet", spChiTietOpt.get());
            return "sp-chi-tiet/chi-tiet";
        } else {
            return "redirect:/san-pham-chi-tiet";
        }
    }

    @GetMapping("/sua/{id}")
    public String hienThiFormCapNhat(@PathVariable Integer id, Model model) {
        Optional<SPChiTiet> spChiTietOpt = spChiTietRepo.findById(id);
        if (spChiTietOpt.isPresent()) {
            model.addAttribute("spChiTiet", spChiTietOpt.get());
            
            // Lấy danh sách sản phẩm, màu sắc, kích thước để hiển thị trong dropdown
            List<SanPham> sanPhams = sanPhamRepo.findAll();
            List<MauSac> mauSacs = mauSacRepo.findAll();
            List<KichThuoc> kichThuocs = kichThuocRepo.findAll();
            
            model.addAttribute("sanPhams", sanPhams);
            model.addAttribute("mauSacs", mauSacs);
            model.addAttribute("kichThuocs", kichThuocs);
            
            return "sp-chi-tiet/cap-nhat";
        } else {
            return "redirect:/san-pham-chi-tiet";
        }
    }

    @PostMapping("/sua/{id}")
    public String capNhatSPChiTiet(@PathVariable Integer id, @ModelAttribute SPChiTiet spChiTiet, RedirectAttributes redirectAttributes) {
        try {
            Optional<SPChiTiet> spChiTietOpt = spChiTietRepo.findById(id);
            if (spChiTietOpt.isPresent()) {
                SPChiTiet existingSPChiTiet = spChiTietOpt.get();
                existingSPChiTiet.setMaSPCT(spChiTiet.getMaSPCT());
                existingSPChiTiet.setSanPham(spChiTiet.getSanPham());
                existingSPChiTiet.setMauSac(spChiTiet.getMauSac());
                existingSPChiTiet.setKichThuoc(spChiTiet.getKichThuoc());
                existingSPChiTiet.setSoLuong(spChiTiet.getSoLuong());
                existingSPChiTiet.setDonGia(spChiTiet.getDonGia());
                existingSPChiTiet.setTrangThai(spChiTiet.getTrangThai());
                
                spChiTietRepo.save(existingSPChiTiet);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chi tiết sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chi tiết sản phẩm!");
            }
            return "redirect:/san-pham-chi-tiet";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật chi tiết sản phẩm: " + e.getMessage());
            return "redirect:/san-pham-chi-tiet/sua/" + id;
        }
    }

    @GetMapping("/xoa/{id}")
    public String xoaSPChiTiet(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<SPChiTiet> spChiTietOpt = spChiTietRepo.findById(id);
            if (spChiTietOpt.isPresent()) {
                spChiTietRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa chi tiết sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chi tiết sản phẩm!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa chi tiết sản phẩm: " + e.getMessage());
        }
        return "redirect:/san-pham-chi-tiet";
    }
} 