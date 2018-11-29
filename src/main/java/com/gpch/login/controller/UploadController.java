package com.gpch.login.controller;

import com.gpch.login.model.FileSave;
import com.gpch.login.model.User;
import com.gpch.login.service.FileService;
import com.gpch.login.utils.MergeFileExcelsUtil;
import com.gpch.login.utils.PdfGenerator;
import com.gpch.login.utils.ReadFileExcelUtil;
import com.itextpdf.text.DocumentException;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/room")
public class UploadController {


    @Autowired
    MergeFileExcelsUtil mergeFileExcelsUtil;
    @Autowired
    PdfGenerator pdfGenerator;
    
    @Autowired
    FileService fileService;
    
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "";

    @GetMapping("/upload")
    public String index() {
        System.out.println(mergeFileExcelsUtil.merge(1).toString());
        return "upload";
    }

    
    @PostMapping("/upload/{roomId}") // //new annotation since 4.3
    public @ResponseBody Map<String, ? extends Object> singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, @PathVariable int roomId, HttpServletRequest request) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
        if (file.isEmpty()) {
        	result.put("code", 1);
    		result.put("message", "No file");
    		return result;
        }
        String rootPath = System.getProperty("user.dir");
        UPLOADED_FOLDER = rootPath + "/src/main/resources/upload/room_"+roomId+"/";
        File dir = new File(UPLOADED_FOLDER);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            FileSave newFile = fileService.saveFileSaveById(user.getId(), roomId, path.toString(), file.getOriginalFilename());
            
            result.put("code", 0);
            result.put("data", newFile);
    		result.put("message", "OK");
            
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.put("code", 1);
		result.put("message", "ERROR");
		return result;
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

    @RequestMapping(value = "/doc/{roomId}", method = RequestMethod.GET)
    public String genDoc(@PathVariable int roomId){
        String rootPath = System.getProperty("user.dir");
        String path_export = rootPath + "/src/main/resources/reports/";
        List<Vector<String>> datas = mergeFileExcelsUtil.merge(roomId);
        //Blank Document
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph p = document.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.createRun().setText("BIÊN BẢN CUỘC HỌP");

        document.createParagraph()
                .createRun()
                .setText("Thời gian bắt đầu: 28-11-2018 9:00:00");

        document.createParagraph()
                .createRun()
                .setText("Thời gian kết thúc: 28-11-2018 10:00:00");

        //Write the Document in file system
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(path_export + "report_" + roomId +".docx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create table
        XWPFTable table = document.createTable();
        //create first row
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Bắt đầu");
        tableRowOne.addNewTableCell().setText("Kết thúc");
        tableRowOne.addNewTableCell().setText("Người nói");
        tableRowOne.addNewTableCell().setText("Nội dung");

        for(Vector<String> data: datas){
            XWPFTableRow tableRow = table.createRow();
            tableRow.getCell(0).setText(data.get(0));
            tableRow.getCell(1).setText(data.get(1));
            tableRow.getCell(2).setText(data.get(4));
            tableRow.getCell(3).setText(data.get(2));
        }

        try {
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("create_table.docx written successully");
        return "report";
    }

}