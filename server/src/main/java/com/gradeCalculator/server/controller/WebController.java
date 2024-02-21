package com.gradeCalculator.server.controller;
import com.gradeCalculator.server.Entities.ModuleEntity;
import com.gradeCalculator.server.Entities.SubjectEntity;
import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class WebController {
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;

    //directs user to the login.html template
    @GetMapping("")
    public String login(){
        return "login";
    }

    //directs user to the main.html template
    @GetMapping("/main")
    public String mainMenu(@RequestParam String sessionId, Model model){
        //if the sessionId is invalid or the username doesn´t exist go to login page
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "login";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "login";
        UserEntity user = optionalUser.get();
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("subjects", user.getSubjects());
        return "main";
    }

    //Directs user to the subject.html template
    @GetMapping("/main/subject")
    public String subjectMenu(@RequestParam String sessionId, Integer subjectId, Model model){
        //if the sessionId is invalid or the username doesn´t exist go to login page
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "login";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "login";
        UserEntity user = optionalUser.get();
        //if the subject doesn´t exist go to login page
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if(optionalSubject.isEmpty())
            return "login";
        SubjectEntity subject = optionalSubject.get();
        //check if user is allowed to access subject
        if(!user.getSubjects().contains(subject))
            return "login";
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("subject", subject);
        model.addAttribute("averageGrade", averageGrade(subject));
        model.addAttribute("bestPossibleGrade", bestPossibleGrade(subject));
        model.addAttribute("worstPossibleGrade", worstPossibleGrade(subject));
        return "subject";
    }

    //grades added together multiplied by grading factor
    private double totalGrade(SubjectEntity subject){
        double grade=0;
        for(ModuleEntity module : subject.getModules()){
            if(module.getGrade()>0 && module.getGrade()<5)
                grade+= module.getGrade()* module.getGradingFactor();
        }
        return grade;
    }

    //grading factor added together
    private double totalGradingFactor(SubjectEntity subject){
        double total=0;
        for(ModuleEntity module : subject.getModules()){
            if(module.getGrade()>0 && module.getGrade()<5)
                total+= module.getGradingFactor();
        }
        return total;
    }

    //Average grade weighted by grading Factor
    private double averageGrade(SubjectEntity subject){
        return totalGrade(subject)/totalGradingFactor(subject);
    }

    //Grade if the rest of the modules are 1.0
    private double bestPossibleGrade(SubjectEntity subject){
        return totalGrade(subject)+(1-totalGradingFactor(subject));
    }

    //Grade if the rest of the modules are 4.0
    private double worstPossibleGrade(SubjectEntity subject){
        return totalGrade(subject)+(1-totalGradingFactor(subject))*4;
    }



}
