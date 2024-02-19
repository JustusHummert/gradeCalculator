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
    private GradeRepository gradeRepository;
    @Autowired
    private ModuleInSubjectRepository moduleInSubjectRepository;
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
        setUpModule = new ModuleEntity("setUpModule");
        setUpSubject = new SubjectEntity("setUpSubject");
        userRepository.save(user);
        moduleRepository.save(setUpModule);
        subjectRepository.save(setUpSubject);
        sessionId = SessionManager.getInstance().addSession(user.getUsername());
    }

    @AfterEach
    void tearDown() {
        gradeRepository.deleteAll(gradeRepository.findByModule(setUpModule));
        moduleInSubjectRepository.deleteAll(moduleInSubjectRepository.findBySubject(setUpSubject));
        userRepository.deleteById(SessionManager.getInstance().getSession(sessionId));
        moduleRepository.delete(setUpModule);
        subjectRepository.delete(setUpSubject);
        moduleRepository.deleteAll(moduleRepository.findByName(tearDownModuleName));
        subjectRepository.deleteAll(subjectRepository.findByName(tearDownSubjectName));
    }

    @Test
    void addNewModule() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule").param("name", tearDownModuleName).param("sessionId", sessionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        assertTrue(moduleRepository.findByName(tearDownModuleName).iterator().hasNext(), "There should be a sessionId with the given name");
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule").param("name", tearDownModuleName).param("sessionId", "fakeSessionId")
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
        assertTrue(gradeRepository.findByModule(setUpModule).iterator().hasNext(), "there should be a grade with the given module.");
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
    void addNewModuleInSubject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/main/addModuleInSubject")
                .param("subjectId", setUpSubject.getId().toString())
                .param("moduleId", setUpModule.getId().toString())
                .param("gradingFactor", "0.4")
                .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        assertTrue(moduleInSubjectRepository.findBySubject(setUpSubject).iterator().hasNext());
        mvc.perform(MockMvcRequestBuilders.post("/main/addModuleInSubject")
                        .param("subjectId", "-1")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("gradingFactor", "0.4")
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subjectId invalid")));
    }

    @Test
    void enroll() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/enroll")
                        .param("subjectId", "-1")
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subjectId invalid")));
        mvc.perform(MockMvcRequestBuilders.post("/main/enroll")
                .param("subjectId", setUpSubject.getId().toString())
                .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        mvc.perform(MockMvcRequestBuilders.post("/main/enroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("already enrolled")));
        mvc.perform(MockMvcRequestBuilders.post("/main/enroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", "fake sessionId"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("sessionId invalid")));
    }

    @Test
    void unenroll() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/unenroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("user not enrolled")));
        mvc.perform(MockMvcRequestBuilders.post("/main/unenroll")
                        .param("subjectId", "-1")
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subjectId invalid")));
        mvc.perform(MockMvcRequestBuilders.post("/main/unenroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", "fake sessionId"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("sessionId invalid")));
        mvc.perform(MockMvcRequestBuilders.post("/main/enroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        mvc.perform(MockMvcRequestBuilders.post("/main/unenroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
    }

    @Test
    void deleteSubject() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/enroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("there is a user enrolled in this subject")));
        mvc.perform(MockMvcRequestBuilders.post("/main/unenroll")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));

    }

}