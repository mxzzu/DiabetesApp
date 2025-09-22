package com.diabetesapp.model;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.DBConfig;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class TherapyRepository {
    private final List<Therapy> therapies = new ArrayList<>();
    private final MongoCollection<Document> therapiesCollection;

    /**
     * Repository for storing therapies of the patients
     */
    public TherapyRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        therapiesCollection = db.getCollection(AppConfig.THERAPIES_COLLECTION_NAME);
        loadTherapies();
    }

    /**
     * Loads therapies from the database
     */
    private void loadTherapies() {
        FindIterable<Document> docs = therapiesCollection.find();
        for (Document d : docs) {
            therapies.add(docToObj(d));
        }
    }

    /**
     * Adds a therapy to the database
     * @param therapy Therapy to add
     */
    private void addTherapyToDB(Therapy therapy) {
        Document doc = new Document("patient", therapy.patient())
                .append("drug", therapy.drug()).append("intakeNumber", therapy.intakeNumber()).append("quantity", therapy.quantity()).append("indications", therapy.indications());

        therapiesCollection.insertOne(doc);
    }

    /**
     * Modifies a therapy on the database
     * @param therapy Therapy to modify
     */
    private void modifyTherapyOnDB(Therapy therapy) {
        Bson filter = eq("patient", therapy.patient());
        Bson drugUpdate = set("drug", therapy.drug());
        Bson intakeUpdate =  set("intakeNumber", therapy.intakeNumber());
        Bson quantityUpdate = set("quantity", therapy.quantity());
        Bson indications = set("indications", therapy.indications());
        Bson updates = Updates.combine(drugUpdate, intakeUpdate, quantityUpdate, indications);
        therapiesCollection.updateOne(filter, updates);
    }

    /**
     * Save therapy to the repository
     * @param therapy Therapy to save
     */
    public void saveTherapy(Therapy therapy) {
        therapies.add(therapy);
        addTherapyToDB(therapy);
    }

    /**
     * Modifies a therapy on the repository
     * @param therapy Therapy to modify
     */
    public void modifyTherapy(Therapy therapy) {
        for (Therapy a : therapies) {
            if (a.patient().equals(therapy.patient()) && a.drug().equals(therapy.drug())) {
                therapies.remove(a);
                therapies.add(therapy);
                modifyTherapyOnDB(therapy);
                return;
            }
        }
    }

    /**
     * Fetches all therapies based on the patient
     * @param patient Username of the patient to search
     * @return Returns a List containing all the Therpies
     */
    public List<Therapy> getTherapiesByPatient(String patient) {
        List<Therapy> therapiesByPatient = new ArrayList<>();
        FindIterable<Document> docs = therapiesCollection.find(new Document("patient", patient));
        for (Document d : docs) {
            therapiesByPatient.add(docToObj(d));
        }
        return therapiesByPatient;
    }

    /**
     * Fetches all the drugs from a patient's therapies
     * @param username Username of the patient
     * @return Returns an ObservableList of String containing the drugs' names
     */
    public ObservableList<String> getDrugsFromTherapy(String username) {
        List<String> drugs = new ArrayList<>();
        for  (Therapy therapy : getTherapiesByPatient(username)) {
            drugs.add(therapy.drug());
        }
        return FXCollections.observableList(drugs);
    }

    /**
     * Fetches expected daily intakes based on patient's therapies
     * @param username Username of the patient
     * @return HashMap with couple <Integer, String> where Integer is the expected intake number
     * and String is the drug of that therapy
     */
    public Map<Integer, String> getExpectedDailyIntakes(String username) {
        Map<Integer, String> count = new HashMap<>();
        FindIterable<Document> docs = therapiesCollection.find(new Document("patient", username));
        for  (Document d : docs) {
            if (d == null) {
                continue;
            }

            count.put(Integer.parseInt(d.getString("intakeNumber")), d.getString("drug"));
        }

        return count;
    }

    /**
     * Parses a JSON Document object into a Therapy
     * @param d Document to parse
     * @return Returns the parsed Therapy object
     */
    private Therapy docToObj(Document d) {
        return new Therapy(d.getString("patient"), d.getString("drug"), d.getString("intakeNumber"), d.getString("quantity"), d.getString("indications"));
    }
}
