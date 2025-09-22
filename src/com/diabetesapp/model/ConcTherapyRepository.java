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

public class ConcTherapyRepository {
    private final List<ConcTherapy> concTherapies = new ArrayList<>();
    private final MongoCollection<Document> concTherapyCollection;

    /**
     * Repository for storing concurrent therapies for patients
     */
    public ConcTherapyRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        concTherapyCollection = db.getCollection(AppConfig.CONCTHERAPY_COLLECTION_NAME);
        loadConcTherapies();
    }

    /**
     * Loads concurrent therapies from database
     */
    private void loadConcTherapies() {
        FindIterable<Document> docs = concTherapyCollection.find();
        for (Document d : docs) {
            concTherapies.add(docToObj(d));
        }
    }

    /**
     * Save a concurrent therapy to the repository and to the database
     * @param therapy Concurrent therapy to save
     */
    public void saveConcTherapy(ConcTherapy therapy) {
        concTherapies.add(therapy);
        concTherapyCollection.insertOne(objToDoc(therapy));
    }

    /**
     * Delete a concurrent therapy from the repository and database
     * @param therapy Concurrent therapy to delete
     */
    public void removeConcTherapy(ConcTherapy therapy) {
        concTherapies.remove(therapy);
        concTherapyCollection.deleteOne(objToDoc(therapy));
    }

    /**
     * Fetches all concurrent therapies for a specified patient
     * @param username Username of the patient
     * @return Returns an ObservableList containing all the concurrent therapies
     */
    public ObservableList<ConcTherapy> getConcTherapiesByUser(String username) {
        List<ConcTherapy> concTherapies = new ArrayList<>();
        FindIterable<Document> docs = concTherapyCollection.find(new  Document("username", username));
        for (Document d : docs) {
            concTherapies.add(docToObj(d));
         }
        return FXCollections.observableList(concTherapies);
    }

    /**
     * Parses a JSON Document object into a Concurrent Therapy
     * @param d Document to parse
     * @return Returns the parsed ConcTherapy object
     */
    private ConcTherapy docToObj(Document d) {
        String username = d.getString("username");
        String symptoms = d.getString("symptoms");
        String drugs = d.getString("drugs");
        LocalDate start = LocalDate.parse(d.getString("start"), AppConfig.DATE_FORMAT);
        LocalDate end = null;

        if (d.get("end") != null) {
            end = LocalDate.parse(d.getString("end"), AppConfig.DATE_FORMAT);
        }

        return new ConcTherapy(username, symptoms, drugs, start, end);
    }

    /**
     * Parses a ConcTherapy object into a JSON Document
     * @param therapy Therapy object to parse
     * @return Returns the parsed JSON Document object
     */
    private Document objToDoc(ConcTherapy therapy) {
        Document doc = new Document("username", therapy.username())
                .append("symptoms", therapy.symptoms()).append("drugs", therapy.drugs()).append("start", therapy.start().format(AppConfig.DATE_FORMAT));

        if (therapy.end() != null) {
            doc.append("end", therapy.end().format(AppConfig.DATE_FORMAT));
        }  else {
            doc.append("end", null);
        }
        return doc;
    }
}
