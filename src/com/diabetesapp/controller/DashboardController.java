package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.NotificationHelper;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML
    private Label statusLabel;

    @FXML
    private TextFlow detectionFlow, notificationFlow;

    @FXML
    private Text intakeLabel, detectionLabel, notificationLabel;

    @FXML
    private AnchorPane rootPane;

    private DetectionRepository  detectionRepository;
    private IntakeRepository intakeRepository;
    private NotificationRepository notificationRepository;
    private final String username = ViewNavigator.getAuthenticatedUsername();
    private List<Notification> notifications;

    @FXML
    public void initialize() {
        detectionRepository = Main.getDetectionRepository();
        intakeRepository = Main.getIntakeRepository();
        notificationRepository = Main.getNotificationRepository();

        fetchDailyDetections();
        fetchDailyIntakes();
        if (!notificationRepository.notificationExists(username) && !ViewNavigator.hasClearedNotification()) {
            updateNotifications();
        }
        notifications = NotificationHelper.fetchNotifications(username, notificationLabel, false);
        NotificationHelper.printColoredNotifications(notifications, notificationFlow);
        NotificationHelper.showPopUpSequentially(username, rootPane, false);
    }

    /**
     * Checks yesterday intakes. If missing, add notification to DB
     */
    private void updateNotifications() {
        List<String> missingDrugs = intakeRepository.getMissingEntries(username, 1);

        if (!missingDrugs.isEmpty()) {
            String yesterday = LocalDate.now().minusDays(1).format(AppConfig.DATE_FORMAT);
            LocalDate today = LocalDate.parse(LocalDate.now().format(AppConfig.DATE_FORMAT), AppConfig.DATE_FORMAT);
            String message = String.format("(%s) Attention: you didn’t record the intake of: %s", yesterday, String.join(", ", missingDrugs));
            Notification newNotification = new Notification(username, today, "Intakes Notification 💊", message, false);
            notificationRepository.saveNotification(newNotification);
        }
    }

    /**
     * Fetch daily detections and list them in a card
     */
    private void fetchDailyDetections() {
        Map<String, Integer> mealOrder = Map.of("Breakfast", 1, "Lunch", 2, "Dinner", 3);
        List<Detection> detections = detectionRepository.getDailyDetections(username);
        if (detections.isEmpty()) {
            detectionLabel.setText("No Detections Found!");
        } else {
            detectionLabel.setManaged(false);
            detectionLabel.setVisible(false);
            detections.sort(Comparator
                    .comparingInt((Detection d) -> mealOrder.get(d.meal()))
                    .thenComparing(Detection::period, Comparator.reverseOrder()));
            printColoredDetections(detections);
        }
    }

    /**
     * Fetch daily intakes and list them in a card
     */
    private void fetchDailyIntakes() {
        List<Intake> intakes = intakeRepository.getDailyIntakes(username);
        if (intakes.isEmpty()) {
            intakeLabel.setText("No Intakes Found!");
        } else {
            intakeLabel.setText(printList(intakes));
            intakeLabel.setStyle("-fx-stroke: #4eb214;");
        }
    }

    /**
     * Format a list
     * @param list List to print
     * @return Returns a formatted string
     */
    private <T> String printList(List<T> list) {
        StringBuilder result = new StringBuilder();
        for (T item : list) {
            result.append(item.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Prints detection list with custom text fill and icon
     * @param list List to print
     */
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
                } else {
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
                } else {
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
            detectionFlow.getChildren().add(icon);
            detectionFlow.getChildren().add(newLine);
        }
    }

    @FXML
    private void handleIntake() {
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        List<Therapy> therapies = therapyRepository.getTherapiesByPatient(username);
        if (therapies.isEmpty()) {
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
    private void handleViewAllIntakes() {
        ViewNavigator.setDataToView("intakes"); ViewNavigator.navigateToAllData();
    }

    @FXML
    private void handleViewAllDetections() {
        ViewNavigator.setDataToView("detections");
        ViewNavigator.navigateToAllData();
    }

    @FXML
    private void handleClearAll() {
        for (Notification notification : notifications) {
            notificationRepository.removeNotifications(notification);
        }
        ViewNavigator.setClearedNotification(true);
        ViewNavigator.navigateToDashboard();
    }

    @FXML
    private void handleViewProfile() {
        ViewNavigator.navigateToProfile();
    }

    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }

    /**
     * Show an error if no therapy was found
     */
    private void showError() {
        statusLabel.setText("No active therapy!");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}