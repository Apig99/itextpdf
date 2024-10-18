package apig.aramian.itextpdfproject;

import apig.aramian.itextpdfproject.ExportMetadata;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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

            processJsonData(data, document);

            document.close();

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", data.getOrDefault("fileName", "generic-data.pdf").toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    private void processJsonData(Map<String, Object> data, Document document) throws DocumentException {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (value) {
                case String s when s.startsWith("data:image/png;base64,") -> {
                    String base64Image = s.substring("data:image/png;base64,".length());
                    addImageFromBase64(base64Image, document);
                }
                case List list -> processList(key, list, document);
                case Map map -> {
                    Paragraph sectionTitle = new Paragraph(key + ":", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
                    document.add(sectionTitle);
                    processJsonData((Map<String, Object>) value, document);
                }
                case null, default -> {
                    Paragraph line = new Paragraph(key + ": " + (value != null ? value.toString() : "null"));
                    document.add(line);
                }
            }
            document.add(Chunk.NEWLINE);
        }
    }

    private void processList(String key, List<?> list, Document document) throws DocumentException {
        if (!list.isEmpty() && list.get(0) instanceof Map) {
            List<Map<String, Object>> tableData = (List<Map<String, Object>>) list;
            createTableFromListOfMaps(key, tableData, document);
        } else {
            Paragraph listTitle = new Paragraph(key + ": ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            document.add(listTitle);

            for (Object item : list) {
                if (item instanceof Map) {
                    processJsonData((Map<String, Object>) item, document);
                } else if (item instanceof List) {
                    processList("Nested List", (List<?>) item, document);
                } else {
                    Paragraph line = new Paragraph(item.toString());
                    document.add(line);
                }
            }
        }
    }

    private void createTableFromListOfMaps(String key, List<Map<String, Object>> tableData, Document document) throws DocumentException {
        if (tableData.isEmpty()) return;

        Map<String, Object> firstRow = tableData.get(0);
        PdfPTable table = new PdfPTable(firstRow.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        for (String column : firstRow.keySet()) {
            PdfPCell header = new PdfPCell();
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            header.setPhrase(new Phrase(column, headFont));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(header);
        }

        for (Map<String, Object> row : tableData) {
            for (String column : firstRow.keySet()) {
                Object cellValue = row.get(column);
                if (cellValue instanceof List) {
                    List<Map<String, Object>> nestedTableData = (List<Map<String, Object>>) cellValue;
                    PdfPTable nestedTable = createNestedTable(nestedTableData);
                    PdfPCell nestedCell = new PdfPCell(nestedTable);
                    table.addCell(nestedCell);
                } else {
                    table.addCell(cellValue != null ? cellValue.toString() : "");
                }
            }
        }
        document.add(table);
    }

    private PdfPTable createNestedTable(List<Map<String, Object>> nestedTableData) throws DocumentException {
        if (nestedTableData.isEmpty()) return null;

        Map<String, Object> firstRow = nestedTableData.get(0);
        PdfPTable nestedTable = new PdfPTable(firstRow.size());
        nestedTable.setWidthPercentage(100);

        for (String column : firstRow.keySet()) {
            PdfPCell header = new PdfPCell();
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            header.setPhrase(new Phrase(column, headFont));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            nestedTable.addCell(header);
        }

        for (Map<String, Object> row : nestedTableData) {
            for (String column : firstRow.keySet()) {
                Object cellValue = row.get(column);
                if (cellValue instanceof List) {
                    List<Map<String, Object>> deeperNestedTableData = (List<Map<String, Object>>) cellValue;
                    PdfPTable deeperNestedTable = createNestedTable(deeperNestedTableData);
                    PdfPCell deeperNestedCell = new PdfPCell(deeperNestedTable);
                    nestedTable.addCell(deeperNestedCell);
                } else {
                    nestedTable.addCell(cellValue != null ? cellValue.toString() : "");
                }
            }
        }

        return nestedTable;
    }

    private void addImageFromBase64(String base64Image, Document document) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            Image img = Image.getInstance(bis.readAllBytes());

            img.scaleToFit(200, 200);
            document.add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
