package org.talend.dataprep.api.service.command.transformation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.ChainedCommand;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

@Component
@Scope("request")
public class SuggestColumnActions extends ChainedCommand<InputStream, DataSetMetadata> {

    private final String column;

    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    private SuggestColumnActions(HttpClient client, HystrixCommand<DataSetMetadata> retrieveMetadata, String columnName) {
        super(PreparationAPI.TRANSFORM_GROUP, client, retrieveMetadata);
        this.column = columnName;
    }

    @Override
    protected InputStream run() throws Exception {
        HttpPost post = new HttpPost(transformationServiceUrl + "/suggest/column");
        post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataSetMetadata metadata = getInput();

        if (metadata == null) {
            // FIXME add dataset id in error message?
            throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_METADATA);
        }

        List<ColumnMetadata> columns = metadata.getRow().getColumns();

        Optional<ColumnMetadata> columnMetadata = columns.stream()
                .filter(columnMetadataCurrent -> StringUtils.equals(column, columnMetadataCurrent.getId())).findFirst();

        if (!columnMetadata.isPresent()) {
            // Column does not exist in data set metadata.
            return new ByteArrayInputStream(new byte[0]);
        }
        ObjectMapper objectMapper = builder.build();
        StringWriter columnMetadataJSON = new StringWriter();
        objectMapper.writer().writeValue(columnMetadataJSON, columnMetadata.get());
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
        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_SUGGESTED_ACTIONS);
    }
}
