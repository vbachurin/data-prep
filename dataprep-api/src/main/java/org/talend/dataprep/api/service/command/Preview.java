package org.talend.dataprep.api.service.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.APIMessages;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.exception.Exceptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Component
@Scope("request")
public class Preview extends HystrixCommand<InputStream> {

    private final HttpClient client;

    private final String transformationServiceUrl;

    private final InputStream body;

    @Autowired
    private WebApplicationContext context;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private Preview(HttpClient client, String transformationServiceUrl, InputStream body) {
        super(APIService.PREPARATION_GROUP);
        this.client = client;
        this.transformationServiceUrl = transformationServiceUrl;
        this.body = body;
    }

    @Override
    protected InputStream run() throws Exception {

        ObjectMapper mapper = builder.build();
        JsonNode tree = mapper.reader().readTree(body);
        JsonNode actions = tree.get("actions");
        JsonNode records = tree.get("records");

        String actionsStr = "{\"actions\": " + actions.toString() + "}";
        String recordsStr = "{\"records\": " + records.toString() + "}";

        String encodedActions = Base64.getEncoder().encodeToString(actionsStr.getBytes());
        InputStream recordsIS = new ByteArrayInputStream(recordsStr.getBytes());

        String uri = transformationServiceUrl + "/transform/?actions=" + encodedActions;
        HttpPost transformationCall = new HttpPost(uri);
        transformationCall.setEntity(new InputStreamEntity(recordsIS));
        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }
}
