package com.upsxace.bio_process_simulator.model.vo;

import lombok.*;

@AllArgsConstructor
@Builder @ToString @Getter @Setter
public class ExperimentConstraintsVo {
        private ExperimentConstraintsMinMaxVo pH;
        private ExperimentConstraintsMinMaxVo temperature;
        private ExperimentConstraintsMinMaxVo dissolvedOxygen;
        private ExperimentConstraintsMinMaxVo glucose;
        private ExperimentConstraintsMinMaxVo lactate;
}
