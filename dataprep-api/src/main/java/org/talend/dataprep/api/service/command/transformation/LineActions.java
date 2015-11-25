package org.talend.dataprep.api.service.command.transformation;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

import java.io.InputStream;

import static org.springframework.http.HttpStatus.OK;
import static org.talend.dataprep.api.service.APIService.TRANSFORM_GROUP;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

/**
 * Return all actions that can be performed on lines.
 */
@Component
@Scope("request")
public class LineActions extends GenericCommand<InputStream> {

    /**
     * Constructor.
     *
     * @param client the http client.
     */
    private LineActions(final HttpClient client) {
        super(TRANSFORM_GROUP, client);
        execute(() -> new HttpGet(transformationServiceUrl + "/actions/line"));
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_SUGGESTED_ACTIONS, e));
        on(OK).then(pipeStream());
    }

}
