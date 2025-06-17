package FXMLControllers.report;

import FXMLControllers.auxclasses.PDFUtils;
import PersistenceClasses.Commissions;
import PersistenceClasses.PaymentMethods;
import PersistenceCRUD.CommissionsCRUD;
import PersistenceCRUD.PaymentMethodsCRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class CommissionReportController {

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> filterPaymentMethodCombo;
    @FXML
    private Button generateButton;
    @FXML
    private Button exportPdfButton;
    @FXML
    private TableView<Commissions> commissionsTable;
    @FXML
    private TableColumn<Commissions, String> idPurchaseColumn;
    @FXML
    private TableColumn<Commissions, String> paymentMethodColumn;
    @FXML
    private TableColumn<Commissions, String> totalCommissionColumn;
    @FXML
    private TableColumn<Commissions, String> dateColumn;
    @FXML
    private TextField commissionPerTypeTotal;

    private final CommissionsCRUD commissionsCRUD = new CommissionsCRUD();
    private final PaymentMethodsCRUD paymentMethodsCRUD = new PaymentMethodsCRUD();

    private ObservableList<Commissions> filteredCommissions = FXCollections.observableArrayList();

    /**
     * Initializes the report view. Loads payment methods and sets up listeners.
     */
    @FXML
    public void initialize() {
        // Prepare payment method filter
        List<PaymentMethods> paymentMethods = paymentMethodsCRUD.getAllPaymentMethods();
        filterPaymentMethodCombo.getItems().clear();
        filterPaymentMethodCombo.getItems().add("Todos");
        filterPaymentMethodCombo.getItems().addAll(
                paymentMethods.stream().map(PaymentMethods::getType).collect(Collectors.toList())
        );
        filterPaymentMethodCombo.getSelectionModel().selectFirst();

        // Set up table columns (assuming Commission, Purchase and PaymentMethods have suitable toString)
        idPurchaseColumn.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPurchases().getId().toString()
                )
        );
        paymentMethodColumn.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPaymentMethods().getType()
                )
        );
        totalCommissionColumn.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getAmount().toPlainString()
                )
        );
        dateColumn.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(
                        new SimpleDateFormat("yyyy-MM-dd").format(data.getValue().getCommissionDate())
                )
        );

        // Disable export initially
        exportPdfButton.setDisable(true);
    }

    /**
     * Gathers filter input, applies filter, updates table and total.
     */
    @FXML
    private void generateReport() {
        LocalDate startLocal = startDatePicker.getValue();
        LocalDate endLocal = endDatePicker.getValue();
        String paymentType = filterPaymentMethodCombo.getValue();

        Date startDate = startLocal != null ? Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date endDate = endLocal != null ? Date.from(endLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

        List<Commissions> allCommissions = commissionsCRUD.getAllCommissions();

        // Filter by date and payment method
        filteredCommissions.setAll(allCommissions.stream()
                .filter(c -> {
                    if (startDate != null && c.getCommissionDate().before(startDate)) {
                        return false;
                    }
                    if (endDate != null && c.getCommissionDate().after(endDate)) {
                        return false;
                    }
                    if (!"Todos".equals(paymentType) && !c.getPaymentMethods().getType().equals(paymentType)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList())
        );

        commissionsTable.setItems(filteredCommissions);

        // Calculate and show total commission
        BigDecimal total = filteredCommissions.stream()
                .map(Commissions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commissionPerTypeTotal.setText(total.toPlainString());

        // Enable export if data present
        exportPdfButton.setDisable(filteredCommissions.isEmpty());
    }

    /**
     * Exports the currently displayed table to a PDF file.
     */
    @FXML
    private void exportToPDF() {
        if (filteredCommissions.isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de comisiones en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("reporte_comisiones.pdf");
        java.io.File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) {
            return;
        }

        String[] headers = {"ID Compra", "Método de Pago", "Total Comisión", "Fecha"};
        List<String[]> rows = filteredCommissions.stream()
                .map(c -> new String[]{
            c.getPurchases().getId().toString(),
            c.getPaymentMethods().getType(),
            c.getAmount().toPlainString(),
            new SimpleDateFormat("yyyy-MM-dd").format(c.getCommissionDate())
        })
                .collect(Collectors.toList());

        String subtitle = buildSubtitle();
        String footer = "Total de comisión: €" + commissionPerTypeTotal.getText();

        try {
            PDFUtils.createTableReportPdf(file.getAbsolutePath(), "Reporte de Pago de Comisiones", subtitle, headers, rows, footer);
            showAlert(Alert.AlertType.INFORMATION, "Exportación PDF", "¡Reporte exportado correctamente!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al exportar PDF", "No se pudo exportar el PDF:\n" + e.getMessage());
        }
    }

    /**
     * Builds a subtitle string describing the applied filters.
     */
    private String buildSubtitle() {
        List<String> parts = new ArrayList<>();
        if (startDatePicker.getValue() != null) {
            parts.add("Desde: " + startDatePicker.getValue());
        }
        if (endDatePicker.getValue() != null) {
            parts.add("Hasta: " + endDatePicker.getValue());
        }
        if (!"Todos".equals(filterPaymentMethodCombo.getValue())) {
            parts.add("Método de Pago: " + filterPaymentMethodCombo.getValue());
        }
        return String.join(" | ", parts);
    }

    /**
     * Utility to show alerts.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
