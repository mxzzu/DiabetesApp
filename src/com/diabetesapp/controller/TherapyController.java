package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.Therapy;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class TherapyController {
    @FXML
    private Label validationLabel1, validationLabel2, validationLabel3, validationLabel4, validationLabel5;

    @FXML
    private MFXTextField drugField, intakeNumberField, quantityField, indicationsField;

    @FXML
    private MFXButton therapyButton;

    @FXML
    MFXFilterComboBox<String> patientBox;

    private TherapyRepository therapyRepository;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        therapyRepository = Main.getTherapyRepository();
        userRepository = Main.getUserRepository();
        patientBox.setItems(userRepository.getAllPatients());

        if (ViewNavigator.getPatientToManage() != null) {
            fillPatientBox();
        }

        if (ViewNavigator.getTherapyToEdit() != null) {
            fillPatientBox();
            drugField.setText(ViewNavigator.getTherapyToEdit().drug());
            intakeNumberField.setText(ViewNavigator.getTherapyToEdit().intakeNumber());
            quantityField.setText(ViewNavigator.getTherapyToEdit().quantity());
            indicationsField.setText(ViewNavigator.getTherapyToEdit().indications());
            therapyButton.setText("Modify Therapy");

        }

        intakeNumberField.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
        quantityField.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());

        Validator.emptyFieldConstraints(patientBox, validationLabel1);
        Validator.emptyFieldConstraints(drugField, validationLabel2);
        Validator.emptyFieldConstraints(intakeNumberField, validationLabel3);
        Validator.emptyFieldConstraints(quantityField, validationLabel4);
        Validator.emptyFieldConstraints(indicationsField, validationLabel5);
    }

    @FXML
    private void addTherapy() {
        boolean check1 = Validator.checkConstraints(patientBox, validationLabel1);
        boolean check2 = Validator.checkConstraints(drugField, validationLabel2);
        boolean check3 = Validator.checkConstraints(intakeNumberField, validationLabel3);
        boolean check4 = Validator.checkConstraints(quantityField, validationLabel4);
        boolean check5 = Validator.checkConstraints(indicationsField, validationLabel5);
        
        if (!check1 || !check2 || !check3 || !check4 || !check5) {
            return;
        }

        Therapy newTherapy = getNewTherapy();
        therapyRepository.saveTherapy(newTherapy);

        back();
    }



    @FXML
    private void handleBack() {
        back();
    }

    private void back() {
        if (ViewNavigator.getPatientToManage() != null) {
            ViewNavigator.navigateToManagePatient();
        } else {
            ViewNavigator.navigateToDashboard();
        }
    }

    private Therapy getNewTherapy() {
        String patientString = patientBox.getValue();
        int start = patientString.indexOf('(') + 1;
        int end = patientString.indexOf(')');
        String patient = patientString.substring(start, end);
        String drug = drugField.getText();
        String intakeNumber = intakeNumberField.getText();
        String quantity = quantityField.getText();
        String indications = indicationsField.getText();

        return new Therapy(patient, drug, intakeNumber, quantity, indications);
    }

    private void fillPatientBox() {
        String patientName = userRepository.getUser(ViewNavigator.getPatientToManage()).getName();
        String patientSurname = userRepository.getUser(ViewNavigator.getPatientToManage()).getSurname();
        String patientString = String.format("%s %s (%s)", patientName, patientSurname, ViewNavigator.getPatientToManage());
        patientBox.setValue(patientString);
        patientBox.setText(patientString);
        patientBox.setDisable(true);
    }
}
