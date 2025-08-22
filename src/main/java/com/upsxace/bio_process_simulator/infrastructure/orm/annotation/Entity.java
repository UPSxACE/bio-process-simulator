package com.upsxace.bio_process_simulator.infrastructure.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // allow annotation to be read in runtime
@Target(ElementType.TYPE) // allow it to be applied to classes
public @interface Entity {
    String name();
}
