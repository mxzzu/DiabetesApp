package com.diabetesapp.controller;

import com.diabetesapp.view.ViewNavigator;
import com.diabetesapp.view.components.NavBar;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private BorderPane mainContainer;
    
    @FXML
    private VBox navBarContainer;
    
    private NavBar navBar;
    
    @FXML
    public void initialize() {
        navBar = new NavBar();
        navBarContainer.getChildren().add(navBar);
        ViewNavigator.setMainController(this);
        ViewNavigator.navigateToHome();
    }
    
    /**
     * Set the content of the main area
     * @param content Node object of the content to show
     */
    public void setContent(Node content) {
        mainContainer.setCenter(content);
    }
    
    /**
     * Update the navigation bar based on authentication status
     * @param isAuthenticated Boolean value that represents whether the user is authenticated
     */
    public void updateNavBar(boolean isAuthenticated) {
        String authUser;
        if (ViewNavigator.getAuthenticatedUser() != null && ViewNavigator.getAuthenticatedUser().getUserType().equals("admin")) {
            authUser = "admin";
        } else if (ViewNavigator.getAuthenticatedUser() != null) {
            authUser = ViewNavigator.getAuthenticatedName();
        } else {
            authUser = null;
        }
        navBar.updateAuthStatus(isAuthenticated, authUser);
    }
}