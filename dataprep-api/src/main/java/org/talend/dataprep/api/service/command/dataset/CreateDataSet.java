package org.talend.dataprep.api.service.command.dataset;

import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.asString;
import static org.talend.dataprep.api.service.command.common.GenericCommand.Defaults.emptyString;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

/**
 * Command used to create a dataset. Basically pass through all data to the DataSet low level API.
 */
@Component
@Scope("request")
public class CreateDataSet extends GenericCommand<String> {

    /**
     * Default constructor.
     *
     * @param client http client.
     * @param name name of the dataset.
     * @param contentType content-type of the dataset.
     * @param dataSetContent Dataset content or import parameters in json for remote datasets.
     */
    private CreateDataSet(HttpClient client, String name, String contentType, InputStream dataSetContent) {
        super(PreparationAPI.DATASET_GROUP, client);
        execute(() -> {
            try {
                final HttpPost post = new HttpPost(datasetServiceUrl + "/datasets/?name=" + URLEncoder.encode(name, "UTF-8"));//$NON-NLS-1$ //$NON-NLS-2$
                post.addHeader("Content-Type", contentType); //$NON-NLS-1$
                post.setEntity(new InputStreamEntity(dataSetContent));
                return post;
            } catch (UnsupportedEncodingException e) {
                throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        });
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_CREATE_DATASET, e));
        on(HttpStatus.NO_CONTENT).then(emptyString());
        on(HttpStatus.ACCEPTED).then(emptyString());
        on(HttpStatus.OK).then(asString());
    }
}
