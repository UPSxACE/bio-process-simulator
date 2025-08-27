package com.upsxace.bio_process_simulator.infrastructure.devices;

import com.upsxace.bio_process_simulator.infrastructure.Sample;
import com.upsxace.bio_process_simulator.infrastructure.supervisor.SimulationClock;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.repository.BioreactorRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AutoSampler {
    private final SimulationClock simulationClock;
    private final LocalDateTime lastAction;
    private final Analyser analyser;
    private final BioreactorRepository bioreactorRepository;

    public AutoSampler(SimulationClock simulationClock, Analyser analyser, BioreactorRepository bioreactorRepository) {
        this.simulationClock = simulationClock;
        this.lastAction = LocalDateTime.now();
        this.analyser = analyser;
        this.bioreactorRepository = bioreactorRepository;
    }

    // TODO: log
    // TODO: add test
    public void requestSampling(UUID experimentId, List<Bioreactor> bioreactors){
        // fictional limit of 1 sampling per second
        if(simulationClock.getCurrentTime().isBefore(lastAction.plus(Duration.ofSeconds(1))))
            return;

        if(bioreactors.isEmpty())
            return;

        var bioreactor = bioreactors.getFirst();
        bioreactor.setLastSampleTime(simulationClock.getCurrentTime());
        bioreactorRepository.save(bioreactor);

        analyser.addSampleToReportQueue(new Sample(
                experimentId,
                bioreactor.getId(),
                bioreactor.getCellType(),
                bioreactor.getPh(),
                bioreactor.getTemperature(),
                bioreactor.getDissolvedOxygen(),
                bioreactor.getGlucose(),
                bioreactor.getLactate(),
                bioreactor.getProductTiter()
        ));
    }
}
