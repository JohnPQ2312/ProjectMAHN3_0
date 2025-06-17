package FXMLControllers.login;

import FXMLControllers.auxclasses.UserSession;
import PersistenceCRUD.UsersCRUD;
import PersistenceClasses.Users;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private CheckBox showPasswordCheck;
    @FXML
    private Label errorLabel;

    private final UsersCRUD usersCRUD = new UsersCRUD();

    @FXML
    private void initialize() {
        // Sincroniza los valores al alternar entre TextField y PasswordField
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());

        // Listener para el check de mostrar contraseña
        showPasswordCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
            }
        });

        // Sincroniza los valores al escribir en uno u otro
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!showPasswordCheck.isSelected()) {
                visiblePasswordField.setText(newVal);
            }
        });
        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setText(newVal);
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Por favor, completa todos los campos.");
            return;
        }

        Users user = usersCRUD.getUserByCredentials(email, password);

        if (user == null) {
            showError("Credenciales incorrectas.");
            return;
        }

        UserSession.setCurrentUser(user);
        showError("");
        goToMainMenu();
    }

    @FXML
    private void handleGoToRegister() {
        try {
            program2.projectmahn3_0.App.setRoot("login/Register");
        } catch (IOException e) {
            showError("No se pudo cargar el registro.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMainMenu() {
        try {
            program2.projectmahn3_0.App.setRoot("admin/MainMenu");
        } catch (IOException e) {
            showError("No se pudo cargar el menú principal.");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}
