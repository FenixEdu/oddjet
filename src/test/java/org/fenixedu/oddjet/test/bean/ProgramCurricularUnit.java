package org.fenixedu.oddjet.test.bean;

import java.io.Serializable;

public class ProgramCurricularUnit implements Serializable {
    private String year;
    private String name;
    private String type;
    private String duration;
    private double credits;
    private int obtainedClassification;
    private int convertedClassification;
    private String observations;

    public String getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDuration() {
        return duration;
    }

    public double getCredits() {
        return credits;
    }

    public int getObtainedClassification() {
        return obtainedClassification;
    }

    public int getConvertedClassification() {
        return convertedClassification;
    }

    public String getObservations() {
        return observations;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public void setObtainedClassification(int obtainedClassification) {
        this.obtainedClassification = obtainedClassification;
    }

    public void setConvertedClassification(int convertedClassification) {
        this.convertedClassification = convertedClassification;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public ProgramCurricularUnit(String year, String name, String type, String duration, double credits,
            int obtainedClassification, int convertedClassification, String observations) {
        this.year = year;
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.credits = credits;
        this.obtainedClassification = obtainedClassification;
        this.convertedClassification = convertedClassification;
        this.observations = observations;
    }

    public ProgramCurricularUnit() {
    }
};