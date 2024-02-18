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

    @PostMapping(path="/addModule")
    public @ResponseBody boolean addNewModule(@RequestParam String name, @RequestParam String sessionId){
        UserEntity user = SessionManager.getInstance().getSession(sessionId);
        if (user==null || name==null)
            return false;
        ModuleEntity m = new ModuleEntity(name);
        moduleRepository.save(m);
        return true;
    }

    @GetMapping(path="/allModules")
    public @ResponseBody Iterable<ModuleEntity> getAllModules(){
        return moduleRepository.findAll();
    }

    @PostMapping(path="/addGrade")
    public @ResponseBody boolean addNewGrade(@RequestParam int moduleId, @RequestParam double grade, @RequestParam String sessionId){
        UserEntity user = SessionManager.getInstance().getSession(sessionId);
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        if (user==null || optionalModule.isEmpty())
            return false;
        ModuleEntity module = optionalModule.get();
        GradeEntity g = new GradeEntity(user, module, grade);
        gradeRepository.save(g);
        return true;
    }

    @PostMapping(path="/yourGrades")
    public @ResponseBody Iterable<GradeEntity> getYourGrades(@RequestParam String sessionId){
        UserEntity user = SessionManager.getInstance().getSession(sessionId);
        if(user==null)
            return null;
        return gradeRepository.findByUser(user);
    }

    @PostMapping(path="/addSubject")
    public @ResponseBody boolean addNewSubject(@RequestParam String name, @RequestParam String sessionId){
        UserEntity user = SessionManager.getInstance().getSession(sessionId);
        if (user==null || name==null)
            return false;
        SubjectEntity s = new SubjectEntity(name);
        subjectRepository.save(s);
        return true;
    }

    @GetMapping(path="/allSubjects")
    public @ResponseBody Iterable<SubjectEntity> getAllSubjects(){
        return subjectRepository.findAll();
    }

    @PostMapping(path = "/addModuleInSubject")
    public @ResponseBody boolean addNewModuleInSubject(@RequestParam int subjectId, @RequestParam int moduleId, @RequestParam double gradingFactor, @RequestParam String sessionId){
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        Optional<ModuleEntity> optionalModule = moduleRepository.findById(moduleId);
        UserEntity user = SessionManager.getInstance().getSession(sessionId);
        if (user==null || optionalSubject.isEmpty() || optionalModule.isEmpty())
            return false;
        ModuleInSubjectEntity moduleInSubjectEntity = new ModuleInSubjectEntity(optionalSubject.get(), optionalModule.get(), gradingFactor);
        moduleInSubjectRepository.save(moduleInSubjectEntity);
        return true;
    }

    @PostMapping(path = "/modulesInSubject")
    public @ResponseBody Iterable<ModuleInSubjectEntity> getModulesInSubject(@RequestParam int subjectId){
        Optional<SubjectEntity> optionalSubject = subjectRepository.findById(subjectId);
        return optionalSubject.map(subject -> moduleInSubjectRepository.findBySubject(subject)).orElse(null);
    }


}
