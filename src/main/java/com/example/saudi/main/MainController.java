package com.example.saudi.main;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


@Controller
public class MainController {


    @GetMapping("/main")
    public String loginPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String userEmail = (String) session.getAttribute("userEmail");

        //세션정보 확인
        model.addAttribute("userId", userId);
        model.addAttribute("userEmail", userEmail);

        return "main";
    }
}
