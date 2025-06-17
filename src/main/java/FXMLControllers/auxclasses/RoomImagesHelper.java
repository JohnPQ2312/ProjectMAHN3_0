package FXMLControllers.auxclasses;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Utility class for selecting, copying, and previewing images for RoomImages.
 * Handles file dialogs, storage, and placeholder images.
 */
public class RoomImagesHelper {

    private static final String IMAGES_FOLDER = "images/rooms/";
    private static final String PLACEHOLDER_RESOURCE = "/images/placeholder.jpg";

    /**
     * Opens a file chooser dialog for the user to select an image file.
     *
     * @return The selected File object, or null if cancelled
     */
    public static File chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Room Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(null);
    }

    /**
     * Copies the selected image file to the application's image folder, using a unique name.
     *
     * @param sourceFile The original image file
     * @return The destination file path, or null if copy fails
     */
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
            showError("Could not copy image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a placeholder image for use in previews or when no image is available.
     *
     * @return The placeholder Image object
     */
    public static Image getPlaceholderImage() {
        return new Image(RoomImagesHelper.class.getResourceAsStream(PLACEHOLDER_RESOURCE));
    }

    /**
     * Shows an error alert with the given message.
     *
     * @param msg The error message to display
     */
    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Image Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}