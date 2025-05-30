package com.diabetesapp.view.components;

import com.diabetesapp.view.ViewNavigator;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.List;

public class UserDropDown extends Button {
    private final Popup popup;

    public UserDropDown(String username, Scene scene) {
        super("Hello, " + username);

        this.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        this.setOnMouseEntered(_ -> this.setStyle("-fx-background-color: #622f81; -fx-text-fill: white; -fx-cursor: hand;"));
        this.setOnMouseExited(_ -> this.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;"));

        // Crea popup
        popup = new Popup();
        popup.setAutoHide(true); // Chiude cliccando fuori

        VBox popupContent = new VBox(10);
        popupContent.setStyle(
                "-fx-background-color: white; " +
                "-fx-padding: 10; " +
                "-fx-border-color: #780dd7; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: #780dd7;"
        );

        Label profile = new Label("Profile");
        Label logout = new Label("Logout");

        for (Label label : List.of(profile, logout)) {
            label.setStyle("-fx-text-fill: #780dd7; -fx-font-size: 14px; -fx-cursor: hand;");
            label.setOnMouseEntered(_ -> label.setStyle("-fx-underline: true; -fx-text-fill: #780dd7; -fx-cursor: hand;"));
            label.setOnMouseExited(_ -> label.setStyle("-fx-underline: false; -fx-text-fill: #780dd7; -fx-cursor: hand;"));
        }

        profile.setOnMouseClicked(_ -> {
            popup.hide();
            ViewNavigator.navigateToProfile();
        });

        logout.setOnMouseClicked(_ -> {
            popup.hide();
            ViewNavigator.logout();
        });

        popupContent.getChildren().addAll(profile, new Separator(), logout);
        popup.getContent().add(popupContent);

        // Mostra/nasconde il popup
        this.setOnAction(_ -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
                Bounds bounds = this.localToScreen(this.getBoundsInLocal());
                popup.show(this, bounds.getMinX(), bounds.getMaxY());
            }
        });

        // Chiudi anche cliccando altrove (se il popup è aperto)
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing() && !popup.getOwnerNode().equals(this) && !popup.getContent().contains(event.getTarget())) {
                popup.hide();
            }
        });
    }
}
