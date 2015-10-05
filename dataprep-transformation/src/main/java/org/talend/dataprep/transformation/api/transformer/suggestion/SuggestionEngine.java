package org.talend.dataprep.transformation.api.transformer.suggestion;

import java.util.List;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.suggestion.model.Suggestion;

public interface SuggestionEngine {
    List<Suggestion> score(List<ActionMetadata> actions, ColumnMetadata column);
}
