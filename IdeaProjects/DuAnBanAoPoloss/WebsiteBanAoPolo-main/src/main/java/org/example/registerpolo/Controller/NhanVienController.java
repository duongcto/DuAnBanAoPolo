package org.example.registerpolo.Controller;




import org.example.registerpolo.Entity.NhanVien;
import org.example.registerpolo.Entity.VaiTro;
import org.example.registerpolo.Repository.NhanVienRepo;
import org.example.registerpolo.Repository.VaiTroRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NhanVienController {

    @Autowired
    NhanVienRepo nhan_vienrepository;

    @Autowired
    VaiTroRepo vai_TroRepository;

    @GetMapping("nhan-vien")
    public String nhan_vien (Model model){
        List<NhanVien> nv = nhan_vienrepository.findAll();
        List<VaiTro> vt= vai_TroRepository.findAll();
        model.addAttribute("nhanvien",nv);
        model.addAttribute("vaitroid",vt);
        model.addAttribute("nhanVien", new NhanVien());
        return "nhan_vien/nhan_vien";
    }

    @PostMapping("add")
    public String add(NhanVien nhanVien){
        nhan_vienrepository.save(nhanVien);
        return "redirect:/nhan-vien";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        nhan_vienrepository.deleteById(id);
        return "redirect:/nhan-vien";
    }
    @GetMapping("detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model){
        NhanVien nv= nhan_vienrepository.findById(id).get();
        List<VaiTro> vt= vai_TroRepository.findAll();
        model.addAttribute("nhanVien",nv);
        model.addAttribute("vaitroid",vt);
        return "nhan_vien/detail";
    }

    @PostMapping("detail/update/{id}")
    public String update(@PathVariable("id") int id, NhanVien nhanVien){
        NhanVien nv= nhan_vienrepository.findById(id).get();
        nhanVien.setId(id);
        NhanVien nvdata= new NhanVien(nhanVien.getId(),nhanVien.getTen(),nhanVien.getMaNV(),nv.getTenDangNhap(),nv.getMatKhau(),nhanVien.getVaiTro(),nhanVien.getTrangThai());
        nhan_vienrepository.save(nvdata);
        return "redirect:/nhan-vien";
    }

    @GetMapping("Exit")
    public String exit(){
        return "redirect:/nhan-vien";
    }



}