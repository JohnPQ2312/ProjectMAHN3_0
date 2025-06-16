package FXMLControllers.admin;

import PersistenceCRUD.ThemesCRUD;
import PersistenceClasses.Themes;
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

public class ThemeManagerController implements Initializable {

    @FXML
    private TableView<Themes> themesTable;

    @FXML
    private TableColumn<Themes, String> nameColumn;
    @FXML
    private TableColumn<Themes, String> featuresColumn;
    @FXML
    private TableColumn<Themes, String> periodColumn;
    @FXML
    private TableColumn<Themes, String> roomColumn;
    @FXML
    private TableColumn<Themes, BigDecimal> idColumn;

    @FXML
    private TextField editName, editFeatures, editPeriod, filterTextField;
    @FXML
    private ComboBox<Rooms> editRoom, roomFilterComboBox;

    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final ThemesCRUD themesCRUD = new ThemesCRUD();
    private final PersistenceCRUD.RoomsCRUD roomsCRUD = new PersistenceCRUD.RoomsCRUD();

    private ObservableList<Themes> masterList;
    private FilteredList<Themes> filteredData;
    private ObservableList<Rooms> roomsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        featuresColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFeatures()));
        periodColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPeriod()));
        roomColumn.setCellValueFactory(cell -> {
            Rooms room = cell.getValue().getRooms();
            return new SimpleStringProperty(room != null ? room.getName() : "Sin sala");
        });
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadRoomsCombo();
        loadFilteredThemes();

        themesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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

    private void loadRoomsCombo() {
        roomsList = FXCollections.observableArrayList(roomsCRUD.getAllRooms());
        roomFilterComboBox.setItems(roomsList);
        editRoom.setItems(roomsList);
    }

    private void loadFilteredThemes() {
        masterList = FXCollections.observableArrayList(themesCRUD.getAllThemes());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        roomFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<Themes> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(themesTable.comparatorProperty());
        themesTable.setItems(sortedList);
    }

    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        Rooms selectedRoom = roomFilterComboBox.getValue();

        filteredData.setPredicate(theme -> {
            boolean matchesText = theme.getName().toLowerCase().contains(text)
                || (theme.getFeatures() != null && theme.getFeatures().toLowerCase().contains(text));
            boolean matchesRoom = (selectedRoom == null) ||
                (theme.getRooms() != null && theme.getRooms().getId().compareTo(selectedRoom.getId()) == 0);
            return matchesText && matchesRoom;
        });
    }

    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        roomFilterComboBox.setValue(null);
    }

    private void populateEditFields(Themes t) {
        editName.setText(t.getName());
        editFeatures.setText(t.getFeatures());
        editPeriod.setText(t.getPeriod());
        editRoom.setValue(t.getRooms());
    }

    private void clearEditFields() {
        editName.clear();
        editFeatures.clear();
        editPeriod.clear();
        editRoom.setValue(null);
    }

    private void disableAllEditFields() {
        editName.setDisable(true);
        editFeatures.setDisable(true);
        editPeriod.setDisable(true);
        editRoom.setDisable(true);
    }

    private void enableAllEditFields() {
        editName.setDisable(false);
        editFeatures.setDisable(false);
        editPeriod.setDisable(false);
        editRoom.setDisable(false);
    }

    @FXML
    private void addNewBtnAction() {
        themesTable.setDisable(true);
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

        Themes t = new Themes();
        t.setName(editName.getText().trim());
        t.setFeatures(editFeatures.getText().trim());
        t.setPeriod(editPeriod.getText().trim());
        t.setRooms(editRoom.getValue());

        themesCRUD.addTheme(t);

        loadFilteredThemes();
        themesTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        themesTable.setDisable(false);
    }

    @FXML
    private void editBtnAction() {
        Themes selected = themesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        themesTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    @FXML
    private void saveChangesBtnAction() {
        Themes selected = themesTable.getSelectionModel().getSelectedItem();
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
        selected.setFeatures(editFeatures.getText().trim());
        selected.setPeriod(editPeriod.getText().trim());
        selected.setRooms(editRoom.getValue());

        themesCRUD.updateTheme(selected);

        loadFilteredThemes();
        themesTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        themesTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    @FXML
    private void eraseBtnAction() {
        Themes selected = themesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        themesCRUD.deleteTheme(selected.getId());

        loadFilteredThemes();
        themesTable.refresh();

        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}