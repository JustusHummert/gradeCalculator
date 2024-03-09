package com.gradeCalculator.server.controller;

import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.bcrypt.BCrypt;


import java.util.Optional;

@Controller
@RequestMapping(path = "")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    //check if username and password combination exists, return SessionId
    @PostMapping(path="/login")
    public @ResponseBody String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request){
        Optional<UserEntity> optional = userRepository.findById(username);
        if(optional.isEmpty())
            return "failed";
        UserEntity user = optional.get();
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());
        if(!hashedPassword.equals(user.getPassword()))
            return "failed";
        request.getSession().setAttribute("username", username);
        return "logged in";
    }

    //create user, hash password
    @PostMapping(path="/register")
    public @ResponseBody String register(@RequestParam String username, @RequestParam String password) {
        if (userRepository.findById(username).isPresent())
            return "username already exists";
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        UserEntity user = new UserEntity(username, hashedPassword, salt);
        userRepository.save(user);
        return "saved";
    }
}
