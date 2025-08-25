package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
@Data
public class InitiateExperimentRequest {
    @NotEmpty
    @NotBlank
    private final String name;
    @Size(min = 1)
    private final List<UUID> bioreactorIds;
    @Min(1)
    private final Integer batchSize;
    @NotEmpty
    @NotBlank
    private final String cellType;
    @NotNull
    private final InitiateExperimentCellInitialValuesDto cellInitialValues;
    @NotNull
    @Min(1)
    private final Integer timeLimitHours;
    @NotNull
    @Valid
    private final InitiateExperimentGoalDto goals;
    @NotNull
    @Valid
    private final InitiateExperimentConstraintsDto constraints;
    @NotNull
    @Valid
    private final InitiateExperimentSamplingPlanDto samplingPlan;
}
