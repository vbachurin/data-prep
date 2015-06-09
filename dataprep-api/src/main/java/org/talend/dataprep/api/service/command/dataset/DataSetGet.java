package org.talend.dataprep.api.service.command.dataset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("request")
public class DataSetGet extends DataPrepCommand<InputStream> {

    private final String dataSetId;

    private final boolean metadata;

    private final boolean columns;

    public DataSetGet(HttpClient client, String dataSetId, boolean metadata, boolean columns) {
        super(PreparationAPI.TRANSFORM_GROUP, client);
        this.dataSetId = dataSetId;
        this.metadata = metadata;
        this.columns = columns;
    }

    @Override
    protected InputStream run() throws Exception {

        final HttpGet contentRetrieval = new HttpGet(datasetServiceUrl + "/datasets/" + dataSetId + "/content/?metadata="
                + metadata + "&columns=" + columns);
        final HttpResponse response = client.execute(contentRetrieval);
        return handleResponse(response, contentRetrieval);
    }

    private InputStream handleResponse(final HttpResponse response, final HttpGet contentRetrieval) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 400) {
            if (statusCode == HttpStatus.SC_NO_CONTENT) {
                // Immediately release connection
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        } else if (statusCode >= 400) { // Error (4xx & 5xx codes)
            final ObjectMapper build = builder.build();
            final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
            errorCode.setHttpStatus(statusCode);
            throw new TDPException(errorCode);
        }
        Exception cause = new Exception(response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase());
        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_CONTENT, cause, TDPExceptionContext.build().put("id",
                dataSetId));
    }
}
