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

    /**
     * Repository for storing patients' intakes
     */
    public IntakeRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        intakesCollection = db.getCollection(AppConfig.INTAKES_COLLECTION_NAME);
        loadIntakes();
    }

    /**
     * Loads intakes from the database
     */
    private void loadIntakes() {
        FindIterable<Document> docs = intakesCollection.find();
        for (Document d : docs) {
            intakes.add(docToObj(d));
        }
    }

    /**
     * Adds an intake to the database
     * @param intake Intake to add
     */
    private void addIntakeToDB(Intake intake) {
        Document doc = new Document("username", intake.username())
                .append("date", intake.date().format(AppConfig.DATE_FORMAT)).append("drugs", intake.drugs()).append("hour", intake.hour()).append("quantity", intake.quantity());

        intakesCollection.insertOne(doc);
    }

    /**
     * Saves an intake to the repository
     * @param intake Intake to save
     */
    public void saveIntake(Intake intake) {
        intakes.add(intake);
        addIntakeToDB(intake);
    }

    /**
     * Fetches daily intakes based on the username
     * @param username Username of the patient to search
     * @return Returns a List object of Intakes
     */
    public List<Intake> getDailyIntakes(String username) {
        return DailyEntityUtils.getDailyEntities(username, intakes);
    }

    /**
     * Fetches all intakes based on the username
     * @param username Username of the patient to search
     * @return Returns an ObservableList of Intakes
     */
    public ObservableList<Intake> getAllIntakesByUser(String username) {
        List<Intake> intakes = new ArrayList<>();
        FindIterable<Document> docs = intakesCollection.find(new Document("username", username));
        for (Document d : docs) {
            intakes.add(docToObj(d));
        }
        return FXCollections.observableList(intakes);
    }

    /**
     * Checks the database for missing entries of intakes of a specified user
     * @param username Username of the patient to check
     * @param daysToCheck Days to check for missing entries
     * @return Returns a List of String containing the missing drugs. If empty, no missing entries were found
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
     * Counts the intake number of a specified drug for a specified username on a specified day
     * @param username Username of the patient to check
     * @param date LocalDate object representing the day to check
     * @param drug Drug name to check
     * @return Returns the number of intakes found for that day
     */
    public long countIntakesForDate(String username, LocalDate date, String drug) {
        String formattedDate = date.format(AppConfig.DATE_FORMAT);

        Document filter = new Document("username", username)
                .append("date", formattedDate).append("drugs", drug);

        return intakesCollection.countDocuments(filter);
    }

    /**
     * Parses a JSON Document object into an Intake
     * @param d Document to parse
     * @return Returns the parsed Intake object
     */
    private Intake docToObj(Document d) {
        return new Intake(d.getString("username"), LocalDate.parse(d.getString("date"), AppConfig.DATE_FORMAT), d.getString("drugs"), d.getString("hour"), d.getString("quantity"));
    }
}
