package FXMLControllers.admin;

import PersistenceCRUD.UsersCRUD;
import PersistenceClasses.Users;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
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

/**
 * Controller for managing users (CRUD, filtering, and table logic).
 * Handles user records, role filtering, and ensures role validation.
 */
public class UserManagerController implements Initializable {

    private static final Set<String> ALLOWED_ROLES = new HashSet<>(Arrays.asList("client", "admin"));

    @FXML private TableView<Users> userTable;
    @FXML private TableColumn<Users, BigDecimal> idColumn;
    @FXML private TableColumn<Users, String> nameColumn;
    @FXML private TableColumn<Users, String> emailColumn;
    @FXML private TableColumn<Users, String> roleColumn;
    @FXML private TableColumn<Users, String> phoneColumn;
    @FXML private TableColumn<Users, java.util.Date> registrationDateColumn;

    @FXML private TextField editName, editEmail, editRole, editPhone, filterTextField;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final UsersCRUD usersCRUD = new UsersCRUD();

    private ObservableList<Users> masterList;
    private FilteredList<Users> filteredData;

    /**
     * Initializes the controller, sets up table columns, listeners, and disables editing until a selection is made.
     */
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

        // Table selection listener for enabling edit/delete and populating edit fields
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

    /**
     * Loads available roles into the role filter combo box.
     */
    private void loadRoleCombo() {
        roleFilterComboBox.setItems(FXCollections.observableArrayList("ADMIN", "CLIENT"));
    }

    /**
     * Loads all users, sets up filtering by name/email and role, and sorting.
     */
    private void loadFilteredUsers() {
        masterList = FXCollections.observableArrayList(usersCRUD.getAllUsers());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        roleFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<Users> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedList);
    }

    /**
     * Applies filters to the user list based on name/email and selected role.
     */
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

    /**
     * Clears all filters (text and role).
     */
    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
        roleFilterComboBox.setValue(null);
    }

    /**
     * Populates the edit fields with the selected user's data.
     */
    private void populateEditFields(Users u) {
        editName.setText(u.getName());
        editEmail.setText(u.getEmail());
        editRole.setText(u.getRole());
        editPhone.setText(u.getPhone());
    }

    /**
     * Clears all edit fields.
     */
    private void clearEditFields() {
        editName.clear();
        editEmail.clear();
        editRole.clear();
        editPhone.clear();
    }

    /**
     * Disables all edit fields.
     */
    private void disableAllEditFields() {
        editName.setDisable(true);
        editEmail.setDisable(true);
        editRole.setDisable(true);
        editPhone.setDisable(true);
    }

    /**
     * Enables all edit fields.
     */
    private void enableAllEditFields() {
        editName.setDisable(false);
        editEmail.setDisable(false);
        editRole.setDisable(false);
        editPhone.setDisable(false);
    }

    /**
     * Handler for the "Add New" button. Prepares form for new user entry.
     */
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

    /**
     * Handler for "Save New" button. Validates and saves a new user.
     * Ensures role is either 'client' or 'admin'.
     */
    @FXML
    private void saveNewBtnAction() {
        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }

        String role = editRole.getText().trim().toLowerCase();
        if (!ALLOWED_ROLES.contains(role)) {
            showAlert("El rol debe ser 'client' o 'admin'.");
            return;
        }

        Users u = new Users();
        u.setName(editName.getText().trim());
        u.setEmail(editEmail.getText().trim());
        u.setRole(role);
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

    /**
     * Handler for the "Edit" button. Enables fields for editing the selected user.
     */
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

    /**
     * Handler for "Save Changes" button. Updates the selected user with new data.
     * Ensures role is valid.
     */
    @FXML
    private void saveChangesBtnAction() {
        Users selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (editName.getText().trim().isEmpty()) {
            showAlert("El nombre es obligatorio.");
            return;
        }

        String role = editRole.getText().trim().toLowerCase();
        if (!ALLOWED_ROLES.contains(role)) {
            showAlert("El rol debe ser 'client' o 'admin'.");
            return;
        }

        selected.setName(editName.getText().trim());
        selected.setEmail(editEmail.getText().trim());
        selected.setRole(role);
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

    /**
     * Handler for "Erase" button. Deletes the selected user.
     */
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

    /**
     * Utility to show information alerts.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}