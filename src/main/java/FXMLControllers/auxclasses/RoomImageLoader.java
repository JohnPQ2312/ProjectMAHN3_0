package FXMLControllers.auxclasses;

import PersistenceClasses.RoomImages;
import javafx.scene.image.Image;
import java.io.File;

// Clase utilitaria para cargar imágenes asociadas a RoomImages
public class RoomImageLoader {

    private static final String DEFAULT_ROOM_IMAGE = "/images/placeholder.jpg";
    
    public static Image loadRoomImage(RoomImages roomImage) {
        try {
            String path = roomImage.getImagePath();
            if (path != null && new File(path).exists()) {
                return new Image(new File(path).toURI().toString());
            } else {
                return new Image(RoomImageLoader.class.getResourceAsStream(DEFAULT_ROOM_IMAGE));
            }
        } catch (Exception e) {
            return new Image(RoomImageLoader.class.getResourceAsStream(DEFAULT_ROOM_IMAGE));
        }
    }
}