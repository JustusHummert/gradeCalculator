package com.gradeCalculator.server.controller;
import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class WebController {
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private ModuleInSubjectRepository moduleInSubjectRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String login(){
        return "login";
    }

    @GetMapping("/main")
    public String mainMenu(@RequestParam String sessionId, Model model){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "login";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "login";
        UserEntity user = optionalUser.get();
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("enrolled", user.getSubjects());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "main";
    }
}
