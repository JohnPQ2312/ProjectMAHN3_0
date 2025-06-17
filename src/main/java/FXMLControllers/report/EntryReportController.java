package FXMLControllers.report;

import FXMLControllers.auxclasses.PDFUtils;
import PersistenceClasses.Entries;
import PersistenceClasses.Rooms;
import PersistenceClasses.Purchases;
import PersistenceCRUD.EntriesCRUD;
import PersistenceCRUD.RoomsCRUD;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class EntryReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> filterRoomEntry;
    @FXML private Button generateButton;
    @FXML private Button exportPdfButton;
    @FXML private TableView<Entries> entriesTable;
    @FXML private TableColumn<Entries, String> entryIdColumn;
    @FXML private TableColumn<Entries, String> roomColumn;
    @FXML private TableColumn<Entries, String> statusColumn;
    @FXML private TableColumn<Entries, String> purchaseDateColumn;

    private final EntriesCRUD entriesCRUD = new EntriesCRUD();
    private final RoomsCRUD roomsCRUD = new RoomsCRUD();

    private ObservableList<Entries> filteredEntries = FXCollections.observableArrayList();

    /**
     * Initializes the report view. Loads rooms and sets up listeners.
     */
    @FXML
    public void initialize() {
        // Load available rooms for filter
        List<Rooms> allRooms = roomsCRUD.getAllRooms();
        filterRoomEntry.getItems().clear();
        filterRoomEntry.getItems().add("Todas");
        filterRoomEntry.getItems().addAll(allRooms.stream().map(Rooms::getName).collect(Collectors.toList()));
        filterRoomEntry.getSelectionModel().selectFirst();

        // Set up table columns
        entryIdColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId().toString()));
        roomColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRooms().getName()));
        statusColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        purchaseDateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        formatPurchaseDate(data.getValue().getPurchases()))
        );

        exportPdfButton.setDisable(true);
    }

    /**
     * Gathers filter input, applies filter, updates table.
     */
    @FXML
    private void generateReport() {
        LocalDate startLocal = startDatePicker.getValue();
        LocalDate endLocal = endDatePicker.getValue();
        String selectedRoom = filterRoomEntry.getValue();

        Date startDate = startLocal != null ? Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date endDate = endLocal != null ? Date.from(endLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

        List<Entries> allEntries = entriesCRUD.getAllEntries();

        // Filter by date and room
        filteredEntries.setAll(allEntries.stream()
                .filter(e -> {
                    // Filter by date of purchase (not visit date)
                    Date purchaseDate = e.getPurchases().getPurchaseDate();
                    if (startDate != null && purchaseDate.before(startDate)) return false;
                    if (endDate != null && purchaseDate.after(endDate)) return false;
                    if (!"Todas".equals(selectedRoom) && !e.getRooms().getName().equals(selectedRoom)) return false;
                    return true;
                })
                .collect(Collectors.toList())
        );

        entriesTable.setItems(filteredEntries);

        // Enable export if there is data
        exportPdfButton.setDisable(filteredEntries.isEmpty());
    }

    /**
     * Exports the currently displayed table to a PDF file.
     */
    @FXML
    private void exportToPDF() {
        if (filteredEntries.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de entradas vendidas en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("reporte_entradas_vendidas.pdf");
        java.io.File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) return;

        String[] headers = {"ID", "Sala", "Estado", "Fecha compra"};
        List<String[]> rows = filteredEntries.stream()
                .map(e -> new String[]{
                        e.getId().toString(),
                        e.getRooms().getName(),
                        e.getStatus(),
                        formatPurchaseDate(e.getPurchases())
                })
                .collect(Collectors.toList());

        String subtitle = buildSubtitle();
        String footer = "Total de entradas vendidas: " + filteredEntries.size();

        try {
            PDFUtils.createTableReportPdf(
                    file.getAbsolutePath(),
                    "Reporte de Entradas Vendidas",
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
        if (!"Todas".equals(filterRoomEntry.getValue()))
            parts.add("Sala: " + filterRoomEntry.getValue());
        return String.join(" | ", parts);
    }

    /**
     * Formats the purchase date for display.
     */
    private String formatPurchaseDate(Purchases purchase) {
        if (purchase == null || purchase.getPurchaseDate() == null) return "N/A";
        return new SimpleDateFormat("yyyy-MM-dd").format(purchase.getPurchaseDate());
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