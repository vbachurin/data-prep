package org.talend.dataprep.api.service.command.aggregation;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.api.service.command.dataset.DataSetGet;
import org.talend.dataprep.api.service.command.preparation.PreparationGet;
import org.talend.dataprep.api.service.command.preparation.PreparationGetContent;
import org.talend.dataprep.cache.ContentCache;
import org.talend.dataprep.cache.ContentCacheKey;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.json.JsonErrorCode;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Aggregate command. Take the content of the dataset or preparation before sending it to the transformation service.
 */
@Component
@Scope("request")
public class Aggregate extends DataPrepCommand<InputStream> {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(Aggregate.class);

    /** The aggregation parameters. */
    private AggregationParameters parameters;

    /** The content cache to get preparation content faster, if available. */
    @Autowired
    protected ContentCache contentCache;

    /**
     * Default constructor.
     *
     * @param client the http client to use.
     */
    public Aggregate(final HttpClient client, final AggregationParameters parameters) {
        super(APIService.TRANSFORM_GROUP, client);
        this.parameters = parameters;
    }

    /**
     * @see DataPrepCommand#run()
     */
    @Override
    protected InputStream run() throws Exception {

        // get the content to work on
        InputStream content = null;
        if (parameters.getDatasetId() != null) {
            content = getDataSetContent(client, parameters.getDatasetId(), parameters.getSampleSize());
        } else {
            content = getPreparationContent(client, parameters.getPreparationId(), parameters.getStepId(),
                    parameters.getSampleSize());
        }

        // call the transformation service to compute the aggregation
        String uri = transformationServiceUrl + "/aggregate"; //$NON-NLS-1$
        HttpPost aggregateCall = new HttpPost(uri);

        String paramsAsJson = builder.build().writer().writeValueAsString(parameters);

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("parameters", new StringBody(paramsAsJson, ContentType.APPLICATION_JSON.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("content", new InputStreamBody(content, ContentType.APPLICATION_JSON)) //$NON-NLS-1$
                .build();
        aggregateCall.setEntity(reqEntity);

        try {
            HttpResponse response = client.execute(aggregateCall);
            int statusCode = response.getStatusLine().getStatusCode();

            // 400 and 500 errors
            if (statusCode >= 400) {
                final ObjectMapper build = builder.build();
                final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
                errorCode.setHttpStatus(statusCode);
                throw new TDPException(errorCode);
            }

            InputStream result = response.getEntity().getContent();
            return new ReleasableInputStream(result, aggregateCall::releaseConnection);

        } catch (TDPException tdpe) {
            throw tdpe;
        } catch (Exception e) {
            LOG.error("exception while processing aggregation : " + e.getMessage(), e);
            throw new TDPException(CommonErrorCodes.UNABLE_TO_AGGREGATE, e);
        }

    }

    /**
     * Return the content of the wanted preparation as input stream.
     *
     * @param preparationId the preparation
     * @param stepId
     * @return
     */
    private InputStream getPreparationContent(HttpClient client, String preparationId, String stepId, Long sampleSize) {

        // clean the step parameter
        String step = stepId;
        if (StringUtils.isEmpty(stepId)) {
            step = "head"; //$NON-NLS-1$
        }

        // get the preparation first
        final Preparation preparation = getPreparation(preparationId);

        // get it from the cache if it's available
        ContentCacheKey cacheKey = new ContentCacheKey(preparation, step, sampleSize);
        if (contentCache.has(cacheKey)) {
            return contentCache.get(cacheKey);
        }

        // call the preparation service
        PreparationGetContent command = context.getBean(PreparationGetContent.class, client, preparationId, step, sampleSize);
        return command.execute();
    }

    /**
     * Return the preparation from the preparation service.
     *
     * @param preparationId the wanted preparation.
     * @return the preparation.
     */
    private Preparation getPreparation(String preparationId) {

        final PreparationGet getPreparation = context.getBean(PreparationGet.class, client, preparationId);
        try {
            InputStream temp = getPreparation.execute();
            return builder.build().reader(Preparation.class).readValue(temp);
        } catch (IOException e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_GET_PREPARATION_DETAILS, e);
        }
    }

    /**
     * Return the dataset content as input stream.
     *
     * @param datasetId the wanted dataset id.
     * @return the wanted dataset content.
     */
    private InputStream getDataSetContent(HttpClient client, String datasetId, Long sampleSize) {
        final DataSetGet retrieveDataSet = context.getBean(DataSetGet.class, //
                client, //
                datasetId, //
                false, // metadata
                false, // columns
                sampleSize);
        return retrieveDataSet.execute();
    }

}
