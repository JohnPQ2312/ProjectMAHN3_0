package FXMLControllers.client;

import PersistenceClasses.Entries;
import PersistenceClasses.Users;
import PersistenceCRUD.EntriesCRUD;
import PersistenceCRUD.UsersCRUD;
import PersistenceCRUD.TransfersCRUD;
import PersistenceClasses.Purchases;
import java.math.BigDecimal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SellEntryController implements Initializable {

    @FXML
    private TableView<Entries> myTicketsTable;
    @FXML
    private TableColumn<Entries, java.math.BigDecimal> ticketIdColumn;
    @FXML
    private TableColumn<Entries, String> museumColumn;
    @FXML
    private TableColumn<Entries, String> roomColumn;
    @FXML
    private TableColumn<Entries, String> dateColumn;
    @FXML
    private TableColumn<Entries, String> priceColumn;
    @FXML
    private TableColumn<Entries, String> statusColumn;

    @FXML
    private Button transferButton;
    @FXML
    private Button refundButton;
    @FXML
    private Label transferToLabel;
    @FXML
    private TextField receiverField;
    @FXML
    private Button confirmTransferButton;
    @FXML
    private Label statusLabel;

    private ObservableList<Entries> ticketList = FXCollections.observableArrayList();
    private Entries selectedEntry;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        museumColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRooms().getMuseums().getName()));
        roomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRooms().getName()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getVisitDate() != null ? cellData.getValue().getVisitDate().toString() : ""));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPrice() != null ? cellData.getValue().getPrice().toPlainString() : ""));
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        myTicketsTable.setItems(ticketList);

        hideTransferFields();
        loadUserTickets();
    }

    private void loadUserTickets() {
        Users currentUser = FXMLControllers.auxclasses.UserSession.getCurrentUser();
        if (currentUser == null) {
            statusLabel.setText("Sesión expirada.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        List<Entries> entries = new EntriesCRUD().getAllEntries().stream()
                .filter(e -> e.getPurchases().getUsers().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
        ticketList.setAll(entries);
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        selectedEntry = myTicketsTable.getSelectionModel().getSelectedItem();
        boolean isActive = selectedEntry != null && "ACTIVE".equalsIgnoreCase(selectedEntry.getStatus());
        transferButton.setDisable(!isActive);
        refundButton.setDisable(!isActive);
        hideTransferFields();
        statusLabel.setText("");
    }

    @FXML
    private void handleTransferButton() {
        transferToLabel.setVisible(true);
        receiverField.setVisible(true);
        confirmTransferButton.setVisible(true);
        receiverField.clear();
        statusLabel.setText("");
    }

    @FXML
    private void handleConfirmTransfer() {
        if (selectedEntry == null) {
            return;
        }
        String receiverEmail = receiverField.getText().trim();

        if (receiverEmail.isEmpty()) {
            statusLabel.setText("Debes ingresar un correo de destinatario.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Users receiver = new UsersCRUD().getUserByEmail(receiverEmail);
        Users currentUser = FXMLControllers.auxclasses.UserSession.getCurrentUser();

        if (receiver == null) {
            statusLabel.setText("Destinatario no encontrado.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        if (currentUser != null && receiver.getId().equals(currentUser.getId())) {
            statusLabel.setText("No puedes transferirte una entrada a ti mismo.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            //New buy for receiver
            Purchases nuevaCompra = new Purchases();
            nuevaCompra.setUsers(receiver);
            nuevaCompra.setTotalAmount(BigDecimal.ZERO); // Price 0 because it's a transfered ticket
            nuevaCompra.setTotalCommission(BigDecimal.ZERO); //Commission 0 because it's a transfered ticket
            nuevaCompra.setPurchaseDate(new java.util.Date());
            nuevaCompra.setPaymentMethods(selectedEntry.getPurchases().getPaymentMethods());
            new PersistenceCRUD.PurchasesCRUD().addPurchase(nuevaCompra);

            //Entry reasigned to new user
            selectedEntry.setPurchases(nuevaCompra);
            selectedEntry.setStatus("ACTIVE");
            new EntriesCRUD().updateEntry(selectedEntry);

            //Persist transfer
            new TransfersCRUD().registerTransfer(selectedEntry, currentUser, receiver);

            statusLabel.setText("¡Entrada transferida con éxito!");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception ex) {
            statusLabel.setText("Error al transferir: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }

        hideTransferFields();
        loadUserTickets();
    }

    @FXML
    private void handleRefund() {
        if (selectedEntry == null) {
            return;
        }

        try {
            selectedEntry.setStatus("REFUNDED");
            new EntriesCRUD().updateEntry(selectedEntry);

            statusLabel.setText("¡Reembolso solicitado!");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception ex) {
            statusLabel.setText("Error al solicitar reembolso: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
        loadUserTickets();
    }

    private void hideTransferFields() {
        transferToLabel.setVisible(false);
        receiverField.setVisible(false);
        confirmTransferButton.setVisible(false);
    }
}
