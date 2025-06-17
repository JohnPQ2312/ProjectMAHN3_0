package FXMLControllers.report;

import FXMLControllers.auxclasses.PDFUtils;
import PersistenceClasses.Purchases;
import PersistenceCRUD.PurchasesCRUD;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class SaleReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateButton;
    @FXML private Button exportPdfButton;
    @FXML private TableView<Purchases> salesTable;
    @FXML private TableColumn<Purchases, String> purchaseIdColumn;
    @FXML private TableColumn<Purchases, String> purchaseDateColumn;
    @FXML private TableColumn<Purchases, String> paymentMethodColumn;
    @FXML private TableColumn<Purchases, String> totalAmountColumn;
    @FXML private TableColumn<Purchases, String> totalCommissionColumn;

    private final PurchasesCRUD purchasesCRUD = new PurchasesCRUD();
    private ObservableList<Purchases> filteredSales = FXCollections.observableArrayList();

    /**
     * Initializes the report view. Sets up table columns and listeners.
     */
    @FXML
    public void initialize() {
        // Set up table columns
        purchaseIdColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId().toString()));
        purchaseDateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        formatPurchaseDate(data.getValue().getPurchaseDate())));
        paymentMethodColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPaymentMethods().getType()));
        totalAmountColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        formatEuro(data.getValue().getTotalAmount())));
        totalCommissionColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        formatEuro(data.getValue().getTotalCommission())));

        exportPdfButton.setDisable(true);
    }

    /**
     * Gathers filter input, applies filter, updates table.
     */
    @FXML
    private void generateReport() {
        LocalDate startLocal = startDatePicker.getValue();
        LocalDate endLocal = endDatePicker.getValue();

        Date startDate = startLocal != null ? Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date endDate = endLocal != null ? Date.from(endLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

        List<Purchases> allSales = purchasesCRUD.getAllPurchases();

        filteredSales.setAll(allSales.stream()
                .filter(p -> {
                    Date purchaseDate = p.getPurchaseDate();
                    if (p.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) return false;
                    if (startDate != null && purchaseDate.before(startDate)) return false;
                    if (endDate != null && purchaseDate.after(endDate)) return false;
                    return true;
                })
                .collect(Collectors.toList())
        );

        salesTable.setItems(filteredSales);

        // Enable export if there is data
        exportPdfButton.setDisable(filteredSales.isEmpty());
    }

    /**
     * Exports the currently displayed table to a PDF file, including total sold and total commission footers.
     */
    @FXML
    private void exportToPDF() {
        if (filteredSales.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de ventas en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("reporte_ventas.pdf");
        java.io.File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) return;

        String[] headers = {"ID Compra", "Fecha", "Método de pago", "Total", "Comisión"};
        List<String[]> rows = filteredSales.stream()
                .map(p -> new String[]{
                        p.getId().toString(),
                        formatPurchaseDate(p.getPurchaseDate()),
                        p.getPaymentMethods().getType(),
                        formatEuro(p.getTotalAmount()),
                        formatEuro(p.getTotalCommission())
                })
                .collect(Collectors.toList());

        String subtitle = buildSubtitle();
        String footer = buildFooterTotals();

        try {
            PDFUtils.createTableReportPdf(
                    file.getAbsolutePath(),
                    "Reporte de Ventas",
                    subtitle,
                    headers,
                    rows,
                    footer
            );
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
        if (startDatePicker.getValue() != null)
            parts.add("Desde: " + startDatePicker.getValue());
        if (endDatePicker.getValue() != null)
            parts.add("Hasta: " + endDatePicker.getValue());
        return String.join(" | ", parts);
    }

    /**
     * Builds the footer with total sold and total commissions.
     */
    private String buildFooterTotals() {
        BigDecimal totalSold = filteredSales.stream()
                .map(Purchases::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommission = filteredSales.stream()
                .map(Purchases::getTotalCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return "Total vendido: " + formatEuro(totalSold) + "   |   Total comisiones: " + formatEuro(totalCommission);
    }

    /**
     * Formats the purchase date for display.
     */
    private String formatPurchaseDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * Formats a BigDecimal as euro currency.
     */
    private String formatEuro(BigDecimal amount) {
        if (amount == null) return "€0.00";
        DecimalFormat euroFormat = new DecimalFormat("€#,##0.00");
        return euroFormat.format(amount);
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