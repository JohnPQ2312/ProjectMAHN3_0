package FXMLControllers.admin;

import PersistenceCRUD.CollectionsCRUD;
import PersistenceClasses.Collections;
import PersistenceClasses.Rooms;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for managing collections (colecciones) in the admin interface.
 * Allows add, edit, delete, and filter operations on collections.
 */
public class CollectionManagerController implements Initializable {

    // Table and columns
    @FXML
    private TableView<Collections> collectionsTable;

    @FXML
    private TableColumn<Collections, String> nameColumn;
    @FXML
    private TableColumn<Collections, String> descriptionColumn;
    @FXML
    private TableColumn<Collections, String> centuryColumn;
    @FXML
    private TableColumn<Collections, String> roomColumn;
    @FXML
    private TableColumn<Collections, BigDecimal> idColumn;

    // Edit and filter fields
    @FXML
    private TextField editName, editDescription, editCentury, filterTextField;
    @FXML
    private ComboBox<Rooms> editRoom, roomFilterComboBox;

    // Action buttons
    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final CollectionsCRUD collectionsCRUD = new CollectionsCRUD();
    private final PersistenceCRUD.RoomsCRUD roomsCRUD = new PersistenceCRUD.RoomsCRUD();

    private ObservableList<Collections> masterList;
    private FilteredList<Collections> filteredData;
    private ObservableList<Rooms> roomsList;

    /**
     * Initializes the controller and sets up the UI bindings and listeners.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up column cell factories
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        centuryColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCentury()));
        roomColumn.setCellValueFactory(cell -> {
            Rooms room = cell.getValue().getRooms();
            return new SimpleStringProperty(room != null ? room.getName() : "Sin sala");
        });
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Initial UI state
        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadRoomsCombo();
        loadFilteredCollections();

        // Table selection listener for enabling edit/delete
        collectionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editBtn.setDisable(false);
                eraseBtn.setDisable(false);
                populateEditFields(newVal);
                disableAllEditFields();
            } else {
                editBtn.setDisable(true);
                eraseBtn.setDisable(true);
            }
        });
    }

    /**
     * Loads available rooms into the combo boxes.
     */
    private void loadRoomsCombo() {
        roomsList = FXCollections.observableArrayList(roomsCRUD.getAllRooms());
        roomFilterComboBox.setItems(roomsList);
        editRoom.setItems(roomsList);
    }

    /**
     * Loads collections and sets up filtering and sorting.
     */
    private void loadFilteredCollections() {
        masterList = FXCollections.observableArrayList(collectionsCRUD.getAllCollections());
        filteredData = new FilteredList<>(masterList, p -> true);

        // Filter listeners
        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        roomFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<Collections> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(collectionsTable.comparatorProperty());
        collectionsTable.setItems(sortedList);
    }

    /**
     * Applies filters on the collection list based on text and room.
     */
    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        Rooms selectedRoom = roomFilterComboBox.getValue();

        filteredData.setPredicate(collection -> {
            boolean matchesText = collection.getName().toLowerCase().contains(text)
                || (collection.getCentury() != null && collection.getCentury().toLowerCase().contains(text));
            boolean matchesRoom = (selectedRoom == null) ||
                (collection.getRooms() != null && collection.getRooms().getId().compareTo(selectedRoom.getId()) == 0);
            return matchesText && matchesRoom;
        });
    }

    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        roomFilterComboBox.setValue(null);
    }

    /**
     * Populates the edit fields with data from the selected collection.
     */
    private void populateEditFields(Collections c) {
        editName.setText(c.getName());
        editDescription.setText(c.getDescription());
        editCentury.setText(c.getCentury());
        editRoom.setValue(c.getRooms());
    }

    /**
     * Clears all edit fields.
     */
    private void clearEditFields() {
        editName.clear();
        editDescription.clear();
        editCentury.clear();
        editRoom.setValue(null);
    }

    /**
     * Disables all edit fields.
     */
    private void disableAllEditFields() {
        editName.setDisable(true);
        editDescription.setDisable(true);
        editCentury.setDisable(true);
        editRoom.setDisable(true);
    }

    /**
     * Enables all edit fields.
     */
    private void enableAllEditFields() {
        editName.setDisable(false);
        editDescription.setDisable(false);
        editCentury.setDisable(false);
        editRoom.setDisable(false);
    }

    @FXML
    private void addNewBtnAction() {
        collectionsTable.setDisable(true);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);

        enableAllEditFields();
        clearEditFields();

        saveNewBtn.setDisable(false);
        addNewBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
    }

    @FXML
    private void saveNewBtnAction() {
        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        if (editRoom.getValue() == null) {
            showAlert("Debe seleccionar una sala.");
            return;
        }

        Collections c = new Collections();
        c.setName(editName.getText().trim());
        c.setDescription(editDescription.getText().trim());
        c.setCentury(editCentury.getText().trim());
        c.setRooms(editRoom.getValue());

        collectionsCRUD.addCollection(c);

        loadFilteredCollections();
        collectionsTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        collectionsTable.setDisable(false);
    }

    @FXML
    private void editBtnAction() {
        Collections selected = collectionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        collectionsTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    @FXML
    private void saveChangesBtnAction() {
        Collections selected = collectionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        if (editRoom.getValue() == null) {
            showAlert("Debe seleccionar una sala.");
            return;
        }

        selected.setName(editName.getText().trim());
        selected.setDescription(editDescription.getText().trim());
        selected.setCentury(editCentury.getText().trim());
        selected.setRooms(editRoom.getValue());

        collectionsCRUD.updateCollection(selected);

        loadFilteredCollections();
        collectionsTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        collectionsTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    @FXML
    private void eraseBtnAction() {
        Collections selected = collectionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        collectionsCRUD.deleteCollection(selected.getId());

        loadFilteredCollections();
        collectionsTable.refresh();

        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Shows an informational alert with the given message.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}