package com.diabetesapp.controller;

import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {
    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        if (ViewNavigator.isAuthenticated()) {
            loginButton.setVisible(false);
            loginButton.setManaged(false);
        }
    }
    
    @FXML
    private void handleLogin() {
        ViewNavigator.navigateToLogin();
    }

    @FXML
    private void handleDashboard() {
        ViewNavigator.navigateToDashboard();
    }
}