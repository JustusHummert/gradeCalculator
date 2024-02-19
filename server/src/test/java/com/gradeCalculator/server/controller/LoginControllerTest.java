package com.gradeCalculator.server.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gradeCalculator.server.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

}