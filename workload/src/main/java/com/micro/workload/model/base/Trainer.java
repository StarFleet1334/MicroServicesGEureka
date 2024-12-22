package com.micro.workload.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainers")
@CompoundIndex(name = "firstName_lastName_idx", def = "{ 'firstName': 1, 'lastName': 1 }")
public class Trainer {
    @Id
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
