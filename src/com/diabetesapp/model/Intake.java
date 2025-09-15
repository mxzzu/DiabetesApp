package com.diabetesapp.model;

import java.time.LocalDate;

public record Intake(String username, LocalDate date, String drugs, String hour, String quantity) implements DailyEntity {

    public String toString() {
        return String.format("%s (%s): %s (mg)", drugs.toUpperCase(), hour, quantity);
    }
}
