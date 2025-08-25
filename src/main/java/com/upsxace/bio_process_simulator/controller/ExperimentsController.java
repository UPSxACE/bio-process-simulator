package com.upsxace.bio_process_simulator.controller;

import com.upsxace.bio_process_simulator.dto.InitiateExperimentRequest;
import com.upsxace.bio_process_simulator.model.Experiment;
import com.upsxace.bio_process_simulator.service.ExperimentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/experiments")
@RequiredArgsConstructor
public class ExperimentsController {
    private final ExperimentsService experimentsService;

    @PostMapping
    public ResponseEntity<Experiment> initiateExperiment(
            @RequestBody @Valid InitiateExperimentRequest request
    ){
        return ResponseEntity.ok(experimentsService.initiateExperiment(request));
    }
}
