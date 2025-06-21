package com.diabetesapp.model;

public record Therapy(String patient, String drug, String intakeNumber, String quantity, String indications) {

    @Override
    public String toString() {
        return "Therapy{" +
                "patient='" + patient + '\'' +
                ", drug='" + drug + '\'' +
                ", intakeNumber='" + intakeNumber + '\'' +
                ", quantity='" + quantity + '\'' +
                ", indications='" + indications + '\'' +
                '}';
    }
}
