package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class InitiateExperimentMinMaxConstraintsDto {
    @DecimalMin("0")
    private final Float min;
    @DecimalMin("0")
    private final Float max;
}
