package FXMLControllers.auxclasses;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


//Clase auxiliar para seleccionar y copiar imágenes asociadas a RoomImages.
public class RoomImagesHelper {

    private static final String IMAGES_FOLDER = "images/rooms/";
    private static final String PLACEHOLDER_RESOURCE = "/images/placeholder.jpg";


    //Abre un FileChooser para seleccionar una imagen y devuelve el archivo.
    public static File chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de sala");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(null);
    }


    //Copia el archivo seleccionado a la carpeta de imágenes de la app, devolviendo la ruta.
    public static String copyImageToAppFolder(File sourceFile) {
        try {
            File destDir = new File(IMAGES_FOLDER);
            if (!destDir.exists()) destDir.mkdirs();

            String uniqueName = UUID.randomUUID() + "_" + sourceFile.getName();
            File destFile = new File(destDir, uniqueName);

            try (FileInputStream in = new FileInputStream(sourceFile);
                 FileOutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = in.read(buffer)) > 0) out.write(buffer, 0, bytes);
            }

            return destFile.getPath();
        } catch (IOException e) {
            showError("No se pudo copiar la imagen: " + e.getMessage());
            return null;
        }
    }

    //Devuelve una imagen de placeholder para vistas previas.
    public static Image getPlaceholderImage() {
        return new Image(RoomImagesHelper.class.getResourceAsStream(PLACEHOLDER_RESOURCE));
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error de imagen");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}