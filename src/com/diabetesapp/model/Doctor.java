package com.diabetesapp.model;

public class Doctor extends User {
    private final String mail;

    public Doctor(String username, String psw, String userType, String mail) {
        super(username, psw, userType);
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public String toString() {
        return String.format("%s, %s", super.toString(), mail);
    }
}
