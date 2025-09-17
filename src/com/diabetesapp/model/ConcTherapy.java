package com.diabetesapp.model;

import java.time.LocalDate;

public record ConcTherapy(String username, String symptoms, String drugs, LocalDate start, LocalDate end) {
}
