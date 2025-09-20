package com.diabetesapp.model;

import com.diabetesapp.Main;
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
import java.util.Map;

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

    /**
     * Restituisce la lista dei nomi dei farmaci non assunti il giorno precedente.
     * @param username L'utente da controllare.
     * @return Una lista di stringhe con i nomi dei farmaci mancanti. Se è vuota, significa che è tutto a posto.
     */
    public List<String> getMissingEntries(String username, int daysToCheck) {
        List<String> missingDrugs = new ArrayList<>();

        TherapyRepository therapyRepository = Main.getTherapyRepository();
        Map<Integer, String> expectedIntakes = therapyRepository.getExpectedDailyIntakes(username);

        if (expectedIntakes.isEmpty()) {
            return missingDrugs;
        }

        for (int i = 1; i < daysToCheck + 1; i++) {
            LocalDate dayToCheck = LocalDate.now().minusDays(i);
            for (Map.Entry<Integer, String> entry : expectedIntakes.entrySet()) {
                long actualIntakes = countIntakesForDate(username, dayToCheck, entry.getValue());
                if (actualIntakes < entry.getKey()) {
                    missingDrugs.add(entry.getValue());
                }
            }
        }
        return missingDrugs;
    }

    /**
     * Conta il numero di assunzioni registrate da un utente in una data specifica.
     * Questo metodo interroga direttamente MongoDB.
     *
     * @param username L'utente da controllare.
     * @param date     La data per cui contare le assunzioni.
     * @return il numero di assunzioni (intake) trovate per quel giorno.
     */
    public long countIntakesForDate(String username, LocalDate date, String drug) {
        String formattedDate = date.format(AppConfig.DATE_FORMAT);

        Document filter = new Document("username", username)
                .append("date", formattedDate).append("drugs", drug);

        return intakesCollection.countDocuments(filter);
    }
}
