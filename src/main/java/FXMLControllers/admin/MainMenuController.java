/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FXMLControllers.admin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author jp570
 */
public class MainMenuController implements Initializable {

    @FXML
    private AnchorPane mainContent;
    
    @FXML
    private Label userLbl, roleLbl, screenLbl;
    
    @FXML
    private Button openMuseums, openRooms, openCollections, openSpecies, openThemes, openPrices, openPaymentMethods, openUsers, openRoomImages, openEntryReports, openReviewReports, openCommissionReports, openSaleReports, systemExit;
    
    @FXML
    private Button openEntryScanner, openPurchaseEntry, openReviewScreen, openSellEntry;
    
    @FXML
    private void exit() {
        Platform.exit();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void handleNavigation(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String viewKey = clickedButton.getId();
        loadView(viewKey);
    }
    
    @FXML
    public void loadView(String key) {
        String fxmlPath = null;

        switch (key) {
            case "openMuseums":
                screenLbl.setText("Pantalla actual: Museos");
                fxmlPath = "/fxml/admin/MuseumManager.fxml";
                break;
            case "openRooms":
                screenLbl.setText("Pantalla actual: Salas");
                fxmlPath = "/fxml/admin/RoomManager.fxml";
                break;
            case "openUsers":
                screenLbl.setText("Pantalla actual: Usuarios");
                fxmlPath = "/fxml/admin/UserManager.fxml";
                break;
            case "openCollections":
                screenLbl.setText("Pantalla actual: Colecciones");
                fxmlPath = "/fxml/admin/CollectionManager.fxml";
                break;
            case "openSpecies":
                screenLbl.setText("Pantalla actual: Especies");
                fxmlPath = "/fxml/admin/SpeciesManager.fxml";
                break;
            case "openThemes":
                screenLbl.setText("Pantalla actual: Tematicas");
                fxmlPath = "/fxml/admin/ThemeManager.fxml";
                break;
            case "openPrices":
                screenLbl.setText("Pantalla actual: Precios");
                fxmlPath = "/fxml/admin/PriceManager.fxml";
                break;
            case "openPaymentMethods":
                screenLbl.setText("Pantalla actual: Metodos de pago");
                fxmlPath = "/fxml/admin/PaymentMethodManager.fxml";
                break;
            case "openEntryReports":
                screenLbl.setText("Pantalla actual: Reporte entradas");
                fxmlPath = "/fxml/report/EntryReport.fxml";
                break;
            case "openReviewReports":
                screenLbl.setText("Pantalla actual: Reporte reseñas");
                fxmlPath = "/fxml/report/ReviewReport.fxml";
                break;
            case "openCommissionReports":
                screenLbl.setText("Pantalla actual: Reporte comisiones");
                fxmlPath = "/fxml/report/CommissionReport.fxml";
                break;
            case "openSaleReports":
                screenLbl.setText("Pantalla actual: Reporte ventas");
                fxmlPath = "/fxml/report/SaleReport.fxml";
                break;
            case "openRoomImages":
                screenLbl.setText("Pantalla actual: Imagenes salas");
                fxmlPath = "/fxml/admin/RoomImageManager.fxml";
                break;     
            case "openEntryScanner":
                screenLbl.setText("Pantalla actual: Validacion");
                fxmlPath = "/fxml/client/EntryScanner.fxml";
                break; 
            case "openPurchaseEntry":
                screenLbl.setText("Pantalla actual: Compra entradas");
                fxmlPath = "/fxml/client/PurchaseEntryScreen.fxml";
                break; 
            case "openReviewScreen":
                screenLbl.setText("Pantalla actual: Reseñas");
                fxmlPath = "/fxml/client/ReviewScreen.fxml";
                break; 
            case "openSellEntry":
                screenLbl.setText("Pantalla actual: Admin entradas");
                fxmlPath = "/fxml/client/SellEntry.fxml";
                break;                 
            default:
                System.out.println("Vista no reconocida: " + key);
                return;
        }

        if (fxmlPath == null) {
            System.out.println("Vista no reconocida: " + key);
            return;
        }

        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.setMaxSize(1030, 720);
            mainContent.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
