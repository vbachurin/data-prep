package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DiffTransformerFactory implements TransformerFactory {

    /** No op parsed actions. */
    private static final ParsedActions IDLE_CONSUMER = new ParsedActions(row -> {
    }, Collections.emptyList());

    @Autowired
    private WebApplicationContext context;

    final ActionParser parser = new ActionParser();

    private ParsedActions oldActions;

    private ParsedActions newActions;

    private List<Integer> indexes;

    @Override
    public Transformer get() {
        return context.getBean(DiffTransformer.class, indexes, oldActions, newActions);
    }

    @Override
    public TransformerFactory withActions(final String... actions) {
        oldActions = actions[0] == null ? IDLE_CONSUMER : parser.parse(actions[0]);
        newActions = actions[1] == null ? IDLE_CONSUMER : parser.parse(actions[1]);
        return this;
    }

    @Override
    public TransformerFactory withIndexes(final String indexes) {
        this.indexes = indexes == null ? null : parseIndexes(indexes);
        return this;
    }

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
