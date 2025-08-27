package com.upsxace.bio_process_simulator.infrastructure.supervisor;

import com.upsxace.bio_process_simulator.infrastructure.devices.Analyser;
import com.upsxace.bio_process_simulator.infrastructure.devices.AutoSampler;
import com.upsxace.bio_process_simulator.service.ExperimentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

// Orchestrator object
@Component
@RequiredArgsConstructor
public class Supervisor {
    private final ExperimentsService experimentsService;
    private final SimulationClock simulationClock;
    private final AutoSampler autoSampler;
    private final Analyser analyser;

    public LocalDateTime getCurrentTime(){
        return simulationClock.getCurrentTime();
    }

    public String tick(TickRequest tickRequest){
        var totalOfSeconds = 0;
        if(tickRequest.getSeconds() != null)
            totalOfSeconds+= tickRequest.getSeconds();
        if(tickRequest.getMinutes() != null)
            totalOfSeconds+= tickRequest.getMinutes()*60;
        if(tickRequest.getHours() != null)
            totalOfSeconds+= tickRequest.getHours()*60*60;
        if(tickRequest.getDays() != null)
            totalOfSeconds+= tickRequest.getDays()*24*60*60;

        totalOfSeconds = Math.min(totalOfSeconds, 2_592_000); // cap to 30 days // TODO: add message that warns about this behavior

        for(var i = 0; i < totalOfSeconds; i++){
            simulationClock.addSecond();

            var activeExperiments = experimentsService.getActiveExperiments();

            activeExperiments.forEach(e -> {
                var bioreactorsNeedingSampling = experimentsService.simulateSecond(e).stream()
                        .filter(b -> simulationClock.getCurrentTime().isAfter(
                                b.getLastSampleTime().plus(Duration.ofMinutes(e.getSampleEveryMinutes()))
                        ))
                        .collect(Collectors.toList());

                autoSampler.requestSampling(e.getId(), bioreactorsNeedingSampling);
                var sample = analyser.checkFinishedAnalysis();
                if(sample == null){
                    return;
                }

                experimentsService.handleAnalysisResult(sample);
            });
        }

        return "Simulated a total of " + totalOfSeconds + " seconds.";
    }
}
