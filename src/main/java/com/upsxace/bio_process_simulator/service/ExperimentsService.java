package com.upsxace.bio_process_simulator.service;

import com.upsxace.bio_process_simulator.dto.InitiateExperimentMinMaxConstraintsDto;
import com.upsxace.bio_process_simulator.dto.InitiateExperimentRequest;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.model.Experiment;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import com.upsxace.bio_process_simulator.model.vo.ExperimentConstraintsMinMaxVo;
import com.upsxace.bio_process_simulator.model.vo.ExperimentConstraintsVo;
import com.upsxace.bio_process_simulator.repository.BioreactorRepository;
import com.upsxace.bio_process_simulator.repository.ExperimentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentsService {
    private final BioreactorRepository bioreactorRepository;
    private final ExperimentRepository experimentRepository;
    private final List<UUID> activeExperiments = new ArrayList<>();

    @PostConstruct
    private void setupActiveExperiments(){
        activeExperiments.addAll(experimentRepository.findByActive(true).stream().map(Experiment::getId).toList());
    }

    // TODO: add test
    public final List<Experiment> getActiveExperiments(){
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
            b.setPH(request.getCellInitialValues().getPH());
            b.setTemperature(request.getCellInitialValues().getTemperature());
            b.setDissolvedOxygen(request.getCellInitialValues().getDissolvedOxygen());
            b.setGlucose(request.getCellInitialValues().getGlucose());
            b.setLactate(request.getCellInitialValues().getLactate());
            b.setLastSampleTime(LocalDateTime.now()); // FIXME: base time on supervisor clock
            b.setStatus(BioreactorStatus.ACTIVE);
        });

        bioreactorRepository.saveAll(bioreactors);

        // setup experiment, save and add to active experiments
        var experiment = Experiment.builder()
                .name(request.getName())
                .active(true)
                .bioreactorIds(bioreactors.stream().map(Bioreactor::getId).collect(Collectors.toList()))
                .endDate(LocalDateTime.now().plus(Duration.ofHours(request.getTimeLimitHours()))) // FIXME: base time on supervisor clock
                .targetProductTiterGPerL(request.getGoals().getTargetProductTiterGPerL())
                .sampleEveryMinutes(request.getSamplingPlan().getEveryMinutes())
                .analytes(request.getSamplingPlan().getAnalytes())
                .constraints(
                        request.getConstraints() == null
                                ? new ExperimentConstraintsVo(null, null, null, null, null)
                                : new ExperimentConstraintsVo(
                                resolveConstraintMinMaxVo(request.getConstraints().getPH()),
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
}
