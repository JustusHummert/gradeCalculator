package com.gradeCalculator.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gradeCalculator.entities.ModuleEntity;
import com.gradeCalculator.entities.SubjectEntity;
import com.gradeCalculator.repositories.ModuleRepository;
import com.gradeCalculator.repositories.SubjectRepository;
import com.gradeCalculator.repositories.UserRepository;
import com.gradeCalculator.services.ModuleService;
import com.gradeCalculator.services.SubjectService;
import com.gradeCalculator.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.gradeCalculator.entities.UserEntity;
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
    private UserService userService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private ModuleService moduleService;
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
    void setUp() throws Exception {
        cleanUp();
        UserEntity user = userService.createUser("user", "password");
        UserEntity user2 = userService.createUser("user2", "password");
        setUpSubject = subjectService.createSubject("setUpSubject", user);
        setUpSubject2 = subjectService.createSubject("setUpSubject2", user2);
        setUpModule = moduleService.createModule("setUpModule", 0.4, setUpSubject);
        setUpModule2 = moduleService.createModule("setUpModule2", 0.3, setUpSubject2);
        session = new MockHttpSession();
        session2 = new MockHttpSession();
        sessionNoUser = new MockHttpSession();
        session.setAttribute("username", user.getUsername());
        session2.setAttribute("username", user2.getUsername());
        sessionNoUser.setAttribute("username", "fake username");
    }

    @AfterEach
    void cleanUp(){
        moduleRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
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
                .andExpect(content().string(equalTo("Forbidden")));
        //subject does not belong to user
        mvc.perform(MockMvcRequestBuilders.post("/main/addModule")
                        .param("name", tearDownModuleName)
                        .param("gradingFactor", "0.5")
                        .param("subjectId", setUpSubject2.getId().toString())
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Forbidden")));
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
                .andExpect(content().string(equalTo("Forbidden")));
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
                .andExpect(content().string(equalTo("Forbidden")));
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
        //check if the subject is in the database
        assertTrue(subjectRepository.findById(setUpSubject.getId()).isPresent(), "The subject should be in the database");
        //valid request
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(content().string(equalTo("")));
        //check if request worked
        assertFalse(subjectRepository.findById((setUpSubject.getId())).isPresent(), "Subject should have been deleted");
        //invalid session
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(new MockHttpSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(content().string(equalTo("")));
        //session with invalid username
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(sessionNoUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(content().string(equalTo("")));
        //invalid subjectId
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", "-1")
                        .session(session))
                .andExpect(status().is4xxClientError());
        //subject does not belong to user
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteSubject")
                        .param("subjectId", setUpSubject2.getId().toString())
                        .session(session))
                .andExpect(status().is4xxClientError());
    }
    @Transactional
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
                .andExpect(content().string(equalTo("Forbidden")));
        //subjectId invalid
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule.getId().toString())
                        .param("subjectId", "-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Forbidden")));
        //module not in subject
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule2.getId().toString())
                        .param("subjectId", setUpSubject.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Forbidden")));
        //subject not in user
        mvc.perform(MockMvcRequestBuilders.post("/main/deleteModule")
                        .param("moduleId", setUpModule2.getId().toString())
                        .param("subjectId", setUpSubject2.getId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Forbidden")));
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