package com.diabetesapp.controller;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.model.Intake;
import com.diabetesapp.model.IntakeRepository;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.diabetesapp.Main;
import javafx.scene.input.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntakeController {

    @FXML
    private Label statusLabel;

    @FXML
    private MFXTextField drugField, hourTaken, quantityTaken, otherSymptoms, otherDrugs, period;

    @FXML
    private MFXToggleButton toggleCheckBox;

    private IntakeRepository intakeRepository;
    private String drug = null;

    public void initialize() {
        intakeRepository = Main.getIntakeRepository();
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        statusLabel.setVisible(false);
        drug = therapyRepository.getTherapyByPatient(ViewNavigator.getAuthenticatedUser()).getDrug();
        drugField.setText(drug);
        quantityTaken.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
        hourTaken.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.timeFormatOnly(hourTaken));
    }

    @FXML
    private void addIntake() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date =  dateFormat.format(new Date());
        String hour = hourTaken.getText();
        String quantity = quantityTaken.getText();

        if (hour.isEmpty() || quantity.isEmpty()) {
            showError();
            return;
        }

        Intake newIntake = new Intake(ViewNavigator.getAuthenticatedUser(), date, drug, hour, quantity);
        intakeRepository.saveIntake(newIntake);

        ViewNavigator.navigateToDashboard();
    }

    @FXML
    private void handleToggle() {
        boolean isChecked = toggleCheckBox.isSelected();
        otherSymptoms.setVisible(isChecked);
        otherSymptoms.setManaged(isChecked);
        otherDrugs.setVisible(isChecked);
        otherDrugs.setManaged(isChecked);
        period.setVisible(isChecked);
        period.setManaged(isChecked);
    }

    private void showError() {
        statusLabel.setText("Please fill out all fields");
        statusLabel.setManaged(true);
        statusLabel.setVisible(true);
    }
}
