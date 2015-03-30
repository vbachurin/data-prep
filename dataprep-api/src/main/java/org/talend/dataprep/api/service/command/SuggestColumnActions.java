package org.talend.dataprep.api.service.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class SuggestColumnActions extends ChainedCommand<InputStream, DataSetMetadata> {

    private final String transformServiceUrl;

    private final String column;

    private final HttpClient client;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private SuggestColumnActions(HttpClient client, String transformServiceUrl, HystrixCommand<DataSetMetadata> retrieveMetadata,
            String columnName) {
        super(PreparationAPI.TRANSFORM_GROUP, retrieveMetadata);
        this.transformServiceUrl = transformServiceUrl;
        this.column = columnName;
        this.client = client;
    }

    @Override
    protected InputStream run() throws Exception {
        HttpPost post = new HttpPost(transformServiceUrl + "/suggest/column");
        post.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
        DataSetMetadata metadata = getInput();
        List<ColumnMetadata> columns = metadata.getRow().getColumns();
        ColumnMetadata columnMetadata = null;
        for (ColumnMetadata current : columns) {
            if (current.getId().equals(column)) {
                columnMetadata = current;
                break;
            }
        }
        if (columnMetadata == null) {
            // Column does not exist in data set metadata.
            return new ByteArrayInputStream(new byte[0]);
        }
        ObjectMapper objectMapper = builder.build();
        StringWriter columnMetadataJSON = new StringWriter();
        objectMapper.writer().writeValue(columnMetadataJSON, columnMetadata);
        post.setEntity(new StringEntity(columnMetadataJSON.toString()));
        HttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                // Immediately release connection
                post.releaseConnection();
                return null;
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), post::releaseConnection);
            }
        }
        throw new RuntimeException("Unable to retrieve suggested actions.");
    }
}
