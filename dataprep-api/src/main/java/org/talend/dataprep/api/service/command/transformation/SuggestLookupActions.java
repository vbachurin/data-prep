package org.talend.dataprep.api.service.command.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.preparation.Actions;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.api.service.command.common.ChainedCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.transformation.api.action.metadata.blending.Lookup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.netflix.hystrix.HystrixCommand;

/**
 * Suggestion Lookup actions in addition to column actions.
 *
 * Take the suggested column actions as input and add the lookup ones.
 */
@Component
@Scope("request")
public class SuggestLookupActions extends ChainedCommand<InputStream, InputStream> {

    /**
     * Simplified constructor.
     *
     * @param client the http client to use.
     * @param input the command to execute to get the input.
     */
    public SuggestLookupActions(HttpClient client, SuggestColumnActions input) {
        super(client, input);
        execute(() -> new HttpGet(datasetServiceUrl + "/datasets"));
        on(HttpStatus.OK).then(process());
        // on error, @see getFallBack()
    }

    /**
     * If this command fails, the previous command's response can always be returned.
     * 
     * @see HystrixCommand#getFallback()
     */
    @Override
    protected InputStream getFallback() {
        // return the previous command result
        return getInput();
    }

    /**
     * @return the function that aggregates the SuggestColumnActions with the lookups.
     */
    private BiFunction<HttpRequestBase, HttpResponse, InputStream> process() {

        return (request, response) -> {

            // read suggested actions from previous command
            ArrayNode suggestedActions = null;
            try {
                suggestedActions = (ArrayNode) builder.build().reader(Actions.class).readTree(getInput());

                // list datasets from this command's response
                List<DataSetMetadata> dataSets = null;
                dataSets = builder.build().readValue(response.getEntity().getContent(),
                        new TypeReference<List<DataSetMetadata>>() {
                });

                // create and add all the possible lookup to the suggested actions
                for (DataSetMetadata dataset : dataSets) {
                    // TODO exclude current dataset (get this from the frontend and should be passed by the service)
                    final Lookup lookup = new Lookup();
                    lookup.adapt(dataset, getDatasetUrl(dataset));
                    final JsonNode jsonNode = builder.build().valueToTree(lookup);
                    suggestedActions.add(jsonNode);
                }

                // write the merged actions to the output streams
                return new ReleasableInputStream( //
                        IOUtils.toInputStream(suggestedActions.toString(), "UTF-8"), //
                        request::releaseConnection);
            } catch (IOException e) {
                throw new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_SUGGESTED_ACTIONS, e);
            }

        };

    }

    /**
     * @param dataset the dataset to get from the returned url.
     * @return the url to load the dataset.
     */
    private String getDatasetUrl(DataSetMetadata dataset) {
        return datasetServiceUrl + "/datasets/" + dataset.getId() + "/content?metadata=true";
    }

}
