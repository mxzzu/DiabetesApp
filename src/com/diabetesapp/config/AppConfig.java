package com.diabetesapp.config;

import com.diabetesapp.Main;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class AppConfig {
    public static final String APP_TITLE = "Diabetes App";

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

    /**
     * Creates a handler for the fields that only accepts digits
     * @return Returns the EventHandler object
     */
    public static EventHandler<KeyEvent> digitsOnly() {
        return event -> {
            String character = event.getCharacter();
            if (!character.matches("[0-9]")) {
                event.consume();
            }
        };
    }

    /**
     * Creates a handler for the fields that only accepts a time format
     * @param textField Text field used to build the time format string
     * @return Returns the EventHandler object
     */
    public static EventHandler<KeyEvent> timeFormatOnly(MFXTextField textField) {
        return event -> {
            String character = event.getCharacter();

            if (!character.matches("[0-9]")) {
                event.consume();
                return;
            }

            String currentText = textField.getText().replace(":", "");

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

            currentText += character;

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < currentText.length(); i++) {
                formatted.append(currentText.charAt(i));
                if (i == 1) formatted.append(':');
            }

            event.consume();
            textField.setText(formatted.toString());
            textField.positionCaret(formatted.length());
        };
    }

    /**
     * Creates a handler for the field used to update the user email.
     * Used to allow the user to confirm pressing the ENTER key.
     * @param username Username of the authenticated user
     * @param textField Mail field to which apply the handler
     * @param errorLabel Label where to show any error
     * @return Returns the EventHandler object
     */
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

    /**
     * Extracts and builds the user info to update when changing the email.
     * @param username Username of the authenticated user
     * @param mail Updated email to change
     */
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

    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
}