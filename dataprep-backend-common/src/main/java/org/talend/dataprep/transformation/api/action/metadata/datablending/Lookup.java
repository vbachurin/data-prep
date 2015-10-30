package org.talend.dataprep.transformation.api.action.metadata.datablending;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.COLUMN_ID;
import static org.talend.dataprep.transformation.api.action.metadata.datablending.Lookup.PARAMETERS.*;
import static org.talend.dataprep.transformation.api.action.parameters.ParameterType.STRING;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.annotation.PreDestroy;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.DataSetAction;
import org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters;
import org.talend.dataprep.transformation.api.action.parameters.ColumnParameter;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
@Component(Lookup.ACTION_BEAN_PREFIX + Lookup.LOOKUP_ACTION_NAME)
public class Lookup extends ActionMetadata implements DataSetAction {

    /** The action name. */
    public static final String LOOKUP_ACTION_NAME = "lookup"; //$NON-NLS-1$

    /** This class' logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Lookup.class);

    /** Lookup parameters */
    protected enum PARAMETERS {
                               LOOKUP_DS_NAME,
                               LOOKUP_DS_ID,
                               LOOKUP_DS_URL,
                               LOOKUP_JOIN_ON,
                               LOOKUP_JOIN_ON_NAME, // needed to display human friendly parameters
                               LOOKUP_SELECTED_COLS;

        public String getKey() {
            return this.name().toLowerCase();
        }
    }

    /** The dataprep ready jackson builder. */
    @Autowired
    @Lazy
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
        parameters.add(new Parameter(LOOKUP_DS_NAME.getKey(), STRING, adaptedNameValue, false, false));
        parameters.add(new Parameter(LOOKUP_DS_ID.getKey(), STRING, adaptedDatasetIdValue, false, false));
        parameters.add(new Parameter(LOOKUP_DS_URL.getKey(), STRING, adaptedUrlValue, false, false));
        parameters.add(new Parameter(LOOKUP_JOIN_ON.getKey(), STRING, EMPTY, false, false));
        parameters.add(new Parameter(LOOKUP_JOIN_ON_NAME.getKey(), STRING, EMPTY, false, false));
        // TODO see how serialize multiple column selection in a string...
        parameters.add(
new ColumnParameter(LOOKUP_SELECTED_COLS.name(), EMPTY, false, false, Collections.emptyList(), true));
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
        final String columnId = parameters.get(COLUMN_ID.getKey());
        final String joinValue = row.get(columnId);
        final String joinOn = parameters.get(LOOKUP_JOIN_ON.getKey());

        // get the matching lookup row
        final ObjectMapper mapper = builder.build();
        try (JsonParser parser = mapper.getFactory().createParser(getLookupContent(parameters))) {
            final DataSet lookup = mapper.reader(DataSet.class).readValue(parser);

            LOGGER.debug("lookup dataset #{} - {} loaded", lookup.getMetadata().getId(), lookup.getMetadata().getName());
            DataSetRow matchingRow = getLookupRow(lookup, joinOn, joinValue);

            final List<String> colsToAdd = getColsToAdd(parameters);
            colsToAdd.forEach(toAdd -> {
                // update metadata
                final ColumnMetadata colMetadata = ColumnMetadata.Builder.column()
                        .copy(matchingRow.getRowMetadata().getById(toAdd)).computedId(null).build();
                final String newColId = row.getRowMetadata().insertAfter(columnId, colMetadata);
                // insert new row value
                row.set(newColId, matchingRow.get(toAdd));
            });

        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_READ_LOOKUP_DATASET, e);
        }

    }

    private DataSetRow getLookupRow(DataSet lookup, String joinOn, String joinValue) {
        final Optional<DataSetRow> found = lookup.getRecords() //
                .filter(row -> StringUtils.equals(joinValue, row.get(joinOn))) //
                .findFirst();
        return found.isPresent() ? found.get() : null;
    }


    private List<String> getColsToAdd(Map<String, String> parameters) {
        final String cols = parameters.get(LOOKUP_SELECTED_COLS.getKey());
        return Arrays.asList(cols.split(","));
    }

    private InputStream getLookupContent(Map<String, String> parameters) {
        final String url = parameters.get(LOOKUP_DS_URL.getKey());
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response;
        try {

            response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new IOException("error reading dataset lookup " + url + " -> " + response.getStatusLine());
            }

            LOGGER.debug("Lookup dataset read from {} ", url);
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_READ_LOOKUP_DATASET, e);
        }
    }
}
