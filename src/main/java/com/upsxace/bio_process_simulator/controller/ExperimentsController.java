package com.upsxace.bio_process_simulator.controller;

import com.upsxace.bio_process_simulator.dto.InitiateExperimentRequest;
import com.upsxace.bio_process_simulator.model.Experiment;
import com.upsxace.bio_process_simulator.service.ExperimentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/experiments")
@RequiredArgsConstructor
public class ExperimentsController {
    private final ExperimentsService experimentsService;

    @PostMapping
    public ResponseEntity<Experiment> initiateExperiment(
            @RequestBody @Valid InitiateExperimentRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var uri = uriBuilder.path("/experiments").build().toUri();
        return ResponseEntity.created(uri).body(experimentsService.initiateExperiment(request));
    }

    @GetMapping
    public ResponseEntity<List<Experiment>> getAllExperiments(
            @RequestParam(required = false) Boolean active
    ){
        return ResponseEntity.ok(experimentsService.getAllExperiments(active));
    }
}
