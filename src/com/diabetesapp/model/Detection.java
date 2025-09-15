package com.diabetesapp.model;

import java.time.LocalDate;

public record Detection(String username, LocalDate date, String meal, String period, String level) implements DailyEntity {

    @Override
    public String toString() {
        return String.format("%s (%s): %s (mg/dL)", meal, period, level);
    }
}
