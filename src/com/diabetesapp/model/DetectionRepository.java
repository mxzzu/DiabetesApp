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

public class DetectionRepository {
    private final List<Detection> detections = new ArrayList<>();
    private final MongoCollection<Document> detectionsCollection;

    public DetectionRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        detectionsCollection = db.getCollection(AppConfig.DETECTIONS_COLLECTION_NAME);
        loadDetections();
    }

    /**
     * Load detections from DB
     */
    private void loadDetections() {
        FindIterable<Document> docs = detectionsCollection.find();
        for (Document d : docs) {
            detections.add(new Detection(d.getString("username"), LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("meal"), d.getString("period"), d.getInteger("level")));
        }
    }

    /**
     * Add detection to DB
     */
    private void addDetectionToDB(Detection detection) {
        Document doc = new Document("username", detection.username())
                .append("date", detection.date().format(AppConfig.DATE_FORMAT)).append("meal", detection.meal()).append("period", detection.period()).append("level", detection.level());

        detectionsCollection.insertOne(doc);
    }

    /**
     * Save detection to the repository
     */
    public void saveDetection(Detection detection) {
        detections.add(detection);
        addDetectionToDB(detection);
    }

    public List<Detection> getDailyDetections(String username) {
        return DailyEntityUtils.getDailyEntities(username, detections);
    }

    public ObservableList<Detection> getAllDetectionsByPatient(String patient) {
        List<Detection> detections = new ArrayList<>();
        FindIterable<Document> docs = detectionsCollection.find(new Document("username", patient));
        for (Document d : docs) {
            detections.add(new Detection(d.getString("username"), LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("meal"), d.getString("period"), d.getInteger("level")));
        }

        return FXCollections.observableList(detections);
    }
}
