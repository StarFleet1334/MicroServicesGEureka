package com.micro.workload.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TrainingSessionDTO {
    @NotBlank(message = "Trainer userName is required")
    private String trainerUserName;

    @NotBlank(message = "Trainer first name is required")
    private String trainerFirstName;

    @NotBlank(message = "Trainer last name is required")
    private String trainerLastName;

    @NotNull(message = "isActive status is required")
    private Boolean isActive;

    @NotNull(message = "Training date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    private Number trainingDuration;

    @NotNull(message = "Training action is required")
    private String action;


    public String getTrainerUserName() {
        return trainerUserName;
    }

    public void setTrainerUserName(String trainerUserName) {
        this.trainerUserName = trainerUserName;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Number getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Number trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public String getAction() {
        return action;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "TrainingSessionDTO{" +
                "trainerUserName='" + trainerUserName + '\'' +
                ", trainerFirstName='" + trainerFirstName + '\'' +
                ", trainerLastName='" + trainerLastName + '\'' +
                ", isActive=" + isActive +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                ", action='" + action + '\'' +
                '}';
    }
}
