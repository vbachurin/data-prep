package org.talend.dataprep.transformation.api.action;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.stream.ExtendedStream;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public class ActionTestWorkbench {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionTestWorkbench.class);

    public static void test(RowMetadata rowMetadata, DataSetRowAction... actions) {
        test(new DataSetRow(rowMetadata), actions);
    }

    public static void test(DataSetRow input, DataSetRowAction... actions) {
        test(Collections.singletonList(input), actions);
    }

    public static void test(Collection<DataSetRow> input, DataSetRowAction... actions) {
        TransformationContext context = new TransformationContext();
        final List<DataSetRowAction> allActions = new ArrayList<>();
        Collections.addAll(allActions, actions);
        // Perform transformations
        ExtendedStream.extend(input.stream()).mapOnce(r -> {
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
            DataSetRow current = r;
            final Iterator<DataSetRowAction> iterator = allActions.iterator();
            while (iterator.hasNext()) {
                final DataSetRowAction action = iterator.next();
                final ActionContext actionContext = context.in(action);
                current.setRowMetadata(actionContext.getInputRowMetadata());
                current = action.apply(current, actionContext);
                current.setRowMetadata(actionContext.getOutputRowMetadata());
                // Check whether we should continue using this action or not
                final ActionContext.ActionStatus actionStatus = actionContext.getActionStatus();
                switch (actionStatus) {
                    case OK:
                        LOGGER.debug("[Transformation] Continue using action '{}' (compilation step returned {}).", action, actionStatus);
                        break;
                    case CANCELED:
                        LOGGER.debug("[Transformation] Remove action '{}' (compilation step returned {}).", action, actionStatus);
                        iterator.remove();
                        break;
                }
            }
            context.setPreviousRow(current.clone());
            return current;
        }) //
        .forEach(r -> LOGGER.debug(r.toString()));
    }


}
