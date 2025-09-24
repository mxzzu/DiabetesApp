module com.diabetesapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.mongodb.driver.sync.client;
    requires jbcrypt;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires slf4j.api;
    requires MaterialFX;
    requires org.kordamp.ikonli.core;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;

    opens com.diabetesapp to javafx.fxml;
    exports com.diabetesapp;
    exports com.diabetesapp.controller;
    exports com.diabetesapp.model;
    exports com.diabetesapp.config;
    opens com.diabetesapp.controller to javafx.fxml;
}