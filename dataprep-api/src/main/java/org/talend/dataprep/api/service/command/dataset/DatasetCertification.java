package org.talend.dataprep.api.service.command.dataset;

import static org.talend.dataprep.api.service.command.common.Defaults.asNull;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

@Component
@Scope("request")
public class DatasetCertification extends GenericCommand<Void> {

    private DatasetCertification(HttpClient client, String dataSetId) {
        super(PreparationAPI.DATASET_GROUP, client);
        execute(() -> new HttpPut(datasetServiceUrl + "/datasets/" + dataSetId + "/processcertification"));
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_CERTIFY_DATASET, e));
        on(HttpStatus.OK).then(asNull());
    }
}
