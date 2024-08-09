package com.gradeCalculator.controller;
import com.gradeCalculator.entities.SubjectEntity;
import com.gradeCalculator.entities.UserEntity;
import com.gradeCalculator.services.SubjectService;
import com.gradeCalculator.services.UserService;
import com.gradeCalculator.services.exceptions.Forbidden;
import com.gradeCalculator.services.exceptions.LoginFailed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @Autowired
    UserService userService;
    @Autowired
    SubjectService subjectService;

    /**
     * Directs user to the main.html template or the login.html template
     * If the user is logged in, the main.html template is shown
     * If the user is not logged in, the login.html template is shown
     * @param model The model to add attributes to
     * @param request The request to get the user from
     * @return The template to direct the user to
     */
    @GetMapping("")
    public String mainMenu(Model model, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            model.addAttribute("subjects", user.getSubjects());
            return "main";
        } catch (LoginFailed e) {
            return "login";
        }
    }

    /**
     * Directs user to the subject.html template
     * If the user is logged in, the subject.html template is shown
     * If the user is not logged in, the login.html template is shown
     * If the user does not have access to the subject, the login.html template is shown
     * @param subjectId The id of the subject
     * @param model The model to add attributes to
     * @param request The request to get the user from
     * @return The template to direct the user to
     */
    @GetMapping("/subject")
    public String subjectMenu(int subjectId, Model model, HttpServletRequest request){
        try {
            UserEntity user = userService.getActiveUser(request.getSession());
            SubjectEntity subject = subjectService.getSubject(subjectId, user);
            model.addAttribute("subject", subject);
            model.addAttribute("averageGrade", subjectService.averageGrade(subject));
            model.addAttribute("bestPossibleGrade", subjectService.bestPossibleGrade(subject));
            model.addAttribute("worstPossibleGrade", subjectService.worstPossibleGrade(subject));
            return "subject";
        } catch (LoginFailed e){
            return "login";
        } catch (Forbidden e) {
            return "redirect:/";
        }
    }

}
