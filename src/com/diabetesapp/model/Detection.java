package com.diabetesapp.model;

public class Detection implements DailyEntity {
    private final String username;
    private final String meal;
    private final String period;
    private final String date;
    private final String level;

    public Detection(String username, String date, String meal, String period, String level) {
        this.username = username;
        this.meal = meal;
        this.period = period;
        this.date = date;
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public String getMeal() {
        return meal;
    }

    public String getPeriod() {
        return period;
    }

    public String getDate() {
        return date;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %s (mg/dL)", meal, period, level);
    }
}
