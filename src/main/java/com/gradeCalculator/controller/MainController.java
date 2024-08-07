package com.gradeCalculator.controller;

import com.gradeCalculator.Entities.ModuleEntity;
import com.gradeCalculator.Entities.SubjectEntity;
import com.gradeCalculator.Entities.UserEntity;
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
    public @ResponseBody String addNewModule(String name, double gradingFactor, int subjectId,
                                             HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            SubjectEntity subject = subjectService.getSubject(subjectId, user);
            moduleService.createModule(name, gradingFactor, subject);
        }
        catch (LoginFailed e){
            return "session invalid";
        } catch (Forbidden e) {
            return "Forbidden";
        }
        return "saved";
    }

    /**
     * Add a new grade to a module
     * @param moduleId The id of the module
     * @param grade The grade to add
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path="/addGrade")
    public @ResponseBody String addNewGrade(int moduleId, double grade, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            ModuleEntity module = moduleService.getModule(moduleId, user);
            moduleService.setGrade(module, grade);
        }
        catch (LoginFailed e) {
            return "session invalid";
        }
        catch (Forbidden e){
            return "Forbidden";
        }
        return "saved";
    }

    /**
     * Add a new subject
     * @param name The name of the subject
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path="/addSubject")
    public @ResponseBody String addNewSubject(String name, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            subjectService.createSubject(name, user);
        }
        catch (LoginFailed e){
            return "session invalid";
        }
        return "saved";
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
     * @param subjectId The id of the subject the module belongs to
     * @param request The request to get the user from
     * @return the result of the operation
     */
    @PostMapping(path ="/deleteModule")
    public @ResponseBody String deleteModule(int moduleId, int subjectId, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            SubjectEntity subject = subjectService.getSubject(subjectId, user);
            ModuleEntity module = moduleService.getModule(moduleId, user);
            moduleService.delete(module);
        } catch (LoginFailed e) {
            return "session invalid";
        } catch (Forbidden e) {
            return "Forbidden";
        }
        return "saved";
    }
}
