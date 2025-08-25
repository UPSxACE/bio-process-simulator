package com.upsxace.bio_process_simulator.model;

import com.upsxace.bio_process_simulator.model.enums.ExperimentAnalyte;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import com.upsxace.bio_process_simulator.model.vo.ExperimentConstraintsVo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "experiment")
@Getter @Setter
@Builder
@AllArgsConstructor
@ToString
public class Experiment {
    @Id
    private UUID id;
    private String name;
    private boolean active;
    private List<UUID> bioreactorIds;
    private LocalDateTime endDate;
    private float targetProductTiterGPerL;
    private int sampleEveryMinutes;
    private List<ExperimentAnalyte> analytes;
    private ExperimentConstraintsVo constraints;
}
