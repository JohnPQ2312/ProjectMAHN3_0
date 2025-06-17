package FXMLControllers.admin;

import PersistenceCRUD.RoomsCRUD;
import PersistenceClasses.Rooms;
import PersistenceClasses.Museums;
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
 * Controller for managing museum rooms.
 * Supports CRUD, filtering, and table/UI logic for rooms.
 */
public class RoomManagerController implements Initializable {

    @FXML
    private TableView<Rooms> roomsTable;
    @FXML
    private TableColumn<Rooms, String> nameColumn;
    @FXML
    private TableColumn<Rooms, String> descriptionColumn;
    @FXML
    private TableColumn<Rooms, String> museumColumn; 
    @FXML
    private TableColumn<Rooms, BigDecimal> idColumn;

    @FXML
    private TextField editName, editDescription, filterTextField;
    @FXML
    private ComboBox<Museums> editMuseum; 
    @FXML
    private ComboBox<Museums> museumFilterComboBox; 

    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final RoomsCRUD roomsCRUD = new RoomsCRUD();
    private final PersistenceCRUD.MuseumsCRUD museumsCRUD = new PersistenceCRUD.MuseumsCRUD();

    private ObservableList<Rooms> masterList;
    private FilteredList<Rooms> filteredData;
    private ObservableList<Museums> museumsList;

    /**
     * Initializes the controller, sets up columns, listeners, and disables editing until a selection is made.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        museumColumn.setCellValueFactory(cellData -> {
            Museums museum = cellData.getValue().getMuseums();
            return new SimpleStringProperty(museum != null ? museum.getName() : "Sin museo");
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadMuseumsCombo();
        loadFilteredRooms();

        // Table selection listener to enable edit/delete
        roomsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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
     * Loads all museums into the combo boxes for filtering and editing.
     */
    private void loadMuseumsCombo() {
        museumsList = FXCollections.observableArrayList(museumsCRUD.getAllMuseums());
        museumFilterComboBox.setItems(museumsList);
        editMuseum.setItems(museumsList);
    }

    /**
     * Loads all rooms, sets up filtering and sorting for the table.
     */
    private void loadFilteredRooms() {
        masterList = FXCollections.observableArrayList(roomsCRUD.getAllRooms());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });

        museumFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });

        SortedList<Rooms> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(roomsTable.comparatorProperty());
        roomsTable.setItems(sortedList);
    }

    /**
     * Applies text and museum filters to the rooms list.
     */
    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        Museums selectedMuseum = museumFilterComboBox.getValue();

        filteredData.setPredicate(room -> {
            boolean matchesText = room.getName().toLowerCase().contains(text)
                    || (room.getDescription() != null && room.getDescription().toLowerCase().contains(text));
            boolean matchesMuseum = (selectedMuseum == null) || 
                (room.getMuseums() != null && room.getMuseums().getId().compareTo(selectedMuseum.getId()) == 0);
            return matchesText && matchesMuseum;
        });
    }

    /**
     * Clears all filters (text and museum).
     */
    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        museumFilterComboBox.setValue(null);
    }    

    /**
     * Populates the edit fields with the selected room's data.
     */
    private void populateEditFields(Rooms r) {
        editName.setText(r.getName());
        editDescription.setText(r.getDescription());
        editMuseum.setValue(r.getMuseums());
    }

    /**
     * Clears all edit fields.
     */
    private void clearEditFields() {
        editName.clear();
        editDescription.clear();
        editMuseum.setValue(null);
    }

    /**
     * Disables all edit fields.
     */
    private void disableAllEditFields() {
        editName.setDisable(true);
        editDescription.setDisable(true);
        editMuseum.setDisable(true);
    }

    /**
     * Enables all edit fields.
     */
    private void enableAllEditFields() {
        editName.setDisable(false);
        editDescription.setDisable(false);
        editMuseum.setDisable(false);
    }

    /**
     * Handler for the "Add New" button. Prepares the form for new room entry.
     */
    @FXML
    private void addNewBtnAction() {
        roomsTable.setDisable(true);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);

        enableAllEditFields();
        clearEditFields();

        saveNewBtn.setDisable(false);
        addNewBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
    }

    /**
     * Handler for "Save New" button. Validates and saves a new room.
     */
    @FXML
    private void saveNewBtnAction() {
        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        if (editMuseum.getValue() == null) {
            showAlert("Debe seleccionar un museo.");
            return;
        }

        Rooms r = new Rooms();
        r.setName(editName.getText().trim());
        r.setDescription(editDescription.getText().trim());
        r.setMuseums(editMuseum.getValue());

        roomsCRUD.addRoom(r);

        loadFilteredRooms();
        roomsTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        roomsTable.setDisable(false);
    }

    /**
     * Handler for "Edit" button. Enables fields for editing the selected room.
     */
    @FXML
    private void editBtnAction() {
        Rooms selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        roomsTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    /**
     * Handler for "Save Changes" button. Updates the selected room with new data.
     */
    @FXML
    private void saveChangesBtnAction() {
        Rooms selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        if (editMuseum.getValue() == null) {
            showAlert("Debe seleccionar un museo.");
            return;
        }

        selected.setName(editName.getText().trim());
        selected.setDescription(editDescription.getText().trim());
        selected.setMuseums(editMuseum.getValue());

        roomsCRUD.updateRoom(selected);

        loadFilteredRooms();
        roomsTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        roomsTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Handler for "Erase" button. Deletes the selected room.
     */
    @FXML
    private void eraseBtnAction() {
        Rooms selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        roomsCRUD.deleteRoom(selected.getId());

        loadFilteredRooms();
        roomsTable.refresh();

        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Utility to show information alerts.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}