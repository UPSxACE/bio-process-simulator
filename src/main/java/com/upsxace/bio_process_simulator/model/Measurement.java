package com.upsxace.bio_process_simulator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import lombok.*;

import java.util.UUID;

// TODO: add date (on sample as well) & order by date in creation of report

@Entity(name = "measurement")
@Builder @Getter @Setter
@AllArgsConstructor
public class Measurement {
    @Id
    private UUID id;
    private UUID experimentId;
    private UUID bioreactorId;
    private String cellType;
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
    private float productTiter;
}
