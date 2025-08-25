package com.upsxace.bio_process_simulator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "bioreactor")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Bioreactor {
    @Id
    private UUID id;
    private BioreactorStatus status;
    private String cellType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime lastSampleTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float ph;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float temperature;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float dissolvedOxygen;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float glucose;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float lactate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float productTiter;

    public Bioreactor(String cellType) {
        this.status = BioreactorStatus.ENDED;
        this.cellType = cellType;
    }
}
