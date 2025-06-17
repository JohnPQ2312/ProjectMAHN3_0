package FXMLControllers.auxclasses;

import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class QRUtils {

    /**
     * Generates a QR code and saves it as a PNG image file.
     *
     * @param content  The text to encode in the QR code
     * @param filePath The output file path (PNG)
     * @param size     The width and height in pixels
     */
    public static void generateQrToFile(String content, String filePath, int size) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);
        Path path = new File(filePath).toPath();
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * Generates a QR code and returns it as a BufferedImage (for in-memory use).
     *
     * @param content The text to encode
     * @param size    The image size (pixels)
     * @return BufferedImage with the QR code
     */
    public static BufferedImage generateQrBufferedImage(String content, int size) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Reads a QR code from the first page of a PDF file and returns the decoded text.
     *
     * @param file The PDF file to read from
     * @return The decoded text, or throws exception if not found
     */
    public static String decodeQrFromFile(File file) throws Exception {
        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        }
    }
}