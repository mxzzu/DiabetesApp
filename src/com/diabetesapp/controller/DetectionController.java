package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.DetectionRepository;
import com.diabetesapp.model.Notification;
import com.diabetesapp.model.NotificationRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import java.time.LocalDate;

public class DetectionController {
    @FXML
    private Label validationLabel1, validationLabel2,  validationLabel3;

    @FXML
    private MFXTextField levelField;

    @FXML
    private MFXComboBox<String> mealBox, periodBox;

    private DetectionRepository detectionRepository;
    private NotificationRepository notificationRepository;

    @FXML
    public void initialize() {
        detectionRepository = Main.getDetectionRepository();
        notificationRepository = Main.getNotificationRepository();
        levelField.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
        Validator.createDetectionConstraints(mealBox, periodBox, validationLabel1);
        Validator.createDetectionConstraints(periodBox, mealBox, validationLabel2);
        Validator.emptyFieldConstraints(levelField, validationLabel3);
    }

    @FXML
    private void addDetection() {
        boolean check1 = Validator.checkConstraints(mealBox, validationLabel1);
        boolean check2 = Validator.checkConstraints(periodBox, validationLabel2);
        boolean check3 = Validator.checkConstraints(levelField, validationLabel3);

        if (!check1 || !check2 || !check3) {
            return;
        }

        int level = Integer.parseInt(levelField.getText());
        String meal = mealBox.getValue();
        String period = periodBox.getValue();
        sendNotification(level, meal, period);

        Detection newDetection = new Detection(ViewNavigator.getAuthenticatedUsername(), LocalDate.now(), meal, period, level);
        detectionRepository.saveDetection(newDetection);

        ViewNavigator.navigateToDashboard();

    }

    @FXML
    private void handleBack() {
        ViewNavigator.navigateToDashboard();
    }

    /**
     * Sends a notification to all doctors if the detection level is dangerously high
     * @param level Level of the detection
     * @param meal Meal where the detection was take on
     * @param period Period of the meal where the detection was take on
     */
    private void sendNotification(int level, String meal, String period) {
        if ((period.equals("Before eating") && (level > 180 || level < 70)) || (period.equals("After eating") && (level > 250 || level < 70))) {
            String username = ViewNavigator.getAuthenticatedUsername();
            String today = LocalDate.now().format(AppConfig.DATE_FORMAT);
            String message = String.format("(%s) Attention: %s registered a glucose detection of %s at %s (%s)", today, username, level, meal, period);
            Notification notification = new Notification("All Doctors", LocalDate.parse(today, AppConfig.DATE_FORMAT), "Glucose Alert", message, false);
            notificationRepository.saveNotification(notification);
        }
    }
}