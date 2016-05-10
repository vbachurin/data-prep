package org.talend.dataprep.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import java.io.IOException;

@Configuration
public class Converters {

    @Bean
    public Converter<String, JsonNode> jsonNodeConverter() {
        // Don't convert to lambda -> cause issue for Spring to infer source and target types.
        return new Converter<String, JsonNode>() {
            @Override
            public JsonNode convert(String source) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    return mapper.readTree(source);
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }
        };
    }
}
