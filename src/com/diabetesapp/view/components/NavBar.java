package com.diabetesapp.view.components;

import com.diabetesapp.view.ViewNavigator;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBar extends HBox {
    private boolean isAuthenticated;
    private String username;
    
    public NavBar() {
        this(false, null);
    }
    
    public NavBar(boolean isAuthenticated, String username) {
        this.isAuthenticated = isAuthenticated;
        this.username = username;
        initialize();
    }
    
    /**
     * Initialize the navigation bar
     */
    private void initialize() {
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #780dd7;");
        
        Label brandLabel = new Label("DiabetesApp");
        brandLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        this.getChildren().add(brandLabel);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        this.getChildren().add(spacer);
        
        // Create buttons based on authentication status
        if (isAuthenticated) {
            createAuthenticatedNavButtons();
        } else {
            createUnauthenticatedNavButtons();
        }
    }
    
    /**
     * Create navigation buttons for authenticated users
     */
    private void createAuthenticatedNavButtons() {
        Button homeBtn = createNavButton("Home", _ -> ViewNavigator.navigateToHome());
        Button dashboardBtn = createNavButton("Dashboard", _ -> ViewNavigator.navigateToDashboard());

        Scene currentScene = this.getScene(); // potrebbe essere null all'inizio
        if (currentScene != null) {
            UserDropDown userButton = new UserDropDown(username, currentScene);
            this.getChildren().addAll(homeBtn, dashboardBtn, userButton);
        } else {
            // Delay: aggiungi alla scena appena disponibile
            this.sceneProperty().addListener((_, _, newScene) -> {
                if (newScene != null) {
                    UserDropDown userButton = new UserDropDown(username, newScene);
                    this.getChildren().addAll(homeBtn, dashboardBtn, userButton);
                }
            });
        }
        
        Label userLabel = new Label("Hello, " + username);
        userLabel.setStyle("-fx-text-fill: white;");
    }

    /**
     * Create navigation buttons for unauthenticated users
     */
    private void createUnauthenticatedNavButtons() {
        Button homeBtn = createNavButton("Home", _ -> ViewNavigator.navigateToHome());
        Button loginBtn = createNavButton("Login", _ -> ViewNavigator.navigateToLogin());

        this.getChildren().addAll(homeBtn, loginBtn);
    }
    
    /**
     * Create a styled navigation button
     */
    private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        button.setOnAction(handler);
        
        // Hover effect
        button.setOnMouseEntered(_ ->
            button.setStyle("-fx-background-color: #622f81; -fx-text-fill: white; -fx-cursor: hand;"));
        button.setOnMouseExited(_ ->
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;"));
        
        return button;
    }
    
    /**
     * Update the navigation bar based on authentication status
     */
    public void updateAuthStatus(boolean isAuthenticated, String username) {
        this.isAuthenticated = isAuthenticated;
        this.username = username;
        this.getChildren().clear();
        initialize();
    }
}