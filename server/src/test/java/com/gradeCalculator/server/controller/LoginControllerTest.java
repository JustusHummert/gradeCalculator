package com.gradeCalculator.server.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.equalTo;

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
                .andExpect(content().string(equalTo("")));
        //register the account
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")));
        //try to register again this time it already exists
        mvc.perform(MockMvcRequestBuilders.post("/register").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("false")));
        //login successfully
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
        userRepository.deleteById(username);
        mvc.perform(MockMvcRequestBuilders.post("/login").param("username", username).param("password", "password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }

}