package org.talend.dataprep.transformation.api.action.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.exception.TDPException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.dataprep.exception.error.CommonErrorCodes.MISSING_ACTION_SCOPE;
import static org.talend.dataprep.exception.error.CommonErrorCodes.MISSING_ACTION_SCOPE_PARAMETER;
import static org.talend.dataprep.exception.error.CommonErrorCodes.UNSUPPORTED_ACTION_SCOPE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ActionMetadataValidationTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class ActionMetadataValidationTest {
    @Autowired
    private ActionMetadataValidation validator;

    @Test
    public void checkScopeConsistency_should_pass() throws Exception {
        //given
        final Map<String,String> parameters = new HashMap<>();
        parameters.put("scope", "column");
        parameters.put("column_id", "0001");

        //when
        validator.checkScopeConsistency("cut", parameters);

        //then : should not throw exception
    }

    @Test
    public void checkScopeConsistency_should_throw_exception_on_missing_scope() throws Exception {
        //given
        final Map<String,String> parameters = new HashMap<>();
        parameters.put("column_id", "0001");

        //when
        try {
            validator.checkScopeConsistency("cut", parameters);
            fail("should have thrown TDP exception because param scope is missing");
        }

        //then
        catch (final TDPException e) {
            assertThat(e.getCode(), is(MISSING_ACTION_SCOPE));
        }
    }

    @Test
    public void checkScopeConsistency_should_throw_exception_on_unsupported_scope() throws Exception {
        //given
        final Map<String,String> parameters = new HashMap<>();
        parameters.put("scope", "line");
        parameters.put("row_id", "0001");

        //when
        try {
            validator.checkScopeConsistency("cut", parameters);
            fail("should have thrown TDP exception because line scope is not supported by cut (for example)");
        }

        //then
        catch (final TDPException e) {
            assertThat(e.getCode(), is(UNSUPPORTED_ACTION_SCOPE));
        }
    }

    @Test
    public void checkScopeConsistency_should_throw_exception_on_missing_scope_mandatory_parameter() throws Exception {
        //given
        final Map<String,String> parameters = new HashMap<>();
        parameters.put("scope", "cell");
        parameters.put("row_id", "0001");

        //when
        try {
            validator.checkScopeConsistency("replace_on_value", parameters);
            fail("should have thrown TDP exception because cell scope requires column_id (for example)");
        }

        //then
        catch (final TDPException e) {
            assertThat(e.getCode(), is(MISSING_ACTION_SCOPE_PARAMETER));
        }
    }
}