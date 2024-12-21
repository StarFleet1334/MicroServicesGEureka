package com.micro.workload.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private Map<Integer, YearSummary> yearSummaries = new ConcurrentHashMap<>();

    public Trainer(String username, String firstName, String lastName, boolean status) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

}
