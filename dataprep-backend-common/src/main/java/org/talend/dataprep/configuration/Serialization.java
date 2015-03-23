package org.talend.dataprep.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.talend.dataprep.api.json.DataSetMetadataModule;
import org.talend.dataprep.preparation.json.PreparationMetadataModule;

@Configuration
public class Serialization {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(false);
        builder.modules(Arrays.asList(DataSetMetadataModule.DEFAULT, PreparationMetadataModule.DEFAULT));
        return builder;
    }
}
