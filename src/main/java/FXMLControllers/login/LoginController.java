package FXMLControllers.login;

import FXMLControllers.auxclasses.UserSession;
import PersistenceCRUD.UsersCRUD;
import PersistenceClasses.Users;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for handling user login.
 * Supports password visibility toggle, validation, and navigation.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label errorLabel;

    private final UsersCRUD usersCRUD = new UsersCRUD();

    /**
     * Initializes the password visibility toggle and field synchronization.
     */
    @FXML
    private void initialize() {
        // Synchronize managed/visible properties of password fields
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());

        // Listener for the "show password" checkbox
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

        // Synchronize the text between password fields
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

    /**
     * Handles the login process: validates credentials and navigates to main menu if successful.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please complete all fields.");
            return;
        }

        Users user = usersCRUD.getUserByCredentials(email, password);

        if (user == null) {
            showError("Incorrect credentials.");
            return;
        }

        UserSession.setCurrentUser(user);
        showError("");
        goToMainMenu();
    }

    /**
     * Navigates to the registration screen.
     */
    @FXML
    private void handleGoToRegister() {
        try {
            program2.projectmahn3_0.App.setRoot("login/Register");
        } catch (IOException e) {
            showError("Could not load the registration screen.");
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the main menu after successful login.
     */
    @FXML
    private void goToMainMenu() {
        try {
            program2.projectmahn3_0.App.setRoot("admin/MainMenu");
        } catch (IOException e) {
            showError("Could not load the main menu.");
            e.printStackTrace();
        }
    }

    /**
     * Displays an error message in the error label (styled in red).
     * @param msg The message to display
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}