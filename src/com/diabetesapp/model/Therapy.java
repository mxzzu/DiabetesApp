package com.diabetesapp.model;

public class Therapy {
    private final String patient;
    private final String drug;
    private final String intakeNumber;
    private final String quantity;
    private final String indications;

    public Therapy(String patient, String drug, String intakeNumber, String quantity, String indications) {
        this.patient = patient;
        this.drug = drug;
        this.intakeNumber = intakeNumber;
        this.quantity = quantity;
        this.indications = indications;
    }

    public String getPatient() {
        return patient;
    }

    public String getDrug() {
        return drug;
    }

    public String getIntakeNumber() {
        return intakeNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getIndications() {
        return indications;
    }

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
