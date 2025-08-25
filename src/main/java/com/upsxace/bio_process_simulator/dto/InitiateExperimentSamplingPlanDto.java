package com.upsxace.bio_process_simulator.dto;

import com.upsxace.bio_process_simulator.model.enums.ExperimentAnalyte;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Data
public class InitiateExperimentSamplingPlanDto {
    @NotNull
    @Min(1)
    private final Integer everyMinutes;
    @NotNull
    @NotEmpty
    private final List<ExperimentAnalyte> analytes;
}
