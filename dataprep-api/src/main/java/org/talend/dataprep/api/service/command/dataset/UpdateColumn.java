package org.talend.dataprep.api.service.command.dataset;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.json.JsonErrorCode;
import org.talend.dataprep.preparation.store.ContentCache;
import org.talend.dataprep.preparation.store.ContentCacheKey;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("request")
public class UpdateColumn extends DataPrepCommand<Void> {

    @Autowired
    ContentCache contentCache;

    private final String dataSetId;
    private final String columnId;
    private final InputStream body;

    private UpdateColumn(final HttpClient client, final String dataSetId, final String columnId, final InputStream body) {
        super(PreparationAPI.DATASET_GROUP, client);
        this.dataSetId = dataSetId;
        this.columnId = columnId;
        this.body = body;
    }

    @Override
    protected Void run() throws Exception {
        final HttpPost contentUpdate = new HttpPost(datasetServiceUrl + "/datasets/" + dataSetId + "/column/" + columnId); //$NON-NLS-1$ //$NON-NLS-2$

        try {
            contentUpdate.setHeader("Content-Type", APPLICATION_JSON_VALUE);
            contentUpdate.setEntity(new InputStreamEntity(body));
            HttpResponse response = client.execute(contentUpdate);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                //TODO : evict all preparations at all steps that has the dataset that we just changed
                final Preparation preparation = Preparation.defaultPreparation(dataSetId);
                final ContentCacheKey key = new ContentCacheKey(preparation.id(), Step.ROOT_STEP.id());
                if (contentCache.has(key)) {
                    contentCache.evict(key);
                }
                return null;
            }
            else if (statusCode >= 400) {
                final ObjectMapper build = builder.build();
                final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
                errorCode.setHttpStatus(statusCode);
                throw new TDPException(errorCode);
            }
        } finally {
            contentUpdate.releaseConnection();
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_OR_UPDATE_DATASET,
                TDPExceptionContext.build().put("id", dataSetId));
    }
}
