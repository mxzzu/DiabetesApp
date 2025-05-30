package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.PasswordUtil;
import com.diabetesapp.model.Doctor;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.PersonalInfoCard;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the profile view.
 * Handles displaying user information and updating the user's password.
 */
public class ProfileController {
    @FXML
    private Label statusLabel;

    @FXML
    private VBox personalInfoContainer;

    @FXML
    private MFXPasswordField newPasswordField, confirmPasswordField;

    private UserRepository userRepository;
    private String currentUsername;

    /**
     * Initialize the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        currentUsername = ViewNavigator.getAuthenticatedUser();
        PersonalInfoCard personalInfoCard = new PersonalInfoCard(currentUsername);

        // Hide the status label initially
        statusLabel.setVisible(false);
        personalInfoContainer.getChildren().add(personalInfoCard);
    }

    /**
     * Handle updating the user's password.
     * This method is called when the user clicks the "Update Password" button.
     */
    @FXML
    private void handleUpdatePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill out all password fields");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        
        // Update the user with the new password
        User currentUser = userRepository.getUser(currentUsername);
        User updatedUser = getUser(currentUser, hashedPassword);
        userRepository.saveUser(updatedUser);

        showSuccess();
        
        // Clear fields
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private User getUser(User currentUser, String newPassword) {
        String userType = currentUser.getUserType();
        User updatedUser;
        if (userType.equals("patient")) {
            Patient currentPatient = (Patient) currentUser;
            updatedUser = new Patient(currentUsername, newPassword, userType, currentPatient.getRiskFactors(), currentPatient.getPrevPats(), currentPatient.getComorbidities(), currentPatient.getDocUser());
        } else {
            Doctor currentDoctor = (Doctor) currentUser;
            updatedUser = new Doctor(currentUsername, newPassword, userType, currentDoctor.getMail());
        }
        return updatedUser;
    }

    /**
     * Handle navigating back to the dashboard.
     * This method is called when the user clicks the "Back to Dashboard" button.
     */
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
    
    /**
     * Show an error message in the status label.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
    
    /**
     * Show a success message in the status label.
     */
    private void showSuccess() {
        statusLabel.setText("Password updated successfully");
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}