package org.talend.dataprep.transformation.api.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.Actions;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadata;

/**
 * Parse the actions a dataset and prepare the closures to apply.
 */
@Component
public class ActionParser {

    /** No op parsed actions. */
    //@formatter:off
    public static final ParsedActions IDLE_CONSUMER = new ParsedActions((row, context) -> {}, (rowMetadata, context) -> {});
    //@formatter:on

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private ApplicationContext context;

    /**
     * Return the parsed actions ready to be run.
     *
     * @param actions the actions to be parsed as string.
     * @return the parsed actions.
     */
    public ParsedActions parse(String actions) {
        if (actions == null) {
            // Actions cannot be null (but can be empty string for no op actions).
            throw new IllegalArgumentException("Actions parameter can not be null.");
        }
        try {
            if (StringUtils.isEmpty(actions)) {
                return IDLE_CONSUMER;
            }
            // Parse action JSON
            final Actions parsedActions = builder.build().reader(Actions.class).readValue(actions);
            // Create closures from parsed actions
            List<DataSetRowAction> rowActions = new ArrayList<>();
            List<DataSetMetadataAction> metadataActions = new ArrayList<>();
            for (Action parsedAction : parsedActions.getActions()) {
                final String name = ActionMetadata.ACTION_BEAN_PREFIX + parsedAction.getAction().toLowerCase();
                final ActionMetadata metadata = context.getBean(name, ActionMetadata.class);
                rowActions.add(metadata.create(parsedAction.getParameters()));
                metadataActions.add(metadata.createMetadataClosure(parsedAction.getParameters()));
            }
            // all set: wraps everything and return to caller
            return new ParsedActions(rowActions, metadataActions);
        } catch (Exception e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
