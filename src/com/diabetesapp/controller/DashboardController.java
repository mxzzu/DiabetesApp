package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    @FXML
    private Label welcomeLabel, detectionLabel, notificationLabel, statusLabel;
    @FXML
    private TextFlow flowContainer;
    @FXML
    private Text intakeLabel;

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
        fetchNotifications();
    }

    private void fetchDailyDetections() {
        List<Detection> detections = detectionRepository.getDailyDetections(username);
        if (detections.isEmpty()) {
            detectionLabel.setText("No detections found!");
        } else {
            detectionLabel.setManaged(false);
            printColoredDetections(detections);
        }
    }

    private void fetchDailyIntakes() {
        List<Intake> intakes = intakeRepository.getDailyIntakes(username);
        if (intakes.isEmpty()) {
            intakeLabel.setText("No intakes found!");
        } else {
            intakeLabel.setText(printList(intakes));
            intakeLabel.setStyle("-fx-stroke: #4eb214;");
        }
    }

    private void fetchNotifications() {
        List<Object> notifications = new ArrayList<>();
        if (notifications.isEmpty()) {
            notificationLabel.setText("No notifications found!");
        } else {
            notificationLabel.setText(printList(notifications));
        }
    }

    private <T> String printList(List<T> list) {
        StringBuilder result = new StringBuilder();
        for (T item : list) {
            result.append(item.toString()).append("\n");
        }
        return result.toString();
    }

    private void printColoredDetections(List<Detection> list) {
        String level;
        for (Detection item : list) {
            Text newLine = new Text("  " + item.toString() + "\n");
            int parsedLevel = item.level();
            if (item.period().equals("Before eating")) {
                if (parsedLevel >= 80 && parsedLevel <= 130) {
                    level = "normal";
                    newLine.setStyle("-fx-stroke: #4eb214;");
                } else if ((parsedLevel > 130 && parsedLevel <= 180) || (parsedLevel >= 70 && parsedLevel < 80)) {
                    level = "warning";
                    newLine.setStyle("-fx-stroke: #ff6400;");
                } else { // < 70 OR > 180
                    level = "danger";
                    newLine.setStyle("-fx-stroke: #e10c0c;");
                }
            } else {
                if (parsedLevel >= 80 && parsedLevel <= 180) {
                    level = "normal";
                    newLine.setStyle("-fx-stroke: #4eb214;");
                } else if ((parsedLevel > 180 && parsedLevel <= 250) || (parsedLevel >= 70 && parsedLevel < 80)) {
                    level = "warning";
                    newLine.setStyle("-fx-stroke: #ff6400;");
                } else { // < 70 OR > 250
                    level = "danger";
                    newLine.setStyle("-fx-stroke: #e10c0c;");
                }
            }
            FontIcon icon = new FontIcon();
            icon.setIconSize(13);
            if (level.equals("normal")) {
                icon.setIconLiteral("bi-hand-thumbs-up");
                icon.setIconColor(Color.web("#4eb214"));
            } else if (level.equals("warning")) {
                icon.setIconLiteral("bi-exclamation-circle");
                icon.setIconColor(Color.web("#ff6400"));
            } else {
                icon.setIconLiteral("bi-exclamation-triangle");
                icon.setIconColor(Color.web("#e10c0c"));
            }
            flowContainer.getChildren().add(icon);
            flowContainer.getChildren().add(newLine);
        }
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