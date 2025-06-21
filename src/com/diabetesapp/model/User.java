package com.diabetesapp.model;

public class User {
    private final String username;
    private final String password;
    private final String userType;
    private final String name;
    private final String surname;
    private final String birthDate;
    private final String gender;
    private final String email;
    private final boolean mustChangePassword;
    
    /**
     * Constructor for creating a user with a username and password
     * 
     * @param username The user's username
     * @param password The user's password (stored in plain text for educational purposes)
     */
    public User(String username, String password, String userType, String name, String surname, String birthDate, String gender, String email, boolean mustChangePassword) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.mustChangePassword = mustChangePassword;
    }

    protected User(Patient oldPatient) {
        this.username = oldPatient.getUsername();
        this.password = oldPatient.getPassword();
        this.userType = oldPatient.getUserType();
        this.name = oldPatient.getName();
        this.surname = oldPatient.getSurname();
        this.birthDate = oldPatient.getBirthDate();
        this.gender = oldPatient.getGender();
        this.email = oldPatient.getEmail();
        this.mustChangePassword = oldPatient.isMustChangePassword();
    }

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

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getBirthDate() { return birthDate; }

    public String getGender() { return gender; }

    public String getEmail() { return email; }

    public boolean isMustChangePassword() { return mustChangePassword; }

    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s", username, password, userType, name, surname, birthDate, gender, email, mustChangePassword);
    }
}