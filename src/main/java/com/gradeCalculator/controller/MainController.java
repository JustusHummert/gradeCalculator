package com.gradeCalculator.controller;

import com.gradeCalculator.entities.ModuleEntity;
import com.gradeCalculator.entities.SubjectEntity;
import com.gradeCalculator.entities.UserEntity;
import com.gradeCalculator.services.ModuleService;
import com.gradeCalculator.services.SubjectService;
import com.gradeCalculator.services.UserService;
import com.gradeCalculator.services.exceptions.Forbidden;
import com.gradeCalculator.services.exceptions.LoginFailed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping(path="/main")
public class MainController {
    @Autowired
    UserService userService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    ModuleService moduleService;


    /**
     * Add a new module
     * @param name The name of the module
     * @param gradingFactor The grading factor of the module
     * @param subjectId The id of the subject the module should belong to
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path="/addModule")
    public @ResponseBody String addNewModule(String name, double gradingFactor, double grade, int subjectId,
                                             HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            SubjectEntity subject = subjectService.getSubject(subjectId, user);
            moduleService.createModule(name, gradingFactor, grade, subject);
            return "success";
        }
        catch (LoginFailed | Forbidden e){
            return "failed";
        }
    }

    /**
     * Add a new grade to a module
     * @param moduleId The id of the module
     * @param grade The grade to add
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path="/addGrade")
    public String addNewGrade(int moduleId, double grade, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            ModuleEntity module = moduleService.getModule(moduleId, user);
            moduleService.setGrade(module, grade);
            return "redirect:/subject?subjectId=" + module.getSubject().getId();
        }
        catch (LoginFailed | Forbidden e) {
            return "redirect:/";
        }
    }

    /**
     * Add a new subject
     * @param name The name of the subject
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path="/addSubject")
    public String addNewSubject(String name, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            subjectService.createSubject(name, user);
        }
        catch (LoginFailed e){
            return "redirect:/";
        }
        return "redirect:/";
    }

    /**
     * Delete a subject
     * @param subjectId The id of the subject
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path = "/deleteSubject")
    public String deleteSubject(int subjectId, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            SubjectEntity subject = subjectService.getSubject(subjectId, user);
            subjectService.deleteSubject(subject);
        } catch (Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (LoginFailed e) {
            return "redirect:/";
        }
        return "redirect:/";
    }

    /**
     * Delete a module
     * @param moduleId The id of the module
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path ="/deleteModule")
    public String deleteModule(int moduleId, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            ModuleEntity module = moduleService.getModule(moduleId, user);
            moduleService.delete(module);
            return "redirect:/subject?subjectId=" + module.getSubject().getId();
        } catch (LoginFailed | Forbidden e) {
            return "redirect:/";
        }
    }
}
