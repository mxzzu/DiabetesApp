package com.diabetesapp.controller;

import com.diabetesapp.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.PatientListHeader;
import com.diabetesapp.view.components.PatientListRow;
import java.util.Map;

public class PatientsController {
    @FXML
    private VBox patientManagementSection,  patientListContainer;

    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();
        String currentUsername = ViewNavigator.getAuthenticatedUser();
        User user = userRepository.getUser(currentUsername);
        populatePatientList();
    }

    private void populatePatientList() {
        // Clear the container first
        patientListContainer.getChildren().clear();
        
        // Add a header row
        PatientListHeader headerRow = new PatientListHeader();
        patientListContainer.getChildren().add(headerRow);
        
        // Get all users
        Map<String, User> users = userRepository.getAllUsers();

        boolean hasNonDoctorUsers = false;
        
        // Add a row for each non-admin user
        for (User user : users.values()) {
            // Skip admin users (they should not appear in this list)
            if (user.getUserType().equals("doctor")) {
                continue;
            }
            
            hasNonDoctorUsers = true;
            
            // Create a user row component and add it to the container
            PatientListRow userRow = new PatientListRow(
                user,
                () -> handleManagePatient(user.getUsername())
            );
            
            patientListContainer.getChildren().add(userRow);
        }

        if (!hasNonDoctorUsers) {
            Label noPatientsLabel = new Label("No patient found.");
            patientListContainer.getChildren().add(noPatientsLabel);
        }
    }

    private void handleManagePatient(String username) {
        ViewNavigator.setPatientToManage(username);
        ViewNavigator.navigateToManagePatient();
    }
    
    /**
     * Handle navigating back to the dashboard
     */
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
}