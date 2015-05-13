package org.talend.dataprep.api.service.validation;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class OneNotNullValidator implements ConstraintValidator<OneNotNull, Object> {

    private String[] fieldsName;

    @Override
    public void initialize(final OneNotNull annotation) {
        fieldsName = annotation.value();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            for (final String name : fieldsName) {
                final String prop = BeanUtils.getProperty(value, name);
                if(prop != null) {
                    return true;
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldsName[0])
                .addConstraintViolation();
        return false;
    }
}
