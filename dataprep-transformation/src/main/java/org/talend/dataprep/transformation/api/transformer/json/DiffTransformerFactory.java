package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory for DiffTransformer (transformer that performs preview).
 * 
 * @see DiffTransformer
 */
@Component
public class DiffTransformerFactory implements TransformerFactory {

    /** The spring application context to use to get the DiffTransformer. */
    @Autowired
    private WebApplicationContext context;

    /** The component that parses Actions out of json string. */
    @Autowired
    private ActionParser parser;

    /** The previous actions for the transformer to compute the diff. */
    private ParsedActions previousActions;

    /** The new actions for the transformer. */
    private ParsedActions newActions;

    /** The indexes of row to compute the diff for. */
    private List<Integer> indexes;

    /**
     * @see TransformerFactory#get()
     */
    @Override
    public Transformer get() {
        return context.getBean(DiffTransformer.class, indexes, previousActions, newActions);
    }

    /**
     * Add the actions for the future transformer.
     * 
     * @param previousActions the previous actions used to compute the diff.
     * @param newActions the new actions to display.
     * @return this factory.
     */
    public DiffTransformerFactory withActions(final String previousActions, String newActions) {
        this.previousActions = previousActions == null ? ActionParser.IDLE_CONSUMER : parser.parse(previousActions);
        this.newActions = newActions == null ? ActionParser.IDLE_CONSUMER : parser.parse(newActions);
        return this;
    }

    /**
     * Add the indexes for the future transformer.
     * 
     * @param indexes the row indexes to compute the diff for.
     * @return this factory.
     */
    public DiffTransformerFactory withIndexes(final String indexes) {
        this.indexes = indexes == null ? null : parseIndexes(indexes);
        return this;
    }

    /**
     * Parses the given json string into a list of integer.
     * 
     * @param indexes the json string of indexes.
     * @return the list of integer that matches the given json string.
     */
    private List<Integer> parseIndexes(final String indexes) {
        try {
            final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            final JsonNode json = mapper.readTree(indexes);

            final List<Integer> result = new ArrayList<>(json.size());
            for (JsonNode index : json) {
                result.add(index.intValue());
            }
            return result;
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_ACTIONS, e);
        }
    }
}
