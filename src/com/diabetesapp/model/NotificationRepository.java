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

    public NotificationRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        notificationsCollection = db.getCollection(AppConfig.NOTIFICATIONS_COLLECTION_NAME);
        loadNotifications();
    }

    /**
     * Load notifications from DB
     */
    private void loadNotifications() {
        FindIterable<Document> docs = notificationsCollection.find();
        for (Document d : docs) {
            notifications.add(docToObj(d));
        }
    }

    /**
     * Add notifications to DB
     */
    private void addNotificationToDB(Notification notification) {
        Document doc = new Document("username", notification.username())
                .append("date", notification.date().format(AppConfig.DATE_FORMAT)).append("message", notification.message()).append("isAlerted", notification.isAlerted());

        notificationsCollection.insertOne(doc);
    }

    /**
     * Save notification to the repository
     */
    public void saveNotification(Notification notification) {
        notifications.add(notification);
        addNotificationToDB(notification);
    }

    public ObservableList<Notification> getNotificationsByUser(String username) {
        List<Notification> notifications = new ArrayList<>();
        FindIterable<Document> docs = notificationsCollection.find(new  Document("username", username));
        for (Document d : docs) {
            notifications.add(docToObj(d));
        }
        return FXCollections.observableList(notifications);
    }

    public boolean notificationExists(String username) {
        List<Notification> notifications = getNotificationsByUser(username);
        return notifications.stream().anyMatch(notification -> notification.date().isEqual(LocalDate.now()));
    }

    private Notification docToObj(Document d) {
        return new Notification(d.getString("username"), LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("message"), d.getBoolean("isAlerted"));
    }
}
