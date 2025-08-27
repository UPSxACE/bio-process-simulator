package com.upsxace.bio_process_simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CurrentTimeDto {
    private final LocalDateTime currentTime;
}
