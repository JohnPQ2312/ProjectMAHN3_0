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

public class PaymentMethodManagerController implements Initializable {

    @FXML
    private TableView<PaymentMethods> paymentTable;

    @FXML
    private TableColumn<PaymentMethods, BigDecimal> idColumn;
    @FXML
    private TableColumn<PaymentMethods, String> typeColumn;
    @FXML
    private TableColumn<PaymentMethods, String> commissionColumn;

    @FXML
    private TextField editType, editCommission, filterTextField;

    @FXML
    private Button editBtn, eraseBtn, saveChangesBtn, saveNewBtn, addNewBtn, clearFiltersBtn;

    private final PaymentMethodsCRUD paymentMethodsCRUD = new PaymentMethodsCRUD();

    private ObservableList<PaymentMethods> masterList;
    private FilteredList<PaymentMethods> filteredData;

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

    private void loadFilteredPaymentMethods() {
        masterList = FXCollections.observableArrayList(paymentMethodsCRUD.getAllPaymentMethods());
        filteredData = new FilteredList<>(masterList, p -> true);

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<PaymentMethods> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(paymentTable.comparatorProperty());
        paymentTable.setItems(sortedList);
    }

    private void applyFilter() {
        String text = filterTextField.getText() == null ? "" : filterTextField.getText().toLowerCase();

        filteredData.setPredicate(payment -> payment.getType().toLowerCase().contains(text));
    }

    @FXML
    private void clearFiltersBtnAction() {
        filterTextField.clear();
    }

    private void populateEditFields(PaymentMethods p) {
        editType.setText(p.getType());
        editCommission.setText(p.getCommissionPercentage() != null ? p.getCommissionPercentage().toString() : "");
    }

    private void clearEditFields() {
        editType.clear();
        editCommission.clear();
    }

    private void disableAllEditFields() {
        editType.setDisable(true);
        editCommission.setDisable(true);
    }

    private void enableAllEditFields() {
        editType.setDisable(false);
        editCommission.setDisable(false);
    }

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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}