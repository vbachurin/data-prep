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
import org.talend.dataprep.exception.error.CommonErrorCodes;

@Component
@Scope("request")
public class PreparationList extends GenericCommand<InputStream> {

    private PreparationList(HttpClient client, Format format) {
        super(APIService.PREPARATION_GROUP, client);
        execute(() -> {
            switch (format) {
            case SHORT:
                return new HttpGet(preparationServiceUrl + "/preparations"); //$NON-NLS-1$
            case LONG:
                return new HttpGet(preparationServiceUrl + "/preparations/all"); //$NON-NLS-1$
            default:
                throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_REQUEST,
                        new IllegalArgumentException("Unsupported format: " + format));
            }
        });
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_LIST, e));
        on(HttpStatus.NO_CONTENT, HttpStatus.ACCEPTED).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
    }

    public enum Format {
        SHORT,
        LONG
    }
}
