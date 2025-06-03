/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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

/**
 * FXML Controller class
 *
 * @author jp570
 */
public class MuseumManagerController implements Initializable {

    @FXML
    private TableView<Museums> museumsTable;

    @FXML
    private TableColumn<Museums, String> nameColumn;
    @FXML
    private TableColumn<Museums, String> typeColumn;
    @FXML
    private TableColumn<Museums, String> locationColumn;
    @FXML
    private TableColumn<Museums, Date> foundationColumn;
    @FXML
    private TableColumn<Museums, String> directorColumn;
    @FXML
    private TableColumn<Museums, String> websiteColumn;
    @FXML
    private TableColumn<Museums, BigDecimal> idColumn;

    @FXML
    private TextField editName, editType, editLocation, editDirector, editWebsite, filterTextField;
    
    @FXML private DatePicker editFoundation;

    @FXML private ComboBox<String> typeFilterComboBox;
    
    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final MuseumsCRUD museumsCRUD = new MuseumsCRUD();
    
    private ObservableList<Museums> masterList;
    private FilteredList<Museums> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("museumType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        foundationColumn.setCellValueFactory(new PropertyValueFactory<>("foundation"));
        directorColumn.setCellValueFactory(new PropertyValueFactory<>("director"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadFilteredMuseums();

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

    private void loadMuseums() {
        List<Museums> museums = museumsCRUD.getAllMuseums();
        ObservableList<Museums> obs = FXCollections.observableArrayList(museums);
        museumsTable.setItems(obs);
    }
    
    private void loadFilteredMuseums() {
        masterList = FXCollections.observableArrayList(museumsCRUD.getAllMuseums());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });

        typeFilterComboBox.setItems(FXCollections.observableArrayList("Arte", "Historia", "Musical", "Militar"));
        typeFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter();
        });

        SortedList<Museums> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(museumsTable.comparatorProperty());
        museumsTable.setItems(sortedList);
    }

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
    
    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        typeFilterComboBox.setValue(null);
    }

    private void populateEditFields(Museums m) {
        editName.setText(m.getName());
        editType.setText(m.getMuseumType());
        editLocation.setText(m.getLocation());
        editFoundation.setValue(m.getFoundation().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        editDirector.setText(m.getDirector());
        editWebsite.setText(m.getWebsite());
    }

    private void clearEditFields() {
        editName.clear();
        editType.clear();
        editLocation.clear();
        editFoundation.setValue(null);
        editDirector.clear();
        editWebsite.clear();
    }

    private void disableAllEditFields() {
        editName.setDisable(true);
        editType.setDisable(true);
        editLocation.setDisable(true);
        editFoundation.setDisable(true);
        editDirector.setDisable(true);
        editWebsite.setDisable(true);
    }

    private void enableAllEditFields() {
        editName.setDisable(false);
        editType.setDisable(false);
        editLocation.setDisable(false);
        editFoundation.setDisable(false);
        editDirector.setDisable(false);
        editWebsite.setDisable(false);
    }

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

    @FXML
    private void saveNewBtnAction() {
        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }
        
        if ((!editType.getText().equalsIgnoreCase("Arte")) && (!editType.getText().equalsIgnoreCase("Historia")) && (!editType.getText().equalsIgnoreCase("Musical")) && (!editType.getText().equalsIgnoreCase("Militar"))){
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
        
        if ((!editType.getText().equalsIgnoreCase("Arte")) || (!editType.getText().equalsIgnoreCase("Historia")) || (!editType.getText().equalsIgnoreCase("Musical")) || (!editType.getText().equalsIgnoreCase("Militar"))){
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
