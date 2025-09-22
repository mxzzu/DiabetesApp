package com.diabetesapp.view.components;

import com.diabetesapp.Main;
import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.validation.Constraint;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;
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

    /**
     * Fetches main user informations like name, surname, date of birth and email
     * @param mailUpdatable Boolean value to enable or disable the Update Mail button
     */
    private void fetchInformation(boolean mailUpdatable) {

        User userToDisplay = userRepository.getUser(username);
        User viewer = ViewNavigator.getAuthenticatedUser();

        createHBox("Name: ", userToDisplay.getName());
        createHBox("Surname: ", userToDisplay.getSurname());
        createHBox("Date of Birth: ", userToDisplay.getBirthDate());

        if (viewer != null && !viewer.getUsername().equals(userToDisplay.getUsername())) {
            createRow("Email:", createEmailNode(userToDisplay));
        } else {
            createHBox("Email: ", userToDisplay.getEmail());
        }

        if (userToDisplay.getUserType().equals("patient")) {
            createSeparator();
            Patient patient = (Patient) userToDisplay;
            User doctor = userRepository.getUser(patient.getDocUser());
            createHBox("Doctor:", String.format("%s %s", doctor.getName(), doctor.getSurname()));

            if (viewer != null && viewer.getUsername().equals(patient.getUsername())) {
                createRow("Doctor Mail:", createEmailNode(doctor));
            } else {
                createHBox("Doctor Mail:", doctor.getEmail());
            }
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

    /**
     * Creates an HBox with the email and an icon that links with the mail client
     * @param user User to which send the email
     * @return Returns the HBox node
     */
    private HBox createEmailNode(User user) {
        Label emailLabel = new Label(user.getEmail());
        FontIcon icon = getIcon(user);
        HBox emailBox = new HBox(5, emailLabel, icon);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        return emailBox;
    }

    /**
     * Creates the icon with the link
     * @param user User to which send the email
     * @return Returns the FontIcon object
     */
    private static FontIcon getIcon(User user) {
        FontIcon mailIcon = new FontIcon("bi-box-arrow-up-right");
        mailIcon.setIconColor(Color.web("#780dd7"));
        mailIcon.setIconSize(18);
        mailIcon.setCursor(Cursor.HAND);

        mailIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            if (Main.getHostServicesInstance() != null) {
                Main.getHostServicesInstance().showDocument("mailto:" + user.getEmail());
            }
        });

        return mailIcon;
    }

    /**
     * Creates a line separator to divide the sections of the card
     */
    private void createSeparator() {
        Separator sep = new Separator();
        sep.getStyleClass().add("separator");
        infoContainer.getChildren().add(sep);
    }

    /**
     * Creates an HBox representing the row
     * @param title Title of the row
     * @param value Value of the row
     */
    private void createHBox(String title, String value) {
        createRow(title, new Label(value));
    }

    /**
     * Creates a row (HBox) with a title and any Node (Label, Link, Icon...)
     * @param title Title of the row
     * @param valueNode Node to show next to the title
     */
    private void createRow(String title, Node valueNode) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("bold-text");
        hBox.getChildren().addAll(titleLabel, valueNode);
        infoContainer.getChildren().add(hBox);
    }

    /**
     * Creates an handler for the mail field
     * @param username Username of the user
     * @param mailField TextField of the mail
     * @param errorLabel Error label to show any error
     * @return Returns the EventHandler object to assing to the field
     */
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