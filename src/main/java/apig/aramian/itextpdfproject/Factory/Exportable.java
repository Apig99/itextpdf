package apig.aramian.itextpdfproject.Factory;

import com.itextpdf.text.*;

public interface Exportable {

    void export(Document document) throws DocumentException;
}
