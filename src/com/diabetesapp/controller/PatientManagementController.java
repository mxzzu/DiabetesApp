package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.TableUtils;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.PersonalInfoCard;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Comparator;
import java.util.Map;

public class PatientManagementController {
    @FXML
    private Label therapyError;

    @FXML
    private VBox personalInfoContainer, informationContainer;

    @FXML
    private MFXTableView<Therapy> therapyTable;

    @FXML
    private MFXButton therapyButton;

    private UserRepository userRepository;
    private TherapyRepository therapyRepository;
    private ObservableList<Therapy> therapies;
    private String patientToManage;
    private Patient patient;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        therapyRepository = Main.getTherapyRepository();
        patientToManage = ViewNavigator.getPatientToManage();
        PersonalInfoCard personalInfoCard = new PersonalInfoCard(patientToManage, false);
        therapies = FXCollections.observableList(therapyRepository.getTherapiesByPatient(patientToManage));

        if (therapies.isEmpty()) {
            therapyError.setVisible(true);
            therapyError.setManaged(true);
            therapyButton.setText("Add Therapy");
            therapyButton.getStyleClass().clear();
            therapyButton.getStyleClass().add("button");
        }

        personalInfoContainer.getChildren().add(personalInfoCard);
        fetchMedicalInformation();
        createTherapyTable();
        TableUtils.setTableSize(therapyTable);
        addListener();
    }

    /**
     * Fetches medical information of the patient and fills the card
     */
    private void fetchMedicalInformation() {
        patient = (Patient) userRepository.getUser(patientToManage);
        String[] riskFactors = patient.getSplittedRiskFactors();
        String[] prevPats =  patient.getSplittedPrevPats();
        String[] coms = patient.getSplittedComorbidities();

        createHBoxContainer(informationContainer, "Risk Factors: ", riskFactors);
        createHBoxContainer(informationContainer,  "Previous Pathologies: ", prevPats);
        createHBoxContainer(informationContainer,  "Comorbidities: ", coms);
    }

    /**
     * Creates therapies table with the therapies of the selected patient
     */
    private void createTherapyTable() {
        MFXTableColumn<Therapy> drugColumn = new MFXTableColumn<>("Drug", false, Comparator.comparing(Therapy::drug));
        MFXTableColumn<Therapy> intakeColumn = new MFXTableColumn<>("Intake Number", false, Comparator.comparing(Therapy::intakeNumber));
        MFXTableColumn<Therapy> quantityColumn = new MFXTableColumn<>("Quantity", false, Comparator.comparing(Therapy::quantity));
        MFXTableColumn<Therapy> indicationsColumn = new MFXTableColumn<>("Indications", false, Comparator.comparing(Therapy::indications));

        drugColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Therapy::drug));
        intakeColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Therapy::intakeNumber));
        quantityColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Therapy::quantity));
        indicationsColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Therapy::indications) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});

        indicationsColumn.setAlignment(Pos.CENTER_RIGHT);

        drugColumn.getStyleClass().add("bold-text");
        intakeColumn.getStyleClass().add("bold-text");
        quantityColumn.getStyleClass().add("bold-text");
        indicationsColumn.getStyleClass().add("bold-text");

        therapyTable.getTableColumns().addAll(drugColumn, intakeColumn, quantityColumn, indicationsColumn);
        therapyTable.getFilters().addAll(
                new StringFilter<>("Drug", Therapy::drug),
                new StringFilter<>("Intake Number", Therapy::intakeNumber),
                new StringFilter<>("Quantity", Therapy::quantity),
                new StringFilter<>("Indications", Therapy::indications)
        );

        therapyTable.setItems(therapies);
    }

    /**
     * Adds listener to the therapies table to enable modify button when selecting a therapy
     * if the logged in doctor is the actual doctor of the patient
     */
    private void addListener() {
        therapyTable.getSelectionModel().selectionProperty().addListener((_, _, newSelection) -> {
            if (patient.getDocUser().equals(ViewNavigator.getAuthenticatedUsername()) && (newSelection != null || !newSelection.isEmpty())) {
                therapyButton.setDisable(false);
            }
        });
    }

    /**
     * Creates HBox container for the medical informations
     * @param container VBox that will contain the HBox
     * @param title Title string of the category
     * @param values Values string array of the values for that category
     */
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

    /**
     * Creates the Label item for the title
     * @param container HBox container of the label
     * @param title Title to show
     */
    private void createTitleLabel(HBox container, String title) {
        container.getChildren().clear();
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label-title");
        container.getChildren().add(titleLabel);
    }

    /**
     * Creates the Label items for the values
     * @param container HBox container of the Labels
     * @param values Values array to show
     */
    private void createValueLabel(HBox container, String[] values) {
        for (String value : values) {
            Label newLabel = new Label(value);
            newLabel.getStyleClass().add("label-value");
            container.getChildren().add(newLabel);
        }
    }

    /**
     * Creates an empty label if there are no values to show
     * @param container HBox container of the empty labels
     */
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
        Map<Integer, MFXTableRow<Therapy>> rows = therapyTable.getCells();
        for (Map.Entry<Integer, MFXTableRow<Therapy>> entry : rows.entrySet()) {
            if (entry.getValue().isSelected()) {
                ViewNavigator.navigateToTherapy(entry.getValue().getData());
            }
        }
    }
}
