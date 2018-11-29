package com.gpch.login.controller;

import com.gpch.login.model.FileSave;
import com.gpch.login.model.User;
import com.gpch.login.service.FileService;
import com.gpch.login.utils.MergeFileExcelsUtil;
import com.gpch.login.utils.PdfGenerator;
import com.gpch.login.utils.ReadFileExcelUtil;
import com.itextpdf.text.DocumentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import reactor.ipc.netty.http.server.HttpServerResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/room")
public class UploadController {


    @Autowired
    MergeFileExcelsUtil mergeFileExcelsUtil;
    @Autowired
    PdfGenerator pdfGenerator;
    
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    FileService fileService;
    
    @Autowired
    ReadFileExcelUtil readFileExcelUtil;
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "";

    @GetMapping("/upload")
    public String index() {
        mergeFileExcelsUtil.merge(1);
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

    @RequestMapping(value = "/report_rom_1.pdf", method = RequestMethod.GET, produces="application/vnd.xls")
    public ResponseEntity<ByteArrayResource> genPDF(HttpServletRequest request, HttpServerResponse reponse){
        try {
            pdfGenerator.genPDF2(1);
            
            String rootPath = System.getProperty("user.dir");
            String path1 = rootPath + "/src/main/resources/reports/report_rom_1.pdf";
            String filenameDB = "report_rom_1.pdf";
            File f = new File(path1 + "");
			FileInputStream fi = new FileInputStream(f);
			byte[] bytes = new byte[(int) f.length()];
			fi.read(bytes);

//			reponse.addHeader("content-disposition", "attachment;filename="
//					+ filenameDB);
//			
			
			
			
			
			String mineType = servletContext.getMimeType(filenameDB);
			
				MediaType mediaType = MediaType.parseMediaType(mineType);
				
				Path path = Paths.get(path1);
					byte[] data = Files.readAllBytes(path);
					ByteArrayResource resource = new ByteArrayResource(data);

						return ResponseEntity.ok()
				// Content-Disposition
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + path.getFileName().toString())
					// Content-Type
							.contentType(mediaType) //
								// Content-Lengh
							.contentLength(data.length) //
								.body(resource);
		    //return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}