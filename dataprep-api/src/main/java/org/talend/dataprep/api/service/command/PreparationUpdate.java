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
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationUpdate extends HystrixCommand<String> {

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final String id;

    private final Preparation preparation;

    private PreparationUpdate(HttpClient client, String preparationServiceURL, String id, Preparation preparation) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceURL;
        this.id = id;
        this.preparation = preparation;
    }

    @Override
    protected String getFallback() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String run() throws Exception {
        HttpPut preparationCreation = new HttpPut(preparationServiceUrl + "/preparations/" + id);
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
            throw new RuntimeException("Unable to update preparation #" + id + ".");
        } finally {
            preparationCreation.releaseConnection();
        }
    }
}
