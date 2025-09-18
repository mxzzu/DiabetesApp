package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
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

    @FXML
    public void initialize() {
        detectionRepository = Main.getDetectionRepository();
        intakeRepository = Main.getIntakeRepository();
        notificationRepository = Main.getNotificationRepository();

        fetchDailyDetections();
        fetchDailyIntakes();
        fetchNotifications();
    }

    private void fetchNotifications() {
        if (!notificationRepository.notificationExists(username)) {
            updateNotifications();
        }
        List<Notification> notifications = notificationRepository.getNotificationsByUser(username);
        if (notifications.isEmpty()) {
            notificationLabel.setText("No Notifications Found!");
        } else {
            notificationLabel.setManaged(false);
            notificationLabel.setVisible(false);
            printColoredNotifications(notifications);
        }
    }

    /**
     * Checks yesterday intakes. If missing, add notification to DB and shows pop-up
     */
    private void updateNotifications() {
        List<String> missingDrugs = intakeRepository.getMissingEntriesForYesterday(username);

        if (!missingDrugs.isEmpty()) {
            String yesterday = LocalDate.now().minusDays(1).format(AppConfig.DATE_FORMAT);
            LocalDate today = LocalDate.parse(LocalDate.now().format(AppConfig.DATE_FORMAT),  AppConfig.DATE_FORMAT);
            String message = String.format("Attention: %s you didn’t record the intake of: %s", yesterday, String.join(", ", missingDrugs));
            Notification newNotification = new Notification(username, today, message, false);
            notificationRepository.saveNotification(newNotification);

            if (!ViewNavigator.hasInitialNotificationBeenShown()) {
                for (String drug : missingDrugs) {
                    showPopUp(drug);
                }
                ViewNavigator.setInitialNotificationShown(true);
            }
        }
    }

    /**
     * Crea e mostra una notifica in basso a destra usando i vincoli dell'AnchorPane.
     * @param drug Il nome del farmaco da mostrare.
     */
    private void showPopUp(String drug) {
        Text header = new Text("Intakes Notification 💊");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        FontIcon closeIcon = new FontIcon("bi-x");
        MFXButton closeButton = new MFXButton("", closeIcon);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setStyle("-fx-background-color: transparent;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerBox = new HBox(header, spacer, closeButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox notificationContent = createVBox(drug, headerBox);

        rootPane.getChildren().add(notificationContent);
        AnchorPane.setBottomAnchor(notificationContent, 20.0);
        AnchorPane.setRightAnchor(notificationContent, 20.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(15));

        Runnable hideNotification = () -> {
            if (!rootPane.getChildren().contains(notificationContent)) return;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), notificationContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(_ -> rootPane.getChildren().remove(notificationContent));
            fadeOut.play();
        };

        delay.setOnFinished(_ -> hideNotification.run());
        closeButton.setOnAction(_ -> {
            delay.stop();
            hideNotification.run();
        });

        delay.play();
    }

    private static VBox createVBox(String drug, HBox headerBox) {
        Text content = new Text("Yesterday you didn’t record all the intakes of \"" + drug + "\".\nRemember to always record them!");

        VBox notificationContent = new VBox(10, headerBox, content);
        notificationContent.setPadding(new Insets(15));
        notificationContent.setStyle("-fx-background-color: #d3d3d3; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.2, 0, 1); -fx-background-radius: 5; -fx-border-radius: 5;");
        notificationContent.setOpacity(0);
        return notificationContent;
    }

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

    private void fetchDailyIntakes() {
        List<Intake> intakes = intakeRepository.getDailyIntakes(username);
        if (intakes.isEmpty()) {
            intakeLabel.setText("No Intakes Found!");
        } else {
            intakeLabel.setText(printList(intakes));
            intakeLabel.setStyle("-fx-stroke: #4eb214;");
        }
    }

    private <T> String printList(List<T> list) {
        StringBuilder result = new StringBuilder();
        for (T item : list) {
            result.append(item.toString()).append("\n");
        }
        return result.toString();
    }

    private void printColoredNotifications(List<Notification> list) {
        for (Notification  notification : list) {
            Text newLine = new Text("  " + notification.toString() + "\n");
            newLine.setStyle("-fx-stroke: #e10c0c;");
            FontIcon icon = new FontIcon();
            icon.setIconSize(13);
            icon.setIconLiteral("bi-exclamation-triangle");
            icon.setIconColor(Color.web("#e10c0c"));
            notificationFlow.getChildren().add(icon);
            notificationFlow.getChildren().add(newLine);
        }
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
    private void handleViewAllIntakes() {
        ViewNavigator.setDataToView("intakes"); ViewNavigator.navigateToAllData();
    }

    @FXML
    private void handleViewAllDetections() {
        ViewNavigator.setDataToView("detections");
        ViewNavigator.navigateToAllData();
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
        statusLabel.setText("Nessuna terapia attiva!");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}