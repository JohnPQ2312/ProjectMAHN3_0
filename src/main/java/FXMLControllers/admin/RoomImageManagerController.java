package FXMLControllers.admin;

import FXMLControllers.auxclasses.RoomImagesHelper;
import FXMLControllers.auxclasses.RoomImageLoader;
import PersistenceCRUD.RoomImagesCRUD;
import PersistenceCRUD.RoomsCRUD;
import PersistenceClasses.RoomImages;
import PersistenceClasses.Rooms;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for managing images associated with rooms.
 * Handles CRUD operations, filtering, previewing images, and file handling.
 */
public class RoomImageManagerController implements Initializable {

    @FXML private TableView<RoomImages> imageTable;
    @FXML private TableColumn<RoomImages, String> pathColumn;
    @FXML private TableColumn<RoomImages, String> descriptionColumn;
    @FXML private TableColumn<RoomImages, String> roomColumn;
    @FXML private ComboBox<Rooms> roomFilterComboBox;
    @FXML private Button clearFiltersBtn;
    @FXML private TextField editPath, editDescription;
    @FXML private ComboBox<Rooms> editRoomComboBox;
    @FXML private ImageView previewImage;
    @FXML private Button browseBtn, editBtn, eraseBtn, saveChangesBtn, addNewBtn, saveNewBtn;

    private final RoomImagesCRUD imagesCRUD = new RoomImagesCRUD();
    private final RoomsCRUD roomsCRUD = new RoomsCRUD();
    private ObservableList<RoomImages> masterList;
    private FilteredList<RoomImages> filteredData;
    private ObservableList<Rooms> roomsList;
    private File selectedImageFile = null; // Stores the file to be used for new image uploads

    /**
     * Initializes the controller, table columns, listeners, and disables editing.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind table columns to RoomImages properties
        pathColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getImagePath()));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        roomColumn.setCellValueFactory(cell -> {
            Rooms room = cell.getValue().getRooms();
            return new SimpleStringProperty(room != null ? room.getName() : "Sin sala");
        });

        // Disable editing fields/buttons at startup (future-proof for UI flows)
        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadRoomsCombo();
        loadFilteredImages();
        showDefaultPreview();

        // Table selection listener for enabling edit/delete and showing preview
        imageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editBtn.setDisable(false);
                eraseBtn.setDisable(false);
                populateEditFields(newVal);
                disableAllEditFields();
                showPreview(newVal);
            } else {
                editBtn.setDisable(true);
                eraseBtn.setDisable(true);
                clearEditFields();
                showDefaultPreview();
            }
        });

        // Room filter combo box listener
        roomFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    /**
     * Loads all available rooms into combo boxes.
     */
    private void loadRoomsCombo() {
        roomsList = FXCollections.observableArrayList(roomsCRUD.getAllRooms());
        roomFilterComboBox.setItems(roomsList);
        editRoomComboBox.setItems(roomsList);
    }

    /**
     * Loads images from the database, sets up filtering and sorting.
     */
    private void loadFilteredImages() {
        masterList = FXCollections.observableArrayList(imagesCRUD.getAllRoomImages());
        filteredData = new FilteredList<>(masterList, p -> true);

        SortedList<RoomImages> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(imageTable.comparatorProperty());
        imageTable.setItems(sortedList);
    }

    /**
     * Applies room filter to the image list.
     */
    private void applyFilter() {
        Rooms selectedRoom = roomFilterComboBox.getValue();
        filteredData.setPredicate(img -> selectedRoom == null ||
                (img.getRooms() != null && img.getRooms().getId().compareTo(selectedRoom.getId()) == 0));
    }

    /**
     * Clears the room filter.
     */
    @FXML
    private void clearFiltersBtnAction() {
        roomFilterComboBox.setValue(null);
    }

    /**
     * Populates the edit fields with the selected image's data.
     */
    private void populateEditFields(RoomImages img) {
        editPath.setText(img.getImagePath());
        editDescription.setText(img.getDescription());
        editRoomComboBox.setValue(img.getRooms());
        selectedImageFile = null;
    }

    /**
     * Clears all edit fields and resets selected image file.
     */
    private void clearEditFields() {
        editPath.clear();
        editDescription.clear();
        editRoomComboBox.setValue(null);
        selectedImageFile = null;
    }

    /**
     * Disables all edit fields and buttons (except add/clear).
     */
    private void disableAllEditFields() {
        editPath.setDisable(true);
        editDescription.setDisable(true);
        editRoomComboBox.setDisable(true);
        browseBtn.setDisable(true);
    }

    /**
     * Enables all edit fields and browse button except for path (auto-filled).
     */
    private void enableAllEditFields() {
        editDescription.setDisable(false);
        editRoomComboBox.setDisable(false);
        browseBtn.setDisable(false);
        // editPath remains disabled (auto-filled only)
    }

    /**
     * Shows image preview for the selected RoomImages record.
     */
    private void showPreview(RoomImages img) {
        if (img.getImagePath() != null) {
            Image image = RoomImageLoader.loadRoomImage(img);
            previewImage.setImage(image);
        } else {
            showDefaultPreview();
        }
    }

    /**
     * Shows the default (placeholder) image in the preview.
     */
    private void showDefaultPreview() {
        previewImage.setImage(RoomImagesHelper.getPlaceholderImage());
    }

    /**
     * Handler for the "Add New" button. Prepares the form for new image entry.
     */
    @FXML
    private void addNewBtnAction() {
        imageTable.setDisable(true);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);

        enableAllEditFields();
        clearEditFields();

        saveNewBtn.setDisable(false);
        addNewBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        showDefaultPreview();
    }

    /**
     * Handler for the "Edit" button. Enables fields for editing the selected image.
     */
    @FXML
    private void editBtnAction() {
        RoomImages selected = imageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        imageTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    /**
     * Handler for the "Browse" button to select an image file.
     */
    @FXML
    private void handleBrowseImage() {
        File file = RoomImagesHelper.chooseImageFile();
        if (file != null) {
            selectedImageFile = file;
            editPath.setText(file.getAbsolutePath()); // Display path only for user info
            previewImage.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Handler for "Save New" button. Validates and saves a new image record.
     */
    @FXML
    private void saveNewBtnAction() {
        if (selectedImageFile == null) {
            showAlert("Debes seleccionar una imagen.");
            return;
        }
        if (editRoomComboBox.getValue() == null) {
            showAlert("Debes seleccionar una sala.");
            return;
        }

        String copiedPath = RoomImagesHelper.copyImageToAppFolder(selectedImageFile);
        if (copiedPath == null) {
            showAlert("No se pudo copiar la imagen.");
            return;
        }

        RoomImages img = new RoomImages();
        img.setImagePath(copiedPath);
        img.setDescription(editDescription.getText().trim());
        img.setRooms(editRoomComboBox.getValue());

        imagesCRUD.addRoomImage(img);

        loadFilteredImages();
        imageTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        imageTable.setDisable(false);
        showDefaultPreview();
    }

    /**
     * Handler for "Save Changes" button. Updates the selected image record.
     */
    @FXML
    private void saveChangesBtnAction() {
        RoomImages selected = imageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (editRoomComboBox.getValue() == null) {
            showAlert("Debes seleccionar una sala.");
            return;
        }

        // If a new image file is selected, copy it and update the path
        if (selectedImageFile != null) {
            String copiedPath = RoomImagesHelper.copyImageToAppFolder(selectedImageFile);
            if (copiedPath == null) {
                showAlert("No se pudo copiar la imagen.");
                return;
            }
            selected.setImagePath(copiedPath);
        }

        selected.setDescription(editDescription.getText().trim());
        selected.setRooms(editRoomComboBox.getValue());

        imagesCRUD.updateRoomImage(selected);

        loadFilteredImages();
        imageTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        imageTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
        showDefaultPreview();
    }

    /**
     * Handler for "Erase" button. Deletes the selected image record.
     */
    @FXML
    private void eraseBtnAction() {
        RoomImages selected = imageTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        imagesCRUD.deleteRoomImage(selected.getId());

        loadFilteredImages();
        imageTable.refresh();

        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
        showDefaultPreview();
    }

    /**
     * Utility to show information alerts.
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}