package FXMLControllers.client;

import FXMLControllers.auxclasses.QRUtils;
import FXMLControllers.auxclasses.RoomImageLoader;
import PersistenceClasses.RoomImages;
import PersistenceClasses.Rooms;
import PersistenceClasses.Reviews;
import PersistenceClasses.Users;
import PersistenceCRUD.RoomImagesCRUD;
import PersistenceCRUD.ReviewsCRUD;
import PersistenceCRUD.RoomsCRUD;
import PersistenceClasses.Entries;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ReviewScreenController implements Initializable {

    @FXML
    private Label dateLabel;
    @FXML
    private Button scanQRButton;
    @FXML
    private ImageView mainImageView;
    @FXML
    private Label speciesLabel;
    @FXML
    private Button prevImageButton;
    @FXML
    private Button nextImageButton;
    @FXML
    private Label roomNameLabel;
    @FXML
    private Label detailLabel;
    @FXML
    private Label avgRatingLabel;
    @FXML
    private Button star1, star2, star3, star4, star5;
    @FXML
    private TextArea reviewTextArea;
    @FXML
    private Button submitReviewButton;
    @FXML
    private Label statusLabel;

    private Rooms currentRoom;
    private List<RoomImages> imagesList = new ArrayList<>();
    private int imageIndex = 0;
    private int selectedStars = 0;
    private final ReviewsCRUD reviewsCRUD = new ReviewsCRUD();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dateLabel.setText(LocalDate.now().toString());
        setStarListeners();
        setImageNavigation();
        resetUI();
    }

    /**
     * Resets the interface to its initial (blank) state. This is called on
     * startup and whenever a new room is loaded.
     */
    private void resetUI() {
        currentRoom = null;
        imagesList.clear();
        imageIndex = 0;
        selectedStars = 0;
        setStarsUI(0);
        mainImageView.setImage(null);
        roomNameLabel.setText("-");
        detailLabel.setText("-");
        speciesLabel.setText("ESPECIES / TEMÁTICA");
        avgRatingLabel.setText("-");
        reviewTextArea.setText("");
        statusLabel.setText("");
        prevImageButton.setDisable(true);
        nextImageButton.setDisable(true);
        submitReviewButton.setDisable(true);
    }

    /**
     * Called when the user clicks the "Scan QR" button. Prompts the user to
     * select a PDF, decodes the QR, and loads room info.
     */
    @FXML
    private void handleScanQRButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showOpenDialog(scanQRButton.getScene().getWindow());

        if (file == null) {
            statusLabel.setText("No se seleccionó archivo.");
            resetUI();
            return;
        }

        statusLabel.setText("Procesando QR...");

        String entryQrCode;
        try {
            entryQrCode = QRUtils.decodeQrFromFile(file);
        } catch (Exception ex) {
            statusLabel.setText("No se pudo leer el QR del PDF. ¿Es el archivo correcto?");
            resetUI();
            return;
        }

        // Search entry by QR
        Entries entry = new PersistenceCRUD.EntriesCRUD().getEntryByQrCode(entryQrCode);

        if (entry == null) {
            statusLabel.setText("Entrada no encontrada o QR inválido.");
            resetUI();
            return;
        }

        // Get room associated to entry
        Rooms room = entry.getRooms();
        if (room == null) {
            statusLabel.setText("No se encontró la sala asociada a la entrada.");
            resetUI();
            return;
        }

        loadRoomInfo(room);
    }

    /**
     * Populates the UI with the room's information, images, and average rating.
     * Enables the review submission section.
     */
    private void loadRoomInfo(Rooms room) {
        this.currentRoom = room;
        roomNameLabel.setText(room.getName());
        detailLabel.setText(room.getDescription() != null ? room.getDescription() : "-");
        speciesLabel.setText("ESPECIES / TEMÁTICA");

        // Load room images (filtered to this room)
        imagesList = new RoomImagesCRUD().getAllRoomImages();
        imagesList.removeIf(img -> !img.getRooms().getId().equals(room.getId()));
        imageIndex = 0;
        showCurrentImage();

        prevImageButton.setDisable(imagesList.size() <= 1);
        nextImageButton.setDisable(imagesList.size() <= 1);

        updateAvgRating();
        submitReviewButton.setDisable(false);
        statusLabel.setText("Sala cargada. Puedes valorar y comentar.");
    }

    /**
     * Displays the currently selected image for the room. Shows a placeholder
     * if there are no images.
     */
    private void showCurrentImage() {
        if (imagesList.isEmpty()) {
            mainImageView.setImage(RoomImageLoader.loadRoomImage(null));
        } else {
            mainImageView.setImage(RoomImageLoader.loadRoomImage(imagesList.get(imageIndex)));
        }
    }

    /**
     * Sets up navigation (previous/next) for the room images.
     */
    private void setImageNavigation() {
        prevImageButton.setOnAction(e -> {
            if (imagesList.size() > 1) {
                imageIndex = (imageIndex - 1 + imagesList.size()) % imagesList.size();
                showCurrentImage();
            }
        });
        nextImageButton.setOnAction(e -> {
            if (imagesList.size() > 1) {
                imageIndex = (imageIndex + 1) % imagesList.size();
                showCurrentImage();
            }
        });
    }

    /**
     * Sets event listeners for the star rating buttons. Allows the user to
     * visually select 1-5 stars.
     */
    private void setStarListeners() {
        star1.setOnAction(e -> selectStars(1));
        star2.setOnAction(e -> selectStars(2));
        star3.setOnAction(e -> selectStars(3));
        star4.setOnAction(e -> selectStars(4));
        star5.setOnAction(e -> selectStars(5));
    }

    //Records and displays the number of stars selected by the user.
    private void selectStars(int num) {
        selectedStars = num;
        setStarsUI(num);
    }

    //Updates the visual appearance of the star buttons.
    private void setStarsUI(int num) {
        Button[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < 5; i++) {
            stars[i].setStyle(i < num ? "-fx-font-size: 20px; -fx-text-fill: gold;" : "-fx-font-size: 20px;");
        }
    }

    //Submit review method
    @FXML
    private void handleSubmitReviewButton() {
        if (currentRoom == null) {
            statusLabel.setText("Primero selecciona una sala escaneando el QR.");
            return;
        }
        if (selectedStars < 1 || selectedStars > 5) {
            statusLabel.setText("Selecciona una valoración (1 a 5 estrellas).");
            return;
        }
        String comment = reviewTextArea.getText();
        if (comment.length() > 300) {
            statusLabel.setText("El comentario no puede exceder 300 caracteres.");
            return;
        }
        // Retrieve the current user from session
        Users currentUser = FXMLControllers.auxclasses.UserSession.getCurrentUser();
        if (currentUser == null) {
            statusLabel.setText("Sesión expirada. Inicia sesión nuevamente.");
            return;
        }
        //Only allow one review per user-room
        List<Reviews> existing = reviewsCRUD.getAllReviews();
        boolean alreadyReviewed = existing.stream()
                .anyMatch(r -> r.getRooms().getId().equals(currentRoom.getId())
                && r.getUsers().getId().equals(currentUser.getId()));
        if (alreadyReviewed) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Valoración ya realizada");
            alert.setHeaderText("Ya has valorado esta sala");
            alert.setContentText("Solo se permite una valoración por sala y usuario.\n¡Gracias por tu interés en mejorar el museo!");
            alert.showAndWait();
            statusLabel.setText("Ya valoraste esta sala.");
            return;
        }

        //Alert for submit confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar envío");
        confirm.setHeaderText("¿Seguro que deseas enviar tu valoración?");
        confirm.setContentText("No podrás modificarla más adelante.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            statusLabel.setText("Envío cancelado por el usuario.");
            return;
        }

        // Construct and persist the review
        Reviews review = new Reviews();
        review.setRooms(currentRoom);
        review.setUsers(currentUser);
        review.setRating((short) selectedStars);
        review.setUserComment(comment);
        review.setReviewDate(new java.util.Date());

        try {
            reviewsCRUD.addReview(review);
            statusLabel.setText("¡Gracias por tu valoración!");
            updateAvgRating();
            reviewTextArea.setText("");
            setStarsUI(0);
            selectedStars = 0;
        } catch (Exception ex) {
            statusLabel.setText("Error al guardar valoración: " + ex.getMessage());
        }

        resetUI();
    }

    /**
     * Calculates and updates the average rating for the current room. Displays
     * both the numeric average and the visual representation.
     */
    private void updateAvgRating() {
        List<Reviews> allReviews = reviewsCRUD.getAllReviews();
        List<Reviews> roomReviews = new ArrayList<>();
        for (Reviews r : allReviews) {
            if (r.getRooms() != null && currentRoom != null
                    && r.getRooms().getId().equals(currentRoom.getId())) {
                roomReviews.add(r);
            }
        }
        if (roomReviews.isEmpty()) {
            avgRatingLabel.setText("Sin valoraciones");
            return;
        }
        double avg = roomReviews.stream()
                .mapToInt(r -> r.getRating() != null ? r.getRating() : 0)
                .average().orElse(0.0);
        avgRatingLabel.setText(String.format("%.2f / 5", avg));
    }
}
