package com.upsxace.bio_process_simulator.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ExperimentConstraintsVo {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExperimentConstraintsMinMaxVo ph;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExperimentConstraintsMinMaxVo temperature;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExperimentConstraintsMinMaxVo dissolvedOxygen;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExperimentConstraintsMinMaxVo glucose;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExperimentConstraintsMinMaxVo lactate;
}
