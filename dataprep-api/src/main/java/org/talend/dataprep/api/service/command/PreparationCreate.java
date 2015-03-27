package org.talend.dataprep.api.service.command;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.springframework.http.MediaType;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.json.PreparationMetadataModule;
import org.talend.dataprep.api.service.APIService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

public class PreparationCreate extends HystrixCommand<String> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final Preparation preparation;

    public PreparationCreate(HttpClient client, String preparationServiceUrl, Preparation preparation) {
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
        // TODO There should be a bean to write Preparation as a JSON string
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(PreparationMetadataModule.DEFAULT);
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
            throw new RuntimeException("Unable to retrieve preparation list.");
        } finally {
            preparationCreation.releaseConnection();
        }
    }
}
