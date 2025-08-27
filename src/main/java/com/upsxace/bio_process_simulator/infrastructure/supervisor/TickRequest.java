package com.upsxace.bio_process_simulator.infrastructure.supervisor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TickRequest {
    private final Integer seconds;
    private final Integer minutes;
    private final Integer hours;
    private final Integer days;
}
