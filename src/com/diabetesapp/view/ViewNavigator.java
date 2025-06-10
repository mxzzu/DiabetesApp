package com.diabetesapp.view;

import com.diabetesapp.Main;
import com.diabetesapp.controller.MainController;
import com.diabetesapp.model.Therapy;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;

/**
 * This class handles navigation between different views in the application.
 * It works as a bridge between controllers and views, allowing for simplified navigation.
 */
public class ViewNavigator {
    // Reference to the main controller
    private static MainController mainController;

    private static String patientToManage = null;
    private static Therapy therapyToEdit = null;
    
    // Current authenticated username
    private static String authenticatedUser = null;
    private static String userType = null;
    
    /**
     * Set the main controller reference
     * @param controller The MainController instance
     */
    public static void setMainController(MainController controller) {
        mainController = controller;
    }

    /**
     * Load and switch to a view
     * @param fxml The name of the FXML file to load
     */
    public static void loadView(String fxml) {
        try {
            URL fxmlUrl = Main.class.getResource("/resources/fxml/" + fxml);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Node view = loader.load();
            mainController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxml);
        }
    }
    
    /**
     * Navigate to the home view
     */
    public static void navigateToHome() {
        patientToManage = null;
        loadView("HomeView.fxml");
    }
    
    /**
     * Navigate to the login view
     */
    public static void navigateToLogin() {
        loadView("LoginView.fxml");
    }

    /**
     * Navigate to the dashboard view (protected)
     * Will redirect to login if not authenticated
     */
    public static void navigateToDashboard() {
        if (isAuthenticated() && getUserType().equals("patient")) {
            loadView("DashboardView.fxml");
        } else if (isAuthenticated()) {
            patientToManage = null;
            loadView("DoctorDashboardView.fxml");
        } else {
            navigateToLogin();
        }
    }
    
    /**
     * Navigate to the profile view (protected)
     * Will redirect to login if not authenticated
     */
    public static void navigateToProfile() {
        if (isAuthenticated()) {
            patientToManage = null;
            loadView("ProfileView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToPatients() {
        if (isAuthenticated()) {
            patientToManage = null;
            loadView("PatientsView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToIntake() {
        if (isAuthenticated()) {
            loadView("IntakeView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToDetection() {
        if (isAuthenticated()) {
            loadView("DetectionView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToTherapy(Therapy therapy) {
        if (isAuthenticated()) {
            therapyToEdit = therapy;
            loadView("TherapyView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToManagePatient() {
        if (isAuthenticated()) {
            loadView("PatientManagementView.fxml");
        } else {
            navigateToLogin();
        }
    }
    
    /**
     * Set the authenticated user
     * @param username The username of the authenticated user
     */
    public static void setAuthenticatedUser(String username, String user) {
        authenticatedUser = username;
        userType = user;
        mainController.updateNavBar(isAuthenticated());
    }

    public static void setPatientToManage(String username) {
        patientToManage = username;
    }
    
    /**
     * Get the authenticated user
     * @return The username of the authenticated user, or null if not authenticated
     */
    public static String getAuthenticatedUser() {
        return authenticatedUser;
    }

    public static String getPatientToManage() {
        return patientToManage;
    }
    public static Therapy getTherapyToEdit() {
        return therapyToEdit;
    }
    
    /**
     * Check if a user is authenticated
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return authenticatedUser != null;
    }

    public static String getUserType() { return userType; }
    
    /**
     * Logout the current user
     */
    public static void logout() {
        authenticatedUser = null;
        patientToManage = null;
        userType = null;
        mainController.updateNavBar(false);
        navigateToHome();
    }
}