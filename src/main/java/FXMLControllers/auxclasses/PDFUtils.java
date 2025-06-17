package FXMLControllers.auxclasses;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;

public class PDFUtils {
    
    
    //Creates a simple PDF with a title and body text.
    public static void createSimplePdf(String pdfPath, String title, String body) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph(body));
        document.close();
    }

    
    //Creates a PDF with a title, an embedded image (e.g., a QR code), and an optional footer.
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
    
    
    //Creates a generic PDF report with a data table.
    public static void createTableReportPdf(
            String pdfPath,
            String title,
            String subtitle,
            String[] headers,
            List<String[]> rows,
            String footer
    ) throws Exception {
        // Landscape orientation for wide tables
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();

        // Main title
        Paragraph titlePara = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        // Subtitle or filter description
        if (subtitle != null && !subtitle.isEmpty()) {
            Paragraph subPara = new Paragraph(subtitle, FontFactory.getFont(FontFactory.HELVETICA, 12));
            subPara.setAlignment(Element.ALIGN_CENTER);
            document.add(subPara);
        }
        document.add(new Paragraph("\n"));

        // Data table
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        // Header cells with grey background
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            cell.setBackgroundColor(new Color(220, 220, 220));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Data rows
        for (String[] row : rows) {
            for (String value : row) {
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", FontFactory.getFont(FontFactory.HELVETICA, 11)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }

        document.add(table);

        // Footer, for totals or additional info
        if (footer != null && !footer.isEmpty()) {
            document.add(new Paragraph("\n"));
            Paragraph footPara = new Paragraph(footer, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11));
            footPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(footPara);
        }

        document.close();
    }
}
