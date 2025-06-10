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

    public UserRepository() {
        MongoClient client = DBConfig.getClient();
        MongoDatabase db = client.getDatabase(AppConfig.DB_NAME);
        usersCollection = db.getCollection(AppConfig.USERS_COLLECTION_NAME);
        loadUsers();
    }

    /**
     * Load users from DB
     */
    private void loadUsers() {
        FindIterable<Document> docs = usersCollection.find();
        for (Document d : docs) {
            if (d.getString("userType").equals("patient")) {
                users.put(d.getString("username"),
                    new Patient(d.getString("username"), d.getString("password"), d.getString("userType"), d.getString("riskFactors"), d.getString("prevPat"), d.getString("comorbidities"), d.getString("docUser")));
            } else {
                users.put(d.getString("username"),
                    new Doctor(d.getString("username"), d.getString("password"), d.getString("userType"), d.getString("mail")));
            }
        }
    }

    private void addUserToDB(User user) {
        Document doc = new Document("username", user.getUsername()).append("password", user.getPassword()).append("userType", user.getUserType());
        if (user.getUserType().equals("patient")) {
            Patient patient = (Patient) user;
            doc.append("riskFactors", patient.getRiskFactors()).append("prevPat", patient.getPrevPats()).append("comorbidities", patient.getComorbidities()).append("docUser", patient.getDocUser());
        } else {
            Doctor doctor = (Doctor) user;
            doc.append("mail", doctor.getMail());
        }

        usersCollection.insertOne(doc);
    }

    private void updateOnDB(User user) {
        Bson filter = eq("username", user.getUsername());
        Bson pswUpdate = set("password", user.getPassword());
        if (user.getUserType().equals("doctor")) {
            Bson mailUpdate = set("mail", ((Doctor) user).getMail());
            usersCollection.updateOne(filter, Updates.combine(pswUpdate, mailUpdate));
        } else {
            usersCollection.findOneAndUpdate(filter, pswUpdate);
        }
    }
    
    /**
     * Save a user to the repository
     */
    public void saveUser(User user) {
        users.put(user.getUsername(), user);
        updateOnDB(user);
    }
    
    /**
     * Get a user by username
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Get all users
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }

    public ObservableList<String> getAllPatients() {
        List<String> patients = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getUserType().equals("patient")) {
                patients.add(user.getUsername());
            }
        }
        return FXCollections.observableList(patients);
    }
}