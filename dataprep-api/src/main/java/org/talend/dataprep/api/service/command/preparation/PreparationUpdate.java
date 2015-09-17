package org.talend.dataprep.api.service.command.preparation;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.talend.dataprep.api.APIErrorCodes.UNABLE_TO_UPDATE_PREPARATION;
import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.asString;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
@Scope("request")
public class PreparationUpdate extends GenericCommand<String> {

    private PreparationUpdate(HttpClient client, String id, Preparation preparation) {
        super(APIService.PREPARATION_GROUP, client);
        execute(() -> {
            try {
                final byte[] preparationJSONValue = builder.build().writeValueAsBytes(preparation);
                final HttpPut preparationCreation = new HttpPut(preparationServiceUrl + "/preparations/" + id);
                preparationCreation.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
                preparationCreation.setEntity(new ByteArrayEntity(preparationJSONValue));
                return preparationCreation;
            } catch (JsonProcessingException e) {
                throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        });
        onError(e -> new TDPException(UNABLE_TO_UPDATE_PREPARATION, e, ExceptionContext.build().put("id", id)));
        on(HttpStatus.OK).then(asString());
    }
}
