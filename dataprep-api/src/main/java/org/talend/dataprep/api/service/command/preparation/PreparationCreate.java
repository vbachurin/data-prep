package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.asString;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;

@Component
@Scope("request")
public class PreparationCreate extends GenericCommand<String> {

    private PreparationCreate(HttpClient client, Preparation preparation) {
        super(APIService.PREPARATION_GROUP, client);
        execute(() -> {
            HttpPut preparationCreation = new HttpPut(preparationServiceUrl + "/preparations");
            // Serialize preparation using configured serialization
            preparationCreation.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            try {
                byte[] preparationJSONValue = builder.build().writeValueAsBytes(preparation);
                preparationCreation.setEntity(new ByteArrayEntity(preparationJSONValue));
            } catch (IOException e) {
                throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_PREPARATION, e);
            }
            return preparationCreation;
        });
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_CREATE_PREPARATION, e));
        on(HttpStatus.OK).then(asString());
    }

}
