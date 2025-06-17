package FXMLControllers.admin;

import PersistenceCRUD.PaymentMethodsCRUD;
import PersistenceClasses.PaymentMethods;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for managing payment methods (CRUD, filtering, and form handling).
 * Empowers the finance department (and you!) to future-proof your payment catalog.
 */
public class PaymentMethodManagerController implements Initializable {

    // Table and columns for payment methods
    @FXML private TableView<PaymentMethods> paymentTable;
    @FXML private TableColumn<PaymentMethods, BigDecimal> idColumn;
    @FXML private TableColumn<PaymentMethods, String> typeColumn;
    @FXML private TableColumn<PaymentMethods, String> commissionColumn;

    // Form fields for editing/adding payment methods
    @FXML private TextField editType, editCommission, filterTextField;

    // Action buttons
    @FXML private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final PaymentMethodsCRUD paymentMethodsCRUD = new PaymentMethodsCRUD();

    // Observable lists for all and filtered payment methods
    private ObservableList<PaymentMethods> masterList;
    private FilteredList<PaymentMethods> filteredData;

    /**
     * Initializes the controller, sets up table columns, listeners, and initial UI state.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        commissionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCommissionPercentage() != null ? cell.getValue().getCommissionPercentage().toString() : ""));

        disableAllEditFields();
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);

        loadFilteredPaymentMethods();

        // Table selection listener for enabling edit/delete
        paymentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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
     * Loads payment methods and sets up filter and sort logic for the table.
     */
    private void loadFilteredPaymentMethods() {
        masterList = FXCollections.observableArrayList(paymentMethodsCRUD.getAllPaymentMethods());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<PaymentMethods> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(paymentTable.comparatorProperty());
        paymentTable.setItems(sortedList);
    }

    /**
     * Applies text filter to the payment methods list.
     */
    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();
        filteredData.setPredicate(payment -> payment.getType().toLowerCase().contains(text));
    }

    /**
     * Clears the text filter.
     */
    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
    }

    /**
     * Populates the edit form fields with data from the selected payment method.
     * @param p The selected payment method
     */
    private void populateEditFields(PaymentMethods p) {
        editType.setText(p.getType());
        editCommission.setText(p.getCommissionPercentage() != null ? p.getCommissionPercentage().toString() : "");
    }

    /**
     * Clears all edit form fields.
     */
    private void clearEditFields() {
        editType.clear();
        editCommission.clear();
    }

    /**
     * Disables all edit fields.
     */
    private void disableAllEditFields() {
        editType.setDisable(true);
        editCommission.setDisable(true);
    }

    /**
     * Enables all edit fields.
     */
    private void enableAllEditFields() {
        editType.setDisable(false);
        editCommission.setDisable(false);
    }

    /**
     * Handler for the "Add New" button. Prepares the form for adding a new payment method.
     */
    @FXML
    private void addNewBtnAction() {
        paymentTable.setDisable(true);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);

        enableAllEditFields();
        clearEditFields();

        saveNewBtn.setDisable(false);
        addNewBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
    }

    /**
     * Handler for "Save New" button. Validates and saves a new payment method.
     * Shows alert if data is invalid.
     */
    @FXML
    private void saveNewBtnAction() {
        if (editType.getText().trim().isEmpty()) {
            showAlert("El tipo es obligatorio.");
            return;
        }

        PaymentMethods p = new PaymentMethods();
        p.setType(editType.getText().trim());
        try {
            p.setCommissionPercentage(editCommission.getText().isEmpty() ? null : new BigDecimal(editCommission.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert("La comisión debe ser un número válido.");
            return;
        }

        paymentMethodsCRUD.addPaymentMethod(p);

        loadFilteredPaymentMethods();
        paymentTable.refresh();

        clearEditFields();
        disableAllEditFields();
        saveNewBtn.setDisable(true);
        addNewBtn.setDisable(false);
        paymentTable.setDisable(false);
    }

    /**
     * Handler for "Edit" button. Enables fields for editing the selected payment method.
     */
    @FXML
    private void editBtnAction() {
        PaymentMethods selected = paymentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        paymentTable.setDisable(true);
        eraseBtn.setDisable(true);
        addNewBtn.setDisable(true);
        saveNewBtn.setDisable(true);

        enableAllEditFields();
        saveChangesBtn.setDisable(false);
    }

    /**
     * Handler for "Save Changes" button. Updates the selected payment method with new data.
     * Validates input and shows alert if data is invalid.
     */
    @FXML
    private void saveChangesBtnAction() {
        PaymentMethods selected = paymentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (editType.getText().trim().isEmpty()) {
            showAlert("El tipo es obligatorio.");
            return;
        }

        selected.setType(editType.getText().trim());
        try {
            selected.setCommissionPercentage(editCommission.getText().isEmpty() ? null : new BigDecimal(editCommission.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert("La comisión debe ser un número válido.");
            return;
        }

        paymentMethodsCRUD.updatePaymentMethod(selected);

        loadFilteredPaymentMethods();
        paymentTable.refresh();

        disableAllEditFields();
        saveChangesBtn.setDisable(true);
        paymentTable.setDisable(false);
        addNewBtn.setDisable(false);
        editBtn.setDisable(true);
        eraseBtn.setDisable(true);
        clearEditFields();
    }

    /**
     * Handler for "Erase" button. Deletes the selected payment method after confirmation.
     */
    @FXML
    private void eraseBtnAction() {
        PaymentMethods selected = paymentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        paymentMethodsCRUD.deletePaymentMethod(selected.getId());

        loadFilteredPaymentMethods();
        paymentTable.refresh();

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