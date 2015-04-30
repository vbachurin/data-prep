package org.talend.dataprep.api.service.command;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import org.talend.dataprep.exception.TDPExceptionContext;

/**
 * Delete the dataset if it's not used by any preparation.
 */
@Component
@Scope("request")
public class DataSetDelete extends HystrixCommand<Void> {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(DataSetDelete.class);

    /** Base url for the dataset service api. */
    private final String dataSetServiceBaseUrl;

    /** Base url for the preparation service api. */
    private final String preparationServiceBaseUrl;

    /** Http client for rest api calls. */
    private final HttpClient client;

    /** The dataset id to delete. */
    private final String dataSetId;

    /** The spring web context. */
    @Autowired
    private WebApplicationContext context;

    /** Jackson object parser needed to read the */
    @Autowired(required = true)
    private Jackson2ObjectMapperBuilder builder;

    /**
     * Default constructor.
     *
     * @param client Http client for rest api calls.
     * @param dataSetServiceBaseUrl Base url for the dataset service api.
     * @param preparationServiceBaseUrl Base url for the preparation service api.
     * @param dataSetId The dataset id to delete.
     */
    private DataSetDelete(HttpClient client, String dataSetServiceBaseUrl, String preparationServiceBaseUrl, String dataSetId) {
        super(PreparationAPI.DATASET_GROUP);
        this.client = client;
        this.dataSetServiceBaseUrl = dataSetServiceBaseUrl;
        this.preparationServiceBaseUrl = preparationServiceBaseUrl;
        this.dataSetId = dataSetId;

    }

    /**
     * @see HystrixCommand#run()
     */
    @Override
    protected Void run() throws Exception {

        List<Preparation> preparations = getPreparationsForDataSet();

        // if the dataset is used by preparation(s), the deletion is forbidden
        if (preparations.size() > 0) {
            LOG.debug("DataSet {} is used by {} preparation(s) and cannot be deleted", dataSetId, preparations.size());
            throw new TDPException(APIErrorCodes.UNABLE_TO_DELETE_DATASET,
                    null,
                    TDPExceptionContext.build().put("dataSetId", dataSetId).put("preparations", preparations));
        }

        return doDeleteDataSet();
    }

    /**
     * @return List of preparation(s) that use this dataset or en empty list if there's none.
     */
    private List<Preparation> getPreparationsForDataSet() throws Exception {

        // call preparation api
        PreparationListForDataSet preparationsForDataSet = context.getBean(PreparationListForDataSet.class, client,
                preparationServiceBaseUrl, dataSetId);
        InputStream jsonInput = preparationsForDataSet.execute();

        // parse and return the response
        ObjectMapper mapper = builder.build();
        return mapper.readValue(jsonInput, new TypeReference<List<Preparation>>() {
        });
    }

    /**
     * Actual method that deletes the DataSet.
     * 
     * @throws Exception if an error occurs.
     */
    private Void doDeleteDataSet() throws Exception {
        HttpDelete contentRetrieval = new HttpDelete(dataSetServiceBaseUrl + "/" + dataSetId);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_DELETE_DATASET, null, TDPExceptionContext.build().put("dataSetId", dataSetId));
    }

}
