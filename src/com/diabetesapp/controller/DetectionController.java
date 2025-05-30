package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.DetectionRepository;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetectionController {
    @FXML
    private Label statusLabel;

    @FXML
    private TextField levelField;

    @FXML
    private ComboBox<String> mealBox, periodBox;

    private DetectionRepository detectionRepository;

    @FXML
    public void initialize() {
        detectionRepository = Main.getDetectionRepository();
        statusLabel.setVisible(false);
    }

    @FXML
    private void addDetection() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date =  dateFormat.format(new Date());
        String level = levelField.getText();
        String meal = mealBox.getValue();
        String period = periodBox.getValue();

        if (level.isEmpty() || meal.isEmpty() || period.isEmpty()) {
            showError();
            return;
        }

        Detection newDetection = new Detection(ViewNavigator.getAuthenticatedUser(), date, meal, period, level);
        detectionRepository.saveDetection(newDetection);

        ViewNavigator.navigateToDashboard();

    }

    private void showError() {
        statusLabel.setText("Please fill out all fields");
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}