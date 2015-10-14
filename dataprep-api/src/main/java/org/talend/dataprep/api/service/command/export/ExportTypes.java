package org.talend.dataprep.api.service.command.export;

import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.GenericCommand;

@Component
@Scope("request")
public class ExportTypes extends GenericCommand<InputStream> {

    private ExportTypes(final HttpClient client) {
        super(APIService.TRANSFORM_GROUP, client);
        execute(() -> new HttpGet(this.transformationServiceUrl + "/export/formats"));
        on(HttpStatus.OK).then(pipeStream());
    }

}
