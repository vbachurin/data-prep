package org.talend.dataprep.api.service.validation;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

public class OneNotBlankValidator implements ConstraintValidator<OneNotBlank, Object> {

    private String[] fieldsName;

    @Override
    public void initialize(final OneNotBlank annotation) {
        fieldsName = annotation.value();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            for (final String name : fieldsName) {
                final String prop = BeanUtils.getProperty(value, name);
                if (isNotBlank(prop)) {
                    return true;
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldsName[0]).addConstraintViolation();
        return false;
    }
}
