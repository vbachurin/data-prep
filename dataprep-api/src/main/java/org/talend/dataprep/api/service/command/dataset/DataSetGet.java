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
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.json.JsonErrorCode;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

/**
 * Command to get a dataset.
 */
@Component
@Scope("request")
public class DataSetGet extends DataPrepCommand<InputStream> {

    /** The dataset id. */
    private final String dataSetId;

    /** True if the metadata is requested. */
    private final boolean metadata;

    /** True if the columns are requested. */
    private final boolean columns;

    /** Optional sample size (if null or <=0, the full dataset is returned). */
    private Long sample;

    /**
     * Constructor.
     *
     * @param client the http client to use.
     * @param dataSetId the requested dataset id.
     * @param metadata true if the metadata is requested.
     * @param columns true if the columns is requested.
     */
    public DataSetGet(HttpClient client, String dataSetId, boolean metadata, boolean columns) {
        super(PreparationAPI.TRANSFORM_GROUP, client);
        this.dataSetId = dataSetId;
        this.metadata = metadata;
        this.columns = columns;
        this.sample = null;
    }

    /**
     * Constructor.
     *
     * @param client the http client to use.
     * @param dataSetId the requested dataset id.
     * @param metadata true if the metadata is requested.
     * @param columns true if the columns is requested.
     * @param sample optional sample size (if null or <=0, the full dataset is returned).
     */
    public DataSetGet(HttpClient client, String dataSetId, boolean metadata, boolean columns, Long sample) {
        this(client, dataSetId, metadata, columns);
        this.sample = sample;
    }

    /**
     * @see HystrixCommand#run()
     */
    @Override
    protected InputStream run() throws Exception {
        String url = datasetServiceUrl + "/datasets/" + dataSetId + "/content?metadata=" + metadata + "&columns=" + columns;
        if (sample != null) {
            url += "&sample=" + sample;
        }
        final HttpGet contentRetrieval = new HttpGet(url);
        final HttpResponse response = client.execute(contentRetrieval);
        return handleResponse(response, contentRetrieval);
    }

    /**
     * Handle the response.
     *
     * @param response the http response.
     * @param contentRetrieval the response content retrieval.
     * @return the dataset content.
     * @throws IOException if an error occurs.
     */
    private InputStream handleResponse(final HttpResponse response, final HttpGet contentRetrieval)
            throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_NO_CONTENT) {
            // Immediately release connection
            contentRetrieval.releaseConnection();
            return new ByteArrayInputStream(new byte[0]);
        } else if (statusCode == HttpStatus.SC_OK) {
            return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
        } else if (statusCode >= 400) { // Error (4xx & 5xx codes)
            final ObjectMapper build = builder.build();
            final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
            errorCode.setHttpStatus(statusCode);
            throw new TDPException(errorCode);
        }
        final Exception cause = new Exception(response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase());
        throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_CONTENT, cause, ExceptionContext.build().put("id",
                dataSetId));
    }
}
