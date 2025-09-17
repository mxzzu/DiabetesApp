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
    public List<String> getMissingEntriesForYesterday(String username) {
        List<String> missingDrugs = new ArrayList<>();

        // 1. Recupera il numero di assunzioni PREVISTE dalla terapia
        TherapyRepository therapyRepository = Main.getTherapyRepository(); // O come ottieni l'istanza
        int expectedIntakes = therapyRepository.getExpectedDailyIntakes(username);

        // Se non ci sono assunzioni previste, non può mancare nulla.
        if (expectedIntakes == 0) {
            return missingDrugs; // Ritorna lista vuota
        }

        // 2. Conta le assunzioni EFFETTIVE di ieri
        LocalDate yesterday = LocalDate.now().minusDays(1);
        long actualIntakes = countIntakesForDate(username, yesterday);

        // 3. Se le assunzioni effettive sono meno di quelle previste...
        if (actualIntakes < expectedIntakes) {
            // ...allora aggiungiamo il nome del farmaco alla lista dei mancanti.
            // Questa è una semplificazione: assume che ci sia un solo farmaco.
            // Se la terapia può averne di più, questa logica andrebbe raffinata.
            Therapy therapy = therapyRepository.getTherapyByPatient(username);
            if (therapy != null) {
                missingDrugs.add(therapy.drug());
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
    public long countIntakesForDate(String username, LocalDate date) {
        // 1. Formatta la data nello stesso modo in cui è salvata nel DB (es. "yyyy-MM-dd")
        String formattedDate = date.format(AppConfig.DATE_FORMAT);

        // 2. Crea il filtro per la query MongoDB, cercando i documenti che
        //    corrispondono sia all'username che alla data.
        Document filter = new Document("username", username)
                .append("date", formattedDate);

        // 3. Esegui la query usando il metodo .countDocuments() di MongoDB,
        //    che è molto efficiente perché conta i risultati senza doverli caricare tutti.
        return intakesCollection.countDocuments(filter);
    }
}
