package com.diabetesapp.view;

import com.diabetesapp.Main;
import com.diabetesapp.controller.MainController;
import com.diabetesapp.model.Therapy;
import com.diabetesapp.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;

public class ViewNavigator {
    // Reference to the main controller
    private static MainController mainController;

    // Current authenticated user info
    private static User authenticatedUser = null;
    private static String userType = null;
    private static boolean mustChangePassword = false;

    // Notifications flags
    private static boolean initialDashboardNotificationShown = false;
    private static boolean clearedNotification = false;

    // Misc Data
    private static String patientToManage = null;
    private static Therapy therapyToEdit = null;
    private static boolean userSaved =  false;
    private static String dataToView = null;

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
        if (isAuthenticated() && isMustChangePassword()) {
            navigateToProfile();
        } else if (isAuthenticated() && getUserType().equals("patient")) {
            loadView("DashboardView.fxml");
        } else if (isAuthenticated() && getUserType().equals("admin")) {
            navigateToRegister(false);
        } else if (isAuthenticated()) {
            patientToManage = null;
            loadView("DoctorDashboardView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToRegister(boolean userEntered) {
        if (userEntered) {
            userSaved = true;
            loadView("RegisterView.fxml");
        } else {
            userSaved = false;
            loadView("RegisterView.fxml");
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

    public static void navigateToMedicalInformations() {
        if (isAuthenticated()) {
            loadView("MedicalInformationsView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToHistory() {
        if (isAuthenticated()) {
            loadView("HistoryView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToData() {
        if (isAuthenticated()) {
            loadView("DataView.fxml");
        } else {
            navigateToLogin();
        }
    }

    public static void navigateToAllData() {
        if (isAuthenticated()) {
            loadView("AllDataView.fxml");
        }  else {
            navigateToLogin();
        }
    }

    /**
     * Set the authenticated user
     * @param user The authenticated user
     */
    public static void setAuthenticatedUser(User user) {
        authenticatedUser = user;
        userType = user.getUserType();
        mainController.updateNavBar(isAuthenticated());

        setInitialNotificationShown(false);
    }

    public static void setPatientToManage(String username) {
        patientToManage = username;
    }

    public static void setUserSaved(boolean value) {
        userSaved = value;
    }

    public static void setDataToView(String data) {
        dataToView = data;
    }

    public static void setMustChangePassword(boolean value) {
        mustChangePassword = value;
    }

    public static void setClearedNotification(boolean value) {
        clearedNotification = value;
    }

    public static void setInitialNotificationShown(boolean shown) {
        initialDashboardNotificationShown = shown;
    }

    /**
     * Get the authenticated user
     * @return The username of the authenticated user, or null if not authenticated
     */
    public static String getAuthenticatedUsername() {
        return authenticatedUser.getUsername();
    }

    public static String getAuthenticatedName() {
        return authenticatedUser.getName();
    }

    public static String getAuthenticatedSurnname() {
        return authenticatedUser.getSurname();
    }

    public static User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public static String getPatientToManage() {
        return patientToManage;
    }

    public static Therapy getTherapyToEdit() {
        return therapyToEdit;
    }

    public static String getDataToView() {
        return dataToView;
    }

    public static boolean isUserSaved() {
        return userSaved;
    }

    public static boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public static boolean hasClearedNotification() {
        return clearedNotification;
    }

    public static boolean hasInitialNotificationBeenShown() {
        return initialDashboardNotificationShown;
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
        userSaved = false;
        mustChangePassword = false;
        clearedNotification = false;
        mainController.updateNavBar(false);
        navigateToHome();

        // Resetta lo stato della notifica alla fine della sessione
        setInitialNotificationShown(false);
    }
}