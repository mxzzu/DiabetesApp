package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.PersonalInfoCard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PatientManagementController {
    @FXML
    private Label statusLabel;

    @FXML
    private VBox personalInfoContainer;

    @FXML
    private HBox riskContainer, prevPatContainer, comContainer;

    private UserRepository userRepository;
    private String patientToManage;

    /**
     * Initialize the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        patientToManage = ViewNavigator.getPatientToManage();
        PersonalInfoCard personalInfoCard = new PersonalInfoCard(patientToManage);

        // Hide the status label initially
        statusLabel.setVisible(false);

        personalInfoContainer.getChildren().add(personalInfoCard);
        fetchInformation();
    }

    private void fetchInformation() {
        Patient temp = (Patient) userRepository.getUser(patientToManage);
        String[] riskFactors = temp.getSplittedRiskFactors();
        String[] prevPats =  temp.getSplittedPrevPats();
        String[] coms = temp.getSplittedComorbidities();

        String RISK_TITLE = "Risk Factors: ";
        createTitleLabel(riskContainer, RISK_TITLE);
        createValueLabel(riskContainer, riskFactors);
        String PREV_PAT_TITLE = "Previous Pathologies: ";
        createTitleLabel(prevPatContainer, PREV_PAT_TITLE);
        createValueLabel(prevPatContainer, prevPats);
        String COM_TITLE = "Comorbidities: ";
        createTitleLabel(comContainer, COM_TITLE);
        createValueLabel(comContainer, coms);
    }

    private void createTitleLabel(HBox container, String title) {
        container.getChildren().clear();
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label-title");
        container.getChildren().add(titleLabel);
    }

    private void createValueLabel(HBox container, String[] values) {
        for (String value : values) {
            Label newLabel = new Label(value);
            newLabel.getStyleClass().add("label-value");
            container.getChildren().add(newLabel);
        }
    }

    @FXML
    private void updateMedicalInformations() {

    }

    /**
     * Handle navigating back to the dashboard.
     * This method is called when the user clicks the "Back to Dashboard" button.
     */
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }

    @FXML
    private void handleBackToPatients() {
        ViewNavigator.navigateToPatients();
    }

    /**
     * Show an error message in the status label.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    /**
     * Show a success message in the status label.
     */
    private void showSuccess() {
        statusLabel.setText("Medical Informations updated successfully");
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
