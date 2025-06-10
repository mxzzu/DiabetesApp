package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.Therapy;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.PersonalInfoCard;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PatientManagementController {
    @FXML
    private Label statusLabel, therapyError;

    @FXML
    private VBox personalInfoContainer, therapyContainer, informationContainer;

    @FXML
    private MFXButton therapyButton;

    private UserRepository userRepository;
    private TherapyRepository therapyRepository;
    private String patientToManage;
    private Therapy therapy;
    private Patient patient;

    /**
     * Initialize the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        therapyRepository = Main.getTherapyRepository();
        patientToManage = ViewNavigator.getPatientToManage();
        PersonalInfoCard personalInfoCard = new PersonalInfoCard(patientToManage);

        // Hide the status label initially
        statusLabel.setVisible(false);

        personalInfoContainer.getChildren().add(personalInfoCard);
        fetchMedicalInformation();
        fetchTherapy();
    }

    private void fetchMedicalInformation() {
        patient = (Patient) userRepository.getUser(patientToManage);
        String[] riskFactors = patient.getSplittedRiskFactors();
        String[] prevPats =  patient.getSplittedPrevPats();
        String[] coms = patient.getSplittedComorbidities();

        createHBoxContainer(informationContainer, "Risk Factors: ", riskFactors);
        createHBoxContainer(informationContainer,  "Previous Pathologies: ", prevPats);
        createHBoxContainer(informationContainer,  "Comorbidities: ", coms);
    }

    private void fetchTherapy() {
        if (!patient.getDocUser().equals(ViewNavigator.getAuthenticatedUser())) {
            therapyButton.setDisable(true);
        }

        therapy = therapyRepository.getTherapyByPatient(patientToManage);
        if (therapy == null) {
            therapyError.setVisible(true);
            therapyError.setManaged(true);
            therapyButton.setText("Add Therapy");
            therapyButton.getStyleClass().clear();
            therapyButton.getStyleClass().add("button");
            return;
        }
        String[] drug = {therapy.getDrug()};
        String[] intakeNumber = {therapy.getIntakeNumber()};
        String[] quantity = {therapy.getQuantity()};
        String[] indications = {therapy.getIndications()};
        createHBoxContainer(therapyContainer, "Drug: " , drug);
        createHBoxContainer(therapyContainer, "Intake Number: ", intakeNumber);
        createHBoxContainer(therapyContainer, "Quantity: ", quantity);
        createHBoxContainer(therapyContainer, "Indications: ", indications);
    }

    private void createHBoxContainer(VBox container, String title, String[] values) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getStyleClass().add("hbox-container");
        hBox.setPadding(new Insets(10, 0, 0, 0));
        createTitleLabel(hBox, title);
        createValueLabel(hBox, values);
        container.getChildren().add(hBox);
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

    @FXML
    private void handleTherapyButton() {
        ViewNavigator.navigateToTherapy(therapy);
    }

    /**
     * Show an error message in the status label.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().add("alert-danger");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    /**
     * Show a success message in the status label.
     */
    private void showSuccess() {
        statusLabel.setText("Medical Informations updated successfully");
        statusLabel.getStyleClass().add("alert-success");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
