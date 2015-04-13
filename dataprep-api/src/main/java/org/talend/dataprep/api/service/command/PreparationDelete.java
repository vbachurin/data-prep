package org.talend.dataprep.api.service.command;


import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;

import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class PreparationDelete extends HystrixCommand<String> {

    private final HttpClient client;

    private final String preparationServiceUrl;

    private final String id;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private PreparationDelete(HttpClient client, String preparationServiceURL, String id) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.preparationServiceUrl = preparationServiceURL;
        this.id = id;
    }

    @Override
    protected String getFallback() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String run() throws Exception {
        HttpDelete contentRetrieval = new HttpDelete(preparationServiceUrl + "/preparations/" + id);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw new RuntimeException("Unable to delete preparation #" + id + ".");
    }
}
