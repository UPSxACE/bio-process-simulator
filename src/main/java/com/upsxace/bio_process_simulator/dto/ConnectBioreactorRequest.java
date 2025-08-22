package com.upsxace.bio_process_simulator.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data @Validated
public class ConnectBioreactorRequest {
    @Min(1)
    @Max(100)
    private final Integer amount;

    @NotBlank
    @NotEmpty
    @Size(min = 3, max = 100)
    private final String cellType;
}
