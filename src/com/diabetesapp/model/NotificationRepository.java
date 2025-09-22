package com.diabetesapp.model;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.DBConfig;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private final List<Notification> notifications = new ArrayList<>();
    private final MongoCollection<Document> notificationsCollection;

    /**
     * Repository for storing notifications for all users
     */
    public NotificationRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        notificationsCollection = db.getCollection(AppConfig.NOTIFICATIONS_COLLECTION_NAME);
        loadNotifications();
    }

    /**
     * Loads notifications from the database
     */
    private void loadNotifications() {
        FindIterable<Document> docs = notificationsCollection.find();
        for (Document d : docs) {
            notifications.add(docToObj(d));
        }
    }

    /**
     * Saves a notification to the repository and the database
     * @param notification Notification to save
     */
    public void saveNotification(Notification notification) {
        notifications.add(notification);
        notificationsCollection.insertOne(objToDoc(notification));
    }

    /**
     * Fetches all notifications for given username
     * @param username Username to fetch
     * @return Returns an ObservableList of notifications
     */
    public ObservableList<Notification> getNotificationsByUser(String username) {
        List<Notification> notifications = new ArrayList<>();
        FindIterable<Document> docs = notificationsCollection.find(new  Document("username", username));
        for (Document d : docs) {
            notifications.add(docToObj(d));
        }
        return FXCollections.observableList(notifications);
    }

    /**
     * Checks whether exists a notification for that user today
     * @param username Username to check
     * @return Returns a boolean based on notification existance
     */
    public boolean notificationExists(String username) {
        List<Notification> notifications = getNotificationsByUser(username);
        return notifications.stream().anyMatch(notification -> notification.date().isEqual(LocalDate.now()));
    }

    /**
     * Swaps an old notification with a new one, used to change the value of isAlerted field
     * @param old Old notification object to delete
     * @param newNotification New notification to insert
     */
    public void setIsAlerted(Notification old, Notification newNotification) {
        removeNotifications(old);
        saveNotification(newNotification);
    }

    /**
     * Removes a specified notification from repository and DB
     * @param notification Old notification object to delete
     */
    public void removeNotifications(Notification notification) {
        notifications.remove(notification);
        notificationsCollection.deleteOne(objToDoc(notification));
    }

    /**
     * Parses a JSON Document into a Notification object
     * @param d Document to parse
     * @return Returns the parsed notification object
     */
    private Notification docToObj(Document d) {
        return new Notification(d.getString("username"), LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("title"), d.getString("message"), d.getBoolean("isAlerted"));
    }

    /**
     * Parses Notification object into a JSON Document
     * @param notification Object to change
     * @return Returns the parsed JSON Document
     */
    private Document objToDoc(Notification notification) {
        return new Document("username", notification.username()).append("date", notification.date().format(AppConfig.DATE_FORMAT)).append("title", notification.title()).append("message", notification.message()).append("isAlerted", notification.isAlerted());
    }
}
