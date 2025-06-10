package com.diabetesapp.view.components;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.model.Doctor;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PersonalInfoCard extends VBox {
    private final VBox infoContainer = new VBox();
    private final String username;
    private final UserRepository userRepository;
    private MFXTextField newField;

    public PersonalInfoCard(String username) {
        this.username = username;
        userRepository = Main.getUserRepository();

        this.getStyleClass().add("card");
        Label title = new Label("Personal Information");
        title.getStyleClass().add("card-header");

        this.getChildren().add(title);

        infoContainer.setSpacing(10);
        infoContainer.setPadding(new Insets(10, 0, 0, 0));

        createHBox("Username: ", username);
        fetchInformation();

        this.getChildren().add(infoContainer);

        this.setPadding(new Insets(15));
    }

    private void fetchInformation() {
        String userType = userRepository.getUser(username).getUserType();
        if (userType.equals("doctor")) {
            String mail = ((Doctor) userRepository.getUser(username)).getMail();
            createHBox("Mail:", mail);
            newField = new MFXTextField();
            newField.setFloatMode(FloatMode.BORDER);
            newField.setFloatingText("New Mail");
            newField.setPrefSize(1000.0, 40.0);
            newField.setVisible(false);
            newField.setManaged(false);
            newField.addEventFilter(KeyEvent.KEY_PRESSED, AppConfig.updateMail(username, newField));
            infoContainer.getChildren().add(newField);
            MFXButton updateButton = new MFXButton("Update Mail");
            updateButton.setButtonType(ButtonType.RAISED);
            updateButton.setMnemonicParsing(false);
            updateButton.getStyleClass().add("button-success");
            updateButton.setOnAction(updateMail());
            infoContainer.getChildren().add(updateButton);
        } else {
            String doc = ((Patient) userRepository.getUser(username)).getDocUser();
            String mail = ((Doctor) userRepository.getUser(doc)).getMail();
            createHBox("Doctor:", doc);
            createHBox("Doctor Mail:", mail);
        }
    }

    private void createHBox(String title, String value) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("form-label");
        Label valueLabel = new Label(value);
        hBox.getChildren().add(titleLabel);
        hBox.getChildren().add(valueLabel);
        infoContainer.getChildren().add(hBox);
    }

    private EventHandler<ActionEvent> updateMail() {
        return _ -> {
            if (!newField.isVisible()) {
                newField.setVisible(true);
                newField.setManaged(true);
            } else {
                AppConfig.changeMail(username, newField.getText());
                ViewNavigator.navigateToProfile();
            }
        };
    }
}
