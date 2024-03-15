package com.gradeCalculator.controller;

import static org.hamcrest.Matchers.*;

import com.gradeCalculator.Entities.ModuleEntity;
import com.gradeCalculator.Entities.SubjectEntity;
import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.repositories.ModuleRepository;
import com.gradeCalculator.repositories.SubjectRepository;
import com.gradeCalculator.repositories.UserRepository;
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
        UserEntity user = new UserEntity("user","password");
        userRepository.save(user);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        //valid request
        mvc.perform(MockMvcRequestBuilders.get("")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("subjects", user.getSubjects()));
        userRepository.delete(user);
        //invalid sessionId
        mvc.perform(MockMvcRequestBuilders.get("")
                        .session(new MockHttpSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        //sessionId not connected to user
        mvc.perform(MockMvcRequestBuilders.get("")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
    }

    @Test
    void subjectMenu() throws Exception{
        UserEntity user = new UserEntity("user","password");
        SubjectEntity subject = new SubjectEntity("subject");
        ModuleEntity module = new ModuleEntity("module", 0.5);
        module.setGrade(1.3);
        subject.getModules().add(module);
        user.getSubjects().add(subject);
        userRepository.save(user);
        moduleRepository.save(module);
        subjectRepository.save(subject);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        System.out.println("test:" +user.getSubjects());
        System.out.println("test2:" +userRepository.findById(user.getUsername()).get().getSubjects());
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
                .andExpect(content().string(equalTo(loginHtmlString)));
        //subject does not exist
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", "-1")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        UserEntity user2 = new UserEntity("user2", "password");
        userRepository.save(user2);
        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute("username", user2.getUsername());
        //user not allowed to access subject
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", subject.getId().toString())
                        .session(session2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        userRepository.delete(user2);
        userRepository.delete(user);
        //sessionId not connected to valid user
        mvc.perform(MockMvcRequestBuilders.get("/subject")
                        .param("subjectId", subject.getId().toString())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(loginHtmlString)));
        subjectRepository.delete(subject);
        moduleRepository.delete(module);
    }
}