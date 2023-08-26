package org.example.Services;

import lombok.extern.slf4j.Slf4j;
import org.example.Controllers.TotalRestController;
import org.example.Pojos.FileInfo;
import org.example.Pojos.TerminalDetailsPojo;
import org.example.helper.CSVHelper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TotalServices implements FilesStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }


    public List<FileInfo> getListFiles() {
        List<FileInfo> fileInfos = loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(TotalRestController.class, "getFile", path.getFileName().toString()).build().toString();

            String filePath = path.toString();
            return new FileInfo(filename,url );
        }).collect(Collectors.toList());

        return fileInfos;
    }

    public void getListDirectories() {

        File file = new File("/path/to/directory");
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        log.info(Arrays.toString(directories));

        /*List<FileInfo> fileInfos = loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(TotalRestController.class, "getFile", path.getFileName().toString()).build().toString();

            String filePath = path.toString();
            return new FileInfo(filename,url );
        }).collect(Collectors.toList());

        return fileInfos;*/
    }

    @Override
    public boolean delete(String filename) {
        try {
            Path file = root.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void getAllTerminalDetails(MultipartFile file){

        HashMap<Long, List<TerminalDetailsPojo>> terminalDetailsPerSite;

        try {
            terminalDetailsPerSite = CSVHelper.hashMapOfTerminalsPerSite(file.getInputStream());

//            CSVHelper.readCSVData(statementPojo);

        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }


//        return terminalDetailsPerSite;
    }

    public List<TerminalDetailsPojo> getListOfTerminals(String fileName){


        File file = new File(root +"\\" + fileName);

        List<TerminalDetailsPojo> terminalDetailsPojoList = new ArrayList<>() ;

        try{
//            InputStream inputStream = TotalServices.class.getResourceAsStream();

            FileInputStream fileInputStream = new FileInputStream(root +"\\" + fileName);
            terminalDetailsPojoList=   CSVHelper.listTerminalsPerSite(fileInputStream);
        }
        catch (Exception e){
            throw new RuntimeException("fail to load csv data: " + e.getMessage());
        }

        return terminalDetailsPojoList;
    }

}
