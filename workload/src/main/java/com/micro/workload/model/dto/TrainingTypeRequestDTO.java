package com.micro.workload.model.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class TrainingTypeRequestDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique identifier, generated by the server")
    private Long id;

    @NotBlank(message = "Training type name is required")
    private String trainingTypeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
