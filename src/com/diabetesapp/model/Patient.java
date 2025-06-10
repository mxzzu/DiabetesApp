package com.diabetesapp.model;

public class Patient extends User {
    private final String riskFactors;
    private final String prevPats;
    private final String comorbidities;
    private final String docUser;

    public Patient(String username, String psw, String userType, String riskFactors, String prevPats, String comorbidities, String docUser) {
        super(username, psw, userType);
        this.riskFactors = riskFactors;
        this.prevPats = prevPats;
        this.comorbidities = comorbidities;
        this.docUser = docUser;
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

    public String toString() {
        return String.format("%s, %s, %s, %s, %s", super.toString(), riskFactors, prevPats, comorbidities, docUser);
    }
}
