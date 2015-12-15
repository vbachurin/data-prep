package org.talend.dataprep.preparation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;
import static org.talend.dataprep.test.SameJSONFile.sameJSONAsFile;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.preparation.store.PreparationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PreparationTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class PreparationSerializationTest {

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @Autowired
    PreparationRepository repository;

    @Test
    public void emptyPreparation() throws Exception {
        Preparation preparation = new Preparation();
        final StringWriter output = new StringWriter();
        builder.build().writer().writeValue(output, preparation);
        final InputStream expected = PreparationSerializationTest.class.getResourceAsStream("emptyPreparation.json");
        assertThat(output.toString(), sameJSONAsFile(expected));
    }

    @Test
    public void namePreparation() throws Exception {
        Preparation preparation = new Preparation();
        preparation.setName("MyName");
        final StringWriter output = new StringWriter();
        builder.build().writer().writeValue(output, preparation);
        final InputStream expected = PreparationSerializationTest.class.getResourceAsStream("namePreparation.json");
        assertThat(output.toString(), sameJSONAsFile(expected));
    }

    @Test
    public void preparationDataSet() throws Exception {
        Preparation preparation = new Preparation();
        preparation.setDataSetId("12345");
        final StringWriter output = new StringWriter();
        builder.build().writer().writeValue(output, preparation);
        final InputStream expected = PreparationSerializationTest.class.getResourceAsStream("dataSetPreparation.json");
        assertThat(output.toString(), sameJSONAsFile(expected));
    }

    @Test
    public void preparationAuthor() throws Exception {
        Preparation preparation = new Preparation();
        preparation.setDataSetId("12345");
        preparation.setAuthor("myAuthor");
        final StringWriter output = new StringWriter();
        builder.build().writer().writeValue(output, preparation);
        final InputStream expected = PreparationSerializationTest.class.getResourceAsStream("authorPreparation.json");
        assertThat(output.toString(), sameJSONAsFile(expected));
    }

    @Test
    public void preparationDetailsSteps() throws Exception {
        Preparation preparation = new Preparation("12345", Step.ROOT_STEP.id());
        preparation.setAuthor("myAuthor");
        final StringWriter output = new StringWriter();
        builder.build().writer().writeValue(output, new PreparationDetails(preparation));
        final InputStream expected = PreparationSerializationTest.class.getResourceAsStream("preparationDetailsSteps.json");
        assertThat(output.toString(), sameJSONAsFile(expected));
    }

    @Test
    public void preparationDetailsStepsWithActions() throws Exception {
        // Add a step
        final List<Action> actions = PreparationTest.getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent1 = ROOT_CONTENT.append(actions);
        repository.add(newContent1);
        final Step s1 = new Step(ROOT_STEP.id(), newContent1.id());
        repository.add(s1);
        // Use it in preparation
        Preparation preparation = new Preparation("12345", s1.id());
        final StringWriter output = new StringWriter();
        builder.build().writer().writeValue(output, new PreparationDetails(preparation));
        final InputStream expected = PreparationSerializationTest.class.getResourceAsStream("preparationDetailsWithStepsAndActions.json");
        assertThat(output.toString(), sameJSONAsFile(expected));
    }
}
