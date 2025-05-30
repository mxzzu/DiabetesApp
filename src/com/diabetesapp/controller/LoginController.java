package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.PasswordUtil;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginController {
    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;
    
    @FXML
    private Label statusLabel;
    
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        statusLabel.setVisible(false);
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
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