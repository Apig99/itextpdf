package apig.aramian.itextpdfproject.Factory;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;

import java.util.Map;

public class MapExportable implements Exportable {
    private Map<String, Object> map;

    public MapExportable(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public void export(Document document) throws DocumentException {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Exportable exportable = ExportableFactory.createExportable(entry.getValue());
            document.add(new Paragraph(entry.getKey() + ":"));
            exportable.export(document);
            document.add(Chunk.NEWLINE);
        }
    }

}

