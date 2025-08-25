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
    private Float pH;
    private Float temperature;
    private Float dissolvedOxygen;
    private Float glucose;
    private Float lactate;
    private Float productTiter;

    public Bioreactor(String cellType){
        this.status = BioreactorStatus.ENDED;
        this.cellType = cellType;
        this.lastSampleTime = LocalDateTime.MIN;
    }
}
