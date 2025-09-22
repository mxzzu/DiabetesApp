package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.NotificationHelper;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardController {
    @FXML
    private Text notificationLabel;

    @FXML
    private TextFlow patientsLabel, notificationFlow;

    @FXML
    private AnchorPane rootPane;

    private final String username = ViewNavigator.getAuthenticatedUsername();

    private UserRepository userRepository;
    private NotificationRepository notificationRepository;
    private IntakeRepository intakeRepository;
    private List<Patient> myPatients;
    private List<Notification> notifications;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        notificationRepository = Main.getNotificationRepository();
        intakeRepository = Main.getIntakeRepository();
        fetchPatientsNumber();
        if (!notificationRepository.notificationExists(username) && !ViewNavigator.hasClearedNotification()) {
            updateNotifications();
        }
        notifications = NotificationHelper.fetchNotifications(username, notificationLabel, true);
        NotificationHelper.printColoredNotifications(notifications, notificationFlow);
        NotificationHelper.showPopUpSequentially(username, rootPane, true);
    }

    /**
     * Fetches the number of patients of the logged in doctor
     */
    private void fetchPatientsNumber() {
        myPatients = userRepository.getPatientsByDoctor(ViewNavigator.getAuthenticatedUsername());
        if (myPatients.isEmpty()) {
            Text text = new Text("You have no patients in the system.");
            patientsLabel.getChildren().add(text);
        } else {
            Text first = new Text("You have: ");
            Text second = new Text(String.valueOf(myPatients.size()));
            second.getStyleClass().add("bold-text");
            Text third = new Text("patients.");
            if (myPatients.size() == 1) {
                third = new Text(" patient.");
            }
            patientsLabel.getChildren().addAll(first, second, third);
        }
    }

    /**
     * Checks patients' intakes. If missing for more than 3 days, adds notification to database
     */
    private void updateNotifications() {
        List<String> missingDrugs;
        List<Patient> patients = new ArrayList<>();
        for (Patient patient :  myPatients) {
            missingDrugs = intakeRepository.getMissingEntries(patient.getUsername(), 4);

            if (missingDrugs.size() > 3) {
                String todayStr = LocalDate.now().format(AppConfig.DATE_FORMAT);
                LocalDate today = LocalDate.parse(todayStr, AppConfig.DATE_FORMAT);
                String message = String.format("(%s) Attention: %s %s didn’t record the intake of: %s for more than 3 days", todayStr, patient.getName(), patient.getSurname(), missingDrugs.getFirst());

                Notification newNotification = new Notification(username, today, "Patient Alert", message, false);
                notificationRepository.saveNotification(newNotification);
                patients.add(patient);
            }
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
    private void handleClearAll() {
        for (Notification notification : notifications) {
            if (notification.username().equals("All Doctors")) {
                ViewNavigator.addClearedNotification(notification);
                continue;
            }
            notificationRepository.removeNotifications(notification);
        }
        ViewNavigator.setClearedNotification(true);
        ViewNavigator.navigateToDashboard();
    }

    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }
}
