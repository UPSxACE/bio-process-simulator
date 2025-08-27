package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class InitiateExperimentCellInitialValuesDto {
    @DecimalMin("0.0")
    @NotNull
    private final Float ph;
    @DecimalMin("0.0")
    @NotNull
    private final Float temperature;
    @DecimalMin("0.0")
    @NotNull
    private final Float dissolvedOxygen;
    @DecimalMin("0.0")
    @NotNull
    private final Float glucose;
    @DecimalMin("0.0")
    @NotNull
    private final Float lactate;
}
