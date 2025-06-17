package FXMLControllers.auxclasses;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

public class PDFUtils {
    
    
    //Crea un PDF simple (título y texto).
    public static void createSimplePdf(String pdfPath, String title, String body) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph(body));
        document.close();
    }

    
    //Crea un PDF con imagen (por ejemplo, un QR generado en runtime).
    public static void createPdfWithImage(String pdfPath, String title, BufferedImage qrImage, String footer) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("\n"));
        com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(qrImage, null);
        pdfImg.scalePercent(50); // Ajusta el tamaño si es necesario
        document.add(pdfImg);
        document.add(new Paragraph("\n"));
        if (footer != null && !footer.isEmpty()) {
            document.add(new Paragraph(footer));
        }
        document.close();
    }
}
