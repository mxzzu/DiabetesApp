package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.DateFilter;
import com.diabetesapp.model.*;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import com.diabetesapp.view.ViewNavigator;
import javafx.scene.layout.VBox;
import java.util.Comparator;
import static com.diabetesapp.config.AppConfig.*;

public class AllDataController {
    @FXML
    private Label cardTitle;

    @FXML
    private VBox concTherapyBox;

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
            setTableSize(intakesTable);
            createConcTherapiesCard();
        } else {
            cardTitle.setText("Detections Table");
            detectionsTable.setManaged(true);
            detectionsTable.setVisible(true);
            detectionRepository = Main.getDetectionRepository();
            detectionsList = detectionRepository.getAllDetectionsByUser(ViewNavigator.getAuthenticatedUsername());
            createDetectionsTable();
            setTableSize(detectionsTable);
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

    private void createDetectionsTable() {
        MFXTableColumn<Detection> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Detection::date));
        MFXTableColumn<Detection> mealColumn = new MFXTableColumn<>("Meal", false, Comparator.comparing(Detection::meal));
        MFXTableColumn<Detection> periodColumn = new MFXTableColumn<>("Period", false, Comparator.comparing(Detection::period));
        MFXTableColumn<Detection> levelColumn = new MFXTableColumn<>("Level", false, Comparator.comparing(Detection::level));

        dateColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(DETECTION_DATE_PARSE_FUNCTION));
        mealColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Detection::meal));
        periodColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Detection::period));
        levelColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Detection::level) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});

        levelColumn.setAlignment(Pos.CENTER_RIGHT);

        dateColumn.getStyleClass().add("bold-text");
        mealColumn.getStyleClass().add("bold-text");
        periodColumn.getStyleClass().add("bold-text");
        levelColumn.getStyleClass().add("bold-text");

        detectionsTable.getTableColumns().addAll(dateColumn, mealColumn, periodColumn, levelColumn);
        detectionsTable.getFilters().addAll(
                new DateFilter<>("Date", Detection::date),
                new StringFilter<>("Meal", Detection::meal),
                new StringFilter<>("Period", Detection::period),
                new IntegerFilter<>("Level", Detection::level)
        );

        detectionsTable.setItems(detectionsList);
    }

    private void createConcTherapiesCard() {
        concTherapyBox.setManaged(true);
        concTherapyBox.setVisible(true);
        concTherapyRepository = Main.getConcTherapyRepository();
        concTherapyList = concTherapyRepository.getConcTherapiesByUser(ViewNavigator.getAuthenticatedUsername());

        createConcTherapyTable();
        setTableSize(concTherapyTable);
    }

    private void createConcTherapyTable() {
        MFXTableColumn<ConcTherapy> symptomsColumn = new MFXTableColumn<>("Symptoms", false, Comparator.comparing(ConcTherapy::symptoms));
        MFXTableColumn<ConcTherapy> drugsColumn = new MFXTableColumn<>("Drugs", false, Comparator.comparing(ConcTherapy::drugs));
        MFXTableColumn<ConcTherapy> startColumn = new MFXTableColumn<>("Start Date", false, Comparator.comparing(ConcTherapy::start));
        MFXTableColumn<ConcTherapy> endColumn = new MFXTableColumn<>("End Date", false, Comparator.comparing(ConcTherapy::end));

        symptomsColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(ConcTherapy::symptoms));
        drugsColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(ConcTherapy::drugs));
        startColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(START_DATE_PARSE_FUNCTION));
        endColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(END_DATE_PARSE_FUNCTION) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});

        endColumn.setAlignment(Pos.CENTER_RIGHT);

        symptomsColumn.getStyleClass().add("bold-text");
        drugsColumn.getStyleClass().add("bold-text");
        startColumn.getStyleClass().add("bold-text");
        endColumn.getStyleClass().add("bold-text");

        concTherapyTable.getTableColumns().addAll(symptomsColumn, drugsColumn, startColumn, endColumn);
        concTherapyTable.getFilters().addAll(
                new StringFilter<>("Symptoms", ConcTherapy::symptoms),
                new StringFilter<>("Drugs", ConcTherapy::drugs),
                new DateFilter<>("Start", ConcTherapy::start),
                new DateFilter<>("End", ConcTherapy::end)
        );

        concTherapyTable.setItems(concTherapyList);
    }

    private void setTableSize(MFXTableView<?> table) {
        table.getTableColumns().getFirst().setPrefWidth(200);
        table.getTableColumns().get(1).setPrefWidth(200);
        table.getTableColumns().get(2).setPrefWidth(200);
        table.getTableColumns().getLast().setPrefWidth(200);
    }

    /**
     * Handle navigating back to the dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
}