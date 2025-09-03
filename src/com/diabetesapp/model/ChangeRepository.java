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
import java.util.ArrayList;
import java.util.List;

public class ChangeRepository {
    private final List<Change> changes = new ArrayList<>();
    private final MongoCollection<Document> changesCollection;

    public ChangeRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        changesCollection = db.getCollection(AppConfig.CHANGES_COLLECTION_NAME);
        loadChanges();
    }

    /**
     * Load changes from DB
     */
    private void loadChanges() {
        FindIterable<Document> docs = changesCollection.find();
        for (Document d : docs) {
            changes.add(new Change(d.getString("patientUser"), d.getString("docName"), d.getString("docUser"), d.getString("change"), d.getString("changeDate")));
        }
    }

    /**
     * Add changes to DB
     */
    private void addChangeToDB(Change change) {
        Document doc = new Document("patientUser", change.patientUser())
                .append("docName", change.docName()).append("docUser", change.docUser()).append("change", change.change()).append("changeDate", change.changeDate());

        changesCollection.insertOne(doc);
    }

    /**
     * Save change to the repository
     */
    public void saveChange(Change change) {
        changes.add(change);
        addChangeToDB(change);
    }

    public ObservableList<Change> getChangesByUser(String patientUser) {
        List<Change> changes = new ArrayList<>();
        FindIterable<Document> docs = changesCollection.find(new  Document("patientUser", patientUser));
        for (Document d : docs) {
            changes.add(new Change(d.getString("patientUser"), d.getString("docName"), d.getString("docUser"), d.getString("change"), d.getString("changeDate")));
        }
        return FXCollections.observableList(changes);
    }
}
