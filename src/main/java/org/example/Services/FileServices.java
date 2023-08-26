package org.example.Services;

import lombok.extern.slf4j.Slf4j;
import org.example.Entities.TerminalDetails;
import org.example.Pojos.*;
import org.example.Reposistories.TerminalDetailsRepository;
import org.example.helper.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class FileServices {

    @Autowired
    private TerminalDetailsRepository terminalDetailsRepository;

    private HashMap<Long, List<StatementPojo>> exStatementPojoList;

    private HashMap<Long, List<TerminalDetailsPojo>> exTerminalDetailsPojoList;

    private List<ConsolidatedStatementWithLocations> ListOfTransactionsPerTerminalDetailPerSite ;

    private RestTemplate restTemplate;

    public ResponseEntity<?> createNewDirectory(FolderPojo folderPojo) {
        String message = "";
        try {

            message = makeNewDirectory(folderPojo);

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), message));
        } catch (Exception e) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
        }


    }

    public ResponseEntity<?> uploadToNewDirectory(FolderPojo folderPojo,MultipartFile file) {
        String message = "";
        try {

            message = uploadCSVToDirectory(folderPojo,file);

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), message));
        } catch (Exception e) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
        }


    }

    public ResponseEntity<?> viewAllSubdirectoriesInUploadsFolder() {

        List<String> directories = listAllSubdirectoriesInUploadsFolder();

        if (directories.isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.NOT_FOUND.value(), "No folders in Uploads"));

        } else {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), "files have been listed " + directories));

        }

    }

    public ResponseEntity<?> readCSVFile(MultipartFile file) {

//      List<StatementPojo> statementPojoList =  readUploadedCSVFile(file);

        HashMap<Long, List<StatementPojo>> statementPojoList = readUploadedCSVFilePerTerminal(file);
        List<StatementPojo> mapList = new ArrayList<>();
        statementPojoList.forEach((key,value)->
        {
            mapList.add(value.get(0));
        });




        if (statementPojoList.isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please upload again"));
        } else {

            exStatementPojoList = statementPojoList;



            return new ResponseEntity<>( mapList, HttpStatus.OK);
        }

    }

    public ResponseEntity<?> fetchAllTerminalDetails() {

        return ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), "terminalDetailsRepository.findAll()"));

    }

    public ResponseEntity<?> ReadAllTerminalDetailsFromCSV(MultipartFile file) {


        HashMap<Long, List<TerminalDetailsPojo>> terminalDetailsList = readUploadedCSVFilePerSite(file);

        if (terminalDetailsList.isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please upload again"));
        } else {

            exTerminalDetailsPojoList = terminalDetailsList;

            return new ResponseEntity<>(terminalDetailsList, HttpStatus.OK);
        }


    }


    public ResponseEntity<?> fetchAllTerminalDetailsByTerminalID(Integer terminalID) {

        Optional<TerminalDetails> tradingName = terminalDetailsRepository.findById(terminalID);


        return tradingName.map(name -> ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), "name"))).
                orElseGet(() -> ResponseEntity.ok(new ResponsePojo(HttpStatus.NOT_FOUND.value(), "All is not good")));
    }

    public ResponseEntity<?> fetchTerminalDetailsCSVAndStatementCSV() {

        if (exTerminalDetailsPojoList.isEmpty() && exStatementPojoList.isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));
        } else {

            /*List<HashMap> hashMaps = new ArrayList<>();

            hashMaps.add(exStatementPojoList);

            hashMaps.add(exTerminalDetailsPojoList);*/

//            getTerminalsFromStatementThatMatchTerminalFromSiteLocation();

            return new ResponseEntity<>( getTerminalsFromStatementThatMatchTerminalFromSiteLocation(), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> fetchTransactionsForSingleTerminal(Long terminalNumber) {
       List<ConsolidatedStatementWithLocations> consolidatedStatementWithLocations = getTransactionsForSingleTerminalFromStatementThatMatchTerminalFromSiteLocation(terminalNumber);

        if (consolidatedStatementWithLocations.isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));
        } else {

            /*List<HashMap> hashMaps = new ArrayList<>();

            hashMaps.add(exStatementPojoList);

            hashMaps.add(exTerminalDetailsPojoList);*/

//            getTerminalsFromStatementThatMatchTerminalFromSiteLocation();

            return new ResponseEntity<>( consolidatedStatementWithLocations, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> fetchTransactionsPerTerminalPerSite() {

        if (getTerminalsFromStatementThatMatchTerminalFromSiteLocation().isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));
        } else {


            getTerminalsFromStatementThatMatchTerminalFromSiteLocation();

            return new ResponseEntity<>(getTerminalsFromStatementThatMatchTerminalFromSiteLocation(), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> fetchGroupedTerminalsPerSiteLocation() {

        if (groupTerminalsPerSiteLocation().isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));
        } else {

            return new ResponseEntity<>(groupTerminalsPerSiteLocation(), HttpStatus.OK);
        }
    }


    public ResponseEntity<?> listDirectoriesResponse() {

        if (listDirectories() == null) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No directories created"));
        } else {

            return new ResponseEntity<>(listDirectories(), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> fetchTotalTransactionsPerTerminalPerSite() {

        if (getTotalsTerminalsFromStatementThatMatchTerminalFromSiteLocation().isEmpty()) {

            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));

        } else {

            return new ResponseEntity<>(getTotalsTerminalsFromStatementThatMatchTerminalFromSiteLocation(), HttpStatus.OK);
        }
    }


    public ResponseEntity<?> fetchAllTerminalDetailsByTerminalIDFromCSV(Integer terminalID) {

        Optional<TerminalDetails> tradingName = terminalDetailsRepository.findById(terminalID);


        return tradingName.map(name -> ResponseEntity.ok(new ResponsePojo(HttpStatus.OK.value(), "name"))).
                orElseGet(() -> ResponseEntity.ok(new ResponsePojo(HttpStatus.NOT_FOUND.value(), "All is not good")));
    }

    public String makeNewDirectory(FolderPojo folderPojo) {

//        String path = "C:\\Users\\user\\IdeaProjects\\DirectoryCreationAndFileUpload-v0.1\\src\\main\\resources\\uploads\\" + folderPojo.getFolderName();

        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String path =  absolutePath+"\\merchant_files\\" + folderPojo.getFolderName();

        //Creating a folder using mkdirs() method
        String message = "";

        try {

            if (checkIfFolderAlreadyExists(Path.of(path))) {

                message = "Folder " + folderPojo.getFolderName() + " already exists";

            } else {

                //Instantiate the File class
                File f1 = new File(path);
                //Creating a folder using mkdirs() method
                f1.mkdirs();

                message = "Folder " + folderPojo.getFolderName() + " has been created successfully";
                System.out.println("Folder " + folderPojo.getFolderName() + " has been created successfully");

            }
        } catch (Exception e) {


            e.printStackTrace();
        }

        return message;
    }

    public List<String> listDirectories() {

//        String path = "C:\\Users\\user\\IdeaProjects\\DirectoryCreationAndFileUpload-v0.1\\src\\main\\resources\\uploads\\" + folderPojo.getFolderName();

        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String path =  absolutePath+"\\merchant_files\\";

        //Creating a folder using mkdirs() method
        String message = "";

        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        log.info(Arrays.toString(directories));


        assert directories != null;
        return convertArrayToList(directories);
    }

    // Generic function to convert an Array to List
    public static <T> List<T> convertArrayToList(T array[])
    {

        // Create an empty List
        List<T> list = new ArrayList<>();

        // Iterate through the array
        for (T t : array) {
            // Add each element into the list
            list.add(t);
        }

        // Return the converted List
        return list;
    }

    public List<String> listAllSubdirectoriesInUploadsFolder() {

//        String path = "C:\\Users\\user\\IdeaProjects\\DirectoryCreationAndFileUpload-v0.1\\src\\main\\resources\\uploads\\";

       /*
       File file = new File(path);
        String[] directories = file.list((dir, name) -> new File(dir, name).isDirectory());
        System.out.println(Arrays.toString(directories));
        */

        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String path =  absolutePath+"\\merchant_files\\" ;

        File file = new File(path);
        List<String> directories = List.of(Objects.requireNonNull(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        })));

        return directories;

    }

    public boolean checkIfFolderAlreadyExists(Path path) {

        boolean isFolderPresent = false;

        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                System.out.println("It is a directory");
                isFolderPresent = true;

            } else if (Files.isRegularFile(path)) {
                isFolderPresent = false;
                System.out.println("File test.txt present");
            }

        } else {
            System.out.println("File not found ");
        }

        return isFolderPresent;
    }

    public String uploadCSVToDirectory(FolderPojo folderPojo,MultipartFile file) {

//        String path = "C:\\Users\\user\\IdeaProjects\\DirectoryCreationAndFileUpload-v0.1\\src\\main\\resources\\uploads\\" + folderPojo.getFolderName();
        Path currentRelativePath = Paths.get("merchant_files"+ folderPojo.getFolderName());
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
//        String path =  absolutePath+"\\merchant_files\\" + folderPojo.getFolderName();

        //Creating a folder using mkdirs() method
        String message = "";

        try {
            Files.copy(file.getInputStream(), currentRelativePath.resolve(file.getOriginalFilename()));
            message = file.getOriginalFilename() + "uploaded";
        } catch (Exception e) {


            throw new RuntimeException(e.getMessage());
        }


        return message;
    }
    public List<StatementPojo> readUploadedCSVFile(MultipartFile file) {

        List<StatementPojo> statementPojo;


        try {
            statementPojo = CSVHelper.csvToTutorials(file.getInputStream());


//            CSVHelper.readCSVData(statementPojo);

        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }


        return statementPojo;

    }

    public HashMap<Long, List<StatementPojo>> readUploadedCSVFilePerTerminal(MultipartFile file) {

        HashMap<Long, List<StatementPojo>> statementPojo;


        try {
            statementPojo = CSVHelper.listTransactionPerTerminal(file.getInputStream());

//            CSVHelper.readCSVData(statementPojo);

        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }


        return statementPojo;

    }

    public HashMap<Long, List<TerminalDetailsPojo>> readUploadedCSVFilePerSite(MultipartFile file) {

        HashMap<Long, List<TerminalDetailsPojo>> terminalDetailsPerSite;

        try {
            terminalDetailsPerSite = CSVHelper.hashMapOfTerminalsPerSite(file.getInputStream());

//            CSVHelper.readCSVData(statementPojo);

        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }


        return terminalDetailsPerSite;

    }

    public  List<ConsolidatedStatementWithLocations> getTransactionsForSingleTerminalFromStatementThatMatchTerminalFromSiteLocation(Long terminalNumber) {

        List<ConsolidatedStatementWithLocations> listOfTransactionsForSingleTerminalPerSite = new ArrayList<>();

        for(Long statementPojo :exStatementPojoList.keySet()){

            if (exStatementPojoList.containsKey(terminalNumber)) {

                listOfTransactionsForSingleTerminalPerSite = compileConsolidatedTransactionsForSingleTerminal(

                        exTerminalDetailsPojoList.get(terminalNumber).get(0).getCustomerName(),
                        exTerminalDetailsPojoList.get(terminalNumber).get(0).getLocation(),
                        exStatementPojoList.get(terminalNumber));

            }
            else {
                log.info(terminalNumber + ": Is not in statement");
            }
        }
        return listOfTransactionsForSingleTerminalPerSite;

    }

    public String getSiteNameOfTerminal(Long terminalNumber) {

        String siteName = "";

        for(Long terminalId :exTerminalDetailsPojoList.keySet()){

            if (exTerminalDetailsPojoList.containsKey(terminalNumber)) {

                siteName = exTerminalDetailsPojoList.get(terminalNumber).get(0).getCustomerName();

            }
            else {
                log.info(terminalNumber + ": Is not in statement");
            }
        }
        return siteName;

    }


    public  HashMap<Long, List<ConsolidatedStatementWithLocations>> getTerminalsFromStatementThatMatchTerminalFromSiteLocation() {

        HashMap<Long, List<ConsolidatedStatementWithLocations>> transactionsPerTerminalDetailPerSiteMap = new HashMap<>();

        exTerminalDetailsPojoList.forEach((terminalDetailsKey, terminalDetailsValue) -> {
            List<ConsolidatedStatementWithLocations> consolidatedStatementWithLocationsList = new ArrayList<>();

            if (exStatementPojoList.containsKey(terminalDetailsKey)) {

                consolidatedStatementWithLocationsList = compileConsolidatedStatement(terminalDetailsValue.get(0).getCustomerName(),terminalDetailsValue.get(0).getLocation(),exStatementPojoList.get(terminalDetailsKey));



                transactionsPerTerminalDetailPerSiteMap.put(terminalDetailsKey,consolidatedStatementWithLocationsList );


       /* if (exTerminalDetailsPojoList.isEmpty() && exStatementPojoList.isEmpty()) {

//            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));
            log.info("Please re-upload statement");

        } else {
            exTerminalDetailsPojoList.forEach((key, value) -> {


                exTerminalDetailsPojoList.containsKey(exStatementPojoList.get(key));
                log.info("Key: {} Value {}", key, value.toString());
//                CustomerName customerName = new CustomerName();
//                customerName.setCustomerName(key);
//                customerName.setUserId(value.get(0).getUserId());
                // CustomerName customerName =  addCustomerName(merchantPojo);

            });

            List<HashMap> hashMaps = new ArrayList<>();

            hashMaps.add(exStatementPojoList);

            hashMaps.add(exTerminalDetailsPojoList);

        }*/

//            return ResponseEntity.ok(new ResponsePojo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please re-upload statement"));
//                    exTerminalDetailsPojoList.containsKey(exStatementPojoList.get(key));
//                    log.info("Key: {} Value {}", key, value.toString());

//                log.info("MatchedKey: {} value{}", terminalDetailsKey, exStatementPojoList.get(terminalDetailsKey).get(0).getCardNumber() + "\n");
//                log.info("MatchedKey: {} value{}", terminalDetailsKey, exStatementPojoList.get(terminalDetailsKey).get(0).getCustomerName() + "\n");
//                log.info("size{}", exStatementPojoList.get(terminalDetailsKey).size());
//                transactionsPerTerminalDetailPerSiteMap.put(value.get(3).getCustomerName(), exStatementPojoList.get(key));

                /*ConsolidatedStatementWithLocations consolidatedStatementWithLocations = new ConsolidatedStatementWithLocations(

                        terminalDetailsValue.get(0).getCustomerName(),
                        exStatementPojoList.get(terminalDetailsKey).get(0).getOutletNumber(),
                        exStatementPojoList.get(terminalDetailsKey).get(0).getTerminalNumber(),
                        exStatementPojoList.get(terminalDetailsKey).get(0).getCardNumber(),
                        exStatementPojoList.get(terminalDetailsKey).get(0).getTransactionAmount(),
                        terminalDetailsValue.get(0).getLocation()

                );*/

            }
            else {
                log.info(terminalDetailsKey + ": Is not in statement");
            }
        });

        return transactionsPerTerminalDetailPerSiteMap;

    }

    public  HashMap<Long, Float> getTotalsTerminalsFromStatementThatMatchTerminalFromSiteLocation() {

        HashMap<Long, Float> transactionsPerTerminalDetailPerSiteMap = new HashMap<>();

        exTerminalDetailsPojoList.forEach((terminalDetailsKey, terminalDetailsValue) -> {
            List<Float> consolidatedStatementWithLocationsList = new ArrayList<>();

            if (exStatementPojoList.containsKey(terminalDetailsKey)) {

//                consolidatedStatementWithLocationsList = compileTotalTransactionsPerTerminal(exStatementPojoList.get(terminalDetailsKey));

                transactionsPerTerminalDetailPerSiteMap.put(terminalDetailsKey,compileTotalTransactionsPerTerminal(exStatementPojoList.get(terminalDetailsKey)));
          }
            else {
                log.info(terminalDetailsKey + ": Is not in statement");
            }
        });

        return transactionsPerTerminalDetailPerSiteMap;

    }


    public  HashMap<String, List<Long>> groupTerminalsPerSiteLocation() {

        HashMap<String, List<Long>> transactionsPerTerminalDetailPerSiteMap = new HashMap<>();



        exTerminalDetailsPojoList.forEach((terminalDetailsKey, terminalDetailsValue) -> {



            if (exStatementPojoList.containsKey(terminalDetailsKey)) {

                transactionsPerTerminalDetailPerSiteMap.put( terminalDetailsValue.get(0).getCustomerName() ,compileGroupedTerminalsPerSiteLocation(terminalDetailsKey));

            }
            else {

                log.info(terminalDetailsKey + ": Is not in statement");
            }
        });

        return transactionsPerTerminalDetailPerSiteMap;

    }


    private List<ConsolidatedStatementWithLocations> compileConsolidatedStatement(String customerName, String location, List<StatementPojo> statementPojoList){

        List<ConsolidatedStatementWithLocations> consolidatedStatementWithLocations = new ArrayList<>();

        statementPojoList.forEach( statementItem ->{
//            log.info("StatementItem:  {} Location {}",statementItem.getCardNumber(), Location );

            consolidatedStatementWithLocations.add( new ConsolidatedStatementWithLocations(
                    customerName,
                    statementItem.getOutletNumber(),
                    statementItem.getTerminalNumber(),
                    statementItem.getCardNumber(),
                    statementItem.getTransactionAmount(),
                    location

            )) ;

        });

        return consolidatedStatementWithLocations;
    }

    private Float compileTotalTransactionsPerTerminal(List<StatementPojo> statementPojoList){

        Float totalAmountOfTransactions = 0f;



        List<Float> consolidatedStatementWithLocations = new ArrayList<>();

        statementPojoList.forEach( statementItem ->{
//            log.info("StatementItem:  {} Location {}",statementItem.getCardNumber(), Location );

            String u = statementItem.getTransactionAmount();
            u = u.replaceAll(",", "");
            consolidatedStatementWithLocations.add(Float.parseFloat(u));

        });

        for (Float total: consolidatedStatementWithLocations){

            totalAmountOfTransactions += total;
        }

        return totalAmountOfTransactions;
    }

    private List<ConsolidatedStatementWithLocations> compileConsolidatedTransactionsForSingleTerminal(String customerName, String location, List<StatementPojo> statementPojoList){

        List<ConsolidatedStatementWithLocations> consolidatedStatementWithLocations = new ArrayList<>();

        statementPojoList.forEach( statementItem ->{
//            log.info("StatementItem:  {} Location {}",statementItem.getCardNumber(), Location );

            consolidatedStatementWithLocations.add( new ConsolidatedStatementWithLocations(
                    customerName,
                    statementItem.getOutletNumber(),
                    statementItem.getTerminalNumber(),
                    statementItem.getCardNumber(),
                    statementItem.getTransactionAmount(),
                    location

            )) ;

        });

        return consolidatedStatementWithLocations;
    }

    private List<Long> compileGroupedTerminalsPerSiteLocation(Long terminalNumberList){

        List<Long> consolidatedStatementWithLocations = new ArrayList<>();

        consolidatedStatementWithLocations.add(terminalNumberList);

        /*terminalNumberList.forEach( statementItem ->{
//            log.info("StatementItem:  {} Location {}",statementItem.getCardNumber(), Location );

            consolidatedStatementWithLocations.add(statementItem);

        });*/

        return consolidatedStatementWithLocations;
    }

    public List<String> getSiteNameOfTerminalFromMap() {

        List<String> siteNames = new ArrayList<>();

        for(Long terminalId :exStatementPojoList.keySet()){

            if (exTerminalDetailsPojoList.containsKey(terminalId)) {

                siteNames.add( exTerminalDetailsPojoList.get(terminalId).get(0).getCustomerName());

            }
            else {
                log.info(terminalId + ": Is not in statement");
            }
        }
        return siteNames;

    }
}