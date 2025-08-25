package com.upsxace.bio_process_simulator.service;

import com.upsxace.bio_process_simulator.dto.CreatedBioreactorDto;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.repository.BioreactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BioreactorService {
    private final BioreactorRepository bioreactorRepository;

    public List<CreatedBioreactorDto> connectBioreactor(int amount, String cellType){
        if(amount < 1)
            throw new IllegalArgumentException("Amount cannot be less than 1");

        var bioreactors = new ArrayList<CreatedBioreactorDto>();
        for(var i = 0; i < amount; i++){
            bioreactors.add(new CreatedBioreactorDto(bioreactorRepository.save(new Bioreactor(cellType)).getId()));
        }

        return bioreactors;
    }

    public List<CreatedBioreactorDto> connectBioreactor(String cellType){
        return connectBioreactor(1, cellType);
    }

    public List<Bioreactor> getAllBioreactors(){
       return new ArrayList<>(bioreactorRepository.findAll());
    }
}
