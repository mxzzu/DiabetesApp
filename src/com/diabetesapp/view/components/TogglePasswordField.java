package com.diabetesapp.view.components;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class TogglePasswordField extends AnchorPane {
    private final PasswordField passwordField = new PasswordField();
    private final TextField visibleField = new TextField();

    private final Image eyeOpen = new Image(getClass().getResourceAsStream("/resources/image/eye.png"));
    private final Image eyeClosed = new Image(getClass().getResourceAsStream("/resources/image/eye-off.png"));

    private final ImageView iconView = new ImageView();

    public TogglePasswordField() {
        // Sincronizza il testo tra passwordField e visibleField
        visibleField.textProperty().bindBidirectional(passwordField.textProperty());

        passwordField.setPromptText("Enter password");
        visibleField.setPromptText("Enter password");
        passwordField.getStyleClass().add("form-control");
        visibleField.getStyleClass().add("form-control");

        //Imposta lunghezza fissa
        passwordField.setPrefWidth(300);
        visibleField.setPrefWidth(300);

        // Nascondi il campo visibile all'inizio
        visibleField.setVisible(false);
        visibleField.setManaged(false);

        // Setup icona occhio ️
        iconView.setImage(eyeClosed);
        iconView.setFitWidth(16);
        iconView.setFitHeight(16);
        Button toggleButton = new Button();
        toggleButton.setGraphic(iconView);
        toggleButton.setFocusTraversable(false);
        toggleButton.setStyle("-fx-background-color: transparent;");

        // Gestione del toggle
        toggleButton.setOnAction(e -> toggleVisibility());

        this.getChildren().addAll(passwordField, visibleField, toggleButton);

        // Posiziona l’occhio sulla destra, centrato verticalmente
        AnchorPane.setRightAnchor(toggleButton, 8.0);
        AnchorPane.setTopAnchor(toggleButton, passwordField.getHeight() / 2);

        // Stessa posizione per i campi
        AnchorPane.setLeftAnchor(passwordField, 0.0);
        AnchorPane.setRightAnchor(passwordField, 0.0);
        AnchorPane.setLeftAnchor(visibleField, 0.0);
        AnchorPane.setRightAnchor(visibleField, 0.0);
    }

    private void toggleVisibility() {
        boolean isPasswordVisible = visibleField.isVisible();
        visibleField.setVisible(!isPasswordVisible);
        visibleField.setManaged(!isPasswordVisible);
        passwordField.setVisible(isPasswordVisible);
        passwordField.setManaged(isPasswordVisible);

        iconView.setImage(isPasswordVisible ? eyeClosed : eyeOpen);

        if (!isPasswordVisible) {
            Platform.runLater(() -> {
                visibleField.requestFocus();
                visibleField.positionCaret(visibleField.getText().length());
            });
        } else {
            Platform.runLater(() -> {
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());
            });
        }
    }

    // Metodo per recuperare la password
    public String getPassword() {
        return passwordField.isVisible() ? passwordField.getText() : visibleField.getText();
    }

    public void clear() {
        passwordField.clear();
        visibleField.clear();
    }
}
