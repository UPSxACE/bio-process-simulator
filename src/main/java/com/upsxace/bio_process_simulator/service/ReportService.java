package com.upsxace.bio_process_simulator.service;

import com.upsxace.bio_process_simulator.infrastructure.supervisor.SimulationClock;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.model.Report;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import com.upsxace.bio_process_simulator.repository.BioreactorRepository;
import com.upsxace.bio_process_simulator.repository.ExperimentRepository;
import com.upsxace.bio_process_simulator.repository.MeasurementRepository;
import com.upsxace.bio_process_simulator.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final MeasurementRepository measurementRepository;
    private final ExperimentRepository experimentRepository;
    private final BioreactorRepository bioreactorRepository;
    private final ReportRepository reportRepository;
    private final SimulationClock simulationClock;

    private void writeStats(Map<String, Object> reportMap, String key, List<Float> values) {
        if (!values.isEmpty()) {
            var total = 0f;
            var min = Float.POSITIVE_INFINITY;
            var max = Float.NEGATIVE_INFINITY;
            for(var value : values) {
                total += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            reportMap.put(key, Map.of(
                    "avg", total / values.size(),
                    "min", min,
                    "max", max
            ));
        }

    }

    // TODO: add test
    public void writeReportForExperiment(UUID experimentId) {
        // TODO: log
        var experiment = experimentRepository.findById(experimentId);
        var bioreactors = bioreactorRepository.findByIdIn(experiment.getBioreactorIds());
        var measurements = measurementRepository.findByExperimentId(experimentId).stream().toList();

        List<Float> ph = new ArrayList<>();
        List<Float> temperature = new ArrayList<>();
        List<Float> dissolvedOxygen = new ArrayList<>();
        List<Float> glucose = new ArrayList<>();
        List<Float> lactate = new ArrayList<>();

        for (var measurement : measurements) {
            if (measurement.getPh() != null)
                ph.add(measurement.getPh());
            if (measurement.getTemperature() != null)
                temperature.add(measurement.getTemperature());
            if (measurement.getDissolvedOxygen() != null)
                dissolvedOxygen.add(measurement.getDissolvedOxygen());
            if (measurement.getGlucose() != null)
                glucose.add(measurement.getGlucose());
            if (measurement.getLactate() != null)
                lactate.add(measurement.getLactate());
        }

        var report = new HashMap<String, Object>();
        report.put("experimentName", experiment.getName());
        report.put("cellType", measurements.getFirst().getCellType());
        report.put("finishedAt", simulationClock.getCurrentTime().toString());
        report.put("outcomes", Map.of(
                "total", bioreactors.size(),
                "success", Map.of("count", bioreactors.stream().filter(b -> b.getStatus().equals(BioreactorStatus.SUCCESSFUL)).count()),
                "failure", Map.of(
                        "count", bioreactors.stream().filter(b -> b.getStatus().equals(BioreactorStatus.FAILED)).count(),
                        "reasons", bioreactors.stream().filter(b -> b.getStatus().equals(BioreactorStatus.FAILED) && b.getReason() != null).map(Bioreactor::getReason).collect(Collectors.toList())
                )
        ));

        var stats = new HashMap<String, Object>();
        writeStats(stats, "pH", ph);
        writeStats(stats, "temperature", temperature);
        writeStats(stats, "dissolvedOxygen", dissolvedOxygen);
        writeStats(stats, "glucose", glucose);
        writeStats(stats, "lactate", lactate);
        report.put("stats", stats);

        reportRepository.save(Report.builder().summary(report).build());
    }

    public List<Report> getAllReports(){
        return new ArrayList<>(reportRepository.findAll());
    }
}
