package com.upsxace.bio_process_simulator.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString @Getter @Setter
public class ExperimentConstraintsMinMaxVo {
    private Float min;
    private Float max;
}
