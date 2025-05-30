package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.DetectionRepository;
import com.diabetesapp.model.Intake;
import com.diabetesapp.model.IntakeRepository;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class DashboardController {
    @FXML
    private Label welcomeLabel, detectionLabel, intakeLabel;

    private DetectionRepository  detectionRepository;
    private IntakeRepository intakeRepository;
    private final String username = ViewNavigator.getAuthenticatedUser();
    
    @FXML
    public void initialize() {
        // This is a protected view, so we should always have an authenticated user
        welcomeLabel.setText("Welcome to your dashboard, " + username + "!");
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
}