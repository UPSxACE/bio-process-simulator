package com.upsxace.bio_process_simulator.service;

import com.upsxace.bio_process_simulator.dto.InitiateExperimentMinMaxConstraintsDto;
import com.upsxace.bio_process_simulator.dto.InitiateExperimentRequest;
import com.upsxace.bio_process_simulator.infrastructure.Sample;
import com.upsxace.bio_process_simulator.infrastructure.supervisor.SimulationClock;
import com.upsxace.bio_process_simulator.infrastructure.supervisor.utils.ConstraintCheck;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.model.Experiment;
import com.upsxace.bio_process_simulator.model.Measurement;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import com.upsxace.bio_process_simulator.model.enums.ExperimentAnalyte;
import com.upsxace.bio_process_simulator.model.vo.ExperimentConstraintsMinMaxVo;
import com.upsxace.bio_process_simulator.model.vo.ExperimentConstraintsVo;
import com.upsxace.bio_process_simulator.repository.BioreactorRepository;
import com.upsxace.bio_process_simulator.repository.ExperimentRepository;
import com.upsxace.bio_process_simulator.repository.MeasurementRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentsService {
    private final BioreactorRepository bioreactorRepository;
    private final ExperimentRepository experimentRepository;
    private final MeasurementRepository measurementRepository;
    private final ReportService reportService;
    private final List<UUID> activeExperiments = new ArrayList<>();
    private final SimulationClock simulationClock;

    @PostConstruct
    private void setupActiveExperiments() {
        activeExperiments.addAll(experimentRepository.findByActive(true).stream().map(Experiment::getId).toList());
    }

    // TODO: add test
    public final List<Experiment> getActiveExperiments() {
        return activeExperiments.stream()
                .map(experimentRepository::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // TODO: test this
    private ExperimentConstraintsMinMaxVo resolveConstraintMinMaxVo(InitiateExperimentMinMaxConstraintsDto dto) {
        if (dto == null)
            return null;

        return new ExperimentConstraintsMinMaxVo(dto.getMin(), dto.getMax());
    }

    // TODO: add test
    public Experiment initiateExperiment(InitiateExperimentRequest request) {
        if (request.getBatchSize() == null && request.getBioreactorIds() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request body must include either  batchSize or bioreactorIds"); // TODO: handle error in ControllerAdvice
        }

        if (request.getBatchSize() != null && request.getBioreactorIds() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot contain both batchSize and bioreactorIds. Provide only one"); // TODO: handle error in ControllerAdvice
        }

        var bioreactors = request.getBatchSize() != null
                ? bioreactorRepository.findByStatus(BioreactorStatus.ENDED).stream().limit(request.getBatchSize()).collect(Collectors.toSet()) // TODO: give priority to bioreactors of same cell type
                : bioreactorRepository.findByStatusAndIdIn(BioreactorStatus.ENDED, request.getBioreactorIds());

        var expectedAmount = request.getBatchSize() != null
                ? request.getBatchSize()
                : request.getBioreactorIds().size();

        if (bioreactors.size() < expectedAmount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is not enough available bioreactors"); // TODO: handle error in ControllerAdvice
        }

        // setup bioreactors and save
        bioreactors.forEach(b -> {
            b.setProductTiter(0f);
            b.setPh(request.getCellInitialValues().getPh());
            b.setTemperature(request.getCellInitialValues().getTemperature());
            b.setDissolvedOxygen(request.getCellInitialValues().getDissolvedOxygen());
            b.setGlucose(request.getCellInitialValues().getGlucose());
            b.setLactate(request.getCellInitialValues().getLactate());
            b.setLastSampleTime(simulationClock.getCurrentTime());
            b.setStatus(BioreactorStatus.ACTIVE);
        });

        bioreactorRepository.saveAll(bioreactors);

        // setup experiment, save and add to active experiments
        var experiment = Experiment.builder()
                .name(request.getName())
                .active(true)
                .bioreactorIds(bioreactors.stream().map(Bioreactor::getId).collect(Collectors.toList()))
                .endDate(simulationClock.getCurrentTime().plus(Duration.ofHours(request.getTimeLimitHours())))
                .targetProductTiterGPerL(request.getGoals().getTargetProductTiterGPerL())
                .sampleEveryMinutes(request.getSamplingPlan().getEveryMinutes())
                .analytes(request.getSamplingPlan().getAnalytes())
                .constraints(
                        request.getConstraints() == null
                                ? new ExperimentConstraintsVo(null, null, null, null, null)
                                : new ExperimentConstraintsVo(
                                resolveConstraintMinMaxVo(request.getConstraints().getPh()),
                                resolveConstraintMinMaxVo(request.getConstraints().getTemperature()),
                                resolveConstraintMinMaxVo(request.getConstraints().getDissolvedOxygen()),
                                resolveConstraintMinMaxVo(request.getConstraints().getGlucose()),
                                resolveConstraintMinMaxVo(request.getConstraints().getLactate())
                        )
                )
                .build();

        experimentRepository.save(experiment);
        activeExperiments.add(experiment.getId());

        return experiment;
    }

    // TODO: add test
    public List<Experiment> getAllExperiments(Boolean active) {
        if (active == null) {
            return new ArrayList<>(experimentRepository.findAll());
        }

        return new ArrayList<>(experimentRepository.findByActive(active));
    }

    // TODO: add test
    public List<Bioreactor> simulateSecond(Experiment experiment) {
        var bioreactors = bioreactorRepository.findByIdIn(experiment.getBioreactorIds());
        bioreactors.forEach(b -> {
            if (b.getStatus() == BioreactorStatus.ACTIVE)
                b.simulateSecond();
        });
        bioreactorRepository.saveAll(new HashSet<>(bioreactors));

        return new ArrayList<>(bioreactors);
    }

    // TODO: add test
    public void handleAnalysisResult(Sample sample) {
        var experiment = experimentRepository.findById(sample.getExperimentId());
        if (experiment == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The experiment of id " + sample.getExperimentId().toString() + " was not found"); // TODO: handle error in ControllerAdvice

        var analytes = experiment.getAnalytes();

        // save measurements based on configured analytes
        var measurementBuilder = Measurement.builder();

        measurementBuilder.cellType(sample.getCellType());
        measurementBuilder.experimentId(sample.getExperimentId());
        measurementBuilder.bioreactorId(sample.getBioreactorId());
        measurementBuilder.productTiter(sample.getProductTiter());

        if (analytes.contains(ExperimentAnalyte.ph))
            measurementBuilder.ph(sample.getPh());
        if (analytes.contains(ExperimentAnalyte.glucose))
            measurementBuilder.glucose(sample.getGlucose());
        if (analytes.contains(ExperimentAnalyte.lactate))
            measurementBuilder.lactate(sample.getLactate());
        if (analytes.contains(ExperimentAnalyte.dissolvedOxygen))
            measurementBuilder.dissolvedOxygen(sample.getDissolvedOxygen());
        if (analytes.contains(ExperimentAnalyte.temperature))
            measurementBuilder.temperature(sample.getTemperature());

        var measurement = measurementBuilder.build();

        measurementRepository.save(measurement);

        // check if constraints were bypassed or if goals were met
        ConstraintCheck[] constraintChecks = new ConstraintCheck[]{
                new ConstraintCheck("pH", experiment.getConstraints().getPh(), sample.getPh()),
                new ConstraintCheck("glucose", experiment.getConstraints().getGlucose(), sample.getGlucose()),
                new ConstraintCheck("lactate", experiment.getConstraints().getLactate(), sample.getLactate()),
                new ConstraintCheck("dissolved oxygen", experiment.getConstraints().getDissolvedOxygen(), sample.getDissolvedOxygen()),
                new ConstraintCheck("temperature", experiment.getConstraints().getTemperature(), sample.getTemperature()),
        };

        List<String> errorMessages = new ArrayList<>();

        for (var constraintCheck : constraintChecks){
            var constraints = constraintCheck.getConstraints();
            if (constraints != null) {
                if (constraints.getMin() != null && constraintCheck.getValue() < constraints.getMin())
                    errorMessages.add("Constraint violation: " + constraintCheck.getName() + " dropped below minimum of " + constraints.getMin());
                if (constraints.getMax() != null && constraintCheck.getValue() > constraints.getMax())
                    errorMessages.add("Constraint violation: " + constraintCheck.getName() + " exceeded maximum of " + constraints.getMax());
            }
        }

        if(simulationClock.getCurrentTime().isAfter(experiment.getEndDate()))
            errorMessages.add("The goal was not reached within the allotted time limit.");

        var hitGoals = errorMessages.isEmpty() && sample.getProductTiter() >= experiment.getTargetProductTiterGPerL();

        if(!errorMessages.isEmpty() || hitGoals){
            // command bioreactor to stop
            // TODO: log
            var bioreactor = bioreactorRepository.findById(sample.getBioreactorId());
            bioreactor.setStatus(errorMessages.isEmpty() ? BioreactorStatus.SUCCESSFUL : BioreactorStatus.FAILED);
            if(!errorMessages.isEmpty()) bioreactor.setReason(errorMessages.getFirst());
            bioreactorRepository.save(bioreactor);

            var allBioreactorsFinished = bioreactorRepository.countByStatusInAndIdIn(
                    List.of(BioreactorStatus.SUCCESSFUL, BioreactorStatus.FAILED), experiment.getBioreactorIds()
            ) == experiment.getBioreactorIds().size();

            if(allBioreactorsFinished){
                // if all bioreactors are finished, close experiment
                // TODO: log
                experiment.setActive(false);
                activeExperiments.remove(experiment.getId());
                experimentRepository.save(experiment);

                // then write report
                // TODO: log
                reportService.writeReportForExperiment(experiment.getId());

                // then reset bioreactors
                // TODO: log
                var bioreactors = bioreactorRepository.findByIdIn(experiment.getBioreactorIds());
                bioreactors.forEach(b -> {
                    b.setStatus(BioreactorStatus.ENDED);
                    b.setReason(null);
                    b.setPh(null);
                    b.setProductTiter(null);
                    b.setGlucose(null);
                    b.setLactate(null);
                    b.setDissolvedOxygen(null);
                    b.setTemperature(null);
                    b.setLastSampleTime(null);
                });
                bioreactorRepository.saveAll(new HashSet<>(bioreactors));
            }
        }
    }
}
