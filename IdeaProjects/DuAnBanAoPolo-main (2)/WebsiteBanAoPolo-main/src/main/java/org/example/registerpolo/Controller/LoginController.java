package org.example.registerpolo.Controller;

import org.example.registerpolo.Entity.NhanVien;
import org.example.registerpolo.Repository.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    NhanVienRepo nhan_vienrepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Tên file login.html trong thư mục templates
    }

    @PostMapping("login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, RedirectAttributes redirectAttributes) {
        List<NhanVien> nv = nhan_vienrepository.findAll();
        int i=0;
        for (NhanVien n:nv) {
            if (n.getTenDangNhap().equals(username) && n.getMatKhau().equals(password)) {
                return "redirect:/trang-chu";
            }else if (!username.equals(null) && !password.equals(null)){
                if (!n.getTenDangNhap().equals(username) && !n.getMatKhau().equals(password)) {
                    redirectAttributes.addFlashAttribute("message1", "Đăng nhập thất bại");
                    return "redirect:/login";
                }
            }

        }
        return "redirect:/trang-chu";
    }

//    @GetMapping("erron")
//    public String erron(Model model,RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("messeger1","Đăng nhập thất bại");
//        return "redirect:/login";
//    }


}
