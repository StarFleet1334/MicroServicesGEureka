package com.micro.workload.model.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Trainer {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private Map<Integer, YearSummary> yearSummaries = new ConcurrentHashMap<>();

    public Trainer() {

    }

    public Trainer(String username, String firstName, String lastName, boolean status) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Map<Integer,YearSummary> getYearSummaries() {
        return yearSummaries;
    }

    public void setYearSummaries(Map<Integer,YearSummary> yearSummaries) {
        this.yearSummaries = yearSummaries;
    }
}
