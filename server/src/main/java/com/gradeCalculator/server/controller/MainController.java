package com.gradeCalculator.server.controller;

import com.gradeCalculator.server.Entities.*;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    private GradeRepository gradeRepository;
    @Autowired
    private ModuleInSubjectRepository moduleInSubjectRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/addModule")
    public @ResponseBody String addNewModule(@RequestParam String name, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        if (name==null)
            return "no name given";
        ModuleEntity m = new ModuleEntity(name);
        moduleRepository.save(m);
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
        GradeEntity g = new GradeEntity(user, module, grade);
        gradeRepository.save(g);
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
        if (name==null)
            return "no name given";
        SubjectEntity s = new SubjectEntity(name);
        subjectRepository.save(s);
        return "saved";
    }

    @PostMapping(path="/enroll")
    public @ResponseBody String enroll(@RequestParam int subjectId, @RequestParam String sessionId){
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
        if(user.getSubjects().contains(subject))
            return "already enrolled";
        user.getSubjects().add(subject);
        userRepository.save(user);
        return "saved";
    }

    @PostMapping(path="/unenroll")
    public @ResponseBody String unenroll(@RequestParam int subjectId, @RequestParam String sessionId){
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if(optionalSubject.isEmpty())
            return "subjectId invalid";
        SubjectEntity subject = optionalSubject.get();
        UserEntity user = optionalUser.get();
        if(!user.getSubjects().contains(subject))
            return "user not enrolled";
        while(user.getSubjects().contains(subject))
            user.getSubjects().remove(subject);
        userRepository.save(user);
        return "saved";
    }

    @PostMapping(path = "/addModuleInSubject")
    public @ResponseBody String addNewModuleInSubject(@RequestParam int subjectId, @RequestParam int moduleId, @RequestParam double gradingFactor, @RequestParam String sessionId){
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        String username = SessionManager.getInstance().getSession(sessionId);
        if(username == null)
            return "sessionId invalid";
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return "username invalid";
        if (optionalSubject.isEmpty())
            return "subjectId invalid";
        if(optionalModule.isEmpty())
            return "moduleId invalid";
        ModuleInSubjectEntity moduleInSubjectEntity = new ModuleInSubjectEntity(optionalSubject.get(), optionalModule.get(), gradingFactor);
        moduleInSubjectRepository.save(moduleInSubjectEntity);
        return "saved";
    }
}
