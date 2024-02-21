package com.gradeCalculator.server.controller;

import static org.hamcrest.Matchers.*;

import com.gradeCalculator.server.Entities.SubjectEntity;
import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.SubjectRepository;
import com.gradeCalculator.server.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Test
    void login() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    void mainMenu() throws Exception {
        UserEntity user = new UserEntity("user","password", "salt");
        userRepository.save(user);
        String sessionId = SessionManager.getInstance().addSession(user.getUsername());
        mvc.perform(MockMvcRequestBuilders.get("/main")
                        .param("sessionId", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andExpect(model().attributeExists("sessionId"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("sessionId", sessionId))
                .andExpect(model().attribute("subjects", user.getSubjects()));
        userRepository.delete(user);
    }

    @Test
    void subjectMenu() throws Exception{
        UserEntity user = new UserEntity("user","password", "salt");
        SubjectEntity subject = new SubjectEntity("subject");
        subjectRepository.save(subject);
        user.getSubjects().add(subject);
        userRepository.save(user);
        String sessionId = SessionManager.getInstance().addSession(user.getUsername());
        mvc.perform(MockMvcRequestBuilders.get("/main/subject")
                        .param("sessionId", sessionId)
                        .param("subjectId", subject.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andExpect(model().attribute("sessionId", sessionId))
                .andExpect(model().attribute("subject", subject))
                .andExpect(model().attribute("averageGrade", Double.NaN))
                .andExpect(model().attribute("bestPossibleGrade", 1.0))
                .andExpect(model().attribute("worstPossibleGrade", 4.0));
        user.getSubjects().remove(subject);
        userRepository.delete(user);
        subjectRepository.delete(subject);
    }
}