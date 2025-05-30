package com.diabetesapp.controller;

import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DoctorDashboardController {
    @FXML
    private Label welcomeLabel;

    private final String username = ViewNavigator.getAuthenticatedUser();

    @FXML
    public void initialize() {
        // This is a protected view, so we should always have an authenticated user
        welcomeLabel.setText("Welcome to your doctor dashboard, " + username + "!");
    }

    @FXML
    private void createTherapy() {
        ViewNavigator.navigateToTherapy();
    }

    @FXML
    private void handlePatients() {
        ViewNavigator.navigateToPatients();
    }

    @FXML
    private void handleViewProfile() {
        ViewNavigator.navigateToProfile();
    }

    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }
}
