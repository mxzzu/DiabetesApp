package com.diabetesapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class DBConfig {
    private static MongoClient client;

    public static MongoClient getClient() {
        if (client == null) {
            client = MongoClients.create("mongodb+srv://mazzu:mazzu@cluster0.l7kh67y.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
        }
        return client;
    }

    public static void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
