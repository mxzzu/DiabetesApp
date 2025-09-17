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
            concTherapies.add(new ConcTherapy(d.getString("username"), d.getString("symptoms"), d.getString("drugs"), LocalDate.parse(d.getString("start"), AppConfig.DATE_FORMAT), LocalDate.parse(d.getString("end"), AppConfig.DATE_FORMAT)));
        }
    }

    /**
     * Add concomitant therapies to DB
     */
    private void addConcTherapyToDB(ConcTherapy therapy) {
        Document doc = new Document("username", therapy.username())
                .append("symptoms", therapy.symptoms()).append("drugs", therapy.drugs()).append("start", therapy.start().format(AppConfig.DATE_FORMAT)).append("end", therapy.end().format(AppConfig.DATE_FORMAT));

        concTherapyCollection.insertOne(doc);
    }

    /**
     * Save concomitant therapy to the repository
     */
    public void saveConcTherapy(ConcTherapy therapy) {
        concTherapies.add(therapy);
        addConcTherapyToDB(therapy);
    }

    public ObservableList<ConcTherapy> getConcTherapiesByUser(String username) {
        List<ConcTherapy> concTherapies = new ArrayList<>();
        FindIterable<Document> docs = concTherapyCollection.find(new  Document("username", username));
        for (Document d : docs) {
            concTherapies.add(new ConcTherapy(d.getString("username"), d.getString("symptoms"), d.getString("drugs"), LocalDate.parse(d.getString("start"), AppConfig.DATE_FORMAT), LocalDate.parse(d.getString("end"), AppConfig.DATE_FORMAT)));
        }
        return FXCollections.observableList(concTherapies);
    }
}
