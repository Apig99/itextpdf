package apig.aramian.itextpdfproject.controller;

import apig.aramian.itextpdfproject.Factory.Exportable;
import apig.aramian.itextpdfproject.Factory.ExportableFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/")
public class ItextPDFController {


    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadGenericPdf(@RequestBody ExportMetadata data) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            if (data.containsKey("title")) {
                Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Paragraph title = new Paragraph(data.get("title").toString(), font);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(Chunk.NEWLINE);
            }

            Exportable exportableData = ExportableFactory.createExportable(data);
            exportableData.export(document);

            document.close();

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", data.getOrDefault("fileName", "generic-data.pdf").toString());

            return ResponseEntity.ok().headers(headers).body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }



}
