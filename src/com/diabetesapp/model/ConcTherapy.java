package com.diabetesapp.model;

import com.diabetesapp.config.AppConfig;

import java.time.LocalDate;

public record ConcTherapy(String username, String symptoms, String drugs, LocalDate start, LocalDate end) {

    public String toString() {
        return String.format("%s %s %s %s", symptoms, drugs, start.format(AppConfig.DATE_FORMAT), (end != null) ? end.format(AppConfig.DATE_FORMAT) : "No end date");
    }
}
