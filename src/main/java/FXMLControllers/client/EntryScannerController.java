package FXMLControllers.client;

import FXMLControllers.auxclasses.QRUtils;
import PersistenceClasses.Entries;
import PersistenceClasses.Rooms;
import PersistenceCRUD.EntriesCRUD;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EntryScannerController {

    @FXML private Button uploadPDFButton;
    @FXML private Label selectedFileLabel;
    @FXML private TextArea allowedRoomsArea;
    @FXML private Label statusLabel;
    @FXML private Label dateLabel;

    @FXML
    public void initialize() {
        dateLabel.setText(LocalDate.now().toString());
    }

    @FXML
    private void handleUploadPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showOpenDialog(uploadPDFButton.getScene().getWindow());

        if (file == null) {
            selectedFileLabel.setText("Ningún archivo seleccionado");
            allowedRoomsArea.clear();
            statusLabel.setText("Adjunte un PDF con el QR de la entrada.");
            return;
        }

        selectedFileLabel.setText("Archivo seleccionado: " + file.getName());
        allowedRoomsArea.clear();
        statusLabel.setText("Procesando QR...");

        // === 1. Extrae el QR de la entrada desde el PDF ===
        String entryQrCode;
        try {
            entryQrCode = QRUtils.decodeQrFromFile(file);
        } catch (Exception ex) {
            statusLabel.setText("No se pudo leer el QR del PDF. ¿Es el ticket correcto?");
            return;
        }

        // === 2. Busca la entrada asociada al QR ===
        EntriesCRUD entriesCRUD = new EntriesCRUD();
        Entries entry = entriesCRUD.getEntryByQrCode(entryQrCode);

        if (entry == null) {
            statusLabel.setText("Entrada no encontrada o QR inválido.");
            return;
        }

        // === 3. Busca todas las entradas de la misma compra ===
        BigDecimal purchaseId = entry.getPurchases().getId();
        List<Entries> entriesOfPurchase = entriesCRUD.getEntriesByPurchaseId(purchaseId);

        // === 4. Filtra por entradas activas y para la fecha de hoy ===
        LocalDate today = LocalDate.now();
        List<Entries> validEntries = entriesOfPurchase.stream()
            .filter(en -> "ACTIVE".equalsIgnoreCase(en.getStatus()))
            .filter(en -> en.getVisitDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().isEqual(today))
            .collect(Collectors.toList());

        if (validEntries.isEmpty()) {
            statusLabel.setText("No existen entradas activas para hoy en la compra enlazada.");
            allowedRoomsArea.setText("");
            return;
        }

        // === 5. Muestra la información de las entradas válidas ===
        StringBuilder sb = new StringBuilder();
        for (Entries ent : validEntries) {
            Rooms room = ent.getRooms();
            sb.append("Sala: ").append(room.getName())
              .append(" - Museo: ").append(room.getMuseums().getName())
              .append(" - Estado: ").append(ent.getStatus())
              .append(" - Fecha: ").append(ent.getVisitDate())
              .append("\n");
        }
        allowedRoomsArea.setText(sb.toString());
        statusLabel.setText("¡Entradas válidas encontradas para esta compra!");

    }
}