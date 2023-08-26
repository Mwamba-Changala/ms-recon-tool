package org.example;

import jakarta.annotation.Resource;
import org.example.ExportAsPdf.PDFGeneratorService;
import org.example.ExportAsPdf.Student;
import org.example.ExportAsPdf.StudentService;
import org.example.Services.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class DirectoryCreationAndFileUploadApplication implements CommandLineRunner{

    @Autowired
    private StudentService studentService;

    public static void main(String[] args) {

        ApplicationContext ac =SpringApplication.run(DirectoryCreationAndFileUploadApplication.class, args);

    }



    @Override
    public void run(String... arg) throws Exception {
//    storageService.deleteAll();
//        storageService.init();

        for (int i = 0; i <= 10; i++) {
            Student student = new Student();
            student.setStudentName("Student Name");
            student.setEmail("student@mail.com");
            student.setMobileNo("XXXXXXXXXX");
            studentService.addStudent(student);
        }
    }



}
