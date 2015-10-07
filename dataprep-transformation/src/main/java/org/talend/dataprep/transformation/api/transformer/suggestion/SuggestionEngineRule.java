package org.talend.dataprep.transformation.api.transformer.suggestion;

import java.util.function.BiFunction;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

interface SuggestionEngineRule extends BiFunction<ActionMetadata, ColumnMetadata, Integer> {
}
