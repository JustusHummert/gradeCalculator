package com.gradeCalculator.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gradeCalculator.repositories.ModuleRepository;
import com.gradeCalculator.repositories.SubjectRepository;
import com.gradeCalculator.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @BeforeEach
    @AfterEach
    void cleanUp(){
        moduleRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void loginAndRegister() throws Exception {
        //login account does not exist
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", "username").param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?wrongUsernameOrPassword"));
        //register the account
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", "username").param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        //try to register again this time it already exists
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", "username").param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?usernameTaken"));
        //login successfully
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", "username").param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        //wrong password
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", "username").param("password", "wrongpassword")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?wrongUsernameOrPassword"));
        userRepository.deleteById("username");
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", "username").param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?wrongUsernameOrPassword"));
    }

    @Test
    void logout() throws Exception{
        MockHttpSession session = new MockHttpSession();
        //register
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", "username").param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        //Should be at login page
        mvc.perform(MockMvcRequestBuilders.get("")
                .session(session))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Login")));
        //login
        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .param("username", "username")
                        .param("password", "password")
                        .accept(MediaType.APPLICATION_JSON).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        //Should be at Main page
        mvc.perform(MockMvcRequestBuilders.get("")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Subjects")));
        //logout
        mvc.perform(MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("logged out")));
        //Should be at login page
        mvc.perform(MockMvcRequestBuilders.get("")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
    }
}