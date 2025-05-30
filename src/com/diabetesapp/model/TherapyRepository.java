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

public class TherapyRepository {
    private final List<Therapy> therapies = new ArrayList<>();
    private final MongoCollection<Document> therapiesCollection;

    public TherapyRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        therapiesCollection = db.getCollection(AppConfig.THERAPIES_COLLECTION_NAME);
        loadTherapies();
    }

    /**
     * Load therapies from DB
     */
    private void loadTherapies() {
        FindIterable<Document> docs = therapiesCollection.find();
        for (Document d : docs) {
            therapies.add(new Therapy(d.getString("patient"), d.getString("drug"), d.getString("intakeNumber"), d.getString("quantity"), d.getString("indications")));
        }
    }

    /**
     * Add therapy to DB
     */
    private void addTherapyToDB(Therapy therapy) {
        Document doc = new Document("patient", therapy.getPatient())
                .append("drug", therapy.getDrug()).append("intakeNumber", therapy.getIntakeNumber()).append("quantity", therapy.getQuantity()).append("indications", therapy.getIndications());

        therapiesCollection.insertOne(doc);
    }

    /**
     * Save therapy to the repository
     */
    public void saveTherapy(Therapy therapy) {
        therapies.add(therapy);
        addTherapyToDB(therapy);
    }

    public List<Therapy> getAllTherapies() {
        return new ArrayList<>(therapies);
    }
}
