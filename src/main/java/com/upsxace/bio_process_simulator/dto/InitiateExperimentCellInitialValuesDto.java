package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class InitiateExperimentCellInitialValuesDto {
    @DecimalMin("0.0")
    private final Float pH;
    @DecimalMin("0.0")
    private final Float temperature;
    @DecimalMin("0.0")
    private final Float dissolvedOxygen;
    @DecimalMin("0.0")
    private final Float glucose;
    @DecimalMin("0.0")
    private final Float lactate;
}
