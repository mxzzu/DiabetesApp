package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DoctorDashboardController {
    @FXML
    private Label notificationLabel;

    @FXML
    private TextFlow patientsLabel;

    private UserRepository userRepository;
    private ObservableList<String> patients;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        fetchPatientsNumber();
    }

    private void fetchPatientsNumber() {
        patients = userRepository.getAllDataPatients(ViewNavigator.getAuthenticatedUsername());
        if (patients.isEmpty()) {
            Text text = new Text("You have no patients in the system.");
            patientsLabel.getChildren().add(text);
        } else {
            Text first = new Text("You have: ");
            Text second = new Text(String.valueOf(patients.size()));
            second.getStyleClass().add("bold-text");
            Text third = new Text("patients.");
            if (patients.size() == 1) {
                third = new Text(" patient.");
            }
            patientsLabel.getChildren().addAll(first, second, third);
        }
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
