package com.diabetesapp.controller;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
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
import java.time.LocalDate;

public class IntakeController {

    @FXML
    private Label validationLabel1, validationLabel2;

    @FXML
    private MFXTextField drugField, hourTaken, quantityTaken, otherSymptoms, otherDrugs, period;

    @FXML
    private MFXToggleButton toggleCheckBox;

    private IntakeRepository intakeRepository;
    private String drug = null;

    public void initialize() {
        intakeRepository = Main.getIntakeRepository();
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        drug = therapyRepository.getTherapyByPatient(ViewNavigator.getAuthenticatedUser().getUsername()).drug();
        drugField.setText(drug);

        quantityTaken.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
        hourTaken.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.timeFormatOnly(hourTaken));

        Validator.emptyFieldConstraints(hourTaken, validationLabel1);
        Validator.emptyFieldConstraints(quantityTaken, validationLabel2);
    }

    @FXML
    private void addIntake() {
        boolean check1 = Validator.checkConstraints(hourTaken, validationLabel1);
        boolean check2 = Validator.checkConstraints(quantityTaken, validationLabel2);

        if (!check1 || !check2) {
            return;
        }

        String hour = hourTaken.getText();
        String quantity = quantityTaken.getText();

        Intake newIntake = new Intake(ViewNavigator.getAuthenticatedUsername(), LocalDate.now(), drug, hour, quantity);
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
}
