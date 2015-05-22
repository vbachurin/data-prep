package org.talend.dataprep.api.service.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates that one of the fields is not null
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = OneNotNullValidator.class)
@Documented
public @interface OneNotNull {

    String[] value();

    String message() default "OneNotNullValidator : all the tested fields are null";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
