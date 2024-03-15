package com.gradeCalculator.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gradeCalculator.repositories.UserRepository;
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

    @Test
    void loginAndRegister() throws Exception {
        //Variable so I can change it when its taken
        String username = "huWI9g20gmimvvemiaävrwrgw";
        //login account doesn´t exist
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("failed")));
        //register the account
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //try to register again this time it already exists
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("username already exists")));
        //login successfully
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not("failed")));
        //wrong password
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", username).param("password", "wrongpassword")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("failed")));
        userRepository.deleteById(username);
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("failed")));
    }

    @Test
    void logout() throws Exception{
        String username = "huWI9g20gmimvvemiaävrwrgw";
        MockHttpSession session = new MockHttpSession();
        //register
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //Should be at login page
        mvc.perform(MockMvcRequestBuilders.get("")
                .session(session))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Login")));
        //login
        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .param("username", username)
                        .param("password", "password")
                        .accept(MediaType.APPLICATION_JSON).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("logged in")));
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
        userRepository.deleteById(username);
    }



}