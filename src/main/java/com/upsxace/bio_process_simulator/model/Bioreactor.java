package com.upsxace.bio_process_simulator.model;

import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "bioreactor")
@Builder @Getter @Setter
public class Bioreactor {
    @Id
    private UUID id;
    private BioreactorStatus status;
    private String cellType;
    private LocalDateTime lastSampleTime;
    private float pH;
    private float temperature;
    private float dissolvedOxygen;
    private float glucose;
    private float lactate;
    private float productTiter;
}
