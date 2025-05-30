package com.diabetesapp.controller;

import com.diabetesapp.model.Intake;
import com.diabetesapp.model.IntakeRepository;
import com.diabetesapp.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.diabetesapp.Main;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntakeController {

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> drugBox;

    @FXML
    private TextField hourTaken, quantityTaken, otherSymptoms, otherDrugs, period;

    @FXML
    private CheckBox toggleCheckBox;

    private IntakeRepository intakeRepository;

    public void initialize() {
        intakeRepository = Main.getIntakeRepository();
        statusLabel.setVisible(false);
    }

    @FXML
    private void addIntake() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date =  dateFormat.format(new Date());
        String drug = drugBox.getValue();
        String hour = hourTaken.getText();
        String quantity = quantityTaken.getText();

        if (drug.equals("null") || hour.isEmpty() || quantity.isEmpty()) {
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
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
    }
}
