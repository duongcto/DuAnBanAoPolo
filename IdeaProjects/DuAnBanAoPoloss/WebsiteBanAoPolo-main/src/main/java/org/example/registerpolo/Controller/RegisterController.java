package org.example.registerpolo.Controller;


import jakarta.servlet.http.HttpSession;
import org.example.registerpolo.Entity.KhachHang;
import org.example.registerpolo.Repository.KhachHangRepository;
import org.example.registerpolo.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class RegisterController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Hiển thị form đăng ký
    @GetMapping("/khach-hang")
    public String showForm(Model model) {
        model.addAttribute("khachHang", new KhachHang());
        return "register";
    }

    // Gửi mã xác nhận
    @PostMapping("/send-code")
    public String sendCode(@ModelAttribute("khachHang") KhachHang khachHang,
                           HttpSession session,
                           Model model) {
        String email = khachHang.getEmail();
        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập email trước khi gửi mã.");
            return "register";
        }

        // Sinh mã ngẫu nhiên 6 chữ số
        String code = String.valueOf(100000 + new Random().nextInt(900000));

        // Lưu mã và thông tin người dùng tạm thời vào session
        session.setAttribute("verifyCode", code);
        session.setAttribute("pendingUser", khachHang);

        // Gửi mã qua email
        emailService.sendSimpleMessage(email, "Mã xác nhận đăng ký", "Mã xác nhận của bạn là: " + code);

        model.addAttribute("message", "Mã xác nhận đã được gửi đến email.");
        model.addAttribute("khachHang", khachHang);
        return "register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@RequestParam("code") String code,
                           HttpSession session,
                           Model model) {

        String sessionCode = (String) session.getAttribute("verifyCode");
        KhachHang pendingUser = (KhachHang) session.getAttribute("pendingUser");

        if (sessionCode == null || pendingUser == null) {
            model.addAttribute("error", "Bạn chưa gửi mã xác nhận.");
            model.addAttribute("khachHang", new KhachHang());
            return "register";
        }

        if (!sessionCode.equals(code)) {
            model.addAttribute("error", "Mã xác nhận không đúng.");
            model.addAttribute("khachHang", pendingUser);
            return "register";
        }

        // Gán mã KH và lưu vào DB
        pendingUser.setMaKH("KH" + System.currentTimeMillis());
        pendingUser.setTrangThai(true);
        khachHangRepository.save(pendingUser);

        // Xoá session
        session.removeAttribute("verifyCode");
        session.removeAttribute("pendingUser");

        model.addAttribute("message", "Đăng ký thành công!");
        model.addAttribute("khachHang", new KhachHang());
        return "register";
    }
}
