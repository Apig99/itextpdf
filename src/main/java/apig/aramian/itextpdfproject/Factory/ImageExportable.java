package apig.aramian.itextpdfproject.Factory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;

import java.io.ByteArrayInputStream;
import java.util.Base64;

class ImageExportable implements Exportable {
    private String base64Image;

    public ImageExportable(String base64Image) {
        this.base64Image = base64Image;
    }

    @Override
    public void export(Document document) throws DocumentException {
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
