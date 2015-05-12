package org.talend.dataprep.api.service.command.preparation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

import com.netflix.hystrix.HystrixCommand;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

@Component
@Scope("request")
public class PreparationList extends DataPrepCommand<InputStream> {

    private final Format format;

    private PreparationList(HttpClient client, Format format) {
        super(APIService.PREPARATION_GROUP, client);
        this.format = format;
    }

    @Override
    protected InputStream run() throws Exception {
        final HttpGet contentRetrieval = getContentRetrieval(this.format);
        final HttpResponse response = client.execute(contentRetrieval);
        final int statusCode = response.getStatusLine().getStatusCode();

        switch(statusCode) {
            case SC_NO_CONTENT:
            case SC_ACCEPTED:
                contentRetrieval.releaseConnection();
                return new ByteArrayInputStream(new byte[0]);

            case SC_OK:
                return new ReleasableInputStream(response.getEntity().getContent(), contentRetrieval::releaseConnection);

            default:
                contentRetrieval.releaseConnection();
                throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_PREPARATION_LIST);
        }
    }

    private HttpGet getContentRetrieval(final Format format) throws IOException {
        switch (format) {
            case SHORT:
                return new HttpGet(preparationServiceUrl + "/preparations"); //$NON-NLS-1$
            case LONG:
                return new HttpGet(preparationServiceUrl + "/preparations/all"); //$NON-NLS-1$
            default:
                throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_REQUEST, new IllegalArgumentException("Unsupported format: " + format));
        }
    }

    public enum Format {
        SHORT,
        LONG
    }
}
