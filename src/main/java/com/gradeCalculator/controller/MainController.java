package com.gradeCalculator.controller;

import com.gradeCalculator.Entities.ModuleEntity;
import com.gradeCalculator.Entities.SubjectEntity;
import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.repositories.ModuleRepository;
import com.gradeCalculator.repositories.SubjectRepository;
import com.gradeCalculator.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    //sets the user
    @ModelAttribute("user")
    public UserEntity getUser(HttpServletRequest request){
        Object usernameObject = request.getSession().getAttribute("username");
        if(usernameObject == null)
            return null;
        String username =  usernameObject.toString();
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if(optionalUser.isEmpty())
            return null;
        return optionalUser.get();
    }

    @PostMapping(path="/addModule")
    public @ResponseBody String addNewModule(@ModelAttribute("user") UserEntity user,@RequestParam String name, @RequestParam double gradingFactor, @RequestParam int subjectId){
        if(user == null)
            return "session invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if(optionalSubject.isEmpty())
            return "subjectId invalid";
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
    public @ResponseBody String addNewGrade(@ModelAttribute("user") UserEntity user, @RequestParam int moduleId, @RequestParam double grade){
        if(user == null)
            return "session invalid";
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        if (optionalModule.isEmpty())
            return "moduleId invalid";
        ModuleEntity module = optionalModule.get();
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
    public @ResponseBody String addNewSubject(@ModelAttribute("user") UserEntity user, @RequestParam String name){
        if(user == null)
            return "session invalid";
        SubjectEntity subject = new SubjectEntity(name);
        subjectRepository.save(subject);
        user.getSubjects().add(subject);
        userRepository.save(user);
        return "saved";
    }

    @PostMapping(path = "/deleteSubject")
    public @ResponseBody String deleteSubject(@ModelAttribute("user") UserEntity user, @RequestParam int subjectId){
        if(user == null)
            return "session invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if (optionalSubject.isEmpty())
            return "subjectId invalid";
        SubjectEntity subject = optionalSubject.get();
        if(!user.getSubjects().contains(subject))
            return "subject does not belong to user";
        user.getSubjects().remove(subject);
        userRepository.save(user);
        subjectRepository.delete(subject);
        return "saved";
    }

    @PostMapping(path ="/deleteModule")
    public @ResponseBody String deleteModule(@ModelAttribute("user") UserEntity user, @RequestParam int moduleId, @RequestParam int subjectId){
        if(user == null)
            return "session invalid";
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        if(optionalModule.isEmpty())
            return "moduleId invalid";
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        if(optionalSubject.isEmpty())
            return "subjectId invalid";
        SubjectEntity subject = optionalSubject.get();
        ModuleEntity module = optionalModule.get();
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
