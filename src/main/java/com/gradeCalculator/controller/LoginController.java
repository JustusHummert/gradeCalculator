package com.gradeCalculator.controller;

import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.repositories.UserRepository;
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
        if(!BCrypt.checkpw(password, user.getPassword()))
            return "failed";
        request.getSession().setAttribute("username", username);
        //set an expiration time for the session 1 hour
        request.getSession().setMaxInactiveInterval(60*60);
        return "logged in";
    }

    //create user, hash password
    @PostMapping(path="/register")
    public @ResponseBody String register(@RequestParam String username, @RequestParam String password) {
        if (userRepository.findById(username).isPresent())
            return "username already exists";
        String hashedPassword = BCrypt.hashpw(password,  BCrypt.gensalt());
        UserEntity user = new UserEntity(username, hashedPassword);
        userRepository.save(user);
        return "saved";
    }
}
