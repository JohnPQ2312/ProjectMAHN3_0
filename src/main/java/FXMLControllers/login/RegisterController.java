package FXMLControllers.login;

import PersistenceCRUD.UsersCRUD;
import PersistenceClasses.Users;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for handling user registration.
 * Validates input, checks for duplicate email, and creates a new user account.
 */
public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UsersCRUD usersCRUD = new UsersCRUD();

    /**
     * Handles the registration logic when the user submits the registration form.
     * Validates all fields, checks for email uniqueness, and creates the user.
     * Shows errors or success messages in the error label.
     */
    @FXML
    private void handleRegister() {
        String username = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please complete all fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }
        if (usersCRUD.getUserByEmail(email) != null) {
            showError("Email is already registered.");
            return;
        }
        if (!phone.matches("\\d{8,15}")) {
            showError("Phone must have between 8 and 15 digits.");
            return;
        }

        Users user = new Users();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("client");
        user.setPhone(phone);
        user.setRegistrationDate(new java.util.Date());

        usersCRUD.addUser(user);

        showError("Registration successful. You can now log in.");
    }

    /**
     * Navigates back to the login screen.
     */
    @FXML
    private void backToLogin() {
        try {
            program2.projectmahn3_0.App.setRoot("login/Login");
        } catch (IOException e) {
            showError("Could not load the login screen.");
            e.printStackTrace();
        }
    }

    /**
     * Displays an error or information message in the error label.
     * @param msg The message to display
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}