package org.example.Controllers;


import org.example.Pojos.StatementPojo;
import org.example.Services.FileServices;
import org.example.Services.TotalServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ms-recon-tool/")
public class FileController {

    @Autowired
    private FileServices fileServices;
@Autowired
    private FileRestController restController;

@Autowired
private TotalServices totalServices;

    @GetMapping("")
    public String homepage() {
        return "redirect:viewListOfFiles";
    }

@GetMapping("test")
    public String canYouReachMe(Model model){



//    model.addAttribute("message", restController.readCSVFileEndPoint(file));

    return "index";

}


@PostMapping("uploadTestFile")
    public String uploadTestfile(Model model, @RequestParam("file") MultipartFile file){



    model.addAttribute("message", restController.readCSVFileEndPoint(file).getBody());

    return "index";
}


    @GetMapping("/viewListOfFiles")
    public String getListFiles(Model model) {


        model.addAttribute("files", totalServices.getListFiles());

        return "files";
    }

    @GetMapping("files/delete/{filename:.+}")
    public String deleteFile(@PathVariable String filename, Model model, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", "Deleted the file successfully: " + filename);

        return "redirect:/ms-recon-tool/viewListOfFiles";
    }

    @GetMapping("files/load/{fileName:.+}")
    public String listTerminalsInFile(@PathVariable String fileName, Model model, RedirectAttributes redirectAttributes) {

//        redirectAttributes.addFlashAttribute("message", "Deleted the file successfully: " + fileName);
        redirectAttributes.addFlashAttribute("listOfTerminals", totalServices.getListOfTerminals(fileName));
        redirectAttributes.addFlashAttribute("listOfTransactions", fileServices.getTerminalsFromStatementThatMatchTerminalFromSiteLocation());
        redirectAttributes.addFlashAttribute("siteNames", fileServices.getSiteNameOfTerminalFromMap());
        return "redirect:/ms-recon-tool/viewAllTransactionsForPerTerminal";

//        return "redirect:/ms-recon-tool/viewListOfTerminals";
    }


    @GetMapping("/viewListOfTerminals")
    public String listOfTerminals(Model model) {

        model.addAttribute("listOfTerminals", totalServices.getListOfTerminals("mt meru sites.csv"));

        return "viewTerminals";
    }


    @GetMapping("statement/load/{terminalNumber:.+}")
    public String loadTransactionsForParticularTerminal(@PathVariable Long terminalNumber, Model model, RedirectAttributes redirectAttributes) {

//        redirectAttributes.addFlashAttribute("message", "Deleted the file successfully: " + fileName);
//        redirectAttributes.addFlashAttribute("listOfTerminals", totalServices.getListOfTerminals(fileName));

        redirectAttributes.addFlashAttribute("listOfTransactions", fileServices.getTransactionsForSingleTerminalFromStatementThatMatchTerminalFromSiteLocation(terminalNumber));
        redirectAttributes.addFlashAttribute("siteName", fileServices.getSiteNameOfTerminal(terminalNumber));


        redirectAttributes.addFlashAttribute("terminalNumber", terminalNumber);

        return "redirect:/ms-recon-tool/viewTransactionsForParticularTerminal";


    }


    @GetMapping("/viewTransactionsForParticularTerminal")
    public String listTransactionsForParticularTerminal(Model model, RedirectAttributes redirectAttributes) {

//        redirectAttributes.addFlashAttribute("message", "Deleted the file successfully: " + fileName);


        return "viewTransactionsForParticularTerminal";
    }


    @GetMapping("/viewAllTransactionsForPerTerminal")
    public String listAllTransactionsForPerTerminal(Model model, RedirectAttributes redirectAttributes) {

//        redirectAttributes.addFlashAttribute("message", "Deleted the file successfully: " + fileName);


        return "viewAllTransactionsForPerTerminal";
    }

}
