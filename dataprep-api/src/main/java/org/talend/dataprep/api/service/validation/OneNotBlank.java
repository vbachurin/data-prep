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
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = OneNotBlankValidator.class)
@Documented
public @interface OneNotBlank {

    String[] value();

    String message() default "OneNotBlankValidator : all the tested fields are null/blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
