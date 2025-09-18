package com.diabetesapp.model;

import java.time.LocalDate;

public record Notification(String username, LocalDate date, String title, String message, boolean isAlerted) {

    public String toString() {
        return String.format("%s", message);
    }
}
