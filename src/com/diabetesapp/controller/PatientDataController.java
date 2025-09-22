package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.TableUtils;
import com.diabetesapp.model.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.diabetesapp.view.ViewNavigator;
import javafx.scene.layout.VBox;
import java.util.Map;

public class PatientDataController {
    @FXML
    private Label cardTitle;

    @FXML
    private VBox concTherapyBox;

    @FXML
    private MFXButton deleteButton;

    @FXML
    private MFXTableView<Intake> intakesTable;
    private IntakeRepository intakeRepository;
    private ObservableList<Intake> intakesList;

    @FXML
    private MFXTableView<Detection> detectionsTable;
    private DetectionRepository detectionRepository;
    private ObservableList<Detection> detectionsList;

    @FXML
    private MFXTableView<ConcTherapy>  concTherapyTable;
    private ConcTherapyRepository concTherapyRepository;
    private ObservableList<ConcTherapy> concTherapyList;

    @FXML
    public void initialize() {
        if (ViewNavigator.getDataToView().equals("intakes")) {
            cardTitle.setText("Intakes Table");
            intakesTable.setManaged(true);
            intakesTable.setVisible(true);
            intakeRepository = Main.getIntakeRepository();
            intakesList = intakeRepository.getAllIntakesByUser(ViewNavigator.getAuthenticatedUsername());
            TableUtils.createIntakesTable(intakesTable, intakesList);
            TableUtils.setTableSize(intakesTable);
            createConcTherapiesCard();
        } else {
            cardTitle.setText("Detections Table");
            detectionsTable.setManaged(true);
            detectionsTable.setVisible(true);
            detectionRepository = Main.getDetectionRepository();
            detectionsList = detectionRepository.getAllDetectionsByPatient(ViewNavigator.getAuthenticatedUsername());
            TableUtils.createDetectionTable(detectionsTable, detectionsList);
            TableUtils.setTableSize(detectionsTable);
        }
    }

    /**
     * Creates concurrent therapies card
     */
    private void createConcTherapiesCard() {
        concTherapyBox.setManaged(true);
        concTherapyBox.setVisible(true);
        concTherapyRepository = Main.getConcTherapyRepository();
        concTherapyList = concTherapyRepository.getConcTherapiesByUser(ViewNavigator.getAuthenticatedUsername());

        TableUtils.createConcTherapyTable(concTherapyTable, concTherapyList);
        TableUtils.setTableSize(concTherapyTable);
        addListener();
    }

    /**
     * Adds listener on the concurrent therapies table to enable the delete button when selecting a row
     */
    private void addListener() {
        concTherapyTable.getSelectionModel().selectionProperty().addListener((_, _, newSelection) -> deleteButton.setDisable(newSelection == null || newSelection.isEmpty()));
    }

    @FXML
    private void handleDeleteConcTherapy() {
        Map<Integer, MFXTableRow<ConcTherapy>> rows = concTherapyTable.getCells();
        for (Map.Entry<Integer, MFXTableRow<ConcTherapy>> entry : rows.entrySet()) {
            if (entry.getValue().isSelected()) {
                concTherapyRepository.removeConcTherapy(entry.getValue().getData());
                ViewNavigator.navigateToAllData();
            }
        }
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
}