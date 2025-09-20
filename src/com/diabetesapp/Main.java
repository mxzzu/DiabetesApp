package com.diabetesapp;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.model.*;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.HostServices;
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
    private static final ChangeRepository changeRepository = new ChangeRepository();
    private static final ConcTherapyRepository concTherapyRepository = new ConcTherapyRepository();
    private static final NotificationRepository notificationRepository = new NotificationRepository();
    private static HostServices hostServices;

    @Override
    public void start(Stage primaryStage) throws Exception {

        hostServices = getHostServices();

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        URL mainViewUrl = getClass().getResource("/resources/fxml/MainView.fxml");
        FXMLLoader loader = new FXMLLoader(mainViewUrl);
    
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1000, 800);
        URL cssUrl = getClass().getResource("/resources/css/styles.css");
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(cssUrl.toExternalForm());
        
        primaryStage.setTitle(AppConfig.APP_TITLE);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/image/logo.png")));
        primaryStage.setScene(scene);
        primaryStage.show();

        //System.out.println(BCrypt.hashpw("Mazzu105", BCrypt.gensalt())); //DEV
    }

    public static HostServices getHostServicesInstance() {
        return hostServices;
    }
    
    /**
     * Get the application-wide repositories
     */
    public static UserRepository getUserRepository() {
        return userRepository;
    }
    public static DetectionRepository getDetectionRepository() { return detectionRepository; }
    public static IntakeRepository getIntakeRepository() { return intakeRepository; }
    public static TherapyRepository getTherapyRepository() { return therapyRepository; }
    public static ChangeRepository getChangeRepository() { return changeRepository; }
    public static ConcTherapyRepository getConcTherapyRepository() { return concTherapyRepository; }
    public static NotificationRepository getNotificationRepository() { return notificationRepository; }

    public static void main(String[] args) {
        launch(args);
    }
}