package com.diabetesapp;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.model.DetectionRepository;
import com.diabetesapp.model.IntakeRepository;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.model.UserRepository;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    
    private static final UserRepository userRepository = new UserRepository();
    private static final DetectionRepository detectionRepository = new DetectionRepository();
    private static final IntakeRepository intakeRepository = new IntakeRepository();
    private static final TherapyRepository therapyRepository = new TherapyRepository();

    @Override
    public void start(Stage primaryStage) throws Exception {

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        // Load the main application view

        URL mainViewUrl = getClass().getResource("/resources/fxml/MainView.fxml");
        FXMLLoader loader = new FXMLLoader(mainViewUrl);
    
        Parent root = loader.load();
        
        // Set up the scene
        Scene scene = new Scene(root, 1000, 800);
        URL cssUrl = getClass().getResource("/resources/css/styles.css");
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(cssUrl.toExternalForm());
        
        // Configure and show the stage
        primaryStage.setTitle(AppConfig.APP_TITLE);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/image/gio.png")));
        primaryStage.setScene(scene);
        primaryStage.show();

        //System.out.println(BCrypt.hashpw("ambro", BCrypt.gensalt())); //DEV
    }
    
    /**
     * Get the application-wide user repository
     */
    public static UserRepository getUserRepository() {
        return userRepository;
    }
    public static DetectionRepository getDetectionRepository() { return detectionRepository; }
    public static IntakeRepository getIntakeRepository() { return intakeRepository; }
    public static TherapyRepository getTherapyRepository() { return therapyRepository; }

    public static void main(String[] args) {
        launch(args);
    }
}