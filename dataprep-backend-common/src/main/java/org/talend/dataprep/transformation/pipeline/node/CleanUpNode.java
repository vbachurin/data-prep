package org.talend.dataprep.transformation.pipeline.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.pipeline.Signal;

/**
 * A node implementation that cleans up transformation context when end of stream is reached.
 */
public class CleanUpNode extends BasicNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanUpNode.class);

    private final TransformationContext context;

    public CleanUpNode(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void signal(Signal signal) {
        if (signal == Signal.END_OF_STREAM || signal == Signal.CANCEL) {
            try {
                context.cleanup();
            } catch (Exception e) {
                LOGGER.error("Unable to clean context at {}.", signal, e);
            }
        }
        super.signal(signal);
    }
}
