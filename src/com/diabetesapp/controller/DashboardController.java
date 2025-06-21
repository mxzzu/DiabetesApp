package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class DashboardController {
    @FXML
    private Label welcomeLabel, detectionLabel, intakeLabel, statusLabel;

    private DetectionRepository  detectionRepository;
    private IntakeRepository intakeRepository;
    private final String authenticatedName = ViewNavigator.getAuthenticatedName();
    private final String username = ViewNavigator.getAuthenticatedUsername();
    
    @FXML
    public void initialize() {
        // This is a protected view, so we should always have an authenticated user
        welcomeLabel.setText("Welcome to your dashboard, " + authenticatedName + "!");
        detectionRepository = Main.getDetectionRepository();
        intakeRepository = Main.getIntakeRepository();
        fetchDailyDetections();
        fetchDailyIntakes();
    }

    private void fetchDailyDetections() {
        List<Detection> detections = detectionRepository.getDailyDetections(username);
        if (detections.isEmpty()) {
            detectionLabel.setText("No detections found!");
        } else {
            detectionLabel.setText(printList(detections));
        }
    }

    private void fetchDailyIntakes() {
        List<Intake> intakes = intakeRepository.getDailyIntakes(username);
        if (intakes.isEmpty()) {
            intakeLabel.setText("No intakes found!");
        } else {
            intakeLabel.setText(printList(intakes));
        }
    }

    private <T> String printList(List<T> list) {
        StringBuilder result = new StringBuilder();
        for (T item : list) {
            result.append(item.toString()).append("\n");
        }
        return result.toString();
    }

    @FXML
    private void handleIntake() {
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        Therapy therapy = therapyRepository.getTherapyByPatient(username);
        if (therapy == null) {
            showError();
            return;
        }
        ViewNavigator.navigateToIntake();
    }

    @FXML
    private void handleDetection() {
        ViewNavigator.navigateToDetection();
    }

    
    @FXML
    private void handleViewProfile() {
        ViewNavigator.navigateToProfile();
    }
    
    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }

    private void showError() {
        statusLabel.setText("No active therapy!");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}