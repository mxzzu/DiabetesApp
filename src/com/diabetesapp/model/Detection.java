package com.diabetesapp.model;

public record Detection(String username, String date, String meal, String period, String level) implements DailyEntity {

    @Override
    public String toString() {
        return String.format("%s (%s): %s (mg/dL)", meal, period, level);
    }
}
