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

    public ConcTherapyRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        concTherapyCollection = db.getCollection(AppConfig.CONCTHERAPY_COLLECTION_NAME);
        loadConcTherapies();
    }

    /**
     * Load concomitant therapies from DB
     */
    private void loadConcTherapies() {
        FindIterable<Document> docs = concTherapyCollection.find();
        for (Document d : docs) {
            concTherapies.add(docToObj(d));
        }
    }

    /**
     * Save concomitant therapy to the repository
     */
    public void saveConcTherapy(ConcTherapy therapy) {
        concTherapies.add(therapy);
        concTherapyCollection.insertOne(objToDoc(therapy));
    }

    /**
     * Delete concomitant therapy from the repository
     */
    public void removeConcTherapy(ConcTherapy therapy) {
        concTherapies.remove(therapy);
        concTherapyCollection.deleteOne(objToDoc(therapy));
    }

    public ObservableList<ConcTherapy> getConcTherapiesByUser(String username) {
        List<ConcTherapy> concTherapies = new ArrayList<>();
        FindIterable<Document> docs = concTherapyCollection.find(new  Document("username", username));
        for (Document d : docs) {
            concTherapies.add(docToObj(d));
         }
        return FXCollections.observableList(concTherapies);
    }

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
