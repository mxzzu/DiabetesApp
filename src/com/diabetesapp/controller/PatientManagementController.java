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
    private Label therapyError;

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
        PersonalInfoCard personalInfoCard = new PersonalInfoCard(patientToManage, false);

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
        if (!patient.getDocUser().equals(ViewNavigator.getAuthenticatedUsername())) {
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
        String[] drug = {therapy.drug()};
        String[] intakeNumber = {therapy.intakeNumber()};
        String[] quantity = {therapy.quantity()};
        String[] indications = {therapy.indications()};
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
        if (values.length == 1 && values[0].isEmpty()) {
            createEmptyValueLabel(hBox);
        } else {
            createValueLabel(hBox, values);
        }
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

    private void createEmptyValueLabel(HBox container) {
        Label newLabel = new Label("No data found!");
        newLabel.getStyleClass().add("empty-value");
        container.getChildren().add(newLabel);
    }

    @FXML
    private void updateMedicalInformations() {
        ViewNavigator.navigateToMedicalInformations();
    }

    @FXML
    private void seeChangesHistory() { ViewNavigator.navigateToHistory(); }

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
}
