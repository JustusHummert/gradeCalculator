package com.gradeCalculator.Server.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public String enterSite() {
        return "Login Controller";
    }
}
