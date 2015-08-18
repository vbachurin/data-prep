package org.talend.dataprep.api.service.command.dataset;

import java.io.ByteArrayInputStream;
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

@Component
@Scope("request")
public class DataSetList extends DataPrepCommand<InputStream> {

    private final HttpClient client;

    private final String sort;

    private final String order;

    private DataSetList(HttpClient client, String sort, String order) {
        super(PreparationAPI.TRANSFORM_GROUP, client);
        this.client = client;
        this.sort = sort;
        this.order = order;
    }

    @Override
    protected InputStream run() throws Exception {
        HttpGet contentRetrieval = new HttpGet(datasetServiceUrl + "/datasets?sort=" + sort + "&order=" + order);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            if (statusCode == HttpStatus.SC_NO_CONTENT || statusCode == HttpStatus.SC_ACCEPTED) {
                return new ByteArrayInputStream(new byte[0]);
            } else if (statusCode == HttpStatus.SC_OK) {
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);
            }
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_DATASETS);
    }
}
