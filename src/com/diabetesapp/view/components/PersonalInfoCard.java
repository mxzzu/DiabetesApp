package com.diabetesapp.view.components;

import com.diabetesapp.Main;
import com.diabetesapp.model.Doctor;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PersonalInfoCard extends VBox {
    private final VBox infoContainer = new VBox();
    private final String username;
    private final UserRepository userRepository;

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
        } else {
            String doc = ((Patient) userRepository.getUser(username)).getDocUser();
            String mail = ((Doctor) userRepository.getUser(doc)).getMail();
            createHBox("Doctor:", doc);
            createHBox("Mail:", mail);
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
}
