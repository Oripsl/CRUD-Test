package com.example.crudTest;


import com.example.crudTest.controllers.StudentController;
import com.example.crudTest.entities.Student;
import com.example.crudTest.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class CrudTestApplicationTests {

    @Autowired
    private StudentController studentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MockMvc mvc;

    @AfterEach
    public void tearDown() {
        studentRepository.deleteAll();
    }


    @Test
    void contextLoads() {
        assertThat(studentController).isNotNull();
    }

    @Test
    void createStudent() throws Exception {
        Student student = new Student();
        student.setFirstName("nome");
        student.setLastName("cognome");
        student.setWorking(true);


        String studentJSON = objectMapper.writeValueAsString(student);

        MvcResult result = this.mvc.perform(post("/students/").contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentResult = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);

        assertThat(studentResult.getId()).isNotNull();

        assertThat(studentResult.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(studentResult.getLastName()).isEqualTo(student.getLastName());
        assertThat(studentResult.isWorking()).isEqualTo(student.isWorking());
    }

    @Test
    void getSingleStudent() throws Exception {
        Student student = new Student();
        student.setFirstName("nome");
        student.setLastName("cognome");
        student.setWorking(true);

        studentRepository.save(student);

        MvcResult result = this.mvc.perform(get("/students/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentResult = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);

        assertThat(studentResult.getId()).isNotNull();

        assertThat(studentResult.getLastName()).isEqualTo(student.getLastName());
        assertThat(studentResult.getFirstName()).isEqualTo(student.getFirstName());
    }

    @Test
    void getAllStudents() throws Exception {
        Student student = new Student();
        student.setFirstName("nome1");
        student.setLastName("cognome1");
        student.setWorking(true);

        Student student1 = new Student();
        student1.setFirstName("nome2");
        student1.setLastName("cognome2");
        student1.setWorking(true);
        student1.setId(12L);

        studentRepository.save(student);
        studentRepository.save(student1);

        MvcResult result = this.mvc.perform(get("/students/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Student> students = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(students).hasSize(2);

        assertThat(students).extracting("firstName").contains("nome1", "nome2");
        assertThat(students).extracting("lastName").contains("cognome1", "cognome2");

    }

    @Test
    void updateStudentNameAndSurname() throws Exception {
        Student student = new Student();
        student.setFirstName("nome1");
        student.setLastName("cognome1");
        student.setWorking(true);

        Student savedStudent = studentRepository.save(student);

        student.setFirstName("Car");
        student.setLastName("Carlo");

        String studentJSON = objectMapper.writeValueAsString(student);


        MvcResult result = this.mvc.perform(put("/students/" + savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentResult = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);

        assertThat(studentResult.getId()).isNotNull();
        assertThat(studentResult.getId()).isEqualTo(savedStudent.getId());

        assertThat(studentResult.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(studentResult.getLastName()).isEqualTo(student.getLastName());

    }

    @Test
    void updateWorkingStatus() throws Exception {
        Student student = new Student();
        student.setFirstName("nome1");
        student.setLastName("cognome1");
        student.setWorking(true);

        Student savedStudent = studentRepository.save(student);

        MvcResult result = this.mvc.perform(put("/students/" + savedStudent.getId() + "/working")
                        .param("isWorking", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentResult = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class);

        assertThat(studentResult.getId()).isEqualTo(savedStudent.getId());

        assertThat(studentResult.isWorking()).isFalse();
    }

    @Test
    void deleteStudent() throws Exception {
        Student student = new Student();
        student.setFirstName("nome1");
        student.setLastName("cognome1");
        student.setWorking(true);

        studentRepository.save(student);

        this.mvc.perform(delete("/students/" + student.getId())).andDo(print())
                .andExpect(status().isOk());

        assertThat(studentRepository.findById(student.getId())).isEmpty();
    }

}
