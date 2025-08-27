package com.upsxace.bio_process_simulator.infrastructure;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Sample {
    private final UUID experimentId;
    private final UUID bioreactorId;
    private final String cellType;
    private final float ph;
    private final float temperature;
    private final float dissolvedOxygen;
    private final float glucose;
    private final float lactate;
    private final float productTiter;
}
