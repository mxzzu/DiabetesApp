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

    /**
     * Repository for storing medical information changes
     */
    public ChangeRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        changesCollection = db.getCollection(AppConfig.CHANGES_COLLECTION_NAME);
        loadChanges();
    }

    /**
     * Loads changes from database
     */
    private void loadChanges() {
        FindIterable<Document> docs = changesCollection.find();
        for (Document d : docs) {
            changes.add(docToObj(d));
        }
    }

    /**
     * Adds a change to the database
     * @param change Change object to add
     */
    private void addChangeToDB(Change change) {
        Document doc = new Document("patientUser", change.patientUser())
                .append("docName", change.docName()).append("docUser", change.docUser()).append("change", change.change()).append("changeDate", change.changeDate());

        changesCollection.insertOne(doc);
    }

    /**
     * Saves a change to the repository
     * @param change Change object to save
     */
    public void saveChange(Change change) {
        changes.add(change);
        addChangeToDB(change);
    }

    /**
     * Fetches all changes for a specified user
     * @param patientUser Username of the patient
     * @return Returns an ObservableList object containing all the changes
     */
    public ObservableList<Change> getChangesByUser(String patientUser) {
        List<Change> changes = new ArrayList<>();
        FindIterable<Document> docs = changesCollection.find(new  Document("patientUser", patientUser));
        for (Document d : docs) {
            changes.add(docToObj(d));
        }
        return FXCollections.observableList(changes);
    }

    /**
     * Parses a JSON Document object into a Change
     * @param d Document to parse
     * @return Returns the parsed Change object
     */
    private Change docToObj(Document d) {
        return new Change(d.getString("patientUser"), d.getString("docName"), d.getString("docUser"), d.getString("change"), d.getString("changeDate"));
    }
}
