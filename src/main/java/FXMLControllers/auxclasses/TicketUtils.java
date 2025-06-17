package FXMLControllers.auxclasses;

import PersistenceClasses.Entries;
import java.awt.image.BufferedImage;
import java.io.File;

public class TicketUtils {

    //Genera un ticket PDF con QR para una entrada de museo.
    public static void generateTicketPdfWithQr(
            String pdfPath,
            String ticketCode,
            String userName,
            String museum,
            String room,
            String date,
            double price
    ) throws Exception {
        // 1. Generar el QR en memoria
        BufferedImage qrImage = QRUtils.generateQrBufferedImage(ticketCode, 250);

        // 2. Preparar el cuerpo del ticket
        String body = "Nombre: " + userName + "\n"
                + "Museo: " + museum + "\n"
                + "Sala: " + room + "\n"
                + "Fecha visita: " + date + "\n"
                + "Precio: " + String.format("%.2f", price) + " euros\n"
                + "Código de ticket: " + ticketCode;

        // 3. Crear el PDF con QR y cuerpo
        PDFUtils.createPdfWithImage(pdfPath, "Entrada Museo - MAHN 3.0", qrImage, body);
    }

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
