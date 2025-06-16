package FXMLControllers.admin;

import PersistenceCRUD.UsersCRUD;
import PersistenceClasses.Users;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagerController implements Initializable {

    @FXML
    private TableView<Users> userTable;

    @FXML
    private TableColumn<Users, BigDecimal> idColumn;
    @FXML
    private TableColumn<Users, String> nameColumn;
    @FXML
    private TableColumn<Users, String> emailColumn;
    @FXML
    private TableColumn<Users, String> roleColumn;
    @FXML
    private TableColumn<Users, String> phoneColumn;
    @FXML
    private TableColumn<Users, Date> registrationDateColumn;

    @FXML
    private TextField editName, editEmail, editRole, editPhone, filterTextField;
    @FXML
    private ComboBox<String> roleFilterComboBox;

    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final UsersCRUD usersCRUD = new UsersCRUD();

    private ObservableList<Users> masterList;
    private FilteredList<Users> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        emailColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));
        phoneColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhone()));
        registrationDateColumn.setCellValueFactory(cellData
                -> new SimpleObjectProperty<>(cellData.getValue().getRegistrationDate())
        );        

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadRoleCombo();
        loadFilteredUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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

    private void loadRoleCombo() {
        roleFilterComboBox.setItems(FXCollections.observableArrayList("ADMIN", "USER", "STAFF"));
    }

    private void loadFilteredUsers() {
        masterList = FXCollections.observableArrayList(usersCRUD.getAllUsers());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        roleFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<Users> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedList);
    }

    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        String role = roleFilterComboBox.getValue();

        filteredData.setPredicate(user -> {
            boolean matchesText = user.getName().toLowerCase().contains(text)
                || user.getEmail().toLowerCase().contains(text);
            boolean matchesRole = (role == null || role.isEmpty()) || user.getRole().equalsIgnoreCase(role);
            return matchesText && matchesRole;
        });
    }

    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        roleFilterComboBox.setValue(null);
    }

    private void populateEditFields(Users u) {
        editName.setText(u.getName());
        editEmail.setText(u.getEmail());
        editRole.setText(u.getRole());
        editPhone.setText(u.getPhone());
    }

    private void clearEditFields() {
        editName.clear();
        editEmail.clear();
        editRole.clear();
        editPhone.clear();
    }

    private void disableAllEditFields() {
        editName.setDisable(true);
        editEmail.setDisable(true);
        editRole.setDisable(true);
        editPhone.setDisable(true);
    }

    private void enableAllEditFields() {
        editName.setDisable(false);
        editEmail.setDisable(false);
        editRole.setDisable(false);
        editPhone.setDisable(false);
    }

    @FXML
    private void addNewBtnAction() {
        userTable.setDisable(true);
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

        Users u = new Users();
        u.setName(editName.getText().trim());
        u.setEmail(editEmail.getText().trim());
        u.setRole(editRole.getText().trim());
        u.setPhone(editPhone.getText().trim());
        u.setRegistrationDate(new Date());

        usersCRUD.addUser(u);

        loadFilteredUsers();
        userTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        userTable.setDisable(false);
    }

    @FXML
    private void editBtnAction() {
        Users selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        userTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    @FXML
    private void saveChangesBtnAction() {
        Users selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }

        selected.setName(editName.getText().trim());
        selected.setEmail(editEmail.getText().trim());
        selected.setRole(editRole.getText().trim());
        selected.setPhone(editPhone.getText().trim());

        usersCRUD.updateUser(selected);

        loadFilteredUsers();
        userTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        userTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    @FXML
    private void eraseBtnAction() {
        Users selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        usersCRUD.deleteUser(selected.getId());

        loadFilteredUsers();
        userTable.refresh();

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