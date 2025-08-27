package com.upsxace.bio_process_simulator.infrastructure.supervisor;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Getter
public class SimulationClock {
    private LocalDateTime currentTime = LocalDateTime.now();

    public void addSecond(){
        currentTime = currentTime.plus(Duration.ofSeconds(1));
    }
}
