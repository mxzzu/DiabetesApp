package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Patient;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.Comparator;

public class PatientsController {
    @FXML
    private MFXTableView<Patient> table;

    private UserRepository userRepository;
    private ObservableList<Patient> patients;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        patients = userRepository.getAllPatients();
        createTable();
        table.getTableColumns().getFirst().setPrefWidth(200);
        table.getTableColumns().get(1).setPrefWidth(200);
        table.getTableColumns().get(2).setPrefWidth(230);
        table.getTableColumns().get(3).setPrefWidth(100);
        table.getTableColumns().getLast().setPrefWidth(70);
    }

    private void createTable() {
        MFXTableColumn<Patient> nameColumn = new MFXTableColumn<>("Name", false, Comparator.comparing(Patient::getName));
        MFXTableColumn<Patient> surnameColumn = new MFXTableColumn<>("Surname", false, Comparator.comparing(Patient::getSurname));
        MFXTableColumn<Patient> cfColumn = new MFXTableColumn<>("Username", false, Comparator.comparing(Patient::getUsername));
        MFXTableColumn<Patient> dataColumn = new MFXTableColumn<>("Data", false);
        MFXTableColumn<Patient> actionColumn = new MFXTableColumn<>("Manage", false);

        nameColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Patient::getName));
        surnameColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Patient::getSurname));
        cfColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Patient::getUsername));
        dataColumn.setRowCellFactory(patient -> {
            MFXTableRowCell<Patient, String> tableRow = new MFXTableRowCell<>(Patient::getButtonLabel);

            FontIcon icon = new FontIcon("bi-clipboard-data");
            icon.setIconColor(Color.web("#780dd7"));
            icon.setIconSize(24);
            icon.getStyleClass().add("table-button");
            icon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
                ViewNavigator.setPatientToManage(patient.getUsername());
                ViewNavigator.navigateToData();
            });
            tableRow.setGraphic(icon);

            return tableRow;
        });
        actionColumn.setRowCellFactory(patient -> {
            MFXTableRowCell<Patient, String> tableRow = new MFXTableRowCell<>(Patient::getButtonLabel);
            tableRow.setAlignment(Pos.CENTER_RIGHT);

            FontIcon icon = new FontIcon("bi-pencil-square");
            icon.setIconColor(Color.web("#780dd7"));
            icon.setIconSize(24);
            icon.getStyleClass().add("table-button");
            icon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
                ViewNavigator.setPatientToManage(patient.getUsername());
                ViewNavigator.navigateToManagePatient();
            });
            tableRow.setGraphic(icon);

            return tableRow;
        });
        actionColumn.setAlignment(Pos.CENTER_RIGHT);

        nameColumn.getStyleClass().add("bold-text");
        surnameColumn.getStyleClass().add("bold-text");
        cfColumn.getStyleClass().add("bold-text");
        dataColumn.getStyleClass().add("bold-text");
        actionColumn.getStyleClass().add("bold-text");

        table.getTableColumns().addAll(nameColumn, surnameColumn, cfColumn, dataColumn, actionColumn);
        table.getFilters().addAll(
                new StringFilter<>("Name", Patient::getName),
                new StringFilter<>("Surname", Patient::getSurname),
                new StringFilter<>("Username", Patient::getUsername)
        );

        table.setItems(patients);
    }

    /**
     * Handle navigating back to the dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
}