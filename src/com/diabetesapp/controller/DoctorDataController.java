package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.TableUtils;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
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

public class DoctorDataController {

    @FXML
    private MFXTableView<Detection> table;
    @FXML
    private MFXTableView<Intake> intakeTable;
    @FXML
    private MFXTableView<ConcTherapy> concTherapiesTable;
    @FXML
    private Label detectionCardTitle, intakeCardTitle;
    @FXML
    private VBox container;

    private String patientToManage;

    private DetectionRepository detectionRepository;
    private UserRepository userRepository;
    private ConcTherapyRepository concTherapyRepository;
    private IntakeRepository intakeRepository;
    private ObservableList<Detection> detections;
    private ObservableList<ConcTherapy> concTherapies;
    private ObservableList<Intake> intakes;
    private MFXGenericDialog dialogContent;
    private MFXStageDialog chartDialog;
    private VBox chartsContainer;
    private ComboBox<Month> monthPicker;
    private ComboBox<Integer> yearPicker;

    private static final String BEFORE_EATING_COLOR = "#1E88E5";
    private static final String AFTER_EATING_COLOR = "#8E44AD";

    @FXML
    public void initialize() {
        patientToManage = ViewNavigator.getPatientToManage();
        detectionRepository = Main.getDetectionRepository();
        intakeRepository = Main.getIntakeRepository();
        concTherapyRepository = Main.getConcTherapyRepository();
        userRepository = Main.getUserRepository();
        detections = detectionRepository.getAllDetectionsByPatient(patientToManage);
        intakes = intakeRepository.getAllIntakesByUser(patientToManage);
        concTherapies = concTherapyRepository.getConcTherapiesByUser(patientToManage);
        User patient = userRepository.getUser(patientToManage);
        String detectionTitle = String.format("Detection Table of: %s %s (%s)", patient.getName(), patient.getSurname(), patient.getUsername());
        String intakeTitle = String.format("Intake Table of: %s %s (%s)", patient.getName(), patient.getSurname(), patient.getUsername());
        detectionCardTitle.setText(detectionTitle);
        intakeCardTitle.setText(intakeTitle);

        TableUtils.createDetectionTable(table, detections);
        TableUtils.createIntakesTable(intakeTable, intakes);
        TableUtils.createConcTherapyTable(concTherapiesTable, concTherapies);

        TableUtils.setTableSize(table);
        TableUtils.setTableSize(intakeTable);
        TableUtils.setTableSize(concTherapiesTable);

        setupChartDialog();
    }

    /**
     * Sets up the Dialag object of the chart pop-up
     */
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

    /**
     * Creates the layout of the chart pop-up
     */
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
                updateChartsFromPickers();
            }
        });

        monthPicker.valueProperty().addListener((_, _, _) -> updateChartsFromPickers());

        Label pickerLabel = new Label("Select Period:");
        HBox pickerContainer = new HBox(10, pickerLabel, monthPicker, yearPicker);
        pickerContainer.setAlignment(Pos.CENTER);

        HBox colorLegend = createColorLegend();
        HBox rangesLegend = createRangesLegend();

        chartsContainer = new VBox(20);
        chartsContainer.setAlignment(Pos.CENTER);

        MFXScrollPane scrollPane = new MFXScrollPane(chartsContainer);
        scrollPane.setFitToWidth(true);

        mainLayout.getChildren().addAll(pickerContainer, colorLegend, rangesLegend, scrollPane);
        dialogContent.setContent(mainLayout);

        if (!yearPicker.getItems().isEmpty()) {
            int currentYear = LocalDate.now().getYear();
            if (yearPicker.getItems().contains(currentYear)) {
                yearPicker.setValue(currentYear);
            } else {
                yearPicker.setValue(yearPicker.getItems().getFirst());
            }
        } else {
            chartsContainer.getChildren().add(new Label("No detection data available."));
        }
    }

    /**
     * Populates the month ComboBox of the specified year based on the detection in the database
     * @param year Year picked by the user
     */
    private void populateMonthPicker(int year) {
        Month previouslySelectedMonth = monthPicker.getValue();

        List<Month> sortedMonths = detections.stream()
                .filter(d -> d.date().getYear() == year)
                .map(d -> d.date().getMonth())
                .distinct()
                .sorted()
                .toList();

        monthPicker.getItems().setAll(sortedMonths);

        Month currentMonth = LocalDate.now().getMonth();
        if (year == LocalDate.now().getYear() && sortedMonths.contains(currentMonth)) {
            monthPicker.setValue(currentMonth);
        } else if (sortedMonths.contains(previouslySelectedMonth)) {
            monthPicker.setValue(previouslySelectedMonth);
        } else if (!sortedMonths.isEmpty()) {
            monthPicker.setValue(sortedMonths.getFirst());
        } else {
            monthPicker.setValue(null);
        }
    }

    /**
     * Updates visual charts when the user changed month or year of data
     */
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

    /**
     * Updates visual charts when the user changed month or year of data
     * @param month Month picked
     * @param year Year picked
     */
    private void updateCharts(Month month, int year) {
        chartsContainer.getChildren().clear();

        LineChart<Number, Number> breakfastChart = createMealChart("Breakfast", month, year);
        LineChart<Number, Number> lunchChart = createMealChart("Lunch", month, year);
        LineChart<Number, Number> dinnerChart = createMealChart("Dinner", month, year);

        chartsContainer.getChildren().addAll(breakfastChart, lunchChart, dinnerChart);
    }

    /**
     * Creates the detection chart based on the meal and by the month and year choosed by the user
     * @param mealType Meal to be shown
     * @param month Month choosed by the user
     * @param year Year choosed by the user
     * @return Returns the LineChart object to show
     */
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

        lineChart.getStyleClass().add("glucose-chart");
        String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
        lineChart.getStylesheets().add(cssPath);

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

        styleChartLines(beforeSeries, afterSeries);

        return lineChart;
    }

    /**
     * Styles the charts based on the series of input given
     * @param beforeSeries Before Eating series
     * @param afterSeries Afer eating series
     */
    private void styleChartLines(XYChart.Series<Number, Number> beforeSeries, XYChart.Series<Number, Number> afterSeries) {
        Platform.runLater(() -> {
            Node beforeLine = beforeSeries.getNode().lookup(".chart-series-line");
            if (beforeLine != null) {
                beforeLine.setStyle("-fx-stroke: " + BEFORE_EATING_COLOR + ";");
            }

            Node afterLine = afterSeries.getNode().lookup(".chart-series-line");
            if (afterLine != null) {
                afterLine.setStyle("-fx-stroke: " + AFTER_EATING_COLOR + ";");
            }
        });
    }

    /**
     * Creates the axis of the charts based on month and year
     * @param month Month object representing the month
     * @param year Integer value of the year
     * @return Returns the NumberAxis object to use on the charts
     */
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

    /**
     * Returns the color to show on the chart
     * @param period String value of the period of the detection
     * @param parsedLevel Level of the detection
     * @return Returns the HEX color code
     */
    public String getGlucoseColor(String period, int parsedLevel) {
        if (period.equals("Before eating")) {
            if (parsedLevel >= 80 && parsedLevel <= 130) return "#4CAF50";
            if ((parsedLevel > 130 && parsedLevel <= 180) || (parsedLevel >= 70 && parsedLevel < 80)) return "#FF9800";
        } else {
            if (parsedLevel >= 80 && parsedLevel <= 180) return "#4CAF50";
            if ((parsedLevel > 180 && parsedLevel <= 250) || (parsedLevel >= 70 && parsedLevel < 80)) return "#FF9800";
        }
        return "#F44336";
    }

    /**
     * Creates the legend of the colors used in the charts
     * @return Returns HBox object to use as legend in the pop-up
     */
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

    /**
     * Creates the item in the legend box
     * @param color HEX color code to use for the item
     * @param text Text to show near the color
     * @return Returns the HBox object of the item
     */
    private HBox createLegendItem(String color, String text) {
        Circle circle = new Circle(8, Color.web(color));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(0.5);
        Label label = new Label(text);
        HBox itemBox = new HBox(5, circle, label);
        itemBox.setAlignment(Pos.CENTER);
        return itemBox;
    }

    /**
     * Creates the ranges of the detections level for the legend box
     * @return Returns the HBox object to use in the legend box
     */
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