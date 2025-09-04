package com.diabetesapp.model;

import java.util.*;
import java.util.stream.Collectors;

public class Patient extends User {
    private final String riskFactors;
    private final String prevPats;
    private final String comorbidities;
    private final String docUser;

    public Patient(String username, String psw, String userType, String name, String surname, String birthDate, String gender, String email, boolean mustChangePassword, String riskFactors, String prevPats, String comorbidities, String docUser) {
        super(username, psw, userType,  name, surname, birthDate, gender, email, mustChangePassword);
        this.riskFactors = riskFactors;
        this.prevPats = prevPats;
        this.comorbidities = comorbidities;
        this.docUser = docUser;
    }

    public Patient(Patient oldPatient, String riskFactors, String prevPats, String comorbidities) {
        super(oldPatient);
        this.riskFactors = riskFactors;
        this.prevPats = prevPats;
        this.comorbidities = comorbidities;
        this.docUser = oldPatient.getDocUser();
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public String getPrevPats() {
        return prevPats;
    }

    public String getComorbidities() {
        return comorbidities;
    }

    public String[] getSplittedRiskFactors() {
        return riskFactors.trim().split(",");
    }

    public String[] getSplittedPrevPats() {
        return prevPats.trim().split(",");
    }

    public String[] getSplittedComorbidities() {
        return comorbidities.trim().split(",");
    }

    public String getDocUser() {
        return docUser;
    }

    public String getButtonLabel() {
        return "";
    }

    public String toString() {
        return String.format("%s, %s, %s, %s, %s", super.toString(), riskFactors, prevPats, comorbidities, docUser);
    }

    public String diff(Patient p2) {
        List<String> allDiffs = new ArrayList<>();
        String oldFactors = this.getRiskFactors();
        String newFactors = p2.getRiskFactors();
        String oldPats = this.getPrevPats();
        String newPats = p2.getPrevPats();
        String oldCom = this.getComorbidities();
        String newCom = p2.getComorbidities();

        if (!oldFactors.equals(newFactors)) {
            allDiffs.add(checkDiff(oldFactors, newFactors, "RiskFactors: "));
        }

        if (!oldPats.equals(newPats)) {
            allDiffs.add(checkDiff(oldPats, newPats, "PrevPats: "));
        }

        if (!oldCom.equals(newCom)) {
            allDiffs.add(checkDiff(oldCom, newCom, "Comorbidities: "));
        }

        return String.join("; ", allDiffs);
    }

    private String checkDiff(String oldItems, String newItems, String title) {
        Set<String> oldSet = Arrays.stream(oldItems.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> newSet = Arrays.stream(newItems.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // 2. Calcola le differenze usando la logica degli insiemi.
        // Aggiunti: elementi presenti nel nuovo set ma non nel vecchio.
        Set<String> added = new HashSet<>(newSet);
        added.removeAll(oldSet);

        // Rimossi: elementi presenti nel vecchio set ma non nel nuovo.
        Set<String> removed = new HashSet<>(oldSet);
        removed.removeAll(newSet);

        if (added.isEmpty() && removed.isEmpty()) {
            return ""; // Se non ci sono differenze, restituisce una stringa vuota.
        }

        List<String> changes = new ArrayList<>();

        // Aggiunge alla stringa tutti gli elementi rimossi.
        for (String factor : removed) {
            changes.add(factor + " removed");
        }

        // Aggiunge alla stringa tutti gli elementi aggiunti.
        for (String factor : added) {
            changes.add(factor + " added");
        }

        return title + String.join(", ", changes);
    }
}
