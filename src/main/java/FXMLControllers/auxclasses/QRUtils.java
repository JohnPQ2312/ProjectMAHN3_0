/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FXMLControllers.auxclasses;

import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class QRUtils {

    
    //Genera un QR en un archivo de imagen (PNG).
    //content Texto a codificar.
    //filePath Ruta donde guardar el PNG.
    //size Tamaño (píxeles).
    //Exception Si falla la generación o guardado.
    
    public static void generateQrToFile(String content, String filePath, int size) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);
        Path path = new File(filePath).toPath();
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    
    //Genera un QR y lo devuelve como BufferedImage (para usar en memoria).
    
    public static BufferedImage generateQrBufferedImage(String content, int size) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    
    //Lee el texto de un QR desde un archivo de imagen.
    
    public static String decodeQrFromFile(String filePath) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new File(filePath));
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}