package com.diabetesapp.controller;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.diabetesapp.Main;
import javafx.scene.input.KeyEvent;
import java.time.LocalDate;

public class IntakeController {

    @FXML
    private Label validationLabel1, validationLabel2, validationLabel3,  validationLabel4, validationLabel5, validationLabel6;

    @FXML
    private MFXTextField hourTaken, quantityTaken, otherSymptoms, otherDrugs, start, end;

    @FXML
    private MFXComboBox<String> drugBox;

    @FXML
    private MFXToggleButton toggleCheckBox;

    private IntakeRepository intakeRepository;
    private ConcTherapyRepository concTherapyRepository;

    @FXML
    public void initialize() {
        intakeRepository = Main.getIntakeRepository();
        concTherapyRepository = Main.getConcTherapyRepository();
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        drugBox.setItems(therapyRepository.getDrugsFromTherapy(ViewNavigator.getAuthenticatedUsername()));

        quantityTaken.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
        hourTaken.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.timeFormatOnly(hourTaken));

        Validator.emptyFieldConstraints(drugBox, validationLabel6);
        Validator.emptyFieldConstraints(hourTaken, validationLabel1);
        Validator.emptyFieldConstraints(quantityTaken, validationLabel2);
        Validator.emptyFieldConstraints(otherSymptoms, validationLabel3);
        Validator.createDateConstraints(start, validationLabel4);
        Validator.createDateConstraints(end, validationLabel5);
    }

    @FXML
    private void addIntake() {
        boolean check1 = Validator.checkConstraints(hourTaken, validationLabel1);
        boolean check2 = Validator.checkConstraints(quantityTaken, validationLabel2);
        boolean check3 = Validator.checkConstraints(drugBox, validationLabel6);

        if (!check1 || !check2 || !check3) {
            return;
        }

        if (toggleCheckBox.isSelected()) {
            boolean check4 =  Validator.checkConstraints(otherSymptoms, validationLabel3);
            boolean check5 = Validator.checkConstraints(start, validationLabel3);
            if (otherDrugs.getText().isEmpty() && end.getText().isEmpty()) {
                Validator.removeConstraints(end, validationLabel4);
                if (!check4 || !check5) {
                    return;
                }
            } else {
                boolean check6 = Validator.checkConstraints(end, validationLabel4);
                if (!check4 || !check5 || !check6) {
                    return;
                }
            }
            addConcTherapy();
        }

        String drug = drugBox.getSelectedItem();
        String hour = hourTaken.getText();
        String quantity = quantityTaken.getText();

        Intake newIntake = new Intake(ViewNavigator.getAuthenticatedUsername(), LocalDate.now(), drug, hour, quantity);
        intakeRepository.saveIntake(newIntake);

        ViewNavigator.navigateToDashboard();
    }

    @FXML
    private void handleBack() {
        ViewNavigator.navigateToDashboard();
    }

    /**
     * Adds concurrent therapy on database if user had filled its fields
     */
    private void addConcTherapy() {
        String symptoms = otherSymptoms.getText();
        String drugs = otherDrugs.getText();
        LocalDate startDate = LocalDate.parse(start.getText(), AppConfig.DATE_FORMAT);
        LocalDate endDate = null;
        if (!drugs.isEmpty()) {
            endDate = LocalDate.parse(end.getText(),  AppConfig.DATE_FORMAT);
        } else {
            drugs = "No drugs taken";
        }

        ConcTherapy newConcTherapy = new ConcTherapy(ViewNavigator.getAuthenticatedUsername(), symptoms, drugs, startDate, endDate);
        concTherapyRepository.saveConcTherapy(newConcTherapy);
    }

    @FXML
    private void handleToggle() {
        boolean isChecked = toggleCheckBox.isSelected();
        otherSymptoms.setVisible(isChecked);
        otherSymptoms.setManaged(isChecked);
        otherDrugs.setVisible(isChecked);
        otherDrugs.setManaged(isChecked);
        start.setVisible(isChecked);
        start.setManaged(isChecked);
        end.setVisible(isChecked);
        end.setManaged(isChecked);

        if (!isChecked) {
            validationLabel3.setVisible(false);
            validationLabel3.setManaged(false);
            validationLabel4.setVisible(false);
            validationLabel4.setManaged(false);
            validationLabel5.setVisible(false);
            validationLabel5.setManaged(false);
        }

    }
}
