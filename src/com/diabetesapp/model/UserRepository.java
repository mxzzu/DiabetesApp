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

public class UserRepository {
    private final Map<String, User> users = new HashMap<>();
    private final MongoCollection<Document> usersCollection;

    /**
     * Repository for storing all the users' information
     */
    public UserRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        usersCollection = db.getCollection(AppConfig.USERS_COLLECTION_NAME);
        loadUsers();
    }

    /**
     * Loads users from the database
     */
    private void loadUsers() {
        FindIterable<Document> docs = usersCollection.find();
        for (Document d : docs) {
            if (d.getString("userType").equals("patient")) {
                users.put(d.getString("username"),
                    new Patient(d.getString("username"), d.getString("password"), d.getString("userType"), d.getString("name"), d.getString("surname"), d.getString("birthDate"), d.getString("gender"), d.getString("email"), d.getBoolean("mustChangePassword"), d.getString("riskFactors"), d.getString("prevPats"), d.getString("comorbidities"), d.getString("docUser")));
            } else {
                users.put(d.getString("username"),
                    new User(d.getString("username"), d.getString("password"), d.getString("userType"), d.getString("name"), d.getString("surname"), d.getString("birthDate"), d.getString("gender"), d.getString("email"), d.getBoolean("mustChangePassword")));
            }
        }
    }

    /**
     * Adds a User to the database
     * @param user User to add
     */
    private void addUserToDB(User user) {
        Document doc = new Document("username", user.getUsername()).append("password", user.getPassword()).append("userType", user.getUserType()).append("name", user.getName()).append("surname", user.getSurname()).append("birthDate", user.getBirthDate()).append("gender", user.getGender()).append("email", user.getEmail()).append("gender", user.getGender()).append("mustChangePassword", true);
        if (user.getUserType().equals("patient")) {
            Patient patient = (Patient) user;
            doc.append("riskFactors", patient.getRiskFactors()).append("prevPats", patient.getPrevPats()).append("comorbidities", patient.getComorbidities()).append("docUser", patient.getDocUser());
        }

        usersCollection.insertOne(doc);
    }

    /**
     * Updates user on the database
     * @param user User to update
     */
    private void updateOnDB(User user) {
        Bson filter = eq("username", user.getUsername());
        Bson pswUpdate = set("password", user.getPassword());
        Bson mailUpdate = set("email", user.getEmail());
        Bson mustChangePassword = set("mustChangePassword", user.isMustChangePassword());
        Bson combinedUpdated = Updates.combine(pswUpdate, mailUpdate, mustChangePassword);
        if (user.getUserType().equals("patient")) {
            Patient patient = (Patient) user;
            Bson riskFactorsUpdate = set("riskFactors", patient.getRiskFactors());
            Bson prevPatsUpdate = set("prevPats", patient.getPrevPats());
            Bson comorbiditiesUpdate = set("comorbidities", patient.getComorbidities());
            combinedUpdated = Updates.combine(combinedUpdated, riskFactorsUpdate, prevPatsUpdate, comorbiditiesUpdate);
        }
        usersCollection.updateOne(filter, combinedUpdated);
    }
    
    /**
     * Modifies a user to the repository
     * @param user User to modify
     */
    public void modifyUser(User user) {
        users.put(user.getUsername(), user);
        updateOnDB(user);
    }

    /**
     * Adds a user to the repository
     * @param user User to add
     */
    public void addUser(User user) {
        users.put(user.getUsername(), user);
        addUserToDB(user);
    }
    
    /**
     * Get a user by username
     * @param username Username of the user to fetch
     * @return Returns the User object
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Gets a user by email
     * @param email Email of the user
     * @return Returns the User object. Returns null if none found
     */
    public User getUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Fetches the name, surname and username of all the patients of a specified doctor
     * @param docUser Username of the doctor
     * @return Returns an ObservableList of String formatted with name, surname and username of the patients
     */
    public ObservableList<String> getAllDataPatients(String docUser) {
        List<String> patients = new ArrayList<>();
        Patient patient;
        for (User user : users.values()) {
            if (user.getUserType().equals("patient")) {
                patient = (Patient) user;
                if (patient.getDocUser().equals(docUser)) {
                    patients.add(String.format("%s %s (%s)",  user.getName(), user.getSurname(), user.getUsername()));
                }
            }
        }
        return FXCollections.observableList(patients);
    }

    /**
     * Fetches all the patients of a specified doctor
     * @param docUser Username of the doctor
     * @return Returns a List of all the Patients
     */
    public List<Patient> getPatientsByDoctor(String docUser) {
        List<Patient> patients = new ArrayList<>();
        Patient patient;
        for (User user : users.values()) {
            if (user.getUserType().equals("patient")) {
                patient = (Patient) user;
                if (patient.getDocUser().equals(docUser)) {
                    patients.add(patient);
                }
            }
        }
        return patients;
    }

    /**
     * Fetches all the patients in the database
     * @return Returns an ObservableList of Patient
     */
    public ObservableList<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getUserType().equals("patient")) {
                patients.add((Patient) user);
            }
        }
        return FXCollections.observableList(patients);
    }
}