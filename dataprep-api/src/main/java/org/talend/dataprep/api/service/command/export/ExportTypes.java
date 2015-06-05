package org.talend.dataprep.api.service.command.export;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;

@Component
@Scope("request")
public class ExportTypes extends DataPrepCommand<InputStream> {

    protected ExportTypes(final HttpClient client) {
        super(APIService.TRANSFORM_GROUP, client);
    }

    @Override
    protected InputStream run() throws Exception {
        HttpGet get = new HttpGet(this.transformationServiceUrl + "/export/types");

        HttpResponse response = client.execute(get);

        return new ReleasableInputStream(response.getEntity().getContent(), get::releaseConnection);

    }
}
