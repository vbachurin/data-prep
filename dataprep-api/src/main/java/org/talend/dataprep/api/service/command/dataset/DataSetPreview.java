package org.talend.dataprep.api.service.command.dataset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class DataSetPreview extends DataPrepCommand<InputStream> {

    @Value("${http.retry.pause}")
    public int PAUSE;

    @Value("${http.retry.max_retry}")
    private int MAX_RETRY;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetPreview.class);

    private final String dataSetId;

    private final boolean metadata;

    private final boolean columns;

    private final String sheetName;

    private int retryCount = 0;

    public DataSetPreview(HttpClient client, String dataSetId, boolean metadata, boolean columns, String sheetName) {
        super(PreparationAPI.TRANSFORM_GROUP, client);

        this.dataSetId = dataSetId;
        this.metadata = metadata;
        this.columns = columns;
        this.sheetName = sheetName;
    }

    @Override
    protected InputStream run() throws Exception {

        StringBuilder url = new StringBuilder(datasetServiceUrl + "/datasets/" + dataSetId + "/preview/?metadata=" + metadata
                + "&columns=" + columns);

        if (StringUtils.isNotEmpty(sheetName)) {
            // yup this sheet name can contains weird characters space, great french accents or even chinese characters
            url.append("&sheetName=" + URLEncoder.encode(sheetName, "UTF-8"));
        }

        final HttpGet contentRetrieval = new HttpGet(url.toString());
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
            } else if (statusCode == HttpStatus.SC_ACCEPTED) {
                // Data set exists, but content isn't yet analyzed, retry request
                retryCount++;
                if (retryCount > MAX_RETRY) {
                    LOGGER.error("Failed to retrieve data set content after {} tries.", retryCount);
                    throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_CONTENT_DATASET_NOT_READY,
                            TDPExceptionContext.build().put("id", dataSetId));
                }
                // Pause before retry
                final int pauseTime = PAUSE * retryCount;
                LOGGER.debug("Data set #{} content is not ready, pausing for {} ms.", dataSetId, pauseTime);
                try {
                    TimeUnit.MILLISECONDS.sleep(pauseTime);
                } catch (InterruptedException e) {
                    throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_CONTENT, e, TDPExceptionContext.build().put(
                            "id", dataSetId));
                }
                return handleResponse(client.execute(contentRetrieval), contentRetrieval);
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
