package com.gradeCalculator.controller;

import static org.hamcrest.Matchers.*;

import com.gradeCalculator.entities.ModuleEntity;
import com.gradeCalculator.entities.SubjectEntity;
import com.gradeCalculator.entities.UserEntity;
import com.gradeCalculator.repositories.ModuleRepository;
import com.gradeCalculator.repositories.SubjectRepository;
import com.gradeCalculator.repositories.UserRepository;
import com.gradeCalculator.services.ModuleService;
import com.gradeCalculator.services.SubjectService;
import com.gradeCalculator.services.UserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
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
    @Autowired
    private UserService userService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ModuleService moduleService;

    private final String loginHtmlString;

    @BeforeEach
    @AfterEach
    void cleanUp(){
        moduleRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    public WebControllerTest() throws Exception{
        loginHtmlString = Files.readString(new ClassPathResource("templates/login.html").getFile().toPath());
    }

    @Test
    void login() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    void mainMenu() throws Exception {
        UserEntity user = userService.createUser("user", "password");
        SubjectEntity subject = subjectService.createSubject("subject", user);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        user = userRepository.findById(user.getUsername()).get();
        //valid request
        mvc.perform(MockMvcRequestBuilders.get("")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andExpect(content().string(not(equalTo(loginHtmlString))))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("subjects", user.getSubjects()));
        subjectService.deleteSubject(subject);
        userRepository.deleteById(user.getUsername());
        //invalid sessionId
        mvc.perform(MockMvcRequestBuilders.get("")
                        .session(new MockHttpSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
        //sessionId not connected to user
        mvc.perform(MockMvcRequestBuilders.get("")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    void subjectMenu() throws Exception{
        UserEntity user = userService.createUser("user", "password");
        SubjectEntity subject = subjectService.createSubject("subject", user);
        ModuleEntity module = moduleService.createModule("module", 0.5, 1.3, subject);
        module = moduleService.setGrade(module, 1.3);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        //valid request
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", subject.getId().toString())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andExpect(model().attribute("subject", subject))
                .andExpect(model().attribute("averageGrade", 1.3))
                .andExpect(model().attribute("bestPossibleGrade", 1.15))
                .andExpect(model().attribute("worstPossibleGrade", 2.65));
        //invalid sessionId
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", subject.getId().toString())
                        .session(new MockHttpSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
        //subject does not exist
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", "-1")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        UserEntity user2 = new UserEntity("user2", "password");
        userRepository.save(user2);
        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute("username", user2.getUsername());
        //user not allowed to access subject
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", subject.getId().toString())
                        .session(session2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //sessionId not connected to valid user
        MockHttpSession sessionInvalid = new MockHttpSession();
        sessionInvalid.setAttribute("username", "invalid");
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", subject.getId().toString())
                        .session(sessionInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
}}