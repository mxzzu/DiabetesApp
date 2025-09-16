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
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;

import static com.diabetesapp.config.AppConfig.DATE_PARSE_FUNCTION;

public class DataController {

    @FXML
    private MFXTableView<Detection> table;
    @FXML
    private Label cardTitle;
    @FXML
    private VBox container;

    private DetectionRepository detectionRepository;
    private UserRepository userRepository;
    private ObservableList<Detection> detections;
    private MFXGenericDialog dialogContent;
    private MFXStageDialog chartDialog;
    private VBox chartsContainer;
    private ComboBox<Month> monthPicker;
    private ComboBox<Integer> yearPicker;

    // COSTANTI PER I COLORI
    private static final String BEFORE_EATING_COLOR = "#1E88E5"; // Blu
    private static final String AFTER_EATING_COLOR = "#8E44AD";  // Viola

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

        setupChartDialog();
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

    private void setupChartDialog() {
        this.dialogContent = MFXDialogs.info()
                .setShowMinimize(false)
                .setShowAlwaysOnTop(false)
                .setHeaderText("Monthly Glucose Charts")
                .setOnClose(_ -> chartDialog.close())
                .get();

        this.chartDialog = new MFXStageDialog(this.dialogContent);
        this.chartDialog.setDraggable(true);
        this.chartDialog.setOwnerNode(container);
        this.chartDialog.setCenterInOwnerNode(true);

        if (this.chartDialog != null) {
            this.chartDialog.setOnShown(_ -> chartDialog.toFront());
        }
    }

    private void createChartDialogLayout() {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPrefSize(780, 720);

        monthPicker = new ComboBox<>();
        yearPicker = new ComboBox<>();

        List<Integer> sortedYears = detections.stream()
                .map(detection -> detection.date().getYear())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        yearPicker.getItems().setAll(sortedYears);

        yearPicker.valueProperty().addListener((_, _, newYear) -> {
            if (newYear != null) {
                populateMonthPicker(newYear);
            }
        });

        monthPicker.valueProperty().addListener((_, _, _) -> updateChartsFromPickers());

        Label pickerLabel = new Label("Select Period:");
        HBox pickerContainer = new HBox(10, pickerLabel, monthPicker, yearPicker);
        pickerContainer.setAlignment(Pos.CENTER);

        HBox colorLegend = createColorLegend();
        HBox rangesLegend = createRangesLegend();

        this.chartsContainer = new VBox(20);
        this.chartsContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(this.chartsContainer);
        scrollPane.setFitToWidth(true);

        mainLayout.getChildren().addAll(pickerContainer, colorLegend, rangesLegend, scrollPane);
        dialogContent.setContent(mainLayout);

        // --- LOGICA DI SELEZIONE MESE/ANNO CORRENTE ---
        if (!yearPicker.getItems().isEmpty()) {
            int currentYear = LocalDate.now().getYear();
            // Se l'anno corrente è presente tra gli anni disponibili, selezionalo.
            if (yearPicker.getItems().contains(currentYear)) {
                yearPicker.setValue(currentYear);
            } else {
                // Altrimenti, seleziona l'anno più recente disponibile
                yearPicker.setValue(yearPicker.getItems().getFirst());
            }
        } else {
            chartsContainer.getChildren().add(new Label("No detection data available."));
        }
    }

    private void populateMonthPicker(int year) {
        Month previouslySelectedMonth = monthPicker.getValue();

        List<Month> sortedMonths = detections.stream()
                .filter(d -> d.date().getYear() == year)
                .map(d -> d.date().getMonth())
                .distinct()
                .sorted()
                .toList();

        monthPicker.getItems().setAll(sortedMonths);

        // --- LOGICA DI SELEZIONE MESE CORRENTE ---
        Month currentMonth = LocalDate.now().getMonth();
        // Se l'anno selezionato è l'anno corrente E il mese corrente è tra quelli disponibili...
        if (year == LocalDate.now().getYear() && sortedMonths.contains(currentMonth)) {
            // ...seleziona il mese corrente.
            monthPicker.setValue(currentMonth);
        } else if (sortedMonths.contains(previouslySelectedMonth)) {
            monthPicker.setValue(previouslySelectedMonth);
        } else if (!sortedMonths.isEmpty()) {
            // Altrimenti, seleziona il primo mese disponibile
            monthPicker.setValue(sortedMonths.getFirst());
        } else {
            monthPicker.setValue(null);
        }
    }

    private void updateChartsFromPickers() {
        Month selectedMonth = monthPicker.getValue();
        Integer selectedYear = yearPicker.getValue();

        chartsContainer.getChildren().clear();

        if (selectedMonth != null && selectedYear != null) {
            updateCharts(selectedMonth, selectedYear);
        } else {
            chartsContainer.getChildren().add(new Label("Please select a valid month and year."));
        }
    }

    private void updateCharts(Month month, int year) {
        chartsContainer.getChildren().clear();

        LineChart<Number, Number> breakfastChart = createMealChart("Breakfast", month, year);
        LineChart<Number, Number> lunchChart = createMealChart("Lunch", month, year);
        LineChart<Number, Number> dinnerChart = createMealChart("Dinner", month, year);

        chartsContainer.getChildren().addAll(breakfastChart, lunchChart, dinnerChart);
    }

    private LineChart<Number, Number> createMealChart(String mealType, Month month, int year) {
        NumberAxis xAxis = getNumberAxis(month, year);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Glucose Level (mg/dL)");

        List<Detection> filteredDetections = detections.stream()
                .filter(d -> d.meal().equalsIgnoreCase(mealType) && d.date().getMonth() == month && d.date().getYear() == year)
                .sorted(Comparator.comparing(Detection::date))
                .toList();

        if (filteredDetections.isEmpty()) {
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(250);
        } else {
            IntSummaryStatistics stats = filteredDetections.stream()
                    .mapToInt(Detection::level)
                    .summaryStatistics();
            yAxis.setLowerBound(Math.max(0, stats.getMin() - 25));
            yAxis.setUpperBound(stats.getMax() + 25);
        }
        yAxis.setAutoRanging(false);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Glucose Levels for " + mealType + " (" + month + " " + year + ")");
        lineChart.setLegendVisible(true);
        lineChart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<Number, Number> beforeSeries = new XYChart.Series<>();
        beforeSeries.setName("Before eating");
        XYChart.Series<Number, Number> afterSeries = new XYChart.Series<>();
        afterSeries.setName("After eating");

        for (Detection detection : filteredDetections) {
            try {
                int day = detection.date().getDayOfMonth();
                int levelValue = detection.level();
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(day, levelValue);

                if (detection.period().equalsIgnoreCase("Before eating")) {
                    beforeSeries.getData().add(dataPoint);
                } else {
                    afterSeries.getData().add(dataPoint);
                }

                dataPoint.nodeProperty().addListener((_, _, newNode) -> {
                    if (newNode != null) {
                        String color = getGlucoseColor(detection.period(), levelValue);
                        newNode.setStyle("-fx-background-color: " + color + ", white; -fx-background-insets: 0, 2;");
                    }
                });

            } catch (NumberFormatException e) {
                System.err.println("Impossibile convertire il livello in numero: " + detection.level());
            }
        }

        lineChart.getData().addAll(beforeSeries, afterSeries);

        // applica sempre i colori blu/viola per linee e legenda
        styleSeries(lineChart, beforeSeries, afterSeries);

        return lineChart;
    }

    private static NumberAxis getNumberAxis(Month month, int year) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day of the Month");
        final int lastDayOfMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(lastDayOfMonth + 1);
        xAxis.setTickUnit(2);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                int value = object.intValue();
                if (value > 0 && value <= lastDayOfMonth) {
                    if (value % 2 != 0 && value != 1) return "";
                    return String.valueOf(value);
                }
                return "";
            }
            @Override
            public Number fromString(String string) { return 0; }
        });
        return xAxis;
    }

    private String getGlucoseColor(String period, int parsedLevel) {
        if (period.equals("Before eating")) {
            if (parsedLevel >= 80 && parsedLevel <= 130) return "#4CAF50";
            if ((parsedLevel > 130 && parsedLevel <= 180) || (parsedLevel >= 70 && parsedLevel < 80)) return "#FF9800";
        } else { //after eating
            if (parsedLevel >= 80 && parsedLevel <= 180) return "#4CAF50";
            if ((parsedLevel > 180 && parsedLevel <= 250) || (parsedLevel >= 70 && parsedLevel < 80)) return "#FF9800";
        }
        return "#F44336";
    }

    private HBox createColorLegend() {
        HBox legendBox = new HBox(30);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setPadding(new Insets(0, 0, 10, 0));

        Label title = new Label("Point Status:");
        title.setStyle("-fx-font-weight: bold;");

        legendBox.getChildren().addAll(
                title,
                createLegendItem("#4CAF50", "Normal"),
                createLegendItem("#FF9800", "Warning"),
                createLegendItem("#F44336", "Danger")
        );
        return legendBox;
    }

    private HBox createLegendItem(String color, String text) {
        Circle circle = new Circle(8, Color.web(color));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(0.5);
        Label label = new Label(text);
        HBox itemBox = new HBox(5, circle, label);
        itemBox.setAlignment(Pos.CENTER);
        return itemBox;
    }

    private HBox createRangesLegend() {
        HBox mainBox = new HBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(5, 10, 5, 10));
        mainBox.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 5;");

        VBox beforeBox = new VBox(5);
        Label beforeTitle = new Label("Reference (Before Eating)");
        beforeTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: " + BEFORE_EATING_COLOR + ";");
        beforeBox.getChildren().add(beforeTitle);
        beforeBox.getChildren().addAll(
                new TextFlow(new Text("Normal: 80-130")),
                new TextFlow(new Text("Warning: 70-79 & 131-180")),
                new TextFlow(new Text("Danger: < 70 & > 180"))
        );

        VBox afterBox = new VBox(5);
        Label afterTitle = new Label("Reference (After Eating)");
        afterTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: " + AFTER_EATING_COLOR + ";");
        afterBox.getChildren().add(afterTitle);
        afterBox.getChildren().addAll(
                new TextFlow(new Text("Normal: 80-180")),
                new TextFlow(new Text("Warning: 70-79 & 181-250")),
                new TextFlow(new Text("Danger: < 70 & > 250"))
        );

        mainBox.getChildren().addAll(beforeBox, afterBox);
        return mainBox;
    }

    private void styleSeries(LineChart<Number, Number> lineChart,
                             XYChart.Series<Number, Number> beforeSeries,
                             XYChart.Series<Number, Number> afterSeries) {

        Platform.runLater(() -> {
            // Stile linee
            if (beforeSeries.getNode() != null) {
                beforeSeries.getNode().setStyle("-fx-stroke: " + BEFORE_EATING_COLOR + ";");
            }
            if (afterSeries.getNode() != null) {
                afterSeries.getNode().setStyle("-fx-stroke: " + AFTER_EATING_COLOR + ";");
            }

            // Stile legenda "Before eating"
            for (Node node : lineChart.lookupAll(".chart-legend-item-symbol.default-color0")) {
                node.setStyle(
                        "-fx-background-color: " + BEFORE_EATING_COLOR + ";" +
                                "-fx-background-radius: 0;" +
                                "-fx-pref-width: 25px;" +
                                "-fx-pref-height: 4px;"
                );
            }

            // Stile legenda "After eating"
            for (Node node : lineChart.lookupAll(".chart-legend-item-symbol.default-color1")) {
                node.setStyle(
                        "-fx-background-color: " + AFTER_EATING_COLOR + ";" +
                                "-fx-background-radius: 0;" +
                                "-fx-pref-width: 25px;" +
                                "-fx-pref-height: 4px;"
                );
            }
        });
    }

    @FXML
    private void handleShowChart() {
        createChartDialogLayout();
        chartDialog.showDialog();
    }

    @FXML
    private void handleBackToPatients() {
        ViewNavigator.navigateToPatients();
    }
}