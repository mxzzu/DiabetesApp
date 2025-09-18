package com.diabetesapp.controller;

import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class DoctorDashboardController {
    @FXML
    private Label notificationLabel;

    @FXML
    private Text therapyLabel, patientsLabel;

    @FXML
    public void initialize() {

    }

    @FXML
    private void createTherapy() {
        ViewNavigator.navigateToTherapy(null);
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
