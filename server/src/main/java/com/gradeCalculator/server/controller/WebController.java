package com.gradeCalculator.server.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    @GetMapping("")
    public String login(){
        return "login";
    }

    @GetMapping("/main")
    public String mainMenu(@RequestParam String SessionId, Model model){
        model.addAttribute("SessionId", SessionId);
        return "main";
    }
}
