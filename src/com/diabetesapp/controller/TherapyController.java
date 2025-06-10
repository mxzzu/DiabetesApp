package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.model.Therapy;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class TherapyController {
    @FXML
    private Label statusLabel;

    @FXML
    private MFXTextField drugField, intakeNumberField, quantityField, indicationsField;

    @FXML
    MFXFilterComboBox<String> patientBox;

    private TherapyRepository therapyRepository;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        therapyRepository = Main.getTherapyRepository();
        userRepository = Main.getUserRepository();
        statusLabel.setVisible(false);

        patientBox.setItems(userRepository.getAllPatients());

        if (ViewNavigator.getPatientToManage() != null) {
            patientBox.setValue(ViewNavigator.getPatientToManage());
            patientBox.setText(ViewNavigator.getPatientToManage());
            patientBox.setDisable(true);
        }

        if (ViewNavigator.getTherapyToEdit() != null) {
            patientBox.setValue(ViewNavigator.getTherapyToEdit().getPatient());
            patientBox.setText(ViewNavigator.getPatientToManage());
            patientBox.setDisable(true);
            drugField.setText(ViewNavigator.getTherapyToEdit().getDrug());
            intakeNumberField.setText(ViewNavigator.getTherapyToEdit().getIntakeNumber());
            quantityField.setText(ViewNavigator.getTherapyToEdit().getQuantity());
            indicationsField.setText(ViewNavigator.getTherapyToEdit().getIndications());
        }

        intakeNumberField.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
        quantityField.addEventFilter(KeyEvent.KEY_TYPED, AppConfig.digitsOnly());
    }

    @FXML
    private void addTherapy() {
        String patient = patientBox.getValue();
        String drug = drugField.getText();
        String intakeNumber = intakeNumberField.getText();
        String quantity = quantityField.getText();
        String indications = indicationsField.getText();

        if (patient.isEmpty() || drug.isEmpty() || intakeNumber.isEmpty() || quantity.isEmpty() || indications.isEmpty()) {
            showError();
            return;
        }

        Therapy newTherapy = new Therapy(patient, drug, intakeNumber, quantity, indications);
        therapyRepository.saveTherapy(newTherapy);

        ViewNavigator.navigateToDashboard();

    }

    private void showError() {
        statusLabel.setText("Please fill out all fields");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
