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
        super(APIService.TRANSFORM_GROUP, client);
        this.input = input;
        this.response = response;
    }

    @Override
    protected InputStream run() throws Exception {
        String name;
        List<Action> actions;
        InputStream content;
        if (StringUtils.isNotBlank(input.getPreparationId())) {
            // Get name from preparation (preparation is set)
            final PreparationContext context = getContext(input.getPreparationId(), input.getStepId());
            //
            name = context.getPreparation().getName();
            actions = context.getActions();
            content = context.getContent();
        } else {
            // Get name from data set
            final PreparationContext context = getContext(input.getPreparationId(), input.getStepId());
            String dataSetId = input.getDatasetId();
            final JsonNode datasetDetails = getDatasetDetails(dataSetId);
            //
            name = datasetDetails.get("metadata").get("name").textValue();
            actions = context.getActions();
            content = getDatasetContent(dataSetId);
        }
        // Set response headers
        response.setContentType(input.getExportType().getMimeType());
        response.setHeader("Content-Disposition", "attachment; filename=" + name + input.getExportType().getExtension());
        // Get dataset content and call export service
        final String encodedActions = serialize(actions);
        final String uri = getTransformationUri(input.getExportType(), input.getArguments(), encodedActions);
        final HttpPost transformationCall = new HttpPost(uri);
        transformationCall.setEntity(new InputStreamEntity(content));
        final InputStream transformedContent = client.execute(transformationCall).getEntity().getContent();
        return new ReleasableInputStream(transformedContent, transformationCall::releaseConnection);
    }

    /**
     * Create the transformation export uri
     * 
     * @param exportType The export type.
     * @param params optional params
     * @param encodedActions The encoded actions.
     * @return The built URI
     */
    private String getTransformationUri(final ExportType exportType, final Map<String,String> params, final String encodedActions) {
        String result = this.transformationServiceUrl + "/export/" + exportType;
        boolean hasQueryParams = false;

        if (params != null ) {
            for (Map.Entry<String,String> entry:params.entrySet()){
                result = appendQueryParam( result, entry.getKey() + "=" + encode( entry.getValue() ), hasQueryParams );
                hasQueryParams = true;
            }
        }

        if (encodedActions != null) {
            result = appendQueryParam(result, "actions=" + encodedActions, hasQueryParams);
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
