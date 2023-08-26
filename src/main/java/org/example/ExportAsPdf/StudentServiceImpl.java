package org.example.ExportAsPdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    StudentRepository studentRepo;
    @Override
    public void addStudent(Student student) {
        studentRepo.save(student);
    }
    @Override
    public List<Student> getStudentList() {
        return studentRepo.findAll();
    }



}
