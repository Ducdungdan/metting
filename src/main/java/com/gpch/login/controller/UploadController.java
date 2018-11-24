package com.gpch.login.controller;

import com.gpch.login.utils.MergeFileExcelsUtil;
import com.gpch.login.utils.PdfGenerator;
import com.gpch.login.utils.ReadFileExcelUtil;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Controller
public class UploadController {


    @Autowired
    MergeFileExcelsUtil mergeFileExcelsUtil;
    @Autowired
    PdfGenerator pdfGenerator;
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "";

    @GetMapping("/upload")
    public String index() {
        System.out.println(mergeFileExcelsUtil.merge(1).toString());
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            return "upload";
        }
        String rootPath = System.getProperty("user.dir");
        UPLOADED_FOLDER = rootPath + "/src/main/resources/upload/room_1/";
        File dir = new File(UPLOADED_FOLDER);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "upload";
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.GET)
    public String genPDF(){
        try {
            pdfGenerator.genPDF2(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "report";
    }

}