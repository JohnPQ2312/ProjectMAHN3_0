package FXMLControllers.admin;

import PersistenceCRUD.MuseumsCRUD;
import PersistenceClasses.Museums;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MuseumManagerController implements Initializable {

    // Table and column declarations for museums
    @FXML private TableView<Museums> museumsTable;
    @FXML private TableColumn<Museums, String> nameColumn;
    @FXML private TableColumn<Museums, String> typeColumn;
    @FXML private TableColumn<Museums, String> locationColumn;
    @FXML private TableColumn<Museums, Date> foundationColumn;
    @FXML private TableColumn<Museums, String> directorColumn;
    @FXML private TableColumn<Museums, String> websiteColumn;
    @FXML private TableColumn<Museums, BigDecimal> idColumn;

    // Form fields for editing/adding museums
    @FXML private TextField editName, editType, editLocation, editDirector, editWebsite, filterTextField;
    @FXML private DatePicker editFoundation;
    @FXML private ComboBox<String> typeFilterComboBox;

    // Action buttons
    @FXML private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final MuseumsCRUD museumsCRUD = new MuseumsCRUD();

    // Observable lists for master and filtered data
    private ObservableList<Museums> masterList;
    private FilteredList<Museums> filteredData;

    /**
     * Initializes the controller and UI. Sets up table columns, listeners, and filter components.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind table columns to Museums properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("museumType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        foundationColumn.setCellValueFactory(new PropertyValueFactory<>("foundation"));
        directorColumn.setCellValueFactory(new PropertyValueFactory<>("director"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Initial UI state: disable edit fields/buttons (future-proof for new requirements)
        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadFilteredMuseums();

        // Table selection listener: enables edit/delete when a row is selected
        museumsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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

        // Custom cell factory for date formatting in the foundation column
        foundationColumn.setCellFactory(column -> new TableCell<Museums, Date>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    LocalDate localDate = item.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    setText(localDate.format(formatter));
                }
            }
        });
    }

    /**
     * Loads all museums into the table. Used for unfiltered reloads.
     */
    private void loadMuseums() {
        List<Museums> museums = museumsCRUD.getAllMuseums();
        ObservableList<Museums> obs = FXCollections.observableArrayList(museums);
        museumsTable.setItems(obs);
    }

    /**
     * Loads museums and sets up filter and sort logic for the table.
     */
    private void loadFilteredMuseums() {
        masterList = FXCollections.observableArrayList(museumsCRUD.getAllMuseums());
        filteredData = new FilteredList<>(masterList, p -> true);

        // Listener for text filter
        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });

        // Predefined museum types for filter (future-proof: centralize in DB/config if needed)
        typeFilterComboBox.setItems(FXCollections.observableArrayList("Arte", "Historia", "Musical", "Militar"));
        typeFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });

        SortedList<Museums> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(museumsTable.comparatorProperty());
        museumsTable.setItems(sortedList);
    }

    /**
     * Applies text and type filters to the museums list.
     */
    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        String type = typeFilterComboBox.getValue();

        filteredData.setPredicate(museum -> {
            boolean matchesText = museum.getName().toLowerCase().contains(text)
                    || museum.getLocation().toLowerCase().contains(text);
            boolean matchesType = (type == null || type.isEmpty()) || museum.getMuseumType().equals(type);
            return matchesText && matchesType;
        });
    }

    /**
     * Clears all filters.
     */
    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        typeFilterComboBox.setValue(null);
    }

    /**
     * Populates the edit form fields with data from a selected museum.
     */
    private void populateEditFields(Museums m) {
        editName.setText(m.getName());
        editType.setText(m.getMuseumType());
        editLocation.setText(m.getLocation());
        editFoundation.setValue(m.getFoundation().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        editDirector.setText(m.getDirector());
        editWebsite.setText(m.getWebsite());
    }

    /**
     * Clears all edit form fields.
     */
    private void clearEditFields() {
        editName.clear();
        editType.clear();
        editLocation.clear();
        editFoundation.setValue(null);
        editDirector.clear();
        editWebsite.clear();
    }

    /**
     * Disables all edit fields.
     */
    private void disableAllEditFields() {
        editName.setDisable(true);
        editType.setDisable(true);
        editLocation.setDisable(true);
        editFoundation.setDisable(true);
        editDirector.setDisable(true);
        editWebsite.setDisable(true);
    }

    /**
     * Enables all edit fields.
     */
    private void enableAllEditFields() {
        editName.setDisable(false);
        editType.setDisable(false);
        editLocation.setDisable(false);
        editFoundation.setDisable(false);
        editDirector.setDisable(false);
        editWebsite.setDisable(false);
    }

    /**
     * Handler for the "Add New" button. Prepares the form for adding a new museum.
     */
    @FXML
    private void addNewBtnAction() {
        museumsTable.setDisable(true);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);

        enableAllEditFields();
        clearEditFields();

        saveNewBtn.setDisable(false);
        addNewBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
    }

    /**
     * Handler for "Save New" button. Validates and saves a new museum.
     * Shows alert if data is invalid.
     */
    @FXML
    private void saveNewBtnAction() {
        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        // Only allow permitted types
        if ((!editType.getText().equalsIgnoreCase("Arte")) && (!editType.getText().equalsIgnoreCase("Historia")) && (!editType.getText().equalsIgnoreCase("Musical")) && (!editType.getText().equalsIgnoreCase("Militar"))) {
            showAlert("El tipo de museo no es de los tipos permitidos (Arte, Historia, Musical, Militar)");
            return;
        }

        LocalDate changedDate = editFoundation.getValue();

        Museums m = new Museums();
        m.setName(editName.getText().trim());
        m.setMuseumType(editType.getText().trim());
        m.setLocation(editLocation.getText().trim());
        m.setFoundation(Date.from(changedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        m.setDirector(editDirector.getText().trim());
        m.setWebsite(editWebsite.getText().trim());

        museumsCRUD.addMuseum(m);

        loadFilteredMuseums();
        museumsTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        museumsTable.setDisable(false);
    }

    /**
     * Handler for "Edit" button. Enables fields for editing the selected museum.
     */
    @FXML
    private void editBtnAction() {
        Museums selected = museumsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        museumsTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    /**
     * Handler for "Save Changes" button. Updates the selected museum with new data.
     * Validates input and shows alert if data is invalid.
     */
    @FXML
    private void saveChangesBtnAction() {
        Museums selected = museumsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        // Only allow permitted types (future-proof: consider using an enum or config)
        if ((!editType.getText().equalsIgnoreCase("Arte")) && (!editType.getText().equalsIgnoreCase("Historia")) && (!editType.getText().equalsIgnoreCase("Musical")) && (!editType.getText().equalsIgnoreCase("Militar"))) {
            showAlert("El tipo de museo no es de los tipos permitidos (Arte, Historia, Musical, Militar)");
            return;
        }

        LocalDate changedDate = editFoundation.getValue();

        selected.setName(editName.getText().trim());
        selected.setMuseumType(editType.getText().trim());
        selected.setLocation(editLocation.getText().trim());
        selected.setFoundation(Date.from(changedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        selected.setDirector(editDirector.getText().trim());
        selected.setWebsite(editWebsite.getText().trim());

        museumsCRUD.updateMuseum(selected);

        loadFilteredMuseums();
        museumsTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        museumsTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Handler for "Erase" button. Deletes the selected museum after confirmation.
     */
    @FXML
    private void eraseBtnAction() {
        Museums selected = museumsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        museumsCRUD.deleteMuseum(selected.getId());

        loadFilteredMuseums();
        museumsTable.refresh();

        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Utility: Shows an information alert with the given message.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}