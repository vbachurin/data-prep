package org.talend.dataprep.transformation;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.stream.ExtendedStream;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public class BaseTransformer {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseTransformer.class);

    public static ExtendedStream<DataSetRow> baseTransform(Stream<DataSetRow> input, List<DataSetRowAction> allActions, TransformationContext context) {
        return ExtendedStream.extend(input)
            // Perform transformations
            .mapOnce(r -> {
                // Initial compilation of actions
                DataSetRow current = r;
                final Iterator<DataSetRowAction> iterator = allActions.iterator();
                while (iterator.hasNext()) {
                    final DataSetRowAction action = iterator.next();
                    final ActionContext actionContext = context.create(action);
                    actionContext.setInputRowMetadata(current.getRowMetadata().clone());
                    action.compile(actionContext);
                    final ActionContext.ActionStatus actionStatus = actionContext.getActionStatus();
                    switch (actionStatus) {
                        case OK:
                            LOGGER.debug("[Compilation] Continue using action '{}' (compilation step returned {}).", action, actionStatus);
                            current = action.apply(current, actionContext);
                            break;
                        case CANCELED:
                        case DONE:
                            LOGGER.debug("[Compilation] Remove action '{}' (compilation step returned {}).", action, actionStatus);
                            iterator.remove();
                            break;
                    }
                    actionContext.setOutputRowMetadata(current.getRowMetadata().clone());
                }
                context.setPreviousRow(current.clone());
                return current;
            }, //
            r -> {
                // Apply compiled actions on data
                DataSetRow current = r;
                final Iterator<DataSetRowAction> iterator = allActions.iterator();
                while (iterator.hasNext()) {
                    final DataSetRowAction action = iterator.next();
                    final ActionContext actionContext = context.in(action);
                    current.setRowMetadata(actionContext.getInputRowMetadata());
                    if (actionContext.getActionStatus() != ActionContext.ActionStatus.DONE) {
                        // Only apply action if it hasn't indicated it's DONE.
                        current = action.apply(current, actionContext);
                    }
                    current.setRowMetadata(actionContext.getOutputRowMetadata());
                    // Check whether we should continue using this action or not
                    final ActionContext.ActionStatus actionStatus = actionContext.getActionStatus();
                    switch (actionStatus) {
                        case OK:
                            LOGGER.trace("[Transformation] Continue using action '{}' (compilation step returned {}).", action, actionStatus);
                            break;
                        case CANCELED:
                            LOGGER.trace("[Transformation] Remove action '{}' (compilation step returned {}).", action, actionStatus);
                            iterator.remove();
                            break;
                    }
                }
                context.setPreviousRow(current.clone());
                return current;
            });
    }
}
