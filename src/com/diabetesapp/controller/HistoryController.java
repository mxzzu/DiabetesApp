package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.Change;
import com.diabetesapp.model.ChangeRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Comparator;

public class HistoryController {

    @FXML
    private MFXTableView<Change> table;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox container;

    private ChangeRepository changeRepository;
    private ObservableList<Change> changesHistory;
    private MFXGenericDialog dialogContent;
    private MFXStageDialog showMoreDialog;

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

            dialogContent = MFXDialogs.info()
                    .setShowMinimize(false)
                    .setShowAlwaysOnTop(false)
                    .setHeaderText("Change Details")
                    .setOnClose(_ -> showMoreDialog.close())
                    .get();

            showMoreDialog = new MFXStageDialog(this.dialogContent);
            showMoreDialog.setDraggable(true);
            showMoreDialog.setOwnerNode(container);
            showMoreDialog.setCenterInOwnerNode(true);

            if (showMoreDialog != null) {
                showMoreDialog.setOnShown(_ -> showMoreDialog.toFront());
            }
        }
    }

    private void setupTable() {
        MFXTableColumn<Change> docColumn = new MFXTableColumn<>("Doctor", false, Comparator.comparing(Change::docName));
        MFXTableColumn<Change> changeColumn = new MFXTableColumn<>("Change", false, Comparator.comparing(Change::change));
        MFXTableColumn<Change> dateColumn = new MFXTableColumn<>("Date", false, Comparator.comparing(Change::changeDate));

        //docColumn.setRowCellFactory(_ -> new MFXTableRowCell<>(Change::docName));
        docColumn.setRowCellFactory(changeData -> {
            MFXTableRowCell<Change, String> changeRow = new MFXTableRowCell<>(Change::docName);
            changeRow.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
                createDialogContent(changeData);
                showMoreDialog.showDialog();
            });
            return changeRow;
        });
        changeColumn.setRowCellFactory(changeData -> {
            MFXTableRowCell<Change, String> changeRow = new MFXTableRowCell<>(Change::change);
            changeRow.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
                createDialogContent(changeData);
                showMoreDialog.showDialog();
            });
            return changeRow;
        });
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

    private void createDialogContent(Change change) {
        VBox container = new VBox();
        container.setSpacing(10.0);

        /*Label doctorLabel = new Label();
        String docText = "\nDoctor: " + change.docName() + " (" + change.docUser() + ")";
        doctorLabel.setText(docText);
        doctorLabel.setWrapText(true);*/
        Text doctorTitle = new Text("\nDoctor: ");
        doctorTitle.setStyle("-fx-font-weight: bold");
        Text doctorName = new Text(change.docName() + " (" + change.docUser() + ")");
        TextFlow doctorTextFlow = new TextFlow(doctorTitle, doctorName);

        Text changeTitle = new Text("Changes:" + "\n");
        changeTitle.setStyle("-fx-font-weight: bold");
        String[] changesStrings = change.change().split(";");
        Text changes = new Text("");
        for (String c : changesStrings) {
            if (c.startsWith(" ")) {
                c = c.substring(1);
            }
            String prevText = changes.getText();
            // Se removed -> colorato rosso, Se addedd -> colorato verde
            changes.setText(prevText + "-  " + c + "\n");
        }
        TextFlow changeTextFlow = new TextFlow(changeTitle, changes);

        container.getChildren().addAll(doctorTextFlow, changeTextFlow);

        dialogContent.setContent(container);
    }

    @FXML
    private void backToPatient() {
        ViewNavigator.navigateToManagePatient();
    }
}
