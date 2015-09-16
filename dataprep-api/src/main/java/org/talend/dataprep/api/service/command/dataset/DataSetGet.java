package org.talend.dataprep.api.service.command.dataset;

import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.emptyStream;
import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;

/**
 * Command to get a dataset.
 */
@Component
@Scope("request")
public class DataSetGet extends GenericCommand<InputStream> {

    /**
     * Constructor.
     *
     * @param client the http client to use.
     * @param dataSetId the requested dataset id.
     * @param metadata true if the metadata is requested.
     * @param columns true if the columns is requested.
     */
    public DataSetGet(HttpClient client, String dataSetId, boolean metadata, boolean columns) {
        this(client, dataSetId, metadata, columns, null);
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
        super(PreparationAPI.DATASET_GROUP, client);
        execute(() -> {
            String url = datasetServiceUrl + "/datasets/" + dataSetId + "/content?metadata=" + metadata + "&columns=" + columns;
            if (sample != null) {
                url += "&sample=" + sample;
            }
            return new HttpGet(url);
        });
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_CONTENT, e,
                ExceptionContext.build().put("id", dataSetId)));
        on(HttpStatus.NO_CONTENT).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
    }

}
