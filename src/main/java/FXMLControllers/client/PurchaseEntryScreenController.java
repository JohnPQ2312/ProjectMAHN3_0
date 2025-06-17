package FXMLControllers.client;

import FXMLControllers.auxclasses.UserSession;
import FXMLControllers.auxclasses.TicketUtils;
import PersistenceCRUD.CommissionsCRUD;
import PersistenceClasses.Users;
import PersistenceClasses.Museums;
import PersistenceClasses.Rooms;
import PersistenceClasses.PaymentMethods;
import PersistenceClasses.Prices;
import PersistenceClasses.Purchases;
import PersistenceClasses.Entries;
import PersistenceCRUD.MuseumsCRUD;
import PersistenceCRUD.RoomsCRUD;
import PersistenceCRUD.PaymentMethodsCRUD;
import PersistenceCRUD.PricesCRUD;
import PersistenceCRUD.PurchasesCRUD;
import PersistenceCRUD.EntriesCRUD;
import PersistenceClasses.Commissions;
import java.io.File;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.math.BigDecimal;
import java.util.Date;
import javafx.stage.FileChooser;
import javafx.stage.Window;

//Controlador para la pantalla de compra de entradas.
public class PurchaseEntryScreenController implements Initializable {

    @FXML
    private TextField visitorNameField;
    @FXML
    private ComboBox<String> cardTypeField;
    @FXML
    private ComboBox<String> museumComboBox;
    @FXML
    private ComboBox<String> roomComboBox;
    @FXML
    private DatePicker visitDatePicker;
    @FXML
    private Button addEntryButton;
    @FXML
    private Button sellButton;
    @FXML
    private Button deleteEntryButton;
    @FXML
    private TableView<EntryRow> entriesTable;
    @FXML
    private TableColumn<EntryRow, String> museumColumn, roomColumn, dateColumn, daysColumn, priceColumn;
    @FXML
    private Label subtotalLabel, ivaLabel, totalLabel, commissionLabel;

    private ObservableList<EntryRow> entryList = FXCollections.observableArrayList();

    private List<Museums> museums;
    private List<Rooms> rooms;
    private List<PaymentMethods> paymentMethods;

    // Mapas para relacionar nombres con entidades reales
    private Map<String, Museums> museumMap = new HashMap<>();
    private Map<String, Rooms> roomMap = new HashMap<>();
    private Map<String, PaymentMethods> paymentMethodMap = new HashMap<>();

    private double subtotal = 0.0;
    private double iva = 0.0;
    private double total = 0.0;
    private double method_commission = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Users user = UserSession.getCurrentUser();
        if (user != null) {
            visitorNameField.setText(user.getName());
        }

        paymentMethods = new PaymentMethodsCRUD().getAllPaymentMethods();
        for (PaymentMethods pm : paymentMethods) {
            cardTypeField.getItems().add(pm.getType());
            paymentMethodMap.put(pm.getType(), pm);
        }

        museums = new MuseumsCRUD().getAllMuseums();
        for (Museums m : museums) {
            museumComboBox.getItems().add(m.getName());
            museumMap.put(m.getName(), m);
        }

        // Al cambiar de museo, cargar las salas correspondientes
        museumComboBox.setOnAction(e -> {
            roomComboBox.getItems().clear();
            roomMap.clear();
            if (museumComboBox.getValue() != null) {
                Museums selectedMuseum = museumMap.get(museumComboBox.getValue());
                rooms = new RoomsCRUD().getAllRooms();
                for (Rooms r : rooms) {
                    if (r.getMuseums().getId().equals(selectedMuseum.getId())) {
                        roomComboBox.getItems().add(r.getName());
                        roomMap.put(r.getName(), r);
                    }
                }
            }
        });

        // Configurar columnas de la tabla
        museumColumn.setCellValueFactory(new PropertyValueFactory<>("museum"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        daysColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        entriesTable.setItems(entryList);

        addEntryButton.setOnAction(e -> handleAddEntry());
        deleteEntryButton.setOnAction(e -> handleDeleteEntry());
        sellButton.setOnAction(e -> handleSell());

        // Habilitar/deshabilitar botones según si hay entradas
        entryList.addListener((ListChangeListener<EntryRow>) c -> {
            sellButton.setDisable(entryList.isEmpty());
            deleteEntryButton.setDisable(entryList.isEmpty());
        });

        // Estado inicial
        sellButton.setDisable(true);
        deleteEntryButton.setDisable(true);
    }

    //Agrega una entrada temporal a la tabla, con conversiones correctas.
    @FXML
    private void handleAddEntry() {
        String museumName = museumComboBox.getValue();
        String roomName = roomComboBox.getValue();
        LocalDate visitDate = visitDatePicker.getValue();

        if (museumName == null || roomName == null || visitDate == null) {
            showAlert("Completa todos los campos para agregar la entrada.");
            return;
        }

        Rooms selectedRoom = roomMap.get(roomName);

        // Buscar el precio de la sala
        List<Prices> allPrices = new PricesCRUD().getAllPrices();
        Prices priceObj = null;
        for (Prices p : allPrices) {
            if (p.getRooms().getId().equals(selectedRoom.getId())) {
                priceObj = p;
                break;
            }
        }

        double price = 0.0;
        if (priceObj != null) {
            if (visitDate.getDayOfWeek().getValue() == 7) {
                price = priceObj.getSundayPrice().doubleValue();
            } else {
                price = priceObj.getNormalPrice().doubleValue();
            }
        }

        // Días hasta la visita
        long daysToVisit = ChronoUnit.DAYS.between(LocalDate.now(), visitDate);

        // Agregar la entrada temporal a la tabla
        EntryRow entry = new EntryRow(museumName, roomName, visitDate.toString(), String.valueOf(daysToVisit), String.format("%.2f", price));
        entryList.add(entry);

        //Update final prices
        updateTotals();
    }

    //Elimina una entrada seleccionada de la tabla.
    @FXML
    private void handleDeleteEntry() {
        EntryRow selected = entriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            entryList.remove(selected);
            updateTotals();
        } else {
            showAlert("Selecciona una entrada para eliminar.");
        }
    }

    /**
     * Realiza el proceso de compra y conversiones correctas. Persiste la compra
     * y las entradas, y genera los tickets PDF con QR.
     */
    @FXML
    private void handleSell() {
        if (entryList.isEmpty()) {
            showAlert("No hay entradas para vender.");
            return;
        }

        Users user = UserSession.getCurrentUser();
        if (user == null) {
            showAlert("Sesión de usuario expirada.");
            return;
        }

        String paymentType = cardTypeField.getValue();
        if (paymentType == null) {
            showAlert("Selecciona el método de pago.");
            return;
        }
        PaymentMethods pm = paymentMethodMap.get(paymentType);

        // Saves total and commission for persistence
        BigDecimal totalBD = BigDecimal.valueOf(total);
        BigDecimal commissionBD = BigDecimal.valueOf(method_commission);

        Purchases purchase = new Purchases();
        purchase.setUsers(user);
        purchase.setPaymentMethods(pm);
        purchase.setTotalAmount(totalBD);
        purchase.setTotalCommission(commissionBD);
        purchase.setPurchaseDate(new Date());

        PurchasesCRUD purchasesCRUD = new PurchasesCRUD();
        purchasesCRUD.addPurchase(purchase);

        Commissions commission = new Commissions();
        commission.setAmount(commissionBD);
        commission.setCommissionDate(new Date());
        commission.setPaymentMethods(pm);
        commission.setPurchases(purchase);

        CommissionsCRUD commissionsCRUD = new CommissionsCRUD();
        commissionsCRUD.addCommission(commission);

        // Persistir cada entrada y generar su ticket PDF con QR
        EntriesCRUD entriesCRUD = new EntriesCRUD();
        for (EntryRow er : entryList) {
            Rooms room = roomMap.get(er.getRoom());
            LocalDate visitDate = LocalDate.parse(er.getDate());
            double price = Double.parseDouble(er.getPrice().replace(",", "."));

            // Generar código único para el ticket
            String ticketCode = UUID.randomUUID().toString();

            Entries entry = new Entries();
            entry.setPurchases(purchase);
            entry.setRooms(room);
            entry.setVisitDate(java.sql.Date.valueOf(visitDate));
            entry.setPrice(BigDecimal.valueOf(price));
            entry.setQrCode(ticketCode);
            entry.setStatus("ACTIVE");
            entriesCRUD.addEntry(entry);

            // Generar entrada PDF con QR
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar ticket PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            fileChooser.setInitialFileName("ticket_" + entry.getQrCode() + ".pdf");
            Window stage = sellButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    TicketUtils.generateTicketPdf(entry, file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("No se pudo generar el ticket PDF para una de las entradas.");
                }
            }
        }

        showAlert("¡Compra realizada con éxito! Los tickets han sido generados.");
        entryList.clear();
        //Clean prices for new sell
        updateTotals();
    }

    //Actualiza los totales (subtotal, IVA y total) en la interfaz y logica.
    private void updateTotals() {
        subtotal = entryList.stream()
                .mapToDouble(er -> Double.parseDouble(er.getPrice().replace(",", ".")))
                .sum();
        iva = subtotal * 0.13; // 13% Commission

        String paymentType = cardTypeField.getValue();
        PaymentMethods pm = paymentType != null ? paymentMethodMap.get(paymentType) : null;

        method_commission = 0.0;
        if (pm != null) { //Commission per type of payment
            method_commission = (subtotal + iva) * pm.getCommissionPercentage().doubleValue() / 100.0; 
        }

        total = subtotal + iva + method_commission;

        subtotalLabel.setText(String.format("%.2f euros", subtotal));
        ivaLabel.setText(String.format("%.2f euros", iva));
        commissionLabel.setText(String.format("%.2f euros", method_commission));
        totalLabel.setText(String.format("%.2f euros", total));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    //Clase auxiliar para mostrar los datos de cada entrada en la tabla.
    public static class EntryRow {

        private String museum;
        private String room;
        private String date;
        private String days;
        private String price;

        public EntryRow(String museum, String room, String date, String days, String price) {
            this.museum = museum;
            this.room = room;
            this.date = date;
            this.days = days;
            this.price = price;
        }

        public String getMuseum() {
            return museum;
        }

        public String getRoom() {
            return room;
        }

        public String getDate() {
            return date;
        }

        public String getDays() {
            return days;
        }

        public String getPrice() {
            return price;
        }
    }
}
