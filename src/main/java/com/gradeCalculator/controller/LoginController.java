package com.gradeCalculator.controller;

import com.gradeCalculator.services.UserService;
import com.gradeCalculator.services.exceptions.LoginFailed;
import com.gradeCalculator.services.exceptions.UsernameTaken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(path = "")
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * Try to log in the user
     * @param username The username of the user
     * @param password The password of the user
     * @param request The request to log in
     * @return The result of the login
     */
    @PostMapping(path="/login")
    public String login(String username, String password, HttpServletRequest request){
        try {
            request.getSession().setAttribute("username", userService.getUser(username, password).getUsername());
            //set an expiration time for the session 1 hour
            request.getSession().setMaxInactiveInterval(60*60);
            return "redirect:/";
        } catch (LoginFailed e) {
            return "redirect:/?wrongUsernameOrPassword";
        }
    }

    /**
     * Register a new user
     * @param username The username of the new user
     * @param password The password of the new user
     * @param request The request to register
     * @return The result of the registration
     */
    @PostMapping(path="/register")
    public String register(String username, String password, HttpServletRequest request) {
        try {
            request.getSession().setAttribute("username", userService.createUser(username, password));
            //set an expiration time for the session 1 hour
            request.getSession().setMaxInactiveInterval(60*60);
            return "redirect:/";
        } catch (UsernameTaken e) {
            return "redirect:/?usernameTaken";
        }
    }

    /**
     * Log out the user
     * @param request The request to log out
     * @return The result of the logout
     */
    @PostMapping(path="/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect/";
    }
}
