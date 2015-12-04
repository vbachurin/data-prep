package org.talend.dataprep.transformation.api.action.metadata;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for all related unit tests that deal with metadata
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AbstractMetadataBaseTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
public abstract class AbstractMetadataBaseTest {

}
