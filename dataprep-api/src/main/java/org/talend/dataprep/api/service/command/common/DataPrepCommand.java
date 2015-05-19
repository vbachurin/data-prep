package org.talend.dataprep.api.service.command.common;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public abstract class DataPrepCommand<T> extends HystrixCommand<T> {

    protected final HttpClient client;

    protected ObjectMapper objectMapper;

    protected ObjectReader jsonReader;

    protected ObjectWriter jsonWriter;

    @Value("${transformation.service.url}")
    protected String transformationServiceUrl;

    @Value("${dataset.service.url}")
    protected String datasetServiceUrl;

    @Value("${preparation.service.url}")
    protected String preparationServiceUrl;

    @Autowired
    protected Jackson2ObjectMapperBuilder builder;

    @Autowired
    protected WebApplicationContext context;

    protected DataPrepCommand(final HystrixCommandGroupKey groupKey, final HttpClient client) {
        super(groupKey);
        this.client = client;
    }

    /**
     * Create or reuse an object mapper
     */
    protected ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = builder.build();
        }
        return objectMapper;
    }

    /**
     * Create or reuse a json reader
     */
    protected ObjectReader getJsonReader() {
        if (jsonReader == null) {
            jsonReader = getObjectMapper().reader();
        }
        return jsonReader;
    }

    /**
     * Create or reuse a json writer
     */
    protected ObjectWriter getJsonWriter() {
        if (jsonWriter == null) {
            jsonWriter = getObjectMapper().writer();
        }
        return jsonWriter;
    }
}
