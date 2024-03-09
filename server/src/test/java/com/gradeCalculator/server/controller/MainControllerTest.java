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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.gradeCalculator.server.Entities.UserEntity;
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

    MockHttpSession session;
    MockHttpSession session2;
    MockHttpSession sessionNoUser;
    ModuleEntity setUpModule;
    ModuleEntity setUpModule2;
    SubjectEntity setUpSubject;
    SubjectEntity setUpSubject2;
    String tearDownModuleName = "testModule 42";
    String tearDownSubjectName = "testSubject 17";

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity("user","password");
        UserEntity user2 = new UserEntity("user2", "password");
        setUpSubject = new SubjectEntity("setUpSubject");
        setUpSubject2 = new SubjectEntity("setUpSubject2");
        setUpModule = new ModuleEntity("setUpModule",0.4);
        setUpModule2 = new ModuleEntity("setUpModule2", 0.3);
        setUpSubject.getModules().add(setUpModule);
        setUpSubject2.getModules().add(setUpModule2);
        user.getSubjects().add(setUpSubject);
        user2.getSubjects().add(setUpSubject2);
        userRepository.save(user);
        userRepository.save(user2);
        moduleRepository.save(setUpModule);
        moduleRepository.save(setUpModule2);
        subjectRepository.save(setUpSubject);
        subjectRepository.save(setUpSubject2);
        session = new MockHttpSession();
        session2 = new MockHttpSession();
        sessionNoUser = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        session2.setAttribute("username", user2.getUsername());
        sessionNoUser.setAttribute("username", "fake username");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(session.getAttribute("username").toString());
        userRepository.deleteById(session2.getAttribute("username").toString());
        subjectRepository.delete(setUpSubject);
        subjectRepository.delete(setUpSubject2);
        subjectRepository.deleteAll(subjectRepository.findByName(tearDownSubjectName));
        moduleRepository.delete(setUpModule);
        moduleRepository.delete(setUpModule2);
        moduleRepository.deleteAll(moduleRepository.findByName(tearDownModuleName));
    }

    @Test
    void addNewModule() throws Exception {
        //valid request
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //check if request worked
        assertTrue(moduleRepository.findByName(tearDownModuleName).iterator().hasNext(), "There should be a session with the given name");
        //request with invalid session
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(sessionNoUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //request with session saved with invalid username
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(sessionNoUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //invalid subjectId
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", "-1")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subjectId invalid")));
        //subject does not belong to user
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject2.getId().toString())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subject does not belong to user")));
    }

    @Test
    void addNewGrade() throws Exception{
        //valid request
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("grade", "1.0")
                        .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //check if request worked
        //noinspection OptionalGetWithoutIsPresent
        setUpModule = moduleRepository.findById(setUpModule.getId()).get();
        assertEquals(1.0, setUpModule.getGrade(), "The grade of the setupModule should be 1.0");
        //invalid moduleId
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", "-1")
                        .param("grade", "1.0")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("moduleId invalid")));
        //invalid session
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("grade", "1.0")
                        .session(new MockHttpSession())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //session with invalid username
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("grade", "1.0")
                        .session(sessionNoUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //module does not belong to user
        mvc.perform(MockMvcRequestBuilders.post("/main/addGrade")
                        .param("moduleId", setUpModule2.getId().toString())
                        .param("grade", "1.0")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("module does not belong to user")));
    }

    @Test
    void addNewSubject() throws Exception{
        //valid request
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                .param("name", tearDownSubjectName)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //check if request worked
        assertTrue(subjectRepository.findByName(tearDownSubjectName).iterator().hasNext(), "There should be an Subject with the given name");
        //invalid session
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                        .param("name", tearDownSubjectName)
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //session with invalid username
        mvc.perform(MockMvcRequestBuilders.post("/main/addSubject")
                        .param("name", tearDownSubjectName)
                        .session(sessionNoUser))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
    }

    @Test
    void deleteSubject() throws Exception{
        //valid request
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //check if request worked
        assertFalse(subjectRepository.findById((setUpSubject.getId())).isPresent(), "Subject should have been deleted");
        //invalid session
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //session with invalid username
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(sessionNoUser))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //invalid subjectId
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", "-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subjectId invalid")));
        //subject does not belong to user
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject2.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subject does not belong to user")));
    }
    @Test
    void  deleteModule() throws Exception{
        //invalid session
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //session with invalid username
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(sessionNoUser))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("session invalid")));
        //moduleId invalid
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", "-1")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("moduleId invalid")));
        //subjectId invalid
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("subjectId", "-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subjectId invalid")));
        //module not in subject
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule2.getId().toString())
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("module does not belong to subject")));
        //subject not in user
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule2.getId().toString())
                        .param("subjectId", setUpSubject2.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("subject does not belong to user")));
        //valid request
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("saved")));
        //check if request worked
        assertFalse(moduleRepository.findById(setUpModule.getId()).isPresent());
    }

}