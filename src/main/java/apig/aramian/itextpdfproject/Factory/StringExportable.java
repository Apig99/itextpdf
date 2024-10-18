package apig.aramian.itextpdfproject.Factory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;

public class StringExportable implements Exportable {
    private String value;

    public StringExportable(String value) {
        this.value = value;
    }

    @Override
    public void export(Document document) throws DocumentException {
        document.add(new Paragraph(value));
    }
}
