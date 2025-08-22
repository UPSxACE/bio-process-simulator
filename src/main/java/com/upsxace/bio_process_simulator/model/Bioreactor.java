package com.upsxace.bio_process_simulator.model;

import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "bioreactor")
@Builder @Getter @Setter
@AllArgsConstructor
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

    public Bioreactor(String cellType){
        this.status = BioreactorStatus.ENDED;
        this.cellType = cellType;
        this.lastSampleTime = LocalDateTime.MIN;
        this.pH = 0;
        this.temperature = 0;
        this.dissolvedOxygen = 0;
        this.glucose = 0;
        this.lactate = 0;
        this.productTiter = 0;
    }
}
