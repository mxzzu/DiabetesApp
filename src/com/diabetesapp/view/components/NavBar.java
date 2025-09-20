package com.diabetesapp.view.components;

import com.diabetesapp.view.ViewNavigator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

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
        this.setAlignment(Pos.CENTER);
        
        Label brandLabel = new Label("DiabetesApp");
        brandLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        this.getChildren().add(brandLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        this.getChildren().add(spacer);
        
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
        Button userBtn = createNavButton("Hello, " + username, _ -> ViewNavigator.navigateToProfile());

        FontIcon icon = new FontIcon();
        icon.setIconSize(16);
        icon.setIconLiteral("bi-person");
        icon.setIconColor(Color.WHITE);
        userBtn.setGraphic(icon);

        this.getChildren().addAll(homeBtn, dashboardBtn, userBtn);
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