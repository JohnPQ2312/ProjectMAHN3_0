package FXMLControllers.auxclasses;

import PersistenceClasses.RoomImages;
import javafx.scene.image.Image;
import java.io.File;

/**
 * Utility class for loading images associated with RoomImages.
 * Always returns a valid Image object, using a placeholder if needed.
 */
public class RoomImageLoader {

    private static final String DEFAULT_ROOM_IMAGE = "/images/placeholder.jpg";

    /**
     * Loads an image from the file system or returns a default placeholder if not found.
     *
     * @param roomImage The RoomImages entity with the image file path
     * @return The Image for display in the UI
     */
    public static Image loadRoomImage(RoomImages roomImage) {
        try {
            String path = roomImage.getImagePath();
            if (path != null && new File(path).exists()) {
                return new Image(new File(path).toURI().toString());
            } else {
                return new Image(RoomImageLoader.class.getResourceAsStream(DEFAULT_ROOM_IMAGE));
            }
        } catch (Exception e) {
            // Future-proof: always fallback to placeholder
            return new Image(RoomImageLoader.class.getResourceAsStream(DEFAULT_ROOM_IMAGE));
        }
    }
}