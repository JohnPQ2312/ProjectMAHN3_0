package FXMLControllers.admin;

import PersistenceCRUD.PricesCRUD;
import PersistenceClasses.Prices;
import PersistenceClasses.Rooms;
import java.math.BigDecimal;
import java.net.URL;
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
 * Controller for price management. Handles CRUD operations, filtering, 
 * and UI logic for price records associated with museum rooms.
 */
public class PriceManagerController implements Initializable {

    @FXML
    private TableView<Prices> priceTable;
    @FXML
    private TableColumn<Prices, BigDecimal> idColumn;
    @FXML
    private TableColumn<Prices, String> normalPriceColumn;
    @FXML
    private TableColumn<Prices, String> sundayPriceColumn;
    @FXML
    private TableColumn<Prices, String> roomColumn;

    @FXML
    private TextField editNormalPrice, editSundayPrice, filterTextField;
    @FXML
    private ComboBox<Rooms> editRoomComboBox, roomFilterComboBox;

    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final PricesCRUD pricesCRUD = new PricesCRUD();
    private final PersistenceCRUD.RoomsCRUD roomsCRUD = new PersistenceCRUD.RoomsCRUD();

    private ObservableList<Prices> masterList;
    private FilteredList<Prices> filteredData;
    private ObservableList<Rooms> roomsList;

    /**
     * Initializes the controller, sets up table columns, listeners, 
     * and disables editing until a selection is made.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        normalPriceColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNormalPrice() != null ? cell.getValue().getNormalPrice().toString() : ""));
        sundayPriceColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSundayPrice() != null ? cell.getValue().getSundayPrice().toString() : ""));
        roomColumn.setCellValueFactory(cell -> {
            Rooms room = cell.getValue().getRooms();
            return new SimpleStringProperty(room != null ? room.getName() : "Sin sala");
        });

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadRoomsCombo();
        loadFilteredPrices();

        // Listener to enable edit/delete when a row is selected
        priceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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
     * Loads all available rooms into the combo boxes for filtering and editing.
     */
    private void loadRoomsCombo() {
        roomsList = FXCollections.observableArrayList(roomsCRUD.getAllRooms());
        roomFilterComboBox.setItems(roomsList);
        editRoomComboBox.setItems(roomsList);
    }

    /**
     * Loads all prices, sets up filtering by price and room, and sorting.
     */
    private void loadFilteredPrices() {
        masterList = FXCollections.observableArrayList(pricesCRUD.getAllPrices());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        roomFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<Prices> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(priceTable.comparatorProperty());
        priceTable.setItems(sortedList);
    }

    /**
     * Applies filters to the price list based on user input and selected room.
     */
    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        Rooms selectedRoom = roomFilterComboBox.getValue();

        filteredData.setPredicate(price -> {
            boolean matchesText = (price.getNormalPrice() != null && price.getNormalPrice().toString().contains(text))
                || (price.getSundayPrice() != null && price.getSundayPrice().toString().contains(text));
            boolean matchesRoom = (selectedRoom == null) ||
                (price.getRooms() != null && price.getRooms().getId().compareTo(selectedRoom.getId()) == 0);
            return matchesText && matchesRoom;
        });
    }

    /**
     * Clears all filters (text and room).
     */
    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        roomFilterComboBox.setValue(null);
    }

    /**
     * Populates the edit fields with the selected price's data.
     */
    private void populateEditFields(Prices p) {
        editNormalPrice.setText(p.getNormalPrice() != null ? p.getNormalPrice().toString() : "");
        editSundayPrice.setText(p.getSundayPrice() != null ? p.getSundayPrice().toString() : "");
        editRoomComboBox.setValue(p.getRooms());
    }

    /**
     * Clears all edit fields.
     */
    private void clearEditFields() {
        editNormalPrice.clear();
        editSundayPrice.clear();
        editRoomComboBox.setValue(null);
    }

    /**
     * Disables all edit fields.
     */
    private void disableAllEditFields() {
        editNormalPrice.setDisable(true);
        editSundayPrice.setDisable(true);
        editRoomComboBox.setDisable(true);
    }

    /**
     * Enables all edit fields.
     */
    private void enableAllEditFields() {
        editNormalPrice.setDisable(false);
        editSundayPrice.setDisable(false);
        editRoomComboBox.setDisable(false);
    }

    /**
     * Handler for the "Add New" button. Prepares the form for new price entry.
     */
    @FXML
    private void addNewBtnAction() {
        priceTable.setDisable(true);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);

        enableAllEditFields();
        clearEditFields();

        saveNewBtn.setDisable(false);
        addNewBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
    }

    /**
     * Handler for "Save New" button. Validates and saves a new price.
     */
    @FXML
    private void saveNewBtnAction() {
        if (editNormalPrice.getText().trim().isEmpty()) {
            showAlert("El precio normal es obligatorio.");
            return;
        }
        if (editRoomComboBox.getValue() == null) {
            showAlert("Debe seleccionar una sala.");
            return;
        }

        Prices p = new Prices();
        try {
            p.setNormalPrice(new BigDecimal(editNormalPrice.getText().trim()));
            p.setSundayPrice(editSundayPrice.getText().isEmpty() ? null : new BigDecimal(editSundayPrice.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert("Los precios deben ser números válidos.");
            return;
        }
        p.setRooms(editRoomComboBox.getValue());

        pricesCRUD.addPrice(p);

        loadFilteredPrices();
        priceTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        priceTable.setDisable(false);
    }

    /**
     * Handler for "Edit" button. Enables fields for editing the selected price.
     */
    @FXML
    private void editBtnAction() {
        Prices selected = priceTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        priceTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    /**
     * Handler for "Save Changes" button. Updates the selected price with new data.
     */
    @FXML
    private void saveChangesBtnAction() {
        Prices selected = priceTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (editNormalPrice.getText().trim().isEmpty()) {
            showAlert("El precio normal es obligatorio.");
            return;
        }
        if (editRoomComboBox.getValue() == null) {
            showAlert("Debe seleccionar una sala.");
            return;
        }

        try {
            selected.setNormalPrice(new BigDecimal(editNormalPrice.getText().trim()));
            selected.setSundayPrice(editSundayPrice.getText().isEmpty() ? null : new BigDecimal(editSundayPrice.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert("Los precios deben ser números válidos.");
            return;
        }
        selected.setRooms(editRoomComboBox.getValue());

        pricesCRUD.updatePrice(selected);

        loadFilteredPrices();
        priceTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        priceTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Handler for "Erase" button. Deletes the selected price record.
     */
    @FXML
    private void eraseBtnAction() {
        Prices selected = priceTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        pricesCRUD.deletePrice(selected.getId());

        loadFilteredPrices();
        priceTable.refresh();

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