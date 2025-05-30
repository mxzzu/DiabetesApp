package com.diabetesapp.controller;

import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {
    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        // If we're already authenticated, hide login/register buttons
        if (ViewNavigator.isAuthenticated()) {
            loginButton.setVisible(false);
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