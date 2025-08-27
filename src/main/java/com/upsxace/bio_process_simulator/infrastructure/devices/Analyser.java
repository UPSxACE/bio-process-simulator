package com.upsxace.bio_process_simulator.infrastructure.devices;

import com.upsxace.bio_process_simulator.infrastructure.Sample;
import com.upsxace.bio_process_simulator.infrastructure.supervisor.SimulationClock;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Queue;

@Component
public class Analyser {
    private final Queue<Sample> inAnalysis;
    private final SimulationClock simulationClock;
    private final LocalDateTime lastAction;

    public Analyser(SimulationClock simulationClock){
        this.inAnalysis = new ArrayDeque<>();
        this.simulationClock = simulationClock;
        this.lastAction = LocalDateTime.now();
    }

    // TODO: add test
    public void addSampleToReportQueue(Sample sample){
        // TODO: log receiving sample
        inAnalysis.add(sample);
    }

    // TODO: add test
    public Sample checkFinishedAnalysis(){
        // fictional limit of analysing 1 sample per minute
        if(simulationClock.getCurrentTime().isBefore(lastAction.plus(Duration.ofMinutes(1)))){
            return null;
        }

        return inAnalysis.poll();
    }
}
