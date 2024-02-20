package com.gradeCalculator.server.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gradeCalculator.server.Entities.ModuleEntity;
import com.gradeCalculator.server.Entities.SubjectEntity;
import com.gradeCalculator.server.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mvc;

    String sessionId;
    ModuleEntity setUpModule;
    SubjectEntity setUpSubject;
    String tearDownModuleName = "testModule 42";
    String tearDownSubjectName = "testSubject 17";

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity("user","password");
        setUpSubject = new SubjectEntity("setUpSubject");
        setUpModule = new ModuleEntity("setUpModule",0.4);
        setUpSubject.getModules().add(setUpModule);
        user.getSubjects().add(setUpSubject);
        userRepository.save(user);
        moduleRepository.save(setUpModule);
        subjectRepository.save(setUpSubject);
        sessionId = SessionManager.getInstance().addSession(user.getUsername());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(SessionManager.getInstance().getSession(sessionId));
        subjectRepository.delete(setUpSubject);
        subjectRepository.deleteAll(subjectRepository.findByName(tearDownSubjectName));
        moduleRepository.delete(setUpModule);
        moduleRepository.deleteAll(moduleRepository.findByName(tearDownModuleName));
    }

    @Test
    void addNewModule() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("sessionId", sessionId)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        assertTrue(moduleRepository.findByName(tearDownModuleName).iterator().hasNext(), "There should be a sessionId with the given name");
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("sessionId", "fakeSessionId")
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("sessionId invalid")));
    }

    @Test
    void addNewGrade() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("grade", "1.0")
                        .param("sessionId", sessionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //noinspection OptionalGetWithoutIsPresent
        setUpModule = moduleRepository.findById(setUpModule.getId()).get();
        assertEquals(1.0, setUpModule.getGrade(), "The grade of the setupModule should be 1.0");
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", "-1")
                        .param("grade", "1.0")
                        .param("sessionId", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("moduleId invalid")));
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("grade", "1.0")
                        .param("sessionId", "fake sessionId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("sessionId invalid")));
    }

    @Test
    void addNewSubject() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                .param("name", tearDownSubjectName)
                .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        assertTrue(subjectRepository.findByName(tearDownSubjectName).iterator().hasNext(), "There should be an Subject with the given name");
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                        .param("name", tearDownSubjectName)
                        .param("sessionId", "fakeSessionId"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("sessionId invalid")));
    }

    @Test
    void deleteSubject() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
    }

}