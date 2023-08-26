package org.example.ExportAsPdf;

import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/pdf-Gen/")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private FileServices fileServices;
    @GetMapping("/export-to-pdf")
    public void generatePdfFile(HttpServletResponse response) throws DocumentException, IOException
    {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=student" + currentDateTime + ".pdf";
        response.setHeader(headerkey, headervalue);
        List<Student> listofStudents = studentService.getStudentList();
//        PDFGeneratorService generator = new PDFGeneratorService();
//        generator.generate(listofStudents, response);

        TotalPDFGenerator generator = new TotalPDFGenerator();
        generator.generate(fileServices.getTerminalsFromStatementThatMatchTerminalFromSiteLocation(),response);
    }
}
