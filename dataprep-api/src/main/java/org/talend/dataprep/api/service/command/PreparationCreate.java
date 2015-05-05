package org.talend.dataprep.api.service.command;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationCreate extends HystrixCommand<String> {

    private final Preparation preparation;

    private final HttpClient client;

    private final String preparationServiceUrl;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private String preparationJSONValue;

    private PreparationCreate(HttpClient client, String preparationServiceUrl, Preparation preparation) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceUrl;
        this.preparation = preparation;
    }

    @PostConstruct
    public void prepare() {
        try {
            ObjectMapper mapper = builder.build();
            StringWriter json = new StringWriter();
            mapper.writer().writeValue(json, preparation);
            preparationJSONValue = json.toString();
        } catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_PREPARATION, e);
        }
    }

    @Override
    protected String run() throws Exception {
        HttpPut preparationCreation = new HttpPut(preparationServiceUrl + "/preparations");
        try {
            // Serialize preparation using configured serialization
            preparationCreation.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            preparationCreation.setEntity(new StringEntity(preparationJSONValue));
            HttpResponse response = client.execute(preparationCreation);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return IOUtils.toString(response.getEntity().getContent());
            }
            throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_PREPARATION);
        } finally {
            preparationCreation.releaseConnection();
        }
    }
}
