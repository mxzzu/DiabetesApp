package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.DateFilter;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.DetectionRepository;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import java.util.Comparator;
import static com.diabetesapp.config.AppConfig.DATE_PARSE_FUNCTION;

public class DataController {

    @FXML
    private MFXTableView<Detection> table;

    @FXML
    private Label cardTitle;

    private DetectionRepository detectionRepository;
    private UserRepository userRepository;
    private ObservableList<Detection> detections;

    @FXML
    public void initialize() {
        detectionRepository = Main.getDetectionRepository();
        userRepository = Main.getUserRepository();
        detections = detectionRepository.getAllDetectionsByPatient(ViewNavigator.getPatientToManage());
        User patient = userRepository.getUser(ViewNavigator.getPatientToManage());
        String title = String.format("Data Table of: %s %s (%s)", patient.getName(), patient.getSurname(), patient.getUsername());
        cardTitle.setText(title);
        createTable();
        table.getTableColumns().getFirst().setPrefWidth(200);
        table.getTableColumns().get(1).setPrefWidth(200);
        table.getTableColumns().get(2).setPrefWidth(200);
        table.getTableColumns().getLast().setPrefWidth(200);
    }

    private void createTable() {
        MFXTableColumn<Detection> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Detection::date));
        MFXTableColumn<Detection> mealColumn = new MFXTableColumn<>("Meal", false, Comparator.comparing(Detection::meal));
        MFXTableColumn<Detection> periodColumn = new MFXTableColumn<>("Period", false, Comparator.comparing(Detection::period));
        MFXTableColumn<Detection> levelColumn = new MFXTableColumn<>("Level", false, Comparator.comparing(Detection::level));

        dateColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(DATE_PARSE_FUNCTION));
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

        table.getTableColumns().addAll(dateColumn, mealColumn, periodColumn, levelColumn);
        table.getFilters().addAll(
                new DateFilter<>("Date", Detection::date),
                new StringFilter<>("Meal", Detection::meal),
                new StringFilter<>("Period", Detection::period),
                new IntegerFilter<>("Level", Detection::level)
        );

        table.setItems(detections);
    }

    /**
     * Handle navigating back to the patients list
     */
    @FXML
    private void handleBackToPatients() {
        ViewNavigator.navigateToPatients();
    }

}
