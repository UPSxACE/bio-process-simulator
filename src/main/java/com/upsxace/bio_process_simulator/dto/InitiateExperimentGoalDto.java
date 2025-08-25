package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class InitiateExperimentGoalDto {
    @NotNull
    @DecimalMin("0")
    Float targetProductTiterGPerL;
}
