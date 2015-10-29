package org.talend.dataprep.transformation.api.action.metadata.datablending;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.STRING;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;
import org.talend.dataprep.transformation.api.action.metadata.common.AbstractActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.DataSetAction;
import org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters;
import org.talend.dataprep.transformation.api.action.parameters.ColumnParameter;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 *
 */
@Component(Lookup.ACTION_BEAN_PREFIX + Lookup.LOOKUP_ACTION_NAME)
public class Lookup extends AbstractActionMetadata implements DataSetAction {

    /** The action name. */
    public static final String LOOKUP_ACTION_NAME = "lookup"; //$NON-NLS-1$

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Lookup.class);

    /** Lookup parameters */
    protected enum PARAMETERS {
                               lookup_ds_name,
                               lookup_ds_id,
                               lookup_ds_url,
                               lookup_join_on,
                               lookup_selected_cols
    }

    /** The dataprep ready jackson builder. */
    // @Autowired(required = false)
    private Jackson2ObjectMapperBuilder builder;

    /** Adapted value of the name parameter. */
    private String adaptedNameValue = EMPTY;
    /** Adapted value of the dataset_id parameter. */
    private String adaptedDatasetIdValue = EMPTY;
    /** Adapted value of the url parameter. */
    private String adaptedUrlValue = EMPTY;

    /** Http connection manager. */
    private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    /** The http client to use. */
    private CloseableHttpClient httpClient;

    public Lookup() {
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(10);
        httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }

    /**
     * Clean the connection manager before shutting down.
     */
    @PreDestroy
    private void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOGGER.error("Unable to close HTTP client on shutdown.", e);
        }
        this.connectionManager.shutdown();
    }

    /**
     * @return A unique name used to identify action.
     */
    @Override
    public String getName() {
        return LOOKUP_ACTION_NAME;
    }

    /**
     * @return A 'category' for the action used to group similar actions (eg. 'math', 'repair'...).
     */
    @Override
    public String getCategory() {
        return ActionCategory.DATA_BLENDING.getDisplayName();
    }

    /**
     * @see ActionMetadata#getParameters()
     */
    @Override
    public List<Parameter> getParameters() {
        final List<Parameter> parameters = ImplicitParameters.getParameters();
        parameters.add(new Parameter(PARAMETERS.lookup_ds_name.name(), STRING, adaptedNameValue, false, false));
        parameters.add(new Parameter(PARAMETERS.lookup_ds_id.name(), STRING, adaptedDatasetIdValue, false, false));
        parameters.add(new Parameter(PARAMETERS.lookup_ds_url.name(), STRING, adaptedUrlValue, false, false));
        parameters.add(new Parameter(PARAMETERS.lookup_join_on.name(), STRING, EMPTY, false, false));
        parameters.add(
                new ColumnParameter(PARAMETERS.lookup_selected_cols.name(), EMPTY, false, false, Collections.emptyList(), true));
        return parameters;
    }

    /**
     * @see ActionMetadata#acceptColumn(ColumnMetadata)
     */
    @Override
    public boolean acceptColumn(ColumnMetadata column) {
        // because this is a specific action, suggestion will be handled by the API. Hence, default value is false.
        return false;
    }


    /**
     * Adapt the parameters default values according to the given dataset.
     *
     * @param dataset the dataset to adapt the parameters value from.
     * @param datasetUrl the dataset url to use in parameters.
     */
    public void adapt(DataSetMetadata dataset, String datasetUrl) {
        adaptedNameValue = dataset.getName();
        adaptedDatasetIdValue = dataset.getId();
        adaptedUrlValue = datasetUrl;
    }

    /**
     * @see DataSetAction#applyOnDataSet(DataSetRow, TransformationContext, Map)
     */
    @Override
    public void applyOnDataSet(DataSetRow row, TransformationContext context, Map<String, String> parameters) {

        // read parameters
        final String rowId = parameters.get(ImplicitParameters.ROW_ID.getKey());
        final String joinValue = row.get(rowId);
        final String joinOn = parameters.get(PARAMETERS.lookup_join_on.name());

        // get the matching lookup row
        final DataSet lookup = getLookupContent(parameters);
        DataSetRow matchingRow = getLookupRow(lookup, joinOn, joinValue);

        final List<String> colsToAdd = getColsToAdd(parameters);
        colsToAdd.forEach(toAdd -> {
            // update metadata
            final String newColId = row.getRowMetadata().insertAfter(rowId, matchingRow.getRowMetadata().getById(toAdd));
            // insert new row value
            row.set(newColId, matchingRow.get(toAdd));
        });

    }

    private DataSetRow getLookupRow(DataSet lookup, String joinOn, String joinValue) {
        return null;
    }

    private List<String> getColsToAdd(Map<String, String> parameters) {
        final String cols = parameters.get(PARAMETERS.lookup_selected_cols.name());
        return Arrays.asList(cols.split(","));
    }

    private DataSet getLookupContent(Map<String, String> parameters) {
        final String url = parameters.get(PARAMETERS.lookup_ds_url.name());
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response;
        try {

            response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new IOException("error reading dataset lookup" + url + " -> " + response.getStatusLine());
            }

            final DataSet lookup = builder.build().reader(DataSet.class).readValue(response.getEntity().getContent());
            LOGGER.debug("Lookup dataset #{} - {} read from {} ", lookup.getMetadata().getId(), lookup.getMetadata().getName(),
                    url);
            return lookup;

        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_READ_LOOKUP_DATASET, e);
        }
    }
}
