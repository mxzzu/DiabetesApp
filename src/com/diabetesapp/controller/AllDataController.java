package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.DateFilter;
import com.diabetesapp.model.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import com.diabetesapp.view.ViewNavigator;
import javafx.scene.layout.VBox;
import java.util.Comparator;
import java.util.Map;

import static com.diabetesapp.config.AppConfig.*;

public class AllDataController {
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
            createIntakesTable();
            AppConfig.setTableSize(intakesTable);
            createConcTherapiesCard();
        } else {
            cardTitle.setText("Detections Table");
            detectionsTable.setManaged(true);
            detectionsTable.setVisible(true);
            detectionRepository = Main.getDetectionRepository();
            detectionsList = detectionRepository.getAllDetectionsByUser(ViewNavigator.getAuthenticatedUsername());
            AppConfig.createDetectionTable(detectionsTable, detectionsList);
            AppConfig.setTableSize(detectionsTable);
        }
    }

    private void createIntakesTable() {
        MFXTableColumn<Intake> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Intake::date));
        MFXTableColumn<Intake> drugColumn = new MFXTableColumn<>("Drug", false, Comparator.comparing(Intake::drugs));
        MFXTableColumn<Intake> hourColumn = new MFXTableColumn<>("Hour", false, Comparator.comparing(Intake::hour));
        MFXTableColumn<Intake> quantityColumn = new MFXTableColumn<>("Quantity", false, Comparator.comparing(Intake::quantity));

        dateColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(INTAKE_DATE_PARSE_FUNCTION));
        drugColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Intake::drugs));
        hourColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Intake::hour));
        quantityColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Intake::quantity) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});

        quantityColumn.setAlignment(Pos.CENTER_RIGHT);

        dateColumn.getStyleClass().add("bold-text");
        drugColumn.getStyleClass().add("bold-text");
        hourColumn.getStyleClass().add("bold-text");
        quantityColumn.getStyleClass().add("bold-text");

        intakesTable.getTableColumns().addAll(dateColumn, drugColumn, hourColumn, quantityColumn);
        intakesTable.getFilters().addAll(
                new DateFilter<>("Date", Intake::date),
                new StringFilter<>("Drug", Intake::drugs),
                new StringFilter<>("Hour", Intake::hour),
                new StringFilter<>("Quantity", Intake::quantity)
        );

        intakesTable.setItems(intakesList);
    }

    private void createConcTherapiesCard() {
        concTherapyBox.setManaged(true);
        concTherapyBox.setVisible(true);
        concTherapyRepository = Main.getConcTherapyRepository();
        concTherapyList = concTherapyRepository.getConcTherapiesByUser(ViewNavigator.getAuthenticatedUsername());

        AppConfig.createConcTherapyTable(concTherapyTable, concTherapyList);
        AppConfig.setTableSize(concTherapyTable);
        addListener();
    }

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

    /**
     * Handle navigating back to the dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
}