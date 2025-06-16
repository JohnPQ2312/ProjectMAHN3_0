package FXMLControllers.admin;

import PersistenceCRUD.SpeciesCRUD;
import PersistenceClasses.Species;
import PersistenceClasses.Collections;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SpeciesManagerController implements Initializable {

    @FXML
    private TableView<Species> speciesTable;

    @FXML
    private TableColumn<Species, String> scientificNameColumn;
    @FXML
    private TableColumn<Species, String> commonNameColumn;
    @FXML
    private TableColumn<Species, Date> extinctionDateColumn;
    @FXML
    private TableColumn<Species, String> periodColumn;
    @FXML
    private TableColumn<Species, String> weightColumn;
    @FXML
    private TableColumn<Species, String> featuresColumn;
    @FXML
    private TableColumn<Species, String> collectionColumn;

    @FXML
    private TextField editScientificName, editCommonName, editPeriod, editWeight, editFeatures, filterTextField;
    @FXML
    private DatePicker editExtinctionDate;
    @FXML
    private ComboBox<Collections> editCollection, collectionFilterComboBox;

    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final SpeciesCRUD speciesCRUD = new SpeciesCRUD();
    private final PersistenceCRUD.CollectionsCRUD collectionsCRUD = new PersistenceCRUD.CollectionsCRUD();

    private ObservableList<Species> masterList;
    private FilteredList<Species> filteredData;
    private ObservableList<Collections> collectionsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scientificNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getScientificName()));
        commonNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCommonName()));
        extinctionDateColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getExtinctionDate())
        );
        periodColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPeriod()));
        weightColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWeight() != null ? cell.getValue().getWeight().toString() : ""));
        featuresColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFeatures()));
        collectionColumn.setCellValueFactory(cell -> {
            Collections col = cell.getValue().getCollections();
            return new SimpleStringProperty(col != null ? col.getName() : "Sin colección");
        });

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadCollectionsCombo();
        loadFilteredSpecies();

        speciesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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

        extinctionDateColumn.setCellFactory(column -> new TableCell<Species, Date>() {
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

    private void loadCollectionsCombo() {
        collectionsList = FXCollections.observableArrayList(collectionsCRUD.getAllCollections());
        collectionFilterComboBox.setItems(collectionsList);
        editCollection.setItems(collectionsList);
    }

    private void loadFilteredSpecies() {
        masterList = FXCollections.observableArrayList(speciesCRUD.getAllSpecies());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        collectionFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<Species> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(speciesTable.comparatorProperty());
        speciesTable.setItems(sortedList);
    }

    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        Collections selectedCollection = collectionFilterComboBox.getValue();

        filteredData.setPredicate(species -> {
            boolean matchesText = species.getScientificName().toLowerCase().contains(text)
                    || species.getCommonName().toLowerCase().contains(text);
            boolean matchesCollection = (selectedCollection == null)
                    || (species.getCollections() != null && species.getCollections().getId().compareTo(selectedCollection.getId()) == 0);
            return matchesText && matchesCollection;
        });
    }

    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        collectionFilterComboBox.setValue(null);
    }

    private void populateEditFields(Species s) {
        editScientificName.setText(s.getScientificName());
        editCommonName.setText(s.getCommonName());
        if (s.getExtinctionDate() != null) {
            editExtinctionDate.setValue(s.getExtinctionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            editExtinctionDate.setValue(null);
        }
        editPeriod.setText(s.getPeriod());
        editWeight.setText(s.getWeight() != null ? s.getWeight().toString() : "");
        editFeatures.setText(s.getFeatures());
        editCollection.setValue(s.getCollections());
    }

    private void clearEditFields() {
        editScientificName.clear();
        editCommonName.clear();
        editExtinctionDate.setValue(null);
        editPeriod.clear();
        editWeight.clear();
        editFeatures.clear();
        editCollection.setValue(null);
    }

    private void disableAllEditFields() {
        editScientificName.setDisable(true);
        editCommonName.setDisable(true);
        editExtinctionDate.setDisable(true);
        editPeriod.setDisable(true);
        editWeight.setDisable(true);
        editFeatures.setDisable(true);
        editCollection.setDisable(true);
    }

    private void enableAllEditFields() {
        editScientificName.setDisable(false);
        editCommonName.setDisable(false);
        editExtinctionDate.setDisable(false);
        editPeriod.setDisable(false);
        editWeight.setDisable(false);
        editFeatures.setDisable(false);
        editCollection.setDisable(false);
    }

    @FXML
    private void addNewBtnAction() {
        speciesTable.setDisable(true);
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
        if (editScientificName.getText().trim().isEmpty()) {
            showAlert("El nombre científico es obligatorio.");
            return;
        }
        if (editCollection.getValue() == null) {
            showAlert("Debe seleccionar una colección.");
            return;
        }

        Species s = new Species();
        s.setScientificName(editScientificName.getText().trim());
        s.setCommonName(editCommonName.getText().trim());
        LocalDate localDate = editExtinctionDate.getValue();
        s.setExtinctionDate(localDate != null ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null);
        s.setPeriod(editPeriod.getText().trim());
        try {
            s.setWeight(editWeight.getText().isEmpty() ? null : new BigDecimal(editWeight.getText()));
        } catch (NumberFormatException e) {
            showAlert("El peso debe ser un número.");
            return;
        }
        s.setFeatures(editFeatures.getText().trim());
        s.setCollections(editCollection.getValue());

        speciesCRUD.addSpecies(s);

        loadFilteredSpecies();
        speciesTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        speciesTable.setDisable(false);
    }

    @FXML
    private void editBtnAction() {
        Species selected = speciesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        speciesTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    @FXML
    private void saveChangesBtnAction() {
        Species selected = speciesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (editScientificName.getText().trim().isEmpty()) {
            showAlert("El nombre científico es obligatorio.");
            return;
        }
        if (editCollection.getValue() == null) {
            showAlert("Debe seleccionar una colección.");
            return;
        }

        selected.setScientificName(editScientificName.getText().trim());
        selected.setCommonName(editCommonName.getText().trim());
        LocalDate localDate = editExtinctionDate.getValue();
        selected.setExtinctionDate(localDate != null ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null);
        selected.setPeriod(editPeriod.getText().trim());
        try {
            selected.setWeight(editWeight.getText().isEmpty() ? null : new BigDecimal(editWeight.getText()));
        } catch (NumberFormatException e) {
            showAlert("El peso debe ser un número.");
            return;
        }
        selected.setFeatures(editFeatures.getText().trim());
        selected.setCollections(editCollection.getValue());

        speciesCRUD.updateSpecies(selected);

        loadFilteredSpecies();
        speciesTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        speciesTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    @FXML
    private void eraseBtnAction() {
        Species selected = speciesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        speciesCRUD.deleteSpecies(selected.getId());

        loadFilteredSpecies();
        speciesTable.refresh();

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
