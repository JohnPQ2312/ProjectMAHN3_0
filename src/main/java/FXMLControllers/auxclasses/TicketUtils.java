package FXMLControllers.auxclasses;

import PersistenceClasses.Entries;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility class for generating museum ticket PDFs with QR codes.
 * Integrates with QRUtils and PDFUtils for ticket creation.
 */
public class TicketUtils {

    /**
     * Generates a museum ticket as a PDF file, including a QR code and ticket details.
     *
     * @param pdfPath    The output PDF file path
     * @param ticketCode The unique code for the ticket / QR
     * @param userName   The name of the ticket holder
     * @param museum     The museum name
     * @param room       The room name
     * @param date       The visit date as String
     * @param price      The ticket price
     * @throws Exception if PDF or QR generation fails
     */
    public static void generateTicketPdfWithQr(
            String pdfPath,
            String ticketCode,
            String userName,
            String museum,
            String room,
            String date,
            double price
    ) throws Exception {
        // 1. Generate QR image in memory
        BufferedImage qrImage = QRUtils.generateQrBufferedImage(ticketCode, 250);

        // 2. Prepare the ticket body text
        String body = "Name: " + userName + "\n"
                + "Museum: " + museum + "\n"
                + "Room: " + room + "\n"
                + "Visit Date: " + date + "\n"
                + "Price: " + String.format("%.2f", price) + " euros\n"
                + "Ticket Code: " + ticketCode;

        // 3. Create the PDF with QR and body
        PDFUtils.createPdfWithImage(pdfPath, "Museum Entry - MAHN 3.0", qrImage, body);
    }

    /**
     * Generates a museum ticket PDF for a given entry and file.
     *
     * @param entry The entry object containing all ticket information
     * @param file  The destination PDF file
     * @throws Exception if generation fails
     */
    public static void generateTicketPdf(Entries entry, File file) throws Exception {
        String qrCode = entry.getQrCode();
        String userName = entry.getPurchases().getUsers().getName();
        String museum = entry.getRooms().getMuseums().getName();
        String room = entry.getRooms().getName();
        String date = entry.getVisitDate().toString();
        double price = entry.getPrice().doubleValue();

        generateTicketPdfWithQr(
                file.getAbsolutePath(),
                qrCode,
                userName,
                museum,
                room,
                date,
                price
        );
    }
}