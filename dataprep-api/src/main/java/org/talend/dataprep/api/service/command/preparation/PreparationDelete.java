package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.asNull;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;

@Component
@Scope("request")
public class PreparationDelete extends GenericCommand<String> {

    private PreparationDelete(HttpClient client, String id) {
        super(APIService.PREPARATION_GROUP, client);
        execute(() -> {
            return new HttpDelete(preparationServiceUrl + "/preparations/" + id); //$NON-NLS-1$
        });
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_DELETE_PREPARATION, e));
        on(HttpStatus.OK).then(asNull());
    }

}
