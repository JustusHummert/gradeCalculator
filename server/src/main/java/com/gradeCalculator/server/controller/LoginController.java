package com.gradeCalculator.server.controller;

import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;


import java.util.Optional;

@Controller
@RequestMapping(path = "")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/login")
    public @ResponseBody boolean login(@RequestParam String username, @RequestParam String password){
        Optional<UserEntity> optional = userRepository.findById(username);
        UserEntity user;
        if(optional.isPresent())
            user = optional.get();
        else
            return false;
        return (user.getPassword().equals(password));
    }

    //Todo add password hashing
    @PostMapping(path="/register")
    public @ResponseBody boolean register(@RequestParam String username, @RequestParam String password) {
        if (userRepository.findById(username).isPresent())
            return false;
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        return true;
    }
}
