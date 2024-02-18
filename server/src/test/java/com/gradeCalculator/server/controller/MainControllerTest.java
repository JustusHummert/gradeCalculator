package com.gradeCalculator.server.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gradeCalculator.server.Entities.GradeEntity;
import com.gradeCalculator.server.Entities.ModuleEntity;
import com.gradeCalculator.server.Entities.SubjectEntity;
import com.gradeCalculator.server.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.support.NullValue;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.gradeCalculator.server.Entities.UserEntity;
import com.gradeCalculator.server.SessionManagement.SessionManager;
import com.gradeCalculator.server.repositories.*;
import org.apache.catalina.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        sessionId = SessionManager.getInstance().addSession(user);
    }

    @AfterEach
    void tearDown() {
        gradeRepository.deleteAll(gradeRepository.findByModule(setUpModule));
        moduleInSubjectRepository.deleteAll(moduleInSubjectRepository.findBySubject(setUpSubject));
        moduleRepository.delete(setUpModule);
        subjectRepository.delete(setUpSubject);
        userRepository.delete(SessionManager.getInstance().getSession(sessionId));
        moduleRepository.deleteAll(moduleRepository.findByName(tearDownModuleName));
        subjectRepository.deleteAll(subjectRepository.findByName(tearDownSubjectName));
    }

    @Test
    void addNewModule() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule").param("name", tearDownModuleName).param("sessionId", sessionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")));
        assertTrue(moduleRepository.findByName(tearDownModuleName).iterator().hasNext(), "There should be a sessionId with the given name");
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule").param("name", tearDownModuleName).param("sessionId", "fakeSessionId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("false")));
    }

    @Test
    void getAllModules() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/main/allModules")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not("")));
    }

    @Test
    void addNewGrade() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("grade", "1.0")
                        .param("sessionId", sessionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")));
        assertTrue(gradeRepository.findByModule(setUpModule).iterator().hasNext(), "there should be a grade with the given module.");
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", "-1")
                        .param("grade", "1.0")
                        .param("sessionId", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("false")));
    }

    @Test
    void getYourGrades() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/yourGrades")
                .param("sessionId", sessionId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
        gradeRepository.save(new GradeEntity(SessionManager.getInstance().getSession(sessionId), setUpModule, 4.0));
        mvc.perform(MockMvcRequestBuilders.post("/main/yourGrades")
                        .param("sessionId", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not("[]")));
        mvc.perform(MockMvcRequestBuilders.post("/main/yourGrades")
                        .param("sessionId", "fakeSessionId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }

    @Test
    void addNewSubject() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                .param("name", tearDownSubjectName)
                .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")));
        assertTrue(subjectRepository.findByName(tearDownSubjectName).iterator().hasNext(), "There should be an Subject with the given name");
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                        .param("name", tearDownSubjectName)
                        .param("sessionId", "fakeSessionId"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("false")));
    }

    @Test
    void getAllSubjects() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/main/allSubjects"))
                .andExpect(status().isOk())
                .andExpect(content().string(not("")));
    }

    @Test
    void addNewModuleInSubject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/main/addModuleInSubject")
                .param("subjectId", setUpSubject.getId().toString())
                .param("moduleId", setUpModule.getId().toString())
                .param("gradingFactor", "0.4")
                .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")));
        assertTrue(moduleInSubjectRepository.findBySubject(setUpSubject).iterator().hasNext());
        mvc.perform(MockMvcRequestBuilders.post("/main/addModuleInSubject")
                        .param("subjectId", "-1")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("gradingFactor", "0.4")
                        .param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("false")));
    }

    @Test
    void getModulesInSubject() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/main/modulesInSubject")
                .param("subjectId", setUpSubject.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(not("")));
        mvc.perform(MockMvcRequestBuilders.post("/main/modulesInSubject")
                        .param("subjectId", "-1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}