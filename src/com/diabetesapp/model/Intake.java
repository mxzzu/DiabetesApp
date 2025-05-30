package com.diabetesapp.model;

public class Intake implements DailyEntity {
    private final String username;
    private final String date;
    private final String drugs;
    private final String hour;
    private final String quantity;

    public Intake(String username, String date, String drugs, String hour, String quantity) {
        this.username = username;
        this.date = date;
        this.drugs = drugs;
        this.hour = hour;
        this.quantity = quantity;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getDrugs() {
        return drugs;
    }

    public String getHour() {
        return hour;
    }

    public String getQuantity() {
        return quantity;
    }

    public String toString() {
        return String.format("%s (%s): %s (mg)", drugs, hour, quantity);
    }
}
