package com.example.crudTest.service;

import com.example.crudTest.entities.Student;
import com.example.crudTest.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    public Student setUserIsWorkingStatus(Long id, boolean isWorking) {
        return studentRepository.findById(id).map(user -> {
            user.setWorking(isWorking);
            return studentRepository.save(user);
        }).orElse(null);
    }
}
