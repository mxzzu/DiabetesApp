package com.diabetesapp.view.components;

import com.diabetesapp.model.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class PatientListRow extends HBox {
    private final User user;
    private final Runnable onManageAction;
    
    public PatientListRow(User user, Runnable onManageAction) {
        this.user = user;
        this.onManageAction = onManageAction;
        initialize();
    }
    
    private void initialize() {
        this.setSpacing(10);
        this.setPadding(new Insets(5));
        this.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        
        // Username label
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.setPrefWidth(150);
        
        // Spacer to push buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        MFXButton manageButton = new MFXButton("Manage Patient");
        manageButton.setButtonType(ButtonType.RAISED);
        manageButton.setOnAction(_ -> onManageAction.run());

        this.getChildren().addAll(usernameLabel, spacer, manageButton);
    }
}