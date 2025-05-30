package com.diabetesapp.model;

import com.diabetesapp.config.AppConfig;
import com.diabetesapp.config.DBConfig;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.HashMap;
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

    private void changePasswordOnDB(User user) {
        Bson filter = eq("username", user.getUsername());
        Bson update = set("password", user.getPassword());
        usersCollection.findOneAndUpdate(filter, update);
    }
    
    /**
     * Save a user to the repository
     */
    public void saveUser(User user) {
        users.put(user.getUsername(), user);
        changePasswordOnDB(user);
    }
    
    /**
     * Get a user by username
     */
    public User getUser(String username) {
        return users.get(username);
    }
    
    /**
     * Check if a username exists
     */
    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Get all users
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
}