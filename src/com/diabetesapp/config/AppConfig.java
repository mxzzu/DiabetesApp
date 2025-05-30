package com.diabetesapp.config;

import java.io.File;

public class AppConfig {
    // Application settings
    public static final String APP_TITLE = "Diabetes App";
    
    // Data storage settings
    public static final String DATA_DIR = "src/resources/data";
    public static final String DB_NAME = "DiabetesApp";
    public static final String DETECTIONS_COLLECTION_NAME = "detections";
    public static final String INTAKES_COLLECTION_NAME = "intakes";
    public static final String THERAPIES_COLLECTION_NAME = "therapies";
    public static final String USERS_COLLECTION_NAME = "users";

    // Create a data directory if it doesn't exist
    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
}