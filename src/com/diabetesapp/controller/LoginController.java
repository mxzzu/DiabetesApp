package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.PasswordUtil;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.TogglePasswordField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private VBox passwordBox;
    
    @FXML
    private Label statusLabel;
    
    private UserRepository userRepository;
    TogglePasswordField passwordFieldConToggle = new TogglePasswordField();

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        statusLabel.setVisible(false);

        passwordBox.getChildren().add(passwordFieldConToggle);
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordFieldConToggle.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        User user = userRepository.getUser(username);
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            // Login successful
            ViewNavigator.setAuthenticatedUser(username, user.getUserType());
            ViewNavigator.navigateToDashboard();
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}