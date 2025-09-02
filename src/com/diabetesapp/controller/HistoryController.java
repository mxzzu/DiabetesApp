package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Change;
import com.diabetesapp.model.ChangeRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import java.util.Comparator;

public class HistoryController {

    @FXML
    private MFXTableView<Change> table;

    @FXML
    private Label errorLabel;

    private ChangeRepository changeRepository;
    private ObservableList<Change> changesHistory;

    @FXML
    public void initialize() {
        changeRepository = Main.getChangeRepository();

        changesHistory = changeRepository.getChangesByUser(ViewNavigator.getPatientToManage());

        if (changesHistory.isEmpty()) {
            errorLabel.setManaged(true);
            table.setManaged(false);
            table.setVisible(false);
        } else {
            setupTable();
            table.getTableColumns().getFirst().setPrefWidth(200);
            table.getTableColumns().get(1).setPrefWidth(400);
            table.getTableColumns().getLast().setPrefWidth(200);
        }
    }

    private void setupTable() {
        MFXTableColumn<Change> docColumn = new MFXTableColumn<>("Doctor", false, Comparator.comparing(Change::docName));
        MFXTableColumn<Change> changeColumn = new MFXTableColumn<>("Change", true, Comparator.comparing(Change::change));
        MFXTableColumn<Change> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Change::changeDate));

        docColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Change::docName));
        changeColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Change::change));
        dateColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Change::changeDate) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});
        dateColumn.setAlignment(Pos.CENTER_RIGHT);

        table.getTableColumns().addAll(docColumn, changeColumn, dateColumn);
        table.getFilters().addAll(
                new StringFilter<>("Doctor", Change::docName),
                new StringFilter<>("Change", Change::change),
                new StringFilter<>("Date", Change::changeDate)
        );

        table.setItems(changesHistory);
    }

    @FXML
    private void backToPatient() {
        ViewNavigator.navigateToManagePatient();
    }
}
