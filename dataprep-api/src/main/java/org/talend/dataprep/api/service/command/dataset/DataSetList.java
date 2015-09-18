package org.talend.dataprep.api.service.command.dataset;

import static org.talend.dataprep.api.service.command.common.Defaults.emptyStream;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;

@Component
@Scope("request")
public class DataSetList extends GenericCommand<InputStream> {

    private DataSetList(HttpClient client, String sort, String order) {
        super(PreparationAPI.TRANSFORM_GROUP, client);
        execute(() -> new HttpGet(datasetServiceUrl + "/datasets?sort=" + sort + "&order=" + order));
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_LIST_DATASETS, e));
        on(HttpStatus.NO_CONTENT, HttpStatus.ACCEPTED).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
    }

}
