package apig.aramian.itextpdfproject.Factory;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.List;
import java.util.Map;

public class ListExportable implements Exportable {
    private List<Object> list;

    public ListExportable(List<Object> list) {
        this.list = list;
    }

    @Override
    public void export(Document document) throws DocumentException {
        if (!list.isEmpty() && list.get(0) instanceof Map) {
            createTableFromListOfMaps(list, document);
        } else {
            for (Object item : list) {
                Exportable exportable = ExportableFactory.createExportable(item);
                exportable.export(document);
            }
        }
    }

    private void createTableFromListOfMaps(List<?> tableData, Document document) throws DocumentException {
        if (tableData.isEmpty()) return;

        Map<String, Object> firstRow = (Map<String, Object>) tableData.get(0);
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

        for (Object rowObj : tableData) {
            Map<String, Object> row = (Map<String, Object>) rowObj;
            for (String column : firstRow.keySet()) {
                Object cellValue = row.get(column);
                Exportable exportable = ExportableFactory.createExportable(cellValue);
                PdfPCell cell = new PdfPCell();
                if (cellValue instanceof List) {
                    PdfPTable nestedTable = createNestedTable((List<Map<String, Object>>) cellValue);
                    cell = new PdfPCell(nestedTable);
                } else {
                    cell.setPhrase(new Phrase(cellValue != null ? cellValue.toString() : ""));
                }
                table.addCell(cell);
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
                nestedTable.addCell(cellValue != null ? cellValue.toString() : "");
            }
        }
        return nestedTable;
    }
}
