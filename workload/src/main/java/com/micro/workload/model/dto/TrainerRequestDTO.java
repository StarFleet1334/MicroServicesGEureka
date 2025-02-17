package com.micro.workload.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TrainerRequestDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique identifier, generated by the server")
    private Long id;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique userName, generated by the server")
    private String username;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique passWord, generated by the server")
    private String password;

    @NotNull(message = "Training Type `id` is required")
    private Long trainingTypeId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "isActive which is set by false as a default by server")
    private boolean isActive;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Trainer's trainee's list that is empty by default")
    private List<String> trainees;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @NotNull(message = "Training Type `id` is required") Long getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(
            @NotNull(message = "Training Type `id` is required") Long trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<String> trainees) {
        this.trainees = trainees;
    }
}
