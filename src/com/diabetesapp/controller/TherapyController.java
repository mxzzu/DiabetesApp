package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Therapy;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TherapyController {
    @FXML
    private Label statusLabel;

    @FXML
    private TextField patientField, drugField, intakeNumberField, quantityField, indicationsField;

    private TherapyRepository therapyRepository;

    @FXML
    public void initialize() {
        therapyRepository = Main.getTherapyRepository();
        statusLabel.setVisible(false);
    }

    @FXML
    private void addTherapy() {
        String patient = patientField.getText();
        String drug = drugField.getText();
        String intakeNumber = intakeNumberField.getText();
        String quantity = quantityField.getText();
        String indications = indicationsField.getText();

        if (patient.isEmpty() || drug.isEmpty() || intakeNumber.isEmpty() || quantity.isEmpty()) {
            showError();
            return;
        }

        Therapy newTherapy = new Therapy(patient, drug, intakeNumber, quantity, indications);
        therapyRepository.saveTherapy(newTherapy);

        ViewNavigator.navigateToDashboard();

    }

    private void showError() {
        statusLabel.setText("Please fill out all fields");
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
