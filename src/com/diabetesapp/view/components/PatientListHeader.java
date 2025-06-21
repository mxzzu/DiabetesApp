package com.diabetesapp.view.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class PatientListHeader extends HBox {
    
    public PatientListHeader() {
        initialize();
    }
    
    private void initialize() {
        this.setSpacing(10);
        this.setPadding(new Insets(5));
        this.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-padding: 5 5 10 5;");

        Label nameHeader = new Label("Name");
        nameHeader.setStyle("-fx-font-weight: bold;");
        nameHeader.setPrefWidth(150);

        Label surnameHeader = new Label("Surname");
        surnameHeader.setStyle("-fx-font-weight: bold;");
        surnameHeader.setPrefWidth(150);

        Label usernameHeader = new Label("Username");
        usernameHeader.setStyle("-fx-font-weight: bold;");
        usernameHeader.setPrefWidth(150);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label actionsHeader = new Label("Actions");
        actionsHeader.setStyle("-fx-font-weight: bold;");
        
        this.getChildren().addAll(nameHeader, surnameHeader, usernameHeader, spacer, actionsHeader);
    }
}