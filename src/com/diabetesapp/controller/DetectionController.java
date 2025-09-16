package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.DetectionRepository;
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

    @FXML
    public void initialize() {
        detectionRepository = Main.getDetectionRepository();
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

        Detection newDetection = new Detection(ViewNavigator.getAuthenticatedUsername(), LocalDate.now(), meal, period, level);
        detectionRepository.saveDetection(newDetection);

        ViewNavigator.navigateToDashboard();

    }
}