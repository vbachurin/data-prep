package org.talend.dataprep.transformation.pipeline;

import org.talend.dataprep.transformation.actions.common.ActionMetadata;

@FunctionalInterface
public interface ActionRegistry {

    ActionMetadata get(String name);
}
