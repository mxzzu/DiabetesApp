package com.diabetesapp.model;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.DBConfig;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
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
            detections.add(new Detection(d.getString("username"), d.getString("date"), d.getString("meal"), d.getString("period"), d.getString("level")));
        }
    }

    /**
     * Add detection to DB
     */
    private void addDetectionToDB(Detection detection) {
        Document doc = new Document("username", detection.username())
                .append("date", detection.date()).append("meal", detection.meal()).append("period", detection.period()).append("level", detection.level());

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
}
