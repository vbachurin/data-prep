package org.talend.dataprep.transformation.api.action.parameters;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for all parameter tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ParameterBaseTest.class)
@ComponentScan(basePackages = "org.talend.dataprep")
public class ParameterBaseTest {

    @Autowired
    protected Jackson2ObjectMapperBuilder builder;

}
