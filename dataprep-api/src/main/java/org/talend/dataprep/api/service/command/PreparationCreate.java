package org.talend.dataprep.api.service.command;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.exception.Exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationCreate extends HystrixCommand<String> {

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private HttpClient client;

    private String preparationServiceUrl;

    private Preparation preparation;

    private PreparationCreate(HttpClient client, String preparationServiceUrl, Preparation preparation) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.preparation = preparation;
    }

    @Override
    protected String getFallback() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String run() throws Exception {
        HttpPut preparationCreation = new HttpPut(preparationServiceUrl + "/preparations");
        // Serialize preparation using configured serialization
        ObjectMapper mapper = builder.build();
        StringWriter preparationJSONValue = new StringWriter();
        mapper.writer().writeValue(preparationJSONValue, preparation);
        preparationCreation.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        preparationCreation.setEntity(new StringEntity(preparationJSONValue.toString()));
        HttpResponse response = client.execute(preparationCreation);
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            if (statusCode == 200) {
                return IOUtils.toString(response.getEntity().getContent());
            }
            throw Exceptions.User(APIMessages.UNABLE_TO_CREATE_PREPARATION);
        } finally {
            preparationCreation.releaseConnection();
        }
    }
}
