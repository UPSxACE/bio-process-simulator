package com.upsxace.bio_process_simulator.model;

import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Entity(name = "report")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Report {
    @Id
    private UUID id;
    private Map<String, Object> summary;
}
