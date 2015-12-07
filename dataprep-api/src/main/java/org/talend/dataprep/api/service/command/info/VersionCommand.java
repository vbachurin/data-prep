package org.talend.dataprep.api.service.command.info;

import static org.talend.dataprep.api.service.command.common.Defaults.emptyStream;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.netflix.hystrix.HystrixCommandGroupKey;

@Component
@Scope("prototype")
public class VersionCommand extends GenericCommand<InputStream> {

    public static final HystrixCommandGroupKey VERSION_GROUP = HystrixCommandGroupKey.Factory.asKey("version"); //$NON-NLS-1$

    private VersionCommand(HttpClient client, String serviceUrl, String serviceName) {
        super(VERSION_GROUP, client);

        execute(() -> {

            String url = serviceUrl + "/version?serviceName=" + serviceName;
            return new HttpGet(url);
        });
        onError(e -> new TDPException(CommonErrorCodes.UNABLE_TO_GET_SERVICE_VERSION, e,
                ExceptionContext.build().put("version", serviceUrl)));
        on(HttpStatus.NO_CONTENT).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
    }

}
