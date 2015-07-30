package org.talend.dataprep.api.service.command.dataset;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.preparation.store.ContentCache;

@Component
@Scope("request")
public class UpdateColumn
    extends DataPrepCommand<String> {

    @Autowired
    ContentCache contentCache;

    private final String dataSetId;

    private final InputStream columnContent;

    private UpdateColumn( HttpClient client, String dataSetId, InputStream columnContent ) {
        super(PreparationAPI.DATASET_GROUP, client);
        this.dataSetId = dataSetId;
        this.columnContent = columnContent;
    }

    @Override
    protected String run() throws Exception {
        HttpPost contentUpdate = new HttpPost(datasetServiceUrl + "/datasets/" + dataSetId + "/column"); //$NON-NLS-1$ //$NON-NLS-2$
        contentUpdate.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        try {
            contentUpdate.setEntity(new InputStreamEntity(columnContent));
            HttpResponse response = client.execute(contentUpdate);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200) {
                if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    return StringUtils.EMPTY;
                } else if (statusCode == HttpStatus.SC_OK) {

                    // do this async?
                    final Preparation preparation = Preparation.defaultPreparation(dataSetId);
                    if (contentCache.has(preparation.id(), Step.ROOT_STEP.id())) {
                        contentCache.evict( preparation.id(), Step.ROOT_STEP.id());
                    }

                    return IOUtils.toString(response.getEntity().getContent());
                }
            }
        } finally {
            contentUpdate.releaseConnection();
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_OR_UPDATE_DATASET,
                               TDPExceptionContext.build().put("id",dataSetId));
    }
}
