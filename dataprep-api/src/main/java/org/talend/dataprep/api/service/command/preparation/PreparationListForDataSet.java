package org.talend.dataprep.api.service.command.preparation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * Command used to retreive the preparations used by a given dataset.
 */
@Component
@Scope("request")
public class PreparationListForDataSet extends DataPrepCommand<InputStream> {

    /** The wanted DataSet id. */
    private final String dataSetId;

    /**
     * Private constructor.
     *
     * @param client the http client to use to access the preparation service.
     * @param dataSetId the wanted dataset id.
     */
    private PreparationListForDataSet(HttpClient client, String dataSetId) {
        super(APIService.PREPARATION_GROUP, client);
        this.dataSetId = dataSetId;
    }

    /**
     * @see HystrixCommand#run()
     */
    @Override
    protected InputStream run() throws Exception {
        final HttpGet contentRetrieval = new HttpGet(preparationServiceUrl + "/preparations?dataSetId=" + dataSetId); //$NON-NLS-1$
        final HttpResponse response = client.execute(contentRetrieval);
        final int statusCode = response.getStatusLine().getStatusCode();

        switch(statusCode) {
            case SC_NO_CONTENT:
            case SC_ACCEPTED:
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);

            case SC_OK:
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);

            default:
                contentRetrieval.releaseConnection();
                throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_LIST);
        }
    }

}
