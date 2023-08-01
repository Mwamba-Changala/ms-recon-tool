package org.example.Controllers;


import org.example.Pojos.FolderPojo;
import org.example.Pojos.ResponsePojo;
import org.example.Services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v0.1/folderCreation")
public class FileController {
    @Autowired
    FileServices fileServices;
    @PostMapping("/createNewDirectory")
    public ResponseEntity<?> createNewDirectoryEndPoint(@RequestBody FolderPojo folderPojo) {

      return   fileServices.createNewDirectory(folderPojo);

    }

    @PostMapping("/uploadAndReadCSV")
    public ResponseEntity<?> readCSVFileEndPoint(@RequestParam("file") MultipartFile file) {

        return   fileServices.readCSVFile(file);

    }

    @GetMapping("/viewAllSubdirectoriesInUploadsFolder")
    public ResponseEntity<?> viewAllSubdirectoriesInUploadsFolderEndPoint(){

        return fileServices.viewAllSubdirectoriesInUploadsFolder();    }


    @GetMapping("/viewAllTerminals")
    public ResponseEntity<?> fetchAllTerminalDetailsEndPoint(){

        return fileServices.fetchAllTerminalDetails();
    }

    @PostMapping("/viewTerminalsFromUploadedCSV")
    public ResponseEntity<?> fetchAllTerminalDetailsFromUploadedCSVFilePerSiteEndPoint(@RequestParam("file") MultipartFile file){

        return fileServices.ReadAllTerminalDetailsFromCSV(file);
    }

    @GetMapping("/viewAllFromTerminalDetailsCSVAndStatementCSV")
    public ResponseEntity<?> fetchTerminalDetailsCSVAndStatementCSVEndPoint(){

        return fileServices.fetchTerminalDetailsCSVAndStatementCSV();
    }

    @GetMapping("/viewAllTransactionsPerTerminalPerSite")
    public ResponseEntity<?> fetchTransactionsPerTerminalPerSiteEndPoint(){

        return fileServices.fetchTerminalDetailsCSVAndStatementCSV();
    }

    @GetMapping("/canYouReachMe")
    public ResponseEntity<?> canYouReachMe(){

        return ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), "Yes we can"));
    }
}
