package com.diabetesapp.model;

public class User {
    private final String username;
    private final String password;
    private final String userType;
    
    /**
     * Constructor for creating a user with a username and password
     * 
     * @param username The user's username
     * @param password The user's password (stored in plain text for educational purposes)
     */
    public User(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }


    /*
     * Factory method to create a new user
     * 
     * @param username The user's username
     * @param password The user's password
     * @return A new User object
     */
    /*public static User create(String username, String password, String userType) {
        return new User(username, password, userType);
    }

    public static User create(String username, String password) {
        return new User(username, password, "paziente");
    }*/

    /**
     * Get the user's username
     * 
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the user's password
     * 
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    public String toString() {
        return String.format("%s, %s", username, password);
    }
}