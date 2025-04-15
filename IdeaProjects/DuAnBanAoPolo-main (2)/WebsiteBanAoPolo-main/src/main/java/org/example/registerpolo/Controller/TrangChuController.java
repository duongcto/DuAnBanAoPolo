package org.example.registerpolo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trang-chu")
public class TrangChuController {
    @GetMapping
    public String trangChu(Model model){
        return "/trangchu.html";
    }
}
