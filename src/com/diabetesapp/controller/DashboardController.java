package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML
    private Label detectionLabel, notificationLabel, statusLabel;

    @FXML
    private TextFlow flowContainer;

    @FXML
    private Text intakeLabel;

    @FXML
    private AnchorPane rootPane;

    private DetectionRepository  detectionRepository;
    private IntakeRepository intakeRepository;
    private final String username = ViewNavigator.getAuthenticatedUsername();

    @FXML
    public void initialize() {
        // Inizializzazione dei repository
        detectionRepository = Main.getDetectionRepository();
        intakeRepository = Main.getIntakeRepository();

        // Caricamento dei dati giornalieri
        fetchDailyDetections();
        fetchDailyIntakes();

        // Eseguiamo il controllo dopo che la scena è pronta per avere le dimensioni corrette della finestra.
        Platform.runLater(this::checkYesterdayIntakesAndNotify);
    }

    /**
     * Controlla le assunzioni di ieri. Se mancano, mostra un pop-up per ogni farmaco
     * e un riepilogo nel centro notifiche.
     */
    private void checkYesterdayIntakesAndNotify() {
        // 1. Ottieni la LISTA dei farmaci mancanti dal repository
        List<String> missingDrugs = intakeRepository.getMissingEntriesForYesterday(username);

        // 2. Se la lista NON è vuota, ci sono farmaci mancanti
        if (!missingDrugs.isEmpty()) {
            // A. Mostra un riepilogo nel centro notifiche (messaggio persistente)
            String alertMessage = "Attention: yesterday you didn’t record the intake of: " + String.join(", ", missingDrugs);
            notificationLabel.setText(alertMessage);
            notificationLabel.setStyle("-fx-text-fill: #e10c0c; -fx-font-weight: bold;");

            // B. Mostra un pop-up temporaneo per ogni farmaco mancante
            for (String nomeFarmaco : missingDrugs) {
                showMedicationReminder(nomeFarmaco);
            }
        } else {
            // Se è tutto a posto, mostra un messaggio standard
            notificationLabel.setText("No important notifications.");
        }
    }

    /**
     * Crea e mostra una notifica in basso a destra usando i vincoli dell'AnchorPane.
     * @param nomeFarmaco Il nome del farmaco da mostrare.
     */
    private void showMedicationReminder(String nomeFarmaco) {
        // 1. Crea il contenuto grafico della notifica
        VBox notificationContent = getBox(nomeFarmaco);

        // 2. Aggiungi la notifica al rootPane e imposta i vincoli di ancoraggio
        rootPane.getChildren().add(notificationContent);
        AnchorPane.setBottomAnchor(notificationContent, 20.0); // 20px dal basso
        AnchorPane.setRightAnchor(notificationContent, 20.0);  // 20px da destra

        // 3. Mostra la notifica con un'animazione di fade-in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 4. Imposta un timer per farla scomparire dopo 7 secondi
        PauseTransition delay = new PauseTransition(Duration.seconds(7));
        delay.setOnFinished(_ -> {
            // Animazione di fade-out
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), notificationContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(_ -> rootPane.getChildren().remove(notificationContent)); // Rimuovi il nodo alla fine
            fadeOut.play();
        });
        delay.play();
    }

    private static VBox getBox(String nomeFarmaco) {
        Text header = new Text("Intakes Notification 💊");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Text content = new Text("Yesterday you didn’t record all the intakes of " + nomeFarmaco + ".\nRemember to always record them!");
        VBox notificationContent = new VBox(10, header, content);
        notificationContent.setPadding(new Insets(15));
        notificationContent.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.2, 0, 1); -fx-background-radius: 5; -fx-border-radius: 5;");
        notificationContent.setOpacity(0); // Inizia invisibile per l'animazione
        return notificationContent;
    }

    private static VBox getVBox(String nomeFarmaco) {
        Text header = new Text("Intake Notification 💊");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Text content = new Text("Yesterday you didn’t record all the intakes of " + nomeFarmaco + ".\nRemember to always record them!");
        VBox notificationContent = new VBox(10, header, content);
        notificationContent.setPadding(new Insets(15));
        notificationContent.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.2, 0, 1); -fx-background-radius: 5; -fx-border-radius: 5;");
        return notificationContent;
    }

    private void fetchDailyDetections() {
        Map<String, Integer> mealOrder = Map.of("Breakfast", 1, "Lunch", 2, "Dinner", 3);
        List<Detection> detections = detectionRepository.getDailyDetections(username);
        if (detections.isEmpty()) {
            detectionLabel.setText("No Detections Found!");
        } else {
            detectionLabel.setManaged(false);
            detections.sort(Comparator
                    .comparingInt((Detection d) -> mealOrder.get(d.meal()))
                    .thenComparing(Detection::period, Comparator.reverseOrder()));
            printColoredDetections(detections);
        }
    }

    private void fetchDailyIntakes() {
        List<Intake> intakes = intakeRepository.getDailyIntakes(username);
        if (intakes.isEmpty()) {
            intakeLabel.setText("No Intakes Found!");
        } else {
            intakeLabel.setText(printList(intakes));
            intakeLabel.setStyle("-fx-stroke: #4eb214;");
        }
    }

    private <T> String printList(List<T> list) {
        StringBuilder result = new StringBuilder();
        for (T item : list) {
            result.append(item.toString()).append("\n");
        }
        return result.toString();
    }

    private void printColoredDetections(List<Detection> list) {
        String level;
        for (Detection item : list) {
            Text newLine = new Text("  " + item.toString() + "\n");
            int parsedLevel = item.level();
            if (item.period().equals("Before eating")) {
                if (parsedLevel >= 80 && parsedLevel <= 130) {
                    level = "normal";
                    newLine.setStyle("-fx-stroke: #4eb214;");
                } else if ((parsedLevel > 130 && parsedLevel <= 180) || (parsedLevel >= 70 && parsedLevel < 80)) {
                    level = "warning";
                    newLine.setStyle("-fx-stroke: #ff6400;");
                } else {
                    level = "danger";
                    newLine.setStyle("-fx-stroke: #e10c0c;");
                }
            } else {
                if (parsedLevel >= 80 && parsedLevel <= 180) {
                    level = "normal";
                    newLine.setStyle("-fx-stroke: #4eb214;");
                } else if ((parsedLevel > 180 && parsedLevel <= 250) || (parsedLevel >= 70 && parsedLevel < 80)) {
                    level = "warning";
                    newLine.setStyle("-fx-stroke: #ff6400;");
                } else {
                    level = "danger";
                    newLine.setStyle("-fx-stroke: #e10c0c;");
                }
            }
            FontIcon icon = new FontIcon();
            icon.setIconSize(13);
            if (level.equals("normal")) {
                icon.setIconLiteral("bi-hand-thumbs-up");
                icon.setIconColor(Color.web("#4eb214"));
            } else if (level.equals("warning")) {
                icon.setIconLiteral("bi-exclamation-circle");
                icon.setIconColor(Color.web("#ff6400"));
            } else {
                icon.setIconLiteral("bi-exclamation-triangle");
                icon.setIconColor(Color.web("#e10c0c"));
            }
            flowContainer.getChildren().add(icon);
            flowContainer.getChildren().add(newLine);
        }
    }

    @FXML
    private void handleIntake() {
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        Therapy therapy = therapyRepository.getTherapyByPatient(username);
        if (therapy == null) {
            showError();
            return;
        }
        ViewNavigator.navigateToIntake();
    }

    @FXML
    private void handleDetection() {
        ViewNavigator.navigateToDetection();
    }

    @FXML
    private void handleViewAllIntakes() {
        ViewNavigator.setDataToView("intakes"); ViewNavigator.navigateToAllData();
    }

    @FXML
    private void handleViewAllDetections() {
        ViewNavigator.setDataToView("detections");
        ViewNavigator.navigateToAllData();
    }

    @FXML
    private void handleViewProfile() {
        ViewNavigator.navigateToProfile();
    }

    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }

    private void showError() {
        statusLabel.setText("Nessuna terapia attiva!");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}