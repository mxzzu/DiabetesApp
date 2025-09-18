package com.diabetesapp.config;

import com.diabetesapp.Main;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.function.Function;

public class AppConfig {
    // Application settings
    public static final String APP_TITLE = "Diabetes App";
    
    // Data storage settings
    public static final String DATA_DIR = "src/resources/data";
    public static final String DB_NAME = "DiabetesApp";
    public static final String DETECTIONS_COLLECTION_NAME = "detections";
    public static final String INTAKES_COLLECTION_NAME = "intakes";
    public static final String THERAPIES_COLLECTION_NAME = "therapies";
    public static final String USERS_COLLECTION_NAME = "users";
    public static final String CHANGES_COLLECTION_NAME = "changes";
    public static final String CONCTHERAPY_COLLECTION_NAME = "concTherapies";
    public static final String NOTIFICATIONS_COLLECTION_NAME = "notifications";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final Function<Detection, String> DETECTION_DATE_PARSE_FUNCTION = detection ->  detection.date().format(DATE_FORMAT);
    public static final Function<Intake, String> INTAKE_DATE_PARSE_FUNCTION = intake ->  intake.date().format(DATE_FORMAT);
    public static final Function<ConcTherapy, String> START_DATE_PARSE_FUNCTION = concTherapy -> concTherapy.start().format(DATE_FORMAT);
    public static final Function<ConcTherapy, String> END_DATE_PARSE_FUNCTION = concTherapy -> {
        if (concTherapy.end() != null) {
            return concTherapy.end().format(DATE_FORMAT);
        }
        return "No end date";
    };

    public static EventHandler<KeyEvent> digitsOnly() {
        return event -> {
            String character = event.getCharacter();
            if (!character.matches("[0-9]")) {
                event.consume();
            }
        };
    }

    public static EventHandler<KeyEvent> timeFormatOnly(MFXTextField textField) {
        return event -> {
            String character = event.getCharacter();

            // Consenti solo cifre
            if (!character.matches("[0-9]")) {
                event.consume();
                return;
            }

            // Rimuovi i due punti per gestire solo le cifre
            String currentText = textField.getText().replace(":", "");

            // Impedisci l’inserimento di più di 4 cifre
            if (currentText.length() >= 4) {
                event.consume();
                return;
            }
            if (currentText.isEmpty() && character.charAt(0) > '2') {
                event.consume();
                return;
            } else if (currentText.length() == 1 && currentText.charAt(0) == '2') {
                if (character.charAt(0) > '4') {
                    event.consume();
                    return;
                }
            }

            if (currentText.length() == 2 && character.charAt(0) > '5') {
                event.consume();
                return;
            }

            // Aggiungi la cifra digitata
            currentText += character;

            // Costruisci il nuovo testo con il formato hh:mm
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < currentText.length(); i++) {
                formatted.append(currentText.charAt(i));
                if (i == 1) formatted.append(':');
            }

            event.consume(); // blocca inserimento automatico
            textField.setText(formatted.toString());
            textField.positionCaret(formatted.length());
        };
    }

    public static EventHandler<KeyEvent> updateMail(String username, MFXTextField textField, Label errorLabel) {
        return event -> {
            if (event.getCode() == KeyCode.ENTER) {
                boolean check = Validator.checkConstraints(textField, errorLabel);
                if (check) {
                    return;
                }
                String newMail = textField.getText();
                changeMail(username, newMail);
            }
        };
    }

    public static void changeMail(String username, String mail) {
        UserRepository userRepository = Main.getUserRepository();
        User currentUser = userRepository.getUser(username);
        User newUser;
        if (currentUser.getUserType().equals("patient")) {
            Patient patient = (Patient) currentUser;
            newUser = new Patient(patient.getUsername(), patient.getPassword(), patient.getUserType(), patient.getName(), patient.getSurname(), patient.getBirthDate(), patient.getGender(), mail, patient.isMustChangePassword(), patient.getRiskFactors(), patient.getPrevPats(), patient.getComorbidities(), patient.getDocUser());
        } else {
            newUser = new User(currentUser.getUsername(), currentUser.getPassword(), "doctor", currentUser.getName(), currentUser.getSurname(), currentUser.getBirthDate(), currentUser.getGender(), mail, currentUser.isMustChangePassword());
        }
        userRepository.modifyUser(newUser);
        ViewNavigator.navigateToProfile();
    }

    // Create a data directory if it doesn't exist
    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public static void createDetectionTable(MFXTableView<Detection> table, ObservableList<Detection> detections) {
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

        table.getTableColumns().addAll(dateColumn, mealColumn, periodColumn, levelColumn);
        table.getFilters().addAll(
                new DateFilter<>("Date", Detection::date),
                new StringFilter<>("Meal", Detection::meal),
                new StringFilter<>("Period", Detection::period),
                new IntegerFilter<>("Level", Detection::level)
        );
        table.setItems(detections);
    }

    public static void createConcTherapyTable(MFXTableView<ConcTherapy> table,  ObservableList<ConcTherapy> concTherapyList) {
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

        table.getTableColumns().addAll(symptomsColumn, drugsColumn, startColumn, endColumn);
        table.getFilters().addAll(
                new StringFilter<>("Symptoms", ConcTherapy::symptoms),
                new StringFilter<>("Drugs", ConcTherapy::drugs),
                new DateFilter<>("Start", ConcTherapy::start),
                new DateFilter<>("End", ConcTherapy::end)
        );

        table.setItems(concTherapyList);
    }

    public static void setTableSize(MFXTableView<?> table) {
        table.getTableColumns().getFirst().setPrefWidth(200);
        table.getTableColumns().get(1).setPrefWidth(200);
        table.getTableColumns().get(2).setPrefWidth(200);
        table.getTableColumns().getLast().setPrefWidth(200);
    }
}