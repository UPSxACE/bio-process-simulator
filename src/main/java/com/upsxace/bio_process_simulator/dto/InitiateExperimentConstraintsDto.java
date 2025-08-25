package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class InitiateExperimentConstraintsDto {
    @Valid
    private final InitiateExperimentMinMaxConstraintsDto ph;
    @Valid
    private final InitiateExperimentMinMaxConstraintsDto temperature;
    @Valid
    private final InitiateExperimentMinMaxConstraintsDto dissolvedOxygen;
    @Valid
    private final InitiateExperimentMinMaxConstraintsDto glucose;
    @Valid
    private final InitiateExperimentMinMaxConstraintsDto lactate;
}
