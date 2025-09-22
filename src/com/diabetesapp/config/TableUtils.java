package com.diabetesapp.config;

import com.diabetesapp.model.ConcTherapy;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.Intake;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import java.util.Comparator;

public class TableUtils {

    /**
     * Creates and populate the intakes table
     * @param table MFXTableView object to populate
     * @param intakes ObservableList object used to populate the table
     */
    public static void createIntakesTable(MFXTableView<Intake> table, ObservableList<Intake> intakes) {
        MFXTableColumn<Intake> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Intake::date));
        MFXTableColumn<Intake> drugColumn = new MFXTableColumn<>("Drug", false, Comparator.comparing(Intake::drugs));
        MFXTableColumn<Intake> hourColumn = new MFXTableColumn<>("Hour", false, Comparator.comparing(Intake::hour));
        MFXTableColumn<Intake> quantityColumn = new MFXTableColumn<>("Quantity", false, Comparator.comparing(Intake::quantity));

        dateColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(AppConfig.INTAKE_DATE_PARSE_FUNCTION));
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

        table.getTableColumns().addAll(dateColumn, drugColumn, hourColumn, quantityColumn);
        table.getFilters().addAll(
                new DateFilter<>("Date", Intake::date),
                new StringFilter<>("Drug", Intake::drugs),
                new StringFilter<>("Hour", Intake::hour),
                new StringFilter<>("Quantity", Intake::quantity)
        );

        table.setItems(intakes);
    }

    /**
     * Creates and populate the detections table
     * @param table MFXTableView object to populate
     * @param detections ObservableList object used to populate the table
     */
    public static void createDetectionTable(MFXTableView<Detection> table, ObservableList<Detection> detections) {
        MFXTableColumn<Detection> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Detection::date));
        MFXTableColumn<Detection> mealColumn = new MFXTableColumn<>("Meal", false, Comparator.comparing(Detection::meal));
        MFXTableColumn<Detection> periodColumn = new MFXTableColumn<>("Period", false, Comparator.comparing(Detection::period));
        MFXTableColumn<Detection> levelColumn = new MFXTableColumn<>("Level", false, Comparator.comparing(Detection::level));

        dateColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(AppConfig.DETECTION_DATE_PARSE_FUNCTION));
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
     * Creates and populate the concurrent therapies table
     * @param table MFXTableView object to populate
     * @param concTherapyList ObservableList object used to populate the table
     */
    public static void createConcTherapyTable(MFXTableView<ConcTherapy> table, ObservableList<ConcTherapy> concTherapyList) {
        MFXTableColumn<ConcTherapy> symptomsColumn = new MFXTableColumn<>("Symptoms", false, Comparator.comparing(ConcTherapy::symptoms));
        MFXTableColumn<ConcTherapy> drugsColumn = new MFXTableColumn<>("Drugs", false, Comparator.comparing(ConcTherapy::drugs));
        MFXTableColumn<ConcTherapy> startColumn = new MFXTableColumn<>("Start Date", false, Comparator.comparing(ConcTherapy::start));
        MFXTableColumn<ConcTherapy> endColumn = new MFXTableColumn<>("End Date", false, Comparator.comparing(ConcTherapy::end));

        symptomsColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(ConcTherapy::symptoms));
        drugsColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(ConcTherapy::drugs));
        startColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(AppConfig.START_DATE_PARSE_FUNCTION));
        endColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(AppConfig.END_DATE_PARSE_FUNCTION) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});

        endColumn.setAlignment(Pos.CENTER_RIGHT);

        symptomsColumn.getStyleClass().add("bold-text");
        drugsColumn.getStyleClass().add("bold-text");
        startColumn.getStyleClass().add("bold-text");
        endColumn.getStyleClass().add("bold-text");

        table.getTableColumns().addAll(symptomsColumn, drugsColumn, startColumn, endColumn);
        table.getFilters().addAll(
                new StringFilter<>("Symptoms", ConcTherapy::symptoms),
                new StringFilter<>("Drugs", ConcTherapy::drugs),
                new DateFilter<>("Start", ConcTherapy::start),
                new DateFilter<>("End", ConcTherapy::end)
        );

        table.setItems(concTherapyList);
    }

    /**
     * Sets the columns' size for tables with 4 columns
     * @param table Table object to size
     */
    public static void setTableSize(MFXTableView<?> table) {
        table.getTableColumns().getFirst().setPrefWidth(200);
        table.getTableColumns().get(1).setPrefWidth(200);
        table.getTableColumns().get(2).setPrefWidth(200);
        table.getTableColumns().getLast().setPrefWidth(200);
    }

}
