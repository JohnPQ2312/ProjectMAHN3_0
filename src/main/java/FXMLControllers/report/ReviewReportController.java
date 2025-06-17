package FXMLControllers.report;

import FXMLControllers.auxclasses.PDFUtils;
import PersistenceClasses.Museums;
import PersistenceClasses.Reviews;
import PersistenceClasses.Rooms;
import PersistenceCRUD.MuseumsCRUD;
import PersistenceCRUD.ReviewsCRUD;
import PersistenceCRUD.RoomsCRUD;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReviewReportController {

    @FXML private ComboBox<String> filterMuseumComboBox;
    @FXML private ComboBox<String> orderComboBox;
    @FXML private Button generateButton;
    @FXML private Button exportPdfButton;
    @FXML private Button cleanFilterBtn;
    @FXML private TableView<RoomRatingSummary> roomRatingsTable;
    @FXML private TableColumn<RoomRatingSummary, String> roomColumn;
    @FXML private TableColumn<RoomRatingSummary, String> museumColumn;
    @FXML private TableColumn<RoomRatingSummary, String> avgRatingColumn;
    @FXML private TableColumn<RoomRatingSummary, String> reviewCountColumn;

    private final MuseumsCRUD museumsCRUD = new MuseumsCRUD();
    private final RoomsCRUD roomsCRUD = new RoomsCRUD();
    private final ReviewsCRUD reviewsCRUD = new ReviewsCRUD();

    private ObservableList<RoomRatingSummary> displayedSummaries = FXCollections.observableArrayList();

    /**
     * Initializes the report view. Loads museums, sets up order options and listeners.
     */
    @FXML
    public void initialize() {
        // Load museums into combo box
        List<Museums> museums = museumsCRUD.getAllMuseums();
        filterMuseumComboBox.getItems().clear();
        filterMuseumComboBox.getItems().add("Todos");
        filterMuseumComboBox.getItems().addAll(museums.stream().map(Museums::getName).collect(Collectors.toList()));
        filterMuseumComboBox.getSelectionModel().selectFirst();

        // Set up order options
        orderComboBox.getItems().clear();
        orderComboBox.getItems().addAll("Mejor valoradas(10)", "Peor valoradas(10)");
        orderComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        roomColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().roomName));
        museumColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().museumName));
        avgRatingColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedAvgRating()));
        reviewCountColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().reviewCount)));

        exportPdfButton.setDisable(true);
    }

    /**
     * Gathers filter input, applies filter and ordering, updates table.
     */
    @FXML
    private void generateReport() {
        String selectedMuseum = filterMuseumComboBox.getValue();
        String order = orderComboBox.getValue();

        // Aggregate reviews data by room
        List<Reviews> allReviews = reviewsCRUD.getAllReviews();

        // Map: Room -> List<Review>
        Map<Rooms, List<Reviews>> reviewsByRoom = allReviews.stream()
                .collect(Collectors.groupingBy(Reviews::getRooms));

        // Build summaries
        List<RoomRatingSummary> summaries = reviewsByRoom.entrySet().stream()
                .map(e -> new RoomRatingSummary(
                        e.getKey().getName(),
                        e.getKey().getMuseums().getName(),
                        e.getValue().stream().mapToInt(r -> r.getRating()).average().orElse(0.0),
                        e.getValue().size()
                ))
                .collect(Collectors.toList());

        // Apply museum filter
        if (!"Todos".equals(selectedMuseum)) {
            summaries = summaries.stream()
                    .filter(s -> s.museumName.equals(selectedMuseum))
                    .collect(Collectors.toList());
        }

        // Apply ordering
        if ("Mejor valoradas(10)".equals(order)) {
            summaries = summaries.stream()
                    .sorted(Comparator.comparingDouble(RoomRatingSummary::getAvgRating).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            summaries = summaries.stream()
                    .sorted(Comparator.comparingDouble(RoomRatingSummary::getAvgRating))
                    .limit(10)
                    .collect(Collectors.toList());
        }

        displayedSummaries.setAll(summaries);
        roomRatingsTable.setItems(displayedSummaries);

        exportPdfButton.setDisable(displayedSummaries.isEmpty());
    }

    /**
     * Exports the currently displayed table to a PDF file.
     */
    @FXML
    private void exportToPDF() {
        if (displayedSummaries.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de valoración de salas en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("reporte_valoracion_salas.pdf");
        java.io.File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) return;

        String[] headers = {"Sala", "Museo", "Promedio Valoración", "Cantidad de Reseñas"};
        List<String[]> rows = displayedSummaries.stream()
                .map(s -> new String[]{
                        s.roomName,
                        s.museumName,
                        s.getFormattedAvgRating(),
                        String.valueOf(s.reviewCount)
                })
                .collect(Collectors.toList());

        String subtitle = buildSubtitle();
        String footer = "Promedio general de valoración: " + calculateOverallAverage() + " / 5";

        try {
            PDFUtils.createTableReportPdf(file.getAbsolutePath(), "Reporte de Valoración de Salas", subtitle, headers, rows, footer);
            showAlert(Alert.AlertType.INFORMATION, "Exportación PDF", "¡Reporte exportado correctamente!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al exportar PDF", "No se pudo exportar el PDF:\n" + e.getMessage());
        }
    }

    /**
     * Resets all filters to default values.
     */
    @FXML
    private void resetFilters() {
        filterMuseumComboBox.getSelectionModel().selectFirst();
        orderComboBox.getSelectionModel().selectFirst();
        roomRatingsTable.getItems().clear();
        exportPdfButton.setDisable(true);
    }

    /**
     * Builds a subtitle string describing the applied filters.
     */
    private String buildSubtitle() {
        List<String> parts = new ArrayList<>();
        if (!"Todos".equals(filterMuseumComboBox.getValue()))
            parts.add("Museo: " + filterMuseumComboBox.getValue());
        parts.add("Orden: " + orderComboBox.getValue());
        return String.join(" | ", parts);
    }

    /**
     * Calculates the overall average rating for the displayed rooms.
     */
    private String calculateOverallAverage() {
        if (displayedSummaries.isEmpty()) return "N/A";
        double sum = displayedSummaries.stream().mapToDouble(RoomRatingSummary::getAvgRating).sum();
        double avg = sum / displayedSummaries.size();
        return new DecimalFormat("#.##").format(avg);
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

    /**
     * Class for room rating summary.
     */
    public static class RoomRatingSummary {
        String roomName;
        String museumName;
        double avgRating;
        int reviewCount;

        public RoomRatingSummary(String roomName, String museumName, double avgRating, int reviewCount) {
            this.roomName = roomName;
            this.museumName = museumName;
            this.avgRating = avgRating;
            this.reviewCount = reviewCount;
        }

        public double getAvgRating() { return avgRating; }
        public String getFormattedAvgRating() {
            return new DecimalFormat("#.##").format(avgRating);
        }
    }
}