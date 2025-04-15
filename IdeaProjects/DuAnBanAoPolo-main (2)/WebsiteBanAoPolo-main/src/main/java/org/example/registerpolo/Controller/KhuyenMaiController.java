package org.example.registerpolo.Controller;


import org.example.registerpolo.Entity.KhuyenMai;
import org.example.registerpolo.Repository.KhuyenMaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/khuyen-mai")
public class KhuyenMaiController {

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepo;

    @GetMapping
    public String danhSach(Model model) {
        List<KhuyenMai> list = khuyenMaiRepo.findAll();
        model.addAttribute("khuyenMais", list);
        return "khuyenMai/index";
    }

    @GetMapping("/them")
    public String hienFormThem(Model model) {
        model.addAttribute("khuyenMai", new KhuyenMai());
        return "khuyenMai/form";
    }

    @PostMapping("/luu")
    public String luu(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai) {
        khuyenMaiRepo.save(khuyenMai);
        return "redirect:/khuyen-mai";
    }

    @GetMapping("/sua/{id}")
    public String hienFormSua(@PathVariable("id") Integer id, Model model) {
        KhuyenMai km = khuyenMaiRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        model.addAttribute("khuyenMai", km);
        return "khuyenMai/form";
    }

    @GetMapping("/xoa/{id}")
    public String xoa(@PathVariable("id") Integer id) {
        khuyenMaiRepo.deleteById(id);
        return "redirect:/khuyen-mai";
    }
}
