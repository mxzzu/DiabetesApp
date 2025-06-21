package com.diabetesapp.model;

public record Intake(String username, String date, String drugs, String hour, String quantity) implements DailyEntity {

    public String toString() {
        return String.format("%s (%s): %s (mg)", drugs, hour, quantity);
    }
}
