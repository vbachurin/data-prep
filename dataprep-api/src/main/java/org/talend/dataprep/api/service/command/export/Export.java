package org.talend.dataprep.api.service.command.export;

import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.api.ExportParameters;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.fasterxml.jackson.databind.JsonNode;

@Component
@Scope("request")
public class Export extends PreparationCommand<InputStream> {

    private Export(final HttpClient client, final ExportParameters input) {
        super(APIService.TRANSFORM_GROUP, client);
        execute(() -> onExecute(input));
        on(HttpStatus.OK).then(pipeStream());
    }

    /**
     * @param input the export parameters.
     * @return the request to perform.
     */
    private HttpRequestBase onExecute(ExportParameters input) {
        try {
            String name;
            List<Action> actions;
            InputStream content;
            if (StringUtils.isNotBlank(input.getPreparationId())) {
                final Preparation preparation = getPreparation(input.getPreparationId());
                name = preparation.getName();
                actions = getPreparationActions(preparation, input.getStepId());
                content = getDatasetContent(preparation.getDataSetId());
            } else {
                // Get name from data set
                String dataSetId = input.getDatasetId();
                final JsonNode datasetDetails = getDatasetDetails(dataSetId);
                //
                name = datasetDetails.get("metadata").get("name").textValue();
                actions = Collections.emptyList();
                content = getDatasetContent(dataSetId);
            }

            // fileName can come from parameters otherwise we use default preparation or dataset name
            String fileName = input.getArguments().get("exportParameters.fileName");
            if (!StringUtils.isEmpty(fileName)) {
                name = fileName;
            }

            final Map<String, String> inputArguments = input.getArguments();
            inputArguments.put("exportParameters.fileName", name);

            // Get dataset content and execute export service
            final String encodedActions = serializeActions(actions);

            final URI uri = getTransformationUri(input.getExportType(), input.getArguments());
            final HttpPost transformationCall = new HttpPost(uri);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("actions", new StringBody(encodedActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$
                    .addPart("content", new InputStreamBody(content, ContentType.APPLICATION_JSON)) //$NON-NLS-1$
                    .build();

            transformationCall.setEntity(reqEntity);
            return transformationCall;

        } catch (IOException | URISyntaxException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

    /**
     * Create the transformation format uri
     * 
     * @param exportFormat The format type.
     * @param params optional params
     * @return The built URI
     */
    private URI getTransformationUri(final String exportFormat, final Map<String, String> params)
        throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(this.transformationServiceUrl + "/transform/" + exportFormat);

        if (params != null){
            for (Map.Entry<String,String> entry:params.entrySet()){
                uriBuilder.addParameter( entry.getKey(), entry.getValue() );
            }
        }

        return uriBuilder.build();

    }


}
