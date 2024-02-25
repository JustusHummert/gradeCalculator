package com.gradeCalculator.server.controller;

import static org.hamcrest.Matchers.*;

import com.gradeCalculator.server.Entities.ModuleEntity;
import com.gradeCalculator.server.Entities.SubjectEntity;
import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.ModuleRepository;
import com.gradeCalculator.server.repositories.SubjectRepository;
import com.gradeCalculator.server.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    @Autowired
    private ModuleRepository moduleRepository;

    private final String loginHtmlString;

    public WebControllerTest() throws Exception{
        loginHtmlString = Files.readString(new ClassPathResource("templates/login.html").getFile().toPath());
    }

    @Test
    void login() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
    }

    @Test
    void mainMenu() throws Exception {
        UserEntity user = new UserEntity("user","password", "salt");
        userRepository.save(user);
        String sessionId = SessionManager.getInstance().addSession(user.getUsername());
        //valid request
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
        //invalid sessionId
        mvc.perform(MockMvcRequestBuilders.get("/main")
                        .param("sessionId", "fake SessionId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        //sessionId not connected to user
        mvc.perform(MockMvcRequestBuilders.get("/main")
                        .param("sessionId", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
    }

    @Test
    void subjectMenu() throws Exception{
        UserEntity user = new UserEntity("user","password", "salt");
        SubjectEntity subject = new SubjectEntity("subject");
        ModuleEntity module = new ModuleEntity("module", 0.5);
        module.setGrade(1.3);
        subject.getModules().add(module);
        user.getSubjects().add(subject);
        userRepository.save(user);
        subjectRepository.save(subject);
        moduleRepository.save(module);
        String sessionId = SessionManager.getInstance().addSession(user.getUsername());
        //valid request
        mvc.perform(MockMvcRequestBuilders.get("/main/subject")
                        .param("sessionId", sessionId)
                        .param("subjectId", subject.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andExpect(model().attribute("sessionId", sessionId))
                .andExpect(model().attribute("subject", subject))
                .andExpect(model().attribute("averageGrade", 1.3))
                .andExpect(model().attribute("bestPossibleGrade", 1.15))
                .andExpect(model().attribute("worstPossibleGrade", 2.65));
        //invalid sessionId
        mvc.perform(MockMvcRequestBuilders.get("/main/subject")
                        .param("sessionId", "fake sessionId")
                        .param("subjectId", subject.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        //subject does not exist
        mvc.perform(MockMvcRequestBuilders.get("/main/subject")
                        .param("sessionId", sessionId)
                        .param("subjectId", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        UserEntity user2 = new UserEntity("user2", "password", "salt");
        userRepository.save(user2);
        //user not allowed to access subject
        mvc.perform(MockMvcRequestBuilders.get("/main/subject")
                        .param("sessionId", SessionManager.getInstance().addSession(user2.getUsername()))
                        .param("subjectId", subject.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        userRepository.delete(user2);
        userRepository.delete(user);
        //sessionId not connected to valid user
        mvc.perform(MockMvcRequestBuilders.get("/main/subject")
                        .param("sessionId", sessionId)
                        .param("subjectId", subject.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        subjectRepository.delete(subject);
        moduleRepository.delete(module);
    }
}