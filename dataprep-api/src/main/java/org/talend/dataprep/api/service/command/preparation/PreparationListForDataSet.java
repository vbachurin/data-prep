package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.api.service.command.common.Defaults.emptyStream;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;

/**
 * Command used to retrieve the preparations used by a given dataset.
 */
@Component
@Scope("request")
public class PreparationListForDataSet extends GenericCommand<InputStream> {

    /**
     * Private constructor.
     *
     * @param client the http client to use to access the preparation service.
     * @param dataSetId the wanted dataset id.
     */
    private PreparationListForDataSet(HttpClient client, String dataSetId) {
        super(APIService.PREPARATION_GROUP, client);
        execute(() -> {
            return new HttpGet(preparationServiceUrl + "/preparations?dataSetId=" + dataSetId); //$NON-NLS-1$
        });
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_LIST, e));
        on(HttpStatus.NO_CONTENT, HttpStatus.ACCEPTED).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
    }

}
