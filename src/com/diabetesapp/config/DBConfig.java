package com.diabetesapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class DBConfig {
    private static MongoClient client;

    /**
     * Creates and returns the MongoDB client of the database
     * @return Returns the MongoClient object used to connect to the database
     */
    public static MongoClient getClient() {
        if (client == null) {
            client = MongoClients.create("mongodb+srv://mazzu:mazzu@cluster0.l7kh67y.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
        }
        return client;
    }
}
