package com.diabetesapp.config;

import com.diabetesapp.Main;
import com.diabetesapp.model.Doctor;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.File;

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

    public static EventHandler<KeyEvent> updateMail(String username, MFXTextField textField) {
        return event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String newMail = textField.getText();
                changeMail(username, newMail);
                ViewNavigator.navigateToProfile();
            }
        };
    }

    public static void changeMail(String username, String mail) {
        UserRepository userRepository = Main.getUserRepository();
        User currentUser = userRepository.getUser(username);
        User newUser = new Doctor(currentUser.getUsername(), currentUser.getPassword(), "doctor", mail);
        userRepository.saveUser(newUser);
    }

    // Create a data directory if it doesn't exist
    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
}