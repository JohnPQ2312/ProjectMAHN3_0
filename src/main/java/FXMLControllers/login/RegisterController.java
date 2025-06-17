package FXMLControllers.login;

import PersistenceCRUD.UsersCRUD;
import PersistenceClasses.Users;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UsersCRUD usersCRUD = new UsersCRUD();

    @FXML
    private void handleRegister() {
        String username = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Completa todos los campos.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden.");
            return;
        }
        if (usersCRUD.getUserByEmail(email) != null) {
            showError("El correo ya está registrado.");
            return;
        }
        
        if (!phone.matches("\\d{8,15}")) {
            showError("El teléfono debe tener entre 8 y 15 dígitos.");
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

        showError("Registro exitoso. Ahora puedes iniciar sesión.");
    }
    
    @FXML
    private void backToLogin() {
        try {
            program2.projectmahn3_0.App.setRoot("login/Login");
        } catch (IOException e) {
            showError("No se pudo cargar el registro.");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}