package org.talend.dataprep.transformation.actions.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DataprepAction {

    /**
     * Registering name of the Action. It is recommended to leave it blank unless there are conflicting actions.
     */
    String value() default "";

}
