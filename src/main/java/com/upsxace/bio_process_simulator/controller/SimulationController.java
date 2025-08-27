package com.upsxace.bio_process_simulator.controller;

import com.upsxace.bio_process_simulator.dto.CurrentTimeDto;
import com.upsxace.bio_process_simulator.dto.MessageDto;
import com.upsxace.bio_process_simulator.infrastructure.supervisor.Supervisor;
import com.upsxace.bio_process_simulator.infrastructure.supervisor.TickRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationController {
    private final Supervisor supervisor;

    @GetMapping("/time")
    public ResponseEntity<CurrentTimeDto> getCurrentTime(){
        return ResponseEntity.ok(new CurrentTimeDto(supervisor.getCurrentTime()));
    }

    @PostMapping("/tick")
    public ResponseEntity<MessageDto> tick(
            @RequestBody @Valid TickRequest request
    ){
        return ResponseEntity.ok(new MessageDto(supervisor.tick(request)));
    }
}
