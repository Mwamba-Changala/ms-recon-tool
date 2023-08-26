package org.example.Controllers;


import org.example.Pojos.FolderPojo;
import org.example.Pojos.ResponsePojo;
import org.example.Services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v0.1/folderCreation")
public class FileRestController {
    @Autowired
    FileServices fileServices;
    @PostMapping("/createNewDirectory")
    public ResponseEntity<?> createNewDirectoryEndPoint(@RequestBody FolderPojo folderPojo) {

      return   fileServices.createNewDirectory(folderPojo);

    }

    @GetMapping("/listDirectories")
    public ResponseEntity<?> listDirectoriesEndPoint() {

        return   fileServices.listDirectoriesResponse();

    }

    @PostMapping("/uploadAndReadCSV")
    public ResponseEntity<?> readCSVFileEndPoint(@RequestParam("file") MultipartFile file) {

        return   fileServices.readCSVFile(file);

    }

    @GetMapping("/viewAllSubdirectoriesInUploadsFolder")
    public ResponseEntity<?> viewAllSubdirectoriesInUploadsFolderEndPoint(){

        return fileServices.viewAllSubdirectoriesInUploadsFolder();    }

    @GetMapping("/viewTotalTransactionsPerTerminalPerSite")
    public ResponseEntity<?> fetchTotalTransactionsPerTerminalPerSite(){

        return fileServices.fetchTotalTransactionsPerTerminalPerSite();    }

    @GetMapping("/viewGroupedTerminalsPerSiteLocation")
    public ResponseEntity<?> fetchGroupedTerminalsPerSiteLocation(){

        return fileServices.fetchGroupedTerminalsPerSiteLocation();    }

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

    @GetMapping("/uploadToNewDirectory")
    public ResponseEntity<?> uploadToNewDirectoryEndPoint(@RequestParam("file") MultipartFile file, FolderPojo folderPojo){

        return fileServices.uploadToNewDirectory(folderPojo,file);

    }


    @GetMapping("/viewAllTransactionsPerTerminalPerSite")
    public ResponseEntity<?> fetchTransactionsPerTerminalPerSiteEndPoint(){

        return fileServices.fetchTerminalDetailsCSVAndStatementCSV();
    }

    @GetMapping("/viewAllTransactionsForSingleTerminal/{terminalNumber}")
    public ResponseEntity<?> fetchTransactionsForSingleTerminalEndPoint(@PathVariable Long terminalNumber){

        return fileServices.fetchTransactionsForSingleTerminal(terminalNumber);

    }

    @GetMapping("/canYouReachMe")
    public ResponseEntity<?> canYouReachMe(){

        return ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), "Yes we can"));
    }
}
