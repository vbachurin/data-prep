package org.talend.dataprep.api.service.command.preparation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.service.APIService;
import org.talend.dataprep.api.service.command.common.PreparationCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.json.JsonErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.toList;

@Component
@Scope("request")
public class PreparationAddAction extends PreparationCommand<Void> {

    private final AppendStep step;

    private final String preparationId;

    private PreparationAddAction(final HttpClient client, final String preparationId, final AppendStep step) {
        super(APIService.PREPARATION_GROUP, client);
        this.step = step;
        this.preparationId = preparationId;
    }

    @Override
    protected Void run() throws Exception {
        final StepDiff diff = getDiffMetadata();
        step.setDiff(diff);

        final HttpPost actionAppend = new HttpPost(preparationServiceUrl + "/preparations/" + preparationId + "/actions"); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            final String stepAsString = getObjectMapper().writeValueAsString(step);
            final InputStream stepInputStream = new ByteArrayInputStream(stepAsString.getBytes());

            actionAppend.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)); //$NON-NLS-1$
            actionAppend.setEntity(new InputStreamEntity(stepInputStream));
            final HttpResponse response = client.execute(actionAppend);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                final ObjectMapper build = builder.build();
                final JsonErrorCode errorCode = build.reader(JsonErrorCode.class).readValue(response.getEntity().getContent());
                errorCode.setHttpStatus(statusCode);
                throw new TDPException(errorCode);
            }

            return null;
        } finally {
            actionAppend.releaseConnection();
        }
    }

    /**
     * Get the diff metadata introduced by the step to append (ex : the created columns)
     */
    private StepDiff getDiffMetadata() throws IOException {
        // get preparation details
        final Preparation preparation = getPreparation(preparationId);
        final String dataSetId = preparation.getDataSetId();

        // get dataset content with 1 row
        final InputStream content = getDatasetContent(dataSetId, 1L);

        // extract actions
        final List<Action> actions = getPreparationActions(preparation, "head");
        final String serializedParentActions = serializeActions(actions);

        final List<Action> addedActions = new ArrayList<>(actions);
        addedActions.addAll(step.getActions());
        final String serializedActions = serializeActions(addedActions);

        // get created columns ids
        final HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("stepActions", new StringBody(serializedActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("parentActions", new StringBody(serializedParentActions, ContentType.TEXT_PLAIN.withCharset("UTF-8"))) //$NON-NLS-1$ //$NON-NLS-2$
                .addPart("content", new InputStreamBody(content, ContentType.APPLICATION_JSON)) //$NON-NLS-1$
                .build();

        final String uri = transformationServiceUrl + "/transform/diff";
        final HttpPost transformationCall = new HttpPost(uri);
        try {
            transformationCall.setEntity(reqEntity);
            final InputStream diffInputStream = client.execute(transformationCall).getEntity().getContent();
            final ObjectMapper mapper = builder.build();
            return mapper.readValue(diffInputStream, StepDiff.class);
        }
        finally {
            transformationCall.releaseConnection();
        }
    }
}
