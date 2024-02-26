package com.gradeCalculator.server.controller;

import com.gradeCalculator.server.Entities.*;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping(path="/main")
public class MainController {
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/addModule")
    public @ResponseBody String addNewModule(@RequestParam String name, @RequestParam double gradingFactor, @RequestParam int subjectId, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if(optionalSubject.isEmpty())
            return "subjectId invalid";
        UserEntity user = optionalUser.get();
        SubjectEntity subject = optionalSubject.get();
        if(!user.getSubjects().contains(subject))
            return "subject does not belong to user";
        ModuleEntity module = new ModuleEntity(name, gradingFactor);
        moduleRepository.save(module);
        subject.getModules().add(module);
        subjectRepository.save(subject);
        return "saved";
    }

    @PostMapping(path="/addGrade")
    public @ResponseBody String addNewGrade(@RequestParam int moduleId, @RequestParam double grade, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        if (optionalModule.isEmpty())
            return "moduleId invalid";
        ModuleEntity module = optionalModule.get();
        UserEntity user = optionalUser.get();
        //check if Module belongs to user
        boolean belongs=false;
        for(SubjectEntity subject : user.getSubjects()){
            if(subject.getModules().contains(module)){
                belongs=true;
                break;
            }
        }
        if(!belongs)
            return "module does not belong to user";
        module.setGrade(grade);
        moduleRepository.save(module);
        return "saved";
    }

    @PostMapping(path="/addSubject")
    public @ResponseBody String addNewSubject(@RequestParam String name, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        UserEntity user = optionalUser.get();
        SubjectEntity subject = new SubjectEntity(name);
        subjectRepository.save(subject);
        user.getSubjects().add(subject);
        userRepository.save(user);
        return "saved";
    }

    @PostMapping(path = "/deleteSubject")
    public @ResponseBody String deleteSubject(@RequestParam int subjectId, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if (optionalSubject.isEmpty())
            return "subjectId invalid";
        UserEntity user = optionalUser.get();
        SubjectEntity subject = optionalSubject.get();
        if(!user.getSubjects().contains(subject))
            return "subject does not belong to user";
        user.getSubjects().remove(subject);
        userRepository.save(user);
        subjectRepository.delete(subject);
        return "saved";
    }

    @PostMapping(path ="/deleteModule")
    public @ResponseBody String deleteModule(@RequestParam int moduleId, @RequestParam int subjectId, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        if(optionalModule.isEmpty())
            return "moduleId invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if(optionalSubject.isEmpty())
            return "subjectId invalid";
        SubjectEntity subject = optionalSubject.get();
        ModuleEntity module = optionalModule.get();
        UserEntity user = optionalUser.get();
        if(!subject.getModules().contains(module))
            return "module does not belong to subject";
        if(!user.getSubjects().contains(subject))
            return "subject does not belong to user";
        subject.getModules().remove(module);
        moduleRepository.delete(module);
        subjectRepository.save(subject);
        return "saved";
    }
}
