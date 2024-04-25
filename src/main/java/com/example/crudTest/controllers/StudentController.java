package com.example.crudTest.controllers;

import com.example.crudTest.entities.Student;
import com.example.crudTest.repositories.StudentRepository;
import com.example.crudTest.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentService userService;

    @GetMapping("/")
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getSingle(@PathVariable Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @PostMapping("/")
    public @ResponseBody Student create(@RequestBody Student user) {
        return studentRepository.save(user);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student student = studentRepository.findById(id).orElse(null);
        if (!(student == null)) {
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            return studentRepository.save(student);
        }
        return null;
    }

    @PutMapping("/{id}/working")
    public Student updateWorkingStatus(@PathVariable Long id, @RequestParam boolean isWorking) {
        return userService.setUserIsWorkingStatus(id, isWorking);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }
}
