package org.talend.dataprep.transformation.api.transformer.suggestion;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext
public class SuggestionTest {

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private Suggestion suggestion;

    @Test
    public void test1() throws Exception {
        final ObjectMapper mapper = builder.build();
        final InputStream inputStream = SuggestionTest.class.getResourceAsStream("suggest1.json");
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            final List<ActionMetadata> suggest = suggestion.suggest(dataSet);
            for (ActionMetadata actionMetadata : suggest) {
                System.out.println("actionMetadata = " + actionMetadata);
            }
        }
    }

    @Test
    public void test2() throws Exception {
        final ObjectMapper mapper = builder.build();
        final InputStream inputStream = SuggestionTest.class.getResourceAsStream("suggest2.json");
        try (JsonParser parser = mapper.getFactory().createParser(inputStream)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            final List<ActionMetadata> suggest = suggestion.suggest(dataSet);
            for (ActionMetadata actionMetadata : suggest) {
                System.out.println("actionMetadata = " + actionMetadata);
            }
        }
    }
}
