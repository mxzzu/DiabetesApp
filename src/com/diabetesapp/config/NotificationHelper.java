package com.diabetesapp.config;

import com.diabetesapp.Main;
import com.diabetesapp.model.Notification;
import com.diabetesapp.model.NotificationRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationHelper {

    /**
     * Fetch notifications and list them in a card
     * @param username Fetch notifications by this username
     * @param notificationLabel Text item to diasble if no notifications found
     * @param allDoctors Boolean value to fetch the all doctors notification too
     * @return Returns a List object with notifications fetched
     */
    public static List<Notification> fetchNotifications(String username, Text notificationLabel, boolean allDoctors) {
        NotificationRepository notificationRepository = Main.getNotificationRepository();
        List<Notification> notifications = notificationRepository.getNotificationsByUser(username);
        if (allDoctors) {
            List<Notification> doctorsNotification = notificationRepository.getNotificationsByUser("All Doctors");
            if (!doctorsNotification.isEmpty()) {
                List<Notification> clearedNotifications = ViewNavigator.getClearedNotifications();
                for (Notification notification : doctorsNotification) {
                    if (!clearedNotifications.contains(notification)) {
                        notifications.add(notification);
                    }
                }
            }
        }
        if (notifications.isEmpty()) {
            notificationLabel.setText("No Notifications Found!");
        } else {
            notificationLabel.setManaged(false);
            notificationLabel.setVisible(false);
        }
        return notifications;
    }

    /**
     * Prints notification list with text fill and icon
     * @param list List to print
     * @param notificationFlow TextFlow item that will contain notifications
     */
    public static void printColoredNotifications(List<Notification> list, TextFlow notificationFlow) {
        for (Notification  notification : list) {
            Text newLine = new Text("  " + notification.toString() + "\n");
            newLine.setStyle("-fx-stroke: #e10c0c;");
            FontIcon icon = new FontIcon();
            icon.setIconSize(13);
            icon.setIconLiteral("bi-exclamation-triangle");
            icon.setIconColor(Color.web("#e10c0c"));
            notificationFlow.getChildren().add(icon);
            notificationFlow.getChildren().add(newLine);
        }
    }

    /**
     * Shows sequentially the notifications pop-ups
     * @param username User of which notifications should show
     * @param rootPane AnchorPane where to show pop-up
     * @param allDoctors Flag to include All Doctors notifications.
     */
    public static void showPopUpSequentially(String username, AnchorPane rootPane, boolean allDoctors) {
        NotificationRepository notificationRepository = Main.getNotificationRepository();
        List<Notification> notificationsToShow =notificationRepository.getNotificationsByUser(username);

        if (allDoctors) {
            List<Notification> doctorsNotification = notificationRepository.getNotificationsByUser("All Doctors");
            if (!doctorsNotification.isEmpty()) {
                notificationsToShow.addAll(doctorsNotification);
            }
        }

        List<Notification> unalertedNotifications = notificationsToShow.stream()
                .filter(n -> !n.isAlerted())
                .collect(Collectors.toList());

        if (!unalertedNotifications.isEmpty()) {
            displayNextNotification(unalertedNotifications, rootPane, notificationRepository);
        }
    }

    /**
     * Recursive method that shows a pop-up and removes it from the list
     * @param notifications List of notifications to show
     * @param rootPane AnchorPane where to show pop-up
     * @param notificationRepository Notification Repository
     */
    private static void displayNextNotification(List<Notification> notifications, AnchorPane rootPane, NotificationRepository notificationRepository) {
        if (notifications.isEmpty()) {
            return;
        }

        Notification notification = notifications.removeFirst();

        Text header = new Text(notification.title());
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        FontIcon closeIcon = new FontIcon("bi-x");
        MFXButton closeButton = new MFXButton("", closeIcon);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setStyle("-fx-background-color: transparent;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerBox = new HBox(header, spacer, closeButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox notificationContent = createVBox(notification.message(), headerBox);

        rootPane.getChildren().add(notificationContent);
        AnchorPane.setBottomAnchor(notificationContent, 20.0);
        AnchorPane.setRightAnchor(notificationContent, 20.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(10));

        Runnable hideAndShowNext = () -> {
            if (!rootPane.getChildren().contains(notificationContent)) return;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), notificationContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(_ -> {
                rootPane.getChildren().remove(notificationContent);
                displayNextNotification(notifications, rootPane, notificationRepository);
            });
            fadeOut.play();
        };

        delay.setOnFinished(_ -> hideAndShowNext.run());
        closeButton.setOnAction(_ -> {
            delay.stop();
            hideAndShowNext.run();
        });

        Notification newNotification = new Notification(notification.username(), notification.date(), notification.title(), notification.message(), true);
        notificationRepository.setIsAlerted(notification, newNotification);

        delay.play();
    }

    /**
     * Create vbox for notification pop-up
     * @param message Message to be shown in pop-up
     * @param headerBox HBox used for pop-up header
     * @return Returns the content of the pop-up
     */
    private static VBox createVBox(String message, HBox headerBox) {
        Text content = new Text(message);

        VBox notificationContent = new VBox(10, headerBox, content);
        notificationContent.setPadding(new Insets(15));
        notificationContent.setStyle("-fx-background-color: #d3d3d3; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.2, 0, 1); -fx-background-radius: 5; -fx-border-radius: 5;");
        notificationContent.setOpacity(0);
        return notificationContent;
    }
}
