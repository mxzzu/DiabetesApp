package com.diabetesapp.view.components;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.validation.Constraint;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.util.List;
import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

public class PersonalInfoCard extends VBox {
    private final VBox infoContainer = new VBox();
    private final String username;
    private final UserRepository userRepository;
    private MFXTextField newField;

    public PersonalInfoCard(String username, boolean mailUpdatable) {
        this.username = username;
        userRepository = Main.getUserRepository();

        this.getStyleClass().add("card");
        Label title = new Label("Personal Information");
        title.getStyleClass().add("card-header");

        this.getChildren().add(title);

        infoContainer.setSpacing(10);
        infoContainer.setPadding(new Insets(10, 0, 0, 0));

        createHBox("Username: ", username);
        fetchInformation(mailUpdatable);

        this.getChildren().add(infoContainer);

        this.setPadding(new Insets(15));
    }

    private void fetchInformation(boolean mailUpdatable) {
        String userType = userRepository.getUser(username).getUserType();
        String name = userRepository.getUser(username).getName();
        String surname =  userRepository.getUser(username).getSurname();
        String birthDate = userRepository.getUser(username).getBirthDate();
        String email = userRepository.getUser(username).getEmail();
        createHBox("Name: ", name);
        createHBox("Surname: ", surname);
        createHBox("Date of Birth: ", birthDate);
        createHBox("Email: ", email);
        if (userType.equals("patient")) {
            createSeparator();
            String docUser = ((Patient) userRepository.getUser(username)).getDocUser();
            User doc = userRepository.getUser(docUser);
            createHBox("Doctor:", String.format("%s %s", doc.getName(), doc.getSurname()));
            createHBox("Doctor Mail:", doc.getEmail());
        }
        if (mailUpdatable) {
            createSeparator();
            newField = new MFXTextField();
            newField.setFloatMode(FloatMode.BORDER);
            newField.setFloatingText("New Mail");
            newField.setPrefSize(1000.0, 40.0);
            newField.setVisible(false);
            newField.setManaged(false);
            newField.getStyleClass().add("validatedField");
            Label errorLabel = new Label();
            errorLabel.getStyleClass().add("validationLabel");
            errorLabel.setMaxSize(1.7976931348623157E308, Double.NEGATIVE_INFINITY);
            errorLabel.setWrapText(true);
            errorLabel.setTextFill(Paint.valueOf("ef6e6b"));
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
            newField.addEventFilter(KeyEvent.KEY_PRESSED, AppConfig.updateMail(username, newField, errorLabel));
            infoContainer.getChildren().addAll(newField, errorLabel);
            MFXButton updateButton = new MFXButton("Update Mail");
            updateButton.setButtonType(ButtonType.RAISED);
            updateButton.setMnemonicParsing(false);
            updateButton.getStyleClass().add("button-success");
            updateButton.setOnAction(updateMail(username, newField, errorLabel));
            infoContainer.getChildren().add(updateButton);
            Validator.createMailConstraints(newField, errorLabel);
        }
    }

    private void createSeparator() {
        Separator sep = new Separator();
        sep.getStyleClass().add("separator");
        infoContainer.getChildren().add(sep);
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

    private EventHandler<ActionEvent> updateMail(String username, MFXTextField mailField, Label errorLabel) {
        return _ -> {
            if (!mailField.isVisible()) {
                mailField.setVisible(true);
                mailField.setManaged(true);
            } else {
                List<Constraint> constraints = mailField.validate();
                if (!constraints.isEmpty()) {
                    mailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
                    errorLabel.setText(constraints.getFirst().getMessage());
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                    return;
                }
                AppConfig.changeMail(username, newField.getText());
            }
        };
    }
}