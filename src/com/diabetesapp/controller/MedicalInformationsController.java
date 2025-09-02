package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MedicalInformationsController {
    @FXML
    private MFXTextField patientField, riskFactorsField, prevPatField, comField;

    @FXML
    private Label validationLabel1, validationLabel2,  validationLabel3;

    private UserRepository userRepository;
    private ChangeRepository changeRepository;

    @FXML
    private void initialize() {
        userRepository = Main.getUserRepository();
        changeRepository = Main.getChangeRepository();

        if (ViewNavigator.getPatientToManage() != null) {
            fillPatientField();
        }
        fetchInformations();
    }

    private void fetchInformations() {
        Patient patient = (Patient) userRepository.getUser(ViewNavigator.getPatientToManage());
        riskFactorsField.setText(patient.getRiskFactors());
        prevPatField.setText(patient.getPrevPats());
        comField.setText(patient.getComorbidities());
    }

    @FXML
    private void updateInformations() {
        String riskFactors = riskFactorsField.getText();
        String prevPat = prevPatField.getText();
        String com = comField.getText();

        Patient oldPatient = (Patient) userRepository.getUser(ViewNavigator.getPatientToManage());
        Patient newPatient = new Patient(oldPatient, riskFactors, prevPat, com);
        String diff = oldPatient.diff(newPatient);
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String docName = ViewNavigator.getAuthenticatedName() + ' ' + ViewNavigator.getAuthenticatedSurnname();
        Change change = new Change(ViewNavigator.getPatientToManage(), docName, diff, date.format(myFormatObj));
        changeRepository.saveChange(change);
        userRepository.modifyUser(newPatient);

        ViewNavigator.navigateToManagePatient();
    }

    @FXML
    private void backToPatient() {
        ViewNavigator.navigateToManagePatient();
    }

    private void fillPatientField() {
        String patientName = userRepository.getUser(ViewNavigator.getPatientToManage()).getName();
        String patientSurname = userRepository.getUser(ViewNavigator.getPatientToManage()).getSurname();
        String patientString = String.format("%s %s (%s)", patientName, patientSurname, ViewNavigator.getPatientToManage());
        patientField.setText(patientString);
        patientField.setDisable(true);
    }
}
