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

public class IntakeRepository {
    private final List<Intake> intakes = new ArrayList<>();
    private final MongoCollection<Document> intakesCollection;

    public IntakeRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        intakesCollection = db.getCollection(AppConfig.INTAKES_COLLECTION_NAME);
        loadIntakes();
    }

    /**
     * Load intakes from DB
     */
    private void loadIntakes() {
        FindIterable<Document> docs = intakesCollection.find();
        for (Document d : docs) {
            intakes.add(new Intake(d.getString("username"), LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("drugs"), d.getString("hour"), d.getString("quantity")));
        }
    }

    /**
     * Add intake to DB
     */
    private void addIntakeToDB(Intake intake) {
        Document doc = new Document("username", intake.username())
                .append("date", intake.date().format(AppConfig.DATE_FORMAT)).append("drugs", intake.drugs()).append("hour", intake.hour()).append("quantity", intake.quantity());

        intakesCollection.insertOne(doc);
    }

    /**
     * Save intake to the repository
     */
    public void saveIntake(Intake intake) {
        intakes.add(intake);
        addIntakeToDB(intake);
    }

    public List<Intake> getDailyIntakes(String username) {
        return DailyEntityUtils.getDailyEntities(username, intakes);
    }

    public ObservableList<Intake> getAllIntakesByUser(String username) {
        List<Intake> intakes = new ArrayList<>();
        FindIterable<Document> docs = intakesCollection.find(new Document("username", username));
        for (Document d : docs) {
            intakes.add(new Intake(username, LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("drugs"), d.getString("hour"), d.getString("quantity")));
        }
        return FXCollections.observableList(intakes);
    }
}
