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

    /**
     * Method used as comparator for serving icons inside the Patients' table
     * @return Returns an empty string
     */
    public String getButtonLabel() {
        return "";
    }

    public String toString() {
        return String.format("%s, %s, %s, %s, %s", super.toString(), riskFactors, prevPats, comorbidities, docUser);
    }

    /**
     * Checks for differences between two Patient objects, looking at risk factors, prevpats and comorbidities
     * @param p2 Second Patient object
     * @return Returns a String with all the differences joined by ';'
     */
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

    /**
     * Check difference between old attribute and new attribute
     * @param oldItems Old items to check
     * @param newItems New items to check
     * @param title Title of the difference string
     * @return Returns a String with the differences joined by ','
     */
    private String checkDiff(String oldItems, String newItems, String title) {
        Set<String> oldSet = Arrays.stream(oldItems.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> newSet = Arrays.stream(newItems.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> added = new HashSet<>(newSet);
        added.removeAll(oldSet);

        Set<String> removed = new HashSet<>(oldSet);
        removed.removeAll(newSet);

        if (added.isEmpty() && removed.isEmpty()) {
            return "";
        }

        List<String> changes = new ArrayList<>();

        for (String factor : removed) {
            changes.add(factor + " removed");
        }

        for (String factor : added) {
            changes.add(factor + " added");
        }

        return title + String.join(", ", changes);
    }
}
