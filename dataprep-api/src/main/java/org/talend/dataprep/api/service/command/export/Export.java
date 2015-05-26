package org.talend.dataprep.api.service.command.export;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.api.ExportParameters;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.api.type.ExportType;

import com.fasterxml.jackson.databind.JsonNode;

@Component
@Scope("request")
public class Export extends PreparationCommand<InputStream> {

    private final ExportParameters input;

    private final HttpServletResponse response;

    private Export(final HttpClient client, final ExportParameters input, final HttpServletResponse response) {
        super(APIService.PREPARATION_GROUP, client);
        this.input = input;
        this.response = response;
    }

    @Override
    protected InputStream run() throws Exception {
        String dataSetId;
        String encodedActions = null;
        String name;

        // Get dataset id and actions from preparation
        if (StringUtils.isNotBlank(input.getPreparationId())) {
            final JsonNode preparationDetails = getPreparationDetails(input.getPreparationId());

            final List<String> currentStepsIds = getActionsStepIds(preparationDetails, input.getStepId());
            final Map<String, Action> actions = getActions(preparationDetails, currentStepsIds);

            dataSetId = preparationDetails.get("dataSetId").textValue();
            encodedActions = serializeAndEncode(actions);
            name = preparationDetails.get("name").textValue();
        }
        // Get provided dataset id
        else {
            dataSetId = input.getDatasetId();
            final JsonNode datasetDetails = getDatasetDetails(dataSetId);
            name = datasetDetails.get("metadata").get("name").textValue();
        }

        // Set response headers
        response.setContentType(input.getExportType().getMimeType());
        response.setHeader("Content-Disposition", "attachment; filename=" + name + input.getExportType().getEntension());

        // Get dataset content and call export service
        final String uri = getTransformationUri(input.getExportType(), input.getCsvSeparator(), encodedActions);
        final HttpPost transformationCall = new HttpPost(uri);
        final InputStream content = getDatasetContent(dataSetId);
        transformationCall.setEntity(new InputStreamEntity(content));

        return new ReleasableInputStream(client.execute(transformationCall).getEntity().getContent(),
                transformationCall::releaseConnection);
    }

    /**
     * Create the transformation export uri
     * 
     * @param exportType The export type.
     * @param csvSeparator The CSV separator.
     * @param encodedActions The encoded actions.
     * @return The built URI
     */
    private String getTransformationUri(final ExportType exportType, final Character csvSeparator, final String encodedActions) {
        String result = this.transformationServiceUrl + "/transform/" + exportType;
        boolean hasQueryParams = false;

        if (csvSeparator != null) {
            result = appendQueryParam(result, "separator=" + encode(csvSeparator.toString()), hasQueryParams);
            hasQueryParams = true;
        }

        if (encodedActions != null) {
            result = appendQueryParam(result, "actions=" + encodedActions, hasQueryParams);
            hasQueryParams = true;
        }

        return result;
    }

    /**
     * Append a query param to an url
     * 
     * @param url The base url
     * @param param The param to append
     * @param alreadyHasParams True if url already have query params
     * @return The url with query param
     */
    private String appendQueryParam(final String url, final String param, final boolean alreadyHasParams) {
        return url + (alreadyHasParams ? "&" : "?") + param;
    }

}
