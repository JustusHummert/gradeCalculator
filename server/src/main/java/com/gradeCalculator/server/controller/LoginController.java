package com.gradeCalculator.server.controller;

import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
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

    //check if username and password combination exists, return SessionId
    @PostMapping(path="/login")
    public @ResponseBody String login(@RequestParam String username, @RequestParam String password){
        Optional<UserEntity> optional = userRepository.findById(username);
        if(optional.isEmpty())
            return null;
        UserEntity user = optional.get();
        if(!password.equals(user.getPassword()))
            return null;
        return SessionManager.getInstance().addSession(user);
    }

    //Todo add password hashing
    @PostMapping(path="/register")
    public @ResponseBody boolean register(@RequestParam String username, @RequestParam String password) {
        if (userRepository.findById(username).isPresent())
            return false;
        UserEntity user = new UserEntity(username, password);
        userRepository.save(user);
        return true;
    }
}