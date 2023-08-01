package org.example.helper;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.Pojos.StatementPojo;
import org.example.Pojos.TerminalDetailsPojo;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<StatementPojo> csvToTutorials(InputStream is) {

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<StatementPojo> statementPojoList = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                StatementPojo statementPojo = new StatementPojo(

                        csvRecord.get(0),
                       Long.parseLong(csvRecord.get( 1)) ,
                        Long.parseLong(csvRecord.get( 2)),
                        csvRecord.get(3),
                        csvRecord.get(4),
                        csvRecord.get(5)
                        );


                statementPojoList.add(statementPojo);
            }

            return statementPojoList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }


    public static HashMap<Long,List<StatementPojo>> listTransactionPerTerminal(InputStream is) {

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            HashMap<Long, List<StatementPojo>> terminalsFromStatement = new HashMap<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                if(terminalsFromStatement.containsKey(Long.parseLong(csvRecord.get(2)))){

                    List<StatementPojo> statementPojoList = terminalsFromStatement.get(Long.parseLong(csvRecord.get(2)));

                    StatementPojo statementPojo = new StatementPojo(

                            csvRecord.get(0),
                            Long.parseLong(csvRecord.get( 1)) ,
                            Long.parseLong(csvRecord.get( 2)),
                            csvRecord.get(3),
                            csvRecord.get(4),
                            csvRecord.get(5)
                    );


                    statementPojoList.add(statementPojo);
                }else {

                    List<StatementPojo> statementPojoList = new ArrayList<>();

                    StatementPojo statementPojo = new StatementPojo(

                            csvRecord.get(0),
                            Long.parseLong(csvRecord.get(1)) ,
                            Long.parseLong(csvRecord.get(2)),
                            csvRecord.get(3),
                            csvRecord.get(4),
                            csvRecord.get(5)
                    );

                    statementPojoList.add(statementPojo);

                    terminalsFromStatement.put(Long.parseLong(csvRecord.get( 2)),statementPojoList);
                }

            }

            return terminalsFromStatement;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static HashMap<Long,List<TerminalDetailsPojo>> listTerminalsPerSite(InputStream is) {

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            HashMap<Long, List<TerminalDetailsPojo>> terminalsFromStatement = new HashMap<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                if(terminalsFromStatement.containsKey(Long.parseLong(csvRecord.get(2)))){

                    List<TerminalDetailsPojo> statementPojoList = terminalsFromStatement.get(Long.parseLong(csvRecord.get(2)));

                    List<TerminalDetailsPojo> terminalDetailsList = new ArrayList<>();

                    TerminalDetailsPojo terminalDetailsPojo = new TerminalDetailsPojo(
                            Long.parseLong(csvRecord.get(1)),
                            Long.parseLong(csvRecord.get(2)),
                            csvRecord.get(3),
                            csvRecord.get(0));

                    terminalDetailsList.add(terminalDetailsPojo);


                }else {

                    List<TerminalDetailsPojo> terminalDetailsList = new ArrayList<>();

                    TerminalDetailsPojo terminalDetailsPojo = new TerminalDetailsPojo(
                            Long.parseLong(csvRecord.get(1)),
                            Long.parseLong(csvRecord.get(2)),
                            csvRecord.get(3),
                            csvRecord.get(0));

                    terminalDetailsList.add(terminalDetailsPojo);

                    terminalsFromStatement.put(Long.parseLong(csvRecord.get(2)),terminalDetailsList);
                }

            }

            return terminalsFromStatement;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static void readCSVData(List<StatementPojo> statementPojo) {

            statementPojo.forEach(
                    System.out::println
            );
    }
}
