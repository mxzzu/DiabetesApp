package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.PasswordUtil;
import com.diabetesapp.config.TableUtils;
import com.diabetesapp.model.*;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.PersonalInfoCard;
import com.diabetesapp.config.Validator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ProfileController {
    @FXML
    private Label statusLabel, validationLabel1, validationLabel2;

    @FXML
    private VBox personalInfoContainer, therapyCard;

    @FXML
    private MFXTableView<Therapy> therapyTable;

    @FXML
    private MFXPasswordField newPasswordField, confirmPasswordField;

    @FXML
    private MFXButton backToDashboardBtn;

    @FXML
    private MFXProgressSpinner progressSpinner;

    private UserRepository userRepository;
    private String currentUsername;

    private TherapyRepository therapyRepository;
    private ObservableList<Therapy> therapies;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        currentUsername = ViewNavigator.getAuthenticatedUsername();
        PersonalInfoCard personalInfoCard = new PersonalInfoCard(currentUsername, !ViewNavigator.isMustChangePassword());

        statusLabel.setVisible(false);
        personalInfoContainer.getChildren().add(personalInfoCard);

        if (ViewNavigator.getUserType().equals("patient")) {
            createTherapyCard();
        }

        Validator.createPasswordConstraints(newPasswordField, confirmPasswordField, validationLabel1);
        Validator.createPasswordConstraints(confirmPasswordField, newPasswordField, validationLabel2);

        if (ViewNavigator.isMustChangePassword()) {
            backToDashboardBtn.setDisable(true);
        }
    }

    /**
     * Creates Therapies card
     */
    private void createTherapyCard() {
        therapyCard.setManaged(true);
        therapyCard.setVisible(true);
        therapyRepository =  Main.getTherapyRepository();
        therapies = FXCollections.observableArrayList(therapyRepository.getTherapiesByPatient(ViewNavigator.getAuthenticatedUsername()));

        TableUtils.createTherapyTable(therapyTable, therapies);
        TableUtils.setTableSize(therapyTable);
    }

    @FXML
    private void handleUpdatePassword() {
        boolean check1 = Validator.checkConstraints(newPasswordField, validationLabel1);
        boolean check2 = Validator.checkConstraints(confirmPasswordField, validationLabel2);

        if (!check1 || !check2) {
            return;
        }

        String newPassword = newPasswordField.getText();

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        
        User currentUser = userRepository.getUser(currentUsername);
        User updatedUser = getUser(currentUser, hashedPassword);
        userRepository.modifyUser(updatedUser);

        showSuccess();
        
        newPasswordField.clear();
        confirmPasswordField.clear();

        if (ViewNavigator.isMustChangePassword()) {
            ViewNavigator.setMustChangePassword(false);
            progressSpinner.setVisible(true);
            progressSpinner.setManaged(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(_ -> ViewNavigator.navigateToDashboard());
            pause.play();
        }
    }

    /**
     * Creates an updated User object with the newly changed password
     * @param currentUser User object of the current user to modify
     * @param newPassword String with the new password to change
     * @return Returns the updated User object
     */
    private User getUser(User currentUser, String newPassword) {
        String userType = currentUser.getUserType();
        User updatedUser;
        if (userType.equals("patient")) {
            Patient currentPatient = (Patient) currentUser;
            updatedUser = new Patient(currentUsername, newPassword, userType, currentPatient.getName(), currentPatient.getSurname(), currentPatient.getBirthDate(), currentPatient.getGender(), currentPatient.getEmail(), false, currentPatient.getRiskFactors(), currentPatient.getPrevPats(), currentPatient.getComorbidities(), currentPatient.getDocUser());
        } else {
            updatedUser = new User(currentUsername, newPassword, userType, currentUser.getName(), currentUser.getSurname(), currentUser.getBirthDate(), currentUser.getGender(), currentUser.getEmail(), false);
        }
        return updatedUser;
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }

    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }

    /**
     * Show a success message in the status label.
     */
    private void showSuccess() {
        statusLabel.setText("Password updated successfully");
        statusLabel.getStyleClass().add("alert-success");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}