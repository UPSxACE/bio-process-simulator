package com.upsxace.bio_process_simulator.infrastructure.supervisor.utils;

import com.upsxace.bio_process_simulator.model.vo.ExperimentConstraintsMinMaxVo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConstraintCheck {
    private final String name;
    private final ExperimentConstraintsMinMaxVo constraints;
    private final float value;
}
