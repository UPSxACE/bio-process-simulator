package com.upsxace.bio_process_simulator.controller;

import com.upsxace.bio_process_simulator.dto.ConnectBioreactorRequest;
import com.upsxace.bio_process_simulator.dto.CreatedBioreactorDto;
import com.upsxace.bio_process_simulator.service.BioreactorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bioreactors")
@RequiredArgsConstructor
public class BioreactorsController {
    private final BioreactorService bioreactorService;

    @PostMapping("/connect")
    public ResponseEntity<List<CreatedBioreactorDto>> connectBioreactors(
            @RequestBody @Valid ConnectBioreactorRequest request
    ){
        if(request.getAmount() == null){
            return ResponseEntity.ok(bioreactorService.connectBioreactor(request.getCellType()));
        }

        return ResponseEntity.ok(bioreactorService.connectBioreactor(request.getAmount(), request.getCellType()));
    }
}
