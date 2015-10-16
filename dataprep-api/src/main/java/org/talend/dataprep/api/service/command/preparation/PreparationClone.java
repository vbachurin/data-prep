package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.api.service.command.common.Defaults.asNull;

import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

@Component
@Scope("request")
public class PreparationClone
    extends GenericCommand<String> {

    private PreparationClone( HttpClient client, String id, String name ) {
        super(APIService.PREPARATION_GROUP, client);
        execute(() -> onExecute(id, name)); // $NON-NLS-1$
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_CREATE_PREPARATION, e));
        on(HttpStatus.OK).then(asNull());
    }

    private HttpRequestBase onExecute(String id, String name) {
        try {
            URIBuilder uriBuilder = new URIBuilder(preparationServiceUrl + "/preparations/clone/" + id);
            if (StringUtils.isNotEmpty(name)) {
                uriBuilder.addParameter("name", name);
            }
            return new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_PREPARATION, e);
        }
    }
}
