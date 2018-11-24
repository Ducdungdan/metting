package com.gpch.login.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import static com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer.UTF_8;


@Component
public class PdfGenerator {
    @Autowired
    ServletContext context;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MergeFileExcelsUtil mergeFileExcelsUtil;
    public void createPdf(String templateName, Map map) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        Context ctx = new Context();
        if (map != null) {
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String processedHtml = templateEngine.process(templateName, ctx);
        FileOutputStream os = null;
        String fileName = UUID.randomUUID().toString();
        String rootPath = System.getProperty("user.dir");
        try {
            final File outputFile = new File(rootPath + "/src/main/resources/reports/" + fileName + ".pdf");
            os = new FileOutputStream(outputFile);

            String xHtml = convertToXhtml(processedHtml);

            ITextRenderer renderer = new ITextRenderer();

//            renderer.getFontResolver().addFont("/Users/khactu/Desktop/time.ttf", IDENTITY_H, EMBEDDED);
//            renderer.getFontResolver().addFont("/Users/khactu/Desktop/time.ttf",
//                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            String baseUrl = FileSystems
                    .getDefault()
                    .getPath("src", "main", "resources", "static")
                    .toUri()
                    .toURL()
                    .toString();

            renderer.setDocumentFromString(processedHtml, baseUrl);
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
            System.out.println("PDF created successfully");
            System.out.println(outputFile.getAbsolutePath());
            System.out.println(rootPath);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }
    }

    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }

    public void genPDF2(int roomId) throws IOException, DocumentException {

        java.util.List<Vector<String>> datas = mergeFileExcelsUtil.merge(roomId);

        String rootPath = System.getProperty("user.dir");
        String path = rootPath + "/src/main/resources/reports/";
        Document document = new Document();
        PdfPTable table = new PdfPTable(new float[] { 1, 1, 2, 4 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("Start");
        table.addCell("End");
        table.addCell("Speaker");
        table.addCell("Content");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i=0;i<datas.size();i++){
            Vector<String> v = datas.get(i);
            table.addCell(v.get(0));
            table.addCell(v.get(1));
            table.addCell(v.get(2));
            table.addCell(v.get(3));
        }
        PdfWriter.getInstance(document, new FileOutputStream(path + "report_rom_" + roomId + ".pdf"));
        document.open();
        Paragraph paragraph = new Paragraph("BIEN BAN CUOC HOP");
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(10f);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);
        document.add(table);
        document.close();
        System.out.println("Done");
    }
}
